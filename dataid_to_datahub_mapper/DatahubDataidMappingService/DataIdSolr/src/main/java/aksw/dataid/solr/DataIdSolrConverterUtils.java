package aksw.dataid.solr;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.jena.riot.RDFDataMgr;
import org.apache.log4j.Logger;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.SimpleSelector;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.sparql.vocabulary.FOAF;
import com.hp.hpl.jena.vocabulary.RDF;

/**
 * This class can be used to obtain Data ID
 * JSON SOLR documents which can be ingested into SOLR
 * 
 * @author mullekay
 *
 */
public class DataIdSolrConverterUtils {
	
	private static Logger logger = Logger.getLogger(DataIdSolrConverterUtils.class); 
	
	/** this is the root node which can be used to start parsing the RDF graph */
	private static final String ROOT_NODE = "http://rdfs.org/ns/void#DatasetDescription";
	
	/** associated agent uri */
	private static final String ASSOCIATED_AGENT = "http://dataid.dbpedia.org/ns/core#associatedAgent";
	
	/** key for agents */
	private static final String FIELD_TYPE_AGENT = "agents";
	
	/** key for data set */
	private static final String FIELD_TYPE_DATASET = "dataSets";
	
	/** this entry can be used as a link to the actual associated agent document */
	private static final String FIELD_ASSOCIATED_AGENT = "associatedAgent";
	
	/** key for id */
	private static final String FIELD_ID = "id";
	
	/** Jena Model */
	protected Model instanceModel;
	
	/** base URL of ontology */
	protected final List<String> dataIdBaseUris = new ArrayList<>();
	
	/** jena model of ontology */
	protected static Model dataIdOntologyModel;
	
	/** Property which can be used to find associated agents */
	protected final Property associatedAgent;
	
	/** known SOLR field names */
	protected final Collection<String> knownSolrFieldNames;
	
	/**
	 *
	 * @param locationDataidOntology	- URL/path where data id ontology can be found
	 * @param knownSolrFieldNames		- collection of known field names which are supported by SOLR
	 * @param namespacePrefixes			- namespace prefixes of existing model
	 * @throws DataIdSolrException
	 */
	public DataIdSolrConverterUtils(final String dataIdBaseUri,
									final String locationDataidOntology,
									final Collection<String> knownSolrFieldNames)
											throws DataIdSolrException {
		if (null == locationDataidOntology) {
			String msg = "No data id ontology information passed in";
			logger.error(msg);
			throw new DataIdSolrException(msg);
		}
		
		try {		
			// just make sure it is created
			Model dataIdModel = createOntologyModel(locationDataidOntology);
					
			this.associatedAgent = dataIdModel.createProperty("http://dataid.dbpedia.org/ns/core#associatedAgent");
		} catch (Exception e) {
			throw new DataIdSolrException("Was not able to load data id description", e);
		}
		
		if (null != dataIdBaseUri) {
			this.dataIdBaseUris.add(dataIdBaseUri);	
		}
		
		this.knownSolrFieldNames = knownSolrFieldNames;
	}
	
	/**
	 * This method can be used to add a
	 * @param baseUri
	 * @param dataIdLocation
	 * @throws DataIdSolrException
	 */
	public void addDataIdModel(final String baseUri, final String dataIdLocation) throws DataIdSolrException {
		if (null == dataIdLocation) {
			logger.warn("Did not get valid data id location");
		}
		
		try {
			File file = getDataFile(dataIdLocation);
			Model modelDataId = RDFDataMgr.loadModel(file.getPath());
			
			if (null == this.instanceModel) {
				// assign this as our instance model
				this.instanceModel = modelDataId;
				// make sure we load the actual data id ontology as well
				this.instanceModel.add(DataIdSolrConverterUtils.dataIdOntologyModel);
			} else {
				// add new model to existing one
				this.instanceModel.add(modelDataId);
			}
			
			if (null != baseUri && false == baseUri.isEmpty()) {
				this.dataIdBaseUris.add(baseUri);
			}
		} catch (Exception e) {
			throw new DataIdSolrException("Was not able to add new data id model", e);
		} finally {
			
		}
	}
	
	/**
	 * This method can be used to create the model of the dataid ontology
	 * 
	 * @param dataIdOntologyUrl	- url to dataid onotlogy
	 * @return data id jena model
	 * @throws DataIdSolrException
	 */
	protected static synchronized Model createOntologyModel(final String dataIdOntologyUrl) throws DataIdSolrException {
		if (null == dataIdOntologyUrl) {
			logger.warn("No location/url for data id ontology passed in");
			return null;
		}
		
		try {
			File file = getDataFile(dataIdOntologyUrl);
			Model modelDataId = RDFDataMgr.loadModel(file.getPath());
			DataIdSolrConverterUtils.dataIdOntologyModel = modelDataId;		
		} catch (Exception e) {
			throw new DataIdSolrException("Was not able to load data id description", e);
		}
		
		return DataIdSolrConverterUtils.dataIdOntologyModel;	
	}
	
	/**
	 * This method can be used to get the model of the dataid ontology
	 * 
	 * @param dataIdOntologyUrl	- url to dataid onotlogy
	 * @param dataIdBaseUrl		- base uri for dataid ontology
	 * @return data id jena model
	 * @throws DataIdSolrException
	 */
	protected static synchronized Model getOntologyModel() throws DataIdSolrException {
		return DataIdSolrConverterUtils.dataIdOntologyModel;	
	}
	
	/**
	 * This method can be used to load a local or remote file
	 * and make it available to the application.
	 * 
	 * If the file is remote, it is downloaded to the tmp directory
	 * of the local machine. Please make sure to close the file, in
	 * order to free the file resources and to delete the file.
	 * 
	 * @param url
	 * @return
	 */
	protected static File getDataFile(final String url) throws Exception {
		if (null == url) {
			return null;
		}
		
		if (url.startsWith("http")) {
			URL remoteUrl = new URL(url);
			
			String fileName = remoteUrl.getFile();
			String suffix = fileName.substring(fileName.lastIndexOf("."));
			File tmpFile = File.createTempFile("dataid", suffix);
			tmpFile.deleteOnExit();
			
			InputStream stream = remoteUrl.openStream();
			BufferedReader reader = new BufferedReader(
			        new InputStreamReader(stream));

			FileOutputStream writer = new FileOutputStream(tmpFile);
			String inputLine;
			
			
			while (null != (inputLine = reader.readLine())) {
				// make sure we have a newline
				writer.write("\n".getBytes());
				writer.write(inputLine.getBytes());
			}

			writer.flush();
			writer.close();
			
			reader.close();
			stream.close();
			
			return tmpFile;
		} else {
			File file = new File(url);
			if (file.exists()) {
				return file;
			}
			
			URL fileUrl = DataIdSolrConverterUtils.class.getResource(url);
			if (null == fileUrl) {
				fileUrl = DataIdSolrConverterUtils.class.getClassLoader().getResource(url);
			}
			
			if (null != fileUrl) {
				return new File(fileUrl.getFile());
			}
		}
		
		throw new DataIdSolrException("Was not able to find file at URL: " + url);
	}
	
	/**
	 * 
	 * @return Pattern of all registered prefixes
	 */
	public Pattern getPrefixPattern() {
		Pattern pattern = this.createPattern();
		return pattern;
	}
	
	/**
	 * This method can be used to get
	 * the namespace prefixes of the loaded model
	 * @return namespace prefixes
	 */
	public Set<String> getNamespacePrefixes() {
		Set<String> namespacePrefixes = new HashSet<>();
		
		namespacePrefixes.addAll(this.instanceModel.getNsPrefixMap().values());
		namespacePrefixes.addAll(this.dataIdBaseUris);
		
		return Collections.unmodifiableSet(namespacePrefixes);
	}
	
	/**
	 * Method which can be used to create Pattern to filter out namespace prefixes from URI.
	 * 
	 * This method can be used to map URI to SOLR field name.
	 * 
	 * @param nameSpaces	- List of namespaces which are present in the Ontology
	 * @param this.baseUri		- base URI if required or null otherwise
	 * @return Pattern which can be used to find/filter namespace URIs or null if no namespaces are passed in
	 */
	protected Pattern createPattern() {
		// make list of namespace prefixes
		List<String> nameSpaces = new ArrayList<>();
		Map<String, String> namespacePrefixMap = this.instanceModel.getNsPrefixMap();
		for (String nameSpace : namespacePrefixMap.values()) {
			nameSpaces.add(nameSpace);			
		}
		
		// create pattern which contains all these namespace prefixes
		StringBuffer buffer = new StringBuffer();
		int i = 1;
		if (null != this.dataIdBaseUris && false == this.dataIdBaseUris.isEmpty()) {
			buffer.append("((");
			buffer.append(this.dataIdBaseUris.get(0));
			buffer.append(")");
			
			// start with namespace start
			i = 0;
		} else {
			buffer.append("((");
			buffer.append(nameSpaces.get(0));
			buffer.append(")");
		}
		
		if (1 < this.dataIdBaseUris.size()) {
			for (String dataIdBaseUri : this.dataIdBaseUris.subList(1, this.dataIdBaseUris.size())) {
				buffer.append("|(");
				buffer.append(dataIdBaseUri);
				buffer.append(")");
			}
		}
		
		for (; i < nameSpaces.size(); ++i) {
			buffer.append("|(");
			buffer.append(nameSpaces.get(i));
			buffer.append(")");
		}
		buffer.append(")");
		
		Pattern namespacePattern = Pattern.compile(buffer.toString());
		if (logger.isDebugEnabled()) {
			logger.debug("Created pattern: " + namespacePattern.pattern());
		}
		return namespacePattern;
	}
	
	/**
	 * This method can be used to obtain agents which are associated with a DataId/DataSet/etc.
	 * 
	 * @param model					- Jena RDF Model
	 * @param agentIdResource		- Agent Resource (Subject)
	 * @param namespacePattern		- pattern of namespace prefixes
	 * @param associatedAgent		- RDF property/predicate which can be used to find the associated agent
	 * @return List of data documents which contain agent data
	 * @throws DataIdSolrException 
	 */
	protected List<DataIdSolrDocument> getAgents(final Resource agentIdResource,
													 final Pattern namespacePattern,
													 final Property associatedAgent) throws DataIdSolrException {
		if (null == agentIdResource || null == namespacePattern) {
			return Collections.emptyList();
		}
		
		StmtIterator iterAgentsClasses = this.instanceModel.listStatements(
				 new SimpleSelector(agentIdResource, associatedAgent, (RDFNode) null));	
		
		// Output query results
		Set<RDFNode> agentNodes = new HashSet<>();
		while (iterAgentsClasses.hasNext()) {
			Statement statement = iterAgentsClasses.next();
			agentNodes.add(statement.getObject());
		}
		
		// list which stores all the agents which have been found
		List<DataIdSolrDocument> resultAgents = new ArrayList<>();	
		
		// iterate through all the agents and get their information
		for (RDFNode agentNode : agentNodes) {
			StmtIterator iterAgents = this.instanceModel.listStatements(
				    new SimpleSelector(agentNode.asResource(), null, (RDFNode) null));
			
			if (null == iterAgents) {
				continue;
			}
			
			DataIdSolrDocument agentDocument =
					new DataIdSolrDocument(FIELD_ID, agentNode.toString(),
							this.knownSolrFieldNames, this.getNamespacePrefixes());
			
			while (iterAgents.hasNext()) {
				Statement statement = iterAgents.next();
				
				// ignore type statement
				if (statement.getPredicate().equals(RDF.type)) {
					agentDocument.addFieldData("type", namespacePattern.matcher(statement.getObject().toString()).replaceAll(""));
					agentDocument.addFieldData("type", "Agent");
					continue;
				}
				
				String cleanedFieldName = namespacePattern.matcher(statement.getPredicate().toString()).replaceAll("");
				String cleanedValue = statement.getObject().toString();
				
				agentDocument.addFieldData(cleanedFieldName, cleanedValue);
			}
			
			resultAgents.add(agentDocument);
		}
		
		return resultAgents;
	}

	/**
	 * This method can be used to read dataId information from a DataId Jena Model
	 * 
	 * @param dataIds				- List of dataId URIs
	 * @param model					- Jena RDF model
	 * @param namespacePattern		- namespace prefix pattern which can be used to get/filter namespace prefixes
	 * @return List of JSON strings per dataset
	 * @throws DataIdSolrException
	 */
	protected Map<String, List<DataIdSolrDocument>> getJsonSolrDataIds(final Resource dataId, final Model model, final Pattern namespacePattern) throws DataIdSolrException {
		if (null == model || null == dataId) {
			return Collections.emptyMap();
		}
		
		// map which stores information which are obtained from dataset
		Map<String, List<DataIdSolrDocument>> dataSetJsonDocuments = new LinkedHashMap<>();		
			
		StmtIterator iter = model.listStatements(
			    new SimpleSelector(dataId, FOAF.topic, (RDFNode) null));
		
		List<RDFNode> dataSets = new ArrayList<>();
		while (iter.hasNext()) {
			Statement statement = iter.next();
			
			dataSets.add(statement.getObject());
		}
		
		if (dataSets.isEmpty()) {
			// if we don't have a dataset --> continue
			return Collections.emptyMap();
		}		
		
		for (RDFNode dataSet : dataSets) {
			final RDFNode finalDataSet = dataSet;
			
			DataIdSolrDocument dataSetDocument = null;
			
			StmtIterator iterSub = this.instanceModel.listStatements(
				    new SimpleSelector(null, null, (RDFNode) null) {
				    	@Override
				        public boolean selects(Statement s) {
				            return s.getSubject().toString().equals(finalDataSet.toString());
				        };
				    });
			
			dataSetDocument = new DataIdSolrDocument(FIELD_ID, dataSet.toString(),
					this.knownSolrFieldNames, this.getNamespacePrefixes());
			dataSetDocument.addFieldData("type", "Dataset");
			
			while (iterSub.hasNext()) {
				Statement statement = iterSub.next();
				
				// ignore associated agent for now
				if (associatedAgent.equals(statement.getPredicate())) {
					continue;
				}
				
				// store field name and field value
				String cleanedFieldName = namespacePattern.matcher(statement.getPredicate().toString()).replaceAll("");
				
				String cleanedFieldValue = statement.getObject().toString();
				if (RDF.type.equals(statement.getPredicate())) {
					cleanedFieldValue = namespacePattern.matcher(statement.getObject().toString()).replaceAll(""); 
				} else {
					cleanedFieldValue = statement.getObject().toString();
				}
				dataSetDocument.addFieldData(cleanedFieldName, cleanedFieldValue);
			}
			
			// store the dataset document
			List<DataIdSolrDocument> resultDataSets = dataSetJsonDocuments.get(FIELD_TYPE_DATASET);
			if (null == resultDataSets) {
				resultDataSets = new ArrayList<>();
				dataSetJsonDocuments.put(FIELD_TYPE_DATASET, resultDataSets);
			}
			
			resultDataSets.add(dataSetDocument);
			
			// get agent data
			List<DataIdSolrDocument> agentDocs = getAgents(dataSet.asResource(), namespacePattern, associatedAgent);
			if (null == agentDocs || agentDocs.isEmpty()) {
				throw new DataIdSolrException("No associated agents found!");
			}
			
			List<DataIdSolrDocument> agentResults = dataSetJsonDocuments.get(FIELD_TYPE_AGENT);
			if (null == agentResults) {
				agentResults = new ArrayList<>();
				dataSetJsonDocuments.put(FIELD_TYPE_AGENT, agentResults);
			}
			
			// store agent document information
			for (DataIdSolrDocument agentDocument : agentDocs) {
				if (false == agentResults.contains(agentDocument)) {
					agentResults.add(agentDocument);
				}
				
				dataSetDocument.addFieldData(FIELD_ASSOCIATED_AGENT, agentDocument.getId());
			}
		}
		
		return dataSetJsonDocuments;
	}
	
	
	/**
	 * Base Method which can be used to retrieve data id information from an RDF Jena Model
	 * 
	 * @return map of datasets and agents JSON strings
	 * @throws DataIdSolrException
	 */
	public Map<String, List<DataIdSolrDocument>> getJsonSolrDocuments() throws DataIdSolrException {
		
		// create property for associated agent
		Property associatedAgent = this.instanceModel.createProperty(ASSOCIATED_AGENT);
		
		// create pattern out of namespaces which can be used for filtering out or clearing namespaces from URI
		Pattern namespacePattern = createPattern();
		
		// get data id statement
		StmtIterator iterDataId = this.instanceModel.listStatements(
			    new SimpleSelector(null, RDF.type,
			    	this.instanceModel.createResource(ROOT_NODE)));
		
		// iterate through all data ids
		Map<String, List<DataIdSolrDocument>> jsonSolrDocuments = new LinkedHashMap<>();
		while (iterDataId.hasNext()) {
			Statement statement = iterDataId.next();
			
			Resource dataId = statement.getSubject();
			StmtIterator dataIdIterator = this.instanceModel.listStatements(dataId, null, (RDFNode) null);
			
			if (null == dataIdIterator || false == dataIdIterator.hasNext()) {
				continue;
			}
			
			// create new document instance which can store document data
			DataIdSolrDocument dataIdDocument = new DataIdSolrDocument(FIELD_ID, dataId.toString(),
											this.knownSolrFieldNames, this.getNamespacePrefixes());			
			while (dataIdIterator.hasNext()) {
				Statement dataIdStatement = dataIdIterator.next();
				
				if (associatedAgent.equals(dataIdStatement.getPredicate()) ||
					FOAF.topic.equals(dataIdStatement.getPredicate())) {
					continue;
				}
				
				// store data of dataId instance
				String dataIdFieldName = namespacePattern.matcher(dataIdStatement.getPredicate().toString()).replaceAll("");
				String dataIdFieldValue = dataIdStatement.getObject().toString();
				if (RDF.type.equals(dataIdStatement.getPredicate())) {
					dataIdFieldValue = namespacePattern.matcher(dataIdStatement.getObject().toString()).replaceAll("");
				} else {
					dataIdFieldValue = dataIdStatement.getObject().toString();
				}
				dataIdDocument.addFieldData(dataIdFieldName, dataIdFieldValue);
			}
			
			// get associated agents for data Id
			List<DataIdSolrDocument> dataIdAgents = getAgents(dataId, namespacePattern, associatedAgent);
			if (null == dataIdAgents || dataIdAgents.isEmpty()) {
				throw new DataIdSolrException("No associated agents found");
			}
			
			
			// make sure associated agents are added
			for (DataIdSolrDocument agent : dataIdAgents) {				
				dataIdDocument.addFieldData(FIELD_ASSOCIATED_AGENT, agent.getId());
			}
			
			// store data id agents --> they are the first --> no test required
			jsonSolrDocuments.put(FIELD_TYPE_AGENT, dataIdAgents);
			
			Map<String, List<DataIdSolrDocument>> dataSets =
					getJsonSolrDataIds(dataId, this.instanceModel, namespacePattern);
			
			for (String type : dataSets.keySet()) {
				if (type.equals(FIELD_TYPE_AGENT)) {
					// check if the final map has agents stored
					List<DataIdSolrDocument> storedDocAgents = jsonSolrDocuments.get(FIELD_TYPE_AGENT);
					if (null == storedDocAgents) {
						// if not create list
						storedDocAgents = new ArrayList<>();
						jsonSolrDocuments.put(FIELD_TYPE_AGENT, storedDocAgents);
					}
					
					// get retrieved agents
					List<DataIdSolrDocument> retrievedDocAgents = dataSets.get(FIELD_TYPE_AGENT);
					
					// check if we know them already --> if not --> add to the list
					for (DataIdSolrDocument docAgent : retrievedDocAgents)
						if (false == storedDocAgents.contains(docAgent)) {
							storedDocAgents.add(docAgent);
						} else {
							/// TOOD mullekay : logger
							//System.out.println("Know agent: " + docAgent);
						}
				} else {
					List<DataIdSolrDocument> children = dataSets.get(type);					
					dataIdDocument.addChildDocuments(children);
					
					List<DataIdSolrDocument> dataIdDocs = jsonSolrDocuments.get("dataid");
					if (null == dataIdDocs) {
						dataIdDocs = new ArrayList<>();
						jsonSolrDocuments.put("dataid", dataIdDocs);
					}
					
					dataIdDocs.add(dataIdDocument);
				}
			}
		}
			
		return jsonSolrDocuments;
	}
}
