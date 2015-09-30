package aksw.dataid.solr;

import java.io.Closeable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.aksw.dataid.config.DataIdConfig;
import org.apache.log4j.Logger;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.apache.noggit.JSONUtil;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.impl.ConcurrentUpdateSolrClient;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.util.NamedList;

/**
 * This class can be used to deal with all DataId document related
 * issues (e.g. ingestion, deletion, search queries, etc.)
 * 
 * @author mullekay
 *
 */
public class DataIdSolrHandler implements Closeable {
	
	Logger logger = Logger.getLogger(DataIdSolrHandler.class);
	
	/** solr client instance which is used in this application */
	private final SolrClient solrClient;
	
	/** map which specifies supported fields and boost */
	private static final Map<String, Float> fieldBoostMap = getFieldBoostMap();	
	private static Map<String, Float> getFieldBoostMap() {
		Map<String, Float> fieldBoostMap = new HashMap<>();
		
		/// TODO mullekay: Think about covering all the fields (How high is the overhead?)
		fieldBoostMap.put("id", 5f);
		fieldBoostMap.put("associatedAgent", 1f);
		fieldBoostMap.put("type", 1f);
		fieldBoostMap.put("title", 5f);
		fieldBoostMap.put("keyword", 5f);
		fieldBoostMap.put("description", 5f);
		fieldBoostMap.put("label", 5f);
		fieldBoostMap.put("agentName", 2f);
		fieldBoostMap.put("agentMail", 2f);
		fieldBoostMap.put("language", 5f);
		fieldBoostMap.put("license", 2f);
		fieldBoostMap.put("licenseName", 5f);
		fieldBoostMap.put("rights", 2f);
		fieldBoostMap.put("comment", 2f);
		fieldBoostMap.put("distribution", 1f);
		fieldBoostMap.put("downloadURL", 1f);
		fieldBoostMap.put("graphName", 1f);
		fieldBoostMap.put("mediaType", 1f);
		fieldBoostMap.put("tag", 5f);
		fieldBoostMap.put("tool", 1f);
		
		return fieldBoostMap;
	}
	
	/**
	 * Standard constructor which uses default settings from DataIdConfig
	 */
	public DataIdSolrHandler() {
		String solrUrl = DataIdConfig.getInstance().get("solrUrl");
		this.solrClient = this.getSolrClient(solrUrl);
	}
	
	/**
	 * Constructor
	 * 
	 *  @param solrUrl	- URL which contain IP/DNS port and collection name
	 * (e.g. http://localhost:8983/solr/Test)
	 */
	public DataIdSolrHandler(final String solrUrl) {
		this.solrClient = this.getSolrClient(solrUrl);
	}
	
	/**
	 * This method can be used to obtain a Solr client instance
	 * 
	 * @todo Check whether we want to support Solr Cloud setup
	 * @param solrUrl	- URL which contain IP/DNS port and collection name
	 * (e.g. "http://localhost:8983/solr/Test")
	 * @return solr client instance
	 */
	protected SolrClient getSolrClient(final String solrUrl) {
		final SolrClient solrClient;
		// can be swapped/changed once we support solr cloud instances
		if (false) {
			solrClient = new ConcurrentUpdateSolrClient(solrUrl, 16, 64);
		} else {
			solrClient = new HttpSolrClient(solrUrl);
		}
		return solrClient;	    
	}
	
	/**
	 * This method can be used to obtain all the registered and known field names,
	 * which are stored in SOLR.
	 * 
	 * @return collection of known field names
	 * throws DataIdSolrException
	 */
	@SuppressWarnings("unchecked")
	public Collection<String> getKnownSolrFieldNames() throws DataIdSolrException {
		
		try {
			// create query to obtain field information from SOLR
			SolrQuery query = new SolrQuery();
			query.setRequestHandler("/schema/fields");
			query.setParam("includeDynamic", "true");
			query.setParam("showDefaults", "true");
			
			QueryResponse result = this.solrClient.query(query);
			if (null == result) {
				return null;
			}
			
			NamedList<Object> response = result.getResponse();
			if (null == response) {
				return null;
			}
			
			// get actual field data
			List<Object> fieldsInfo = (List<Object>) response.get("fields");
			if (null == fieldsInfo) {
				return null;
			}
			
			// go through all the fields and get name
			Set<String> knownFieldNames = new HashSet<>();
			for (int i = 0; i < fieldsInfo.size(); ++i) {
				NamedList<Object> fieldInfo = (NamedList<Object>) fieldsInfo.get(i);
				if (null == fieldInfo) {
					continue;
				}
				
				String fieldName = (String) fieldInfo.get("name");
				if (null != fieldName && false == fieldName.isEmpty()) {				
					knownFieldNames.add(fieldName);
				}
			}			
			
			// return known field names
			return knownFieldNames;
		} catch (Exception e) {
			throw new DataIdSolrException("Was not able to obtain field names from SOLR", e);
		}
	}
	
	/**
	 * This method can be used to add solr documents to the SOLR index
	 * 
	 * @param solrDocuments	- list of SOLR documents
	 * @throws DataIdSolrException
	 */
	public void addDataIdSolrDocuments(List<DataIdSolrDocument> solrDocuments) throws DataIdSolrException{
		if (null == solrDocuments || solrDocuments.isEmpty()) {
			return;
		}
		
		try {
		for (DataIdSolrDocument solrDoc : solrDocuments) {
			solrClient.add(solrDoc.getSolrDoc(), 10000);		
		}
		
		solrClient.commit();
		
		} catch (Exception e) {
			throw new DataIdSolrException("Error while adding documents to SOLR", e);
		}
		
	}
	
	/**
	 * This method can be used to add facet queries to a SOLR query
	 * 
	 * @param query			- solrQuery to which the queries get added
	 * @param searchQuery	- search query which is used an an input to the solr query
	 */
	protected void addFacetQueries(final SolrQuery query, final Query searchQuery) {
		if (null == query || null == searchQuery) {
			logger.warn("Was not able to create facet query from search query: " + searchQuery);
			return;
		}
		
		// check if it is a boolean query
		if (searchQuery instanceof BooleanQuery) {
			for (BooleanClause clause : ((BooleanQuery) searchQuery).clauses()) {
				Query clauseQuery = clause.getQuery();
				
				this.addFacetQueries(query, clauseQuery);
			}
		} else {
			// store old boost
			float boost = searchQuery.getBoost();
			searchQuery.setBoost(1f);
			
			query.addFacetQuery(searchQuery.toString());
			
			// set old boost
			searchQuery.setBoost(boost);
		}
	}
	
	/**
	 * Method which can be used to send JSON formated queries to the SOLR backend.
	 * 
	 * The following keywords are supported:
	 * searchQuery 	- search query
	 * facetQueries	- facetQueries which is an array of facet filter queries
	 * 
	 * @param queryStringJson	- json query string
	 * @return JSON result string
	 * @throws DataIdSolrException
	 */
	public String search(final String queryStringJson) throws DataIdSolrException {
		if (null == queryStringJson) {
			System.err.print("No Search query passed in");
			return null;
		}
		
		final String searchQueryMarker = "\"searchQuery\":";
		int indexStartQuery = queryStringJson.indexOf(searchQueryMarker);
		if (0 > indexStartQuery) {
			System.err.print("Was not able to find query");
			return null;
		}
		
		int indexStartQueryValue = queryStringJson.indexOf("\"", indexStartQuery + searchQueryMarker.length());
		int indexEndQueryValue = queryStringJson.indexOf("\"", indexStartQueryValue + 1);
		if (0 > indexStartQueryValue || 0 > indexEndQueryValue) {
			System.err.print("Was not able to find query value");
			return null;
		}
		
		String queryString = queryStringJson.substring(indexStartQueryValue + 1, indexEndQueryValue);
		
		List<String> facetFilterQueries = null;
		final String facetFilterQueryMarker =  "\"facetQueries\":[";
		int indexFacetQueryMarkerStart = queryStringJson.indexOf(facetFilterQueryMarker);
		int indexFacetQueryMarkerEnd = queryStringJson.indexOf("]", indexFacetQueryMarkerStart);
		if (0 <= indexFacetQueryMarkerStart) {
			int indexFacetQueryStart = queryStringJson.indexOf("\"", indexFacetQueryMarkerStart +
															facetFilterQueryMarker.length());
			if (0 <= indexFacetQueryStart) {
				facetFilterQueries = new ArrayList<>();
				int indexFacetQueryEnd = queryStringJson.indexOf("\"", indexFacetQueryStart + 1);
				while (0 <= indexFacetQueryStart && 0 <= indexFacetQueryEnd &&
						indexFacetQueryStart < indexFacetQueryMarkerEnd &&
						indexFacetQueryEnd < indexFacetQueryMarkerEnd) {
					facetFilterQueries.add(queryStringJson.substring(indexFacetQueryStart + 1, indexFacetQueryEnd));
					
					indexFacetQueryStart = queryStringJson.indexOf("\"", indexFacetQueryEnd + 1);
					indexFacetQueryEnd = queryStringJson.indexOf("\"", indexFacetQueryStart + 1);
				}
			}
		}
		
		String [] filterQueries = null;
		if (null != facetFilterQueries && false == facetFilterQueries.isEmpty()) {
			filterQueries = facetFilterQueries.toArray(new String[facetFilterQueries.size()]);
		}
		
		String resultJson = this.search(queryString, filterQueries);
		if (null == resultJson) {
			return queryStringJson;
		}
		
		resultJson = resultJson.substring(0, resultJson.length() - 1) +
					"," + queryStringJson.substring(1, queryStringJson.length() - 1) +
					"" +
					"}";
		return resultJson;
	}
	
	/**
	 * This method can be used to perform a text search against the SOLR backend.
	 * 	
	 * @param queryString		- query text string
	 * @param filterQueries		= array of filter queries
	 * @return json string with results
	 * @throws DataIdSolrException
	 */
	public String search(final String queryString, final String[] filterQueries) throws DataIdSolrException {
		if (null == queryString) {
			return null;
		}
		
		// specifies the fields which can be used for search queries
		String[] fields = fieldBoostMap.keySet().toArray(new String[fieldBoostMap.size()]);
		
		try {
			// create a query parser which creates queries for multiple fields
			MultiFieldQueryParser parser =
					new MultiFieldQueryParser(fields, new StandardAnalyzer(), fieldBoostMap);
			String queryStringCleaned = queryString.replace("'", "\"");
			
			Query queries;
			try {
				queries = parser.parse(queryStringCleaned);
			} catch (Exception e) {
				logger.warn("Invalid Query: " + queryString +
							" with error message: " + e.getLocalizedMessage());
				return null;
			}
			
			SolrQuery query = new SolrQuery();
			// store query
			query.setQuery(queries.toString());

			query.setParam("start", "0");
			query.setParam("rows", "100");
			
			if (null != filterQueries && 0 < filterQueries.length) {
				String[] cleanedFilterArray = new String[filterQueries.length];
				for (int i = 0; i < filterQueries.length; ++i) {
					// make sure they are proper phrase queries, if requested
					cleanedFilterArray[i] = filterQueries[i].replace("'", "\"");
				}
				query.setParam("fq", cleanedFilterArray);
			}

			query.setParam("version", "2.2");			
			query.setParam("indent", "on");
			query.setParam("wt", "json");
			query.setParam("callback", "?");
			query.setParam("json.wrf", "on_data");
			
			boolean addFacetInformation = true;
			if (addFacetInformation) {
				// enable facet search
				query.setFacet(true);
				query.setParam("facet.method", "fcs");
				
				// add the actual facet queries
				this.addFacetQueries(query, queries);
			}
			
			QueryResponse response;
			try {
				response = this.solrClient.query(query);
			} catch (Exception e) {
				logger.warn("Error when executing query with SOLR: " + queryString +
						" with error message: " + e.getLocalizedMessage());
				return null;
			}
			
			if (logger.isDebugEnabled()) {
				if (null != response.getResults()) {
					logger.debug("Got number of docs: " + response.getResults().size());					
					logger.debug("Facet Query Results: " + response.getFacetQuery());
				}
			}
			
			// get result information we need to return
			StringBuffer jsonFacetBuffer = new StringBuffer();
			
			Map<String, Integer> facetQueryResults = response.getFacetQuery();
			if (null != facetQueryResults && 0 < facetQueryResults.size()) {
				jsonFacetBuffer.append("\"facetQuery\":[");
				
				boolean hasEntry = false;
				for (String facetQuery : facetQueryResults.keySet()) {
					Integer occuranceCount = facetQueryResults.get(facetQuery);
					if (null != occuranceCount && 0 < occuranceCount) {
						if (hasEntry) {
							jsonFacetBuffer.append(",{\"");
						} else {
							jsonFacetBuffer.append("{\"");
						}
						
//						jsonFacetBuffer.append(facetQuery.toString().replace(":", "\\:"));
						jsonFacetBuffer.append(facetQuery.toString().replace("\"", "'"));
						jsonFacetBuffer.append("\":");
						jsonFacetBuffer.append(occuranceCount);
						jsonFacetBuffer.append("}");
						
						hasEntry = true;
					}
				}
				jsonFacetBuffer.append("],");
			}
			
			// put together JSON strings which need to be returned
			StringBuffer buffer = new StringBuffer();
			
			buffer.append("{");
			String jsonFacets = jsonFacetBuffer.toString();
			if (false == jsonFacets.isEmpty()) {
				buffer.append(jsonFacets);
			}
			
			String jsonDocuments = JSONUtil.toJSON(response.getResults());
			buffer.append("\"documentResults\":");
			buffer.append(jsonDocuments);
			buffer.append("}");
			return buffer.toString();//.replace("\"", "\\\"");
		} catch (Exception e) {
			throw new DataIdSolrException("Exception during search", e);
		}
	}
	
	
	/**
	 * This method can be used to delete a data id doc
	 * and all associated documents (datasets, associated agents)
	 * 
	 * @param dataId
	 * @throws DataIdSolrException
	 */
	public void deleteDataIdDocument(final String dataId) throws DataIdSolrException {
		if (null == dataId) {
			return;
		}	
		
		try {			
			SolrQuery queryChildren = new SolrQuery();
			// look for all the children and get their IDs and associated agents
			queryChildren.setQuery("{!child of='type:\"DatasetDescription\"'}id:\"" + dataId.replace(":", "\\:") + "\"");
			queryChildren.setParam("fl", "id,associatedAgent");
			QueryResponse childrenResult = this.solrClient.query(queryChildren);
			SolrDocumentList results = childrenResult.getResults();
			
			if (logger.isDebugEnabled()) {
				logger.debug("Found documents to delete: " + results.size());
			}
			
			for (SolrDocument result : results) {
				String id = (String) result.get("id");
				@SuppressWarnings("unchecked")
				List<String> associatedAgents = (List<String>) result.get("associatedAgent");
				
				if (null == id || null == associatedAgents) {
					throw new DataIdSolrException(
							"Could not find dataset or associated agent ID");
				}
				
				if (logger.isDebugEnabled()) {
					logger.debug("Going to delete this ID: " + id +
								" and agents " + associatedAgents);
				}
				
				// delete ID and agent IDs
				this.solrClient.deleteById(id, 10000);
				this.solrClient.deleteById(associatedAgents, 10000);
			}
			
			// delete parent document
			this.solrClient.deleteById(dataId, 10000);
			// execute the deletion
			this.solrClient.commit();
		} catch (Exception e) {
			throw new DataIdSolrException("Problem when deleting dataid: " + dataId +
										" and exception: " + e.getMessage(), e);
		}
	}
	
	@Override
	public void close() {
		try {
			this.solrClient.close();
		} catch (Exception e) {
			throw new RuntimeException("Problem when executing close", e);
		}
	}

}
