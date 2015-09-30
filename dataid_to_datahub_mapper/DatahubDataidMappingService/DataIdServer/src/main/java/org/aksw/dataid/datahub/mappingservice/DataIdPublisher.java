package org.aksw.dataid.datahub.mappingservice;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import aksw.dataid.solr.DataIdSolrConverterUtils;
import aksw.dataid.solr.DataIdSolrDocument;
import aksw.dataid.solr.DataIdSolrException;
import aksw.dataid.solr.DataIdSolrHandler;

import org.aksw.dataid.config.DataIdConfig;
import org.aksw.dataid.datahub.jsonobjects.DatahubError;
import org.aksw.dataid.datahub.jsonobjects.Dataset;
import org.aksw.dataid.datahub.jsonobjects.ValidCkanResponse;
import org.aksw.dataid.errors.DataIdInputException;
import org.aksw.dataid.jsonutils.StaticJsonHelper;
import org.aksw.dataid.errors.DataHubMappingException;
import org.aksw.dataid.datahub.propertymapping.DataIdProcesser;
import org.aksw.dataid.datahub.restclient.CkanRestClient;
import org.aksw.dataid.ontology.IdPart;
import org.aksw.dataid.statics.StaticFunctions;
import org.aksw.dataid.virtuoso.VirtuosoDataIdGraph;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFParseException;

import javax.ws.rs.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;

@Path("dataid/publisher")
public class DataIdPublisher
{
    private VirtuosoDataIdGraph graph;
    private DataIdProcesser proc;

    public DataIdPublisher() throws SQLException, DataHubMappingException {
        graph = Main.getGraph();
        proc = new DataIdProcesser(DataIdConfig.getInstance().getMappingConfigPath());
        
        Map<String, Entry<String, String>> ontologyMap = DataIdConfig.getInstance().getOntologies();
		if (null == ontologyMap || ontologyMap.isEmpty()) {
			throw new RuntimeException("No ontology mappings stored in config");
		}
		
		Entry<String, String> dataIdOntologyEntry = ontologyMap.get("dataid");
		if (null == dataIdOntologyEntry) {
			/// TODO mullekay: Logger
			throw new RuntimeException("Was not able to find dataid ontology settings in config");
		}
		
		// get data id data
		String dataIdBaseUrl = dataIdOntologyEntry.getKey();
		String ontologyUrl = dataIdOntologyEntry.getValue();
		
		try {
			// just create an instance, since it loads the data
			DataIdSolrConverterUtils dataIt2SolrConverter = new DataIdSolrConverterUtils(
									dataIdBaseUrl, ontologyUrl, (Collection<String>) null);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
    }

    /**
     *
     * @param id
     * @param apiKey
     * @return
     * @throws IOException
     * @throws DatahubError
     */
    @GET
    @Path("/getdataset")
    @Produces("text/plain")
    @Consumes("text/plain")
    public Dataset getDataset(@QueryParam(value = "id") final String id, @QueryParam(value = "apiKey") final String apiKey) throws IOException, DatahubError {
        Dataset set = null;
		try(CkanRestClient client = Main.CreateCkanRestClient(apiKey))
        {
            set = client.GetDataset(id);
            return set;
        } catch (DatahubError datahubError) {
            if(datahubError.getType().equals("Not Found Error"))
                return null;
            else
                throw datahubError;
        }
    }

    @GET
    @Path("/getstoreddataset")
    @Produces("text/plain")
    @Consumes("text/plain")
        public String getStoredDataset(@QueryParam(value = "title") final String uri) throws DataIdInputException {
        try {
            String id =  graph.getDataIdFile(uri, proc.getMappings().getRdfContext());
            Dataset set = proc.parseToDataHubDataset(id).get(0);
            //TODO DataHub??
            return id;
        } catch (Exception e) {
            throw new DataIdInputException(e);
        }
    }

    @GET
    @Path("/getmappings")
    @Produces("text/html")
    public String getMappings()
    {
		try {
            String path = Main.getMainPath() + "/html/MappingsPage.html";
            String content = new String(Files.readAllBytes(Paths.get(path)));
            content = content.replace("$content", StaticJsonHelper.getPrettyContent(StaticJsonHelper.getJsonContent(DataIdConfig.getInstance().getMappingConfigPath())));
			return content;
		} catch (IOException e) {
            return addHtmlBody(produceHttpResponse(e));
		}
    }

    @GET
    @Path("/dataidvalidator")
    @Produces("text/html")
    public String validator()
    {
        try {
            String path = Main.getMainPath() + "/html/DataIdResultPage.html";
            String content = new String(Files.readAllBytes(Paths.get(path)));
            return content;
        } catch (IOException e) {
            return addHtmlBody(produceHttpResponse(e));
        }
    }

    @GET
    @Path("/dataidcreator")
    @Produces("text/html")
    public String creator()
    {
        try {
            String path = Main.getMainPath() + "/html/dataIdCreate/index.html";
            String content = new String(Files.readAllBytes(Paths.get(path)));
            return content;
        } catch (IOException e) {
            return addHtmlBody(produceHttpResponse(e));
        }
    }

    @POST
    @Path("/validationresulttable")
    @Produces("text/html")
    public String validationresulttable(@QueryParam(value = "url") final String url, String result ) throws IOException {
        if(result == null)
            result = new String(Files.readAllBytes(Paths.get(url)));
        try {
            String path = Main.getMainPath() + "/html/ValidationResultTable.html";
            String content = new String(Files.readAllBytes(Paths.get(path))).replace("$result", result);
            return content;
        } catch (IOException e) {
            return addHtmlBody(produceHttpResponse(e));
        }
    }

    @POST
    @Path("/validateid")
    public String validateDataId(final String ttl) throws DataIdInputException, JsonProcessingException {
            IdPart dataid = new IdPart(ttl);
            dataid.validate();
        return dataid.getJsonLdErrorReport();
    }
    
    @POST
    @Path("/isIdValid")
    public String isIdValid(final String ttl)
    {
        try {
            IdPart dataid = new IdPart(ttl);
            return new Boolean(dataid.validate()).toString();
        } catch (Exception e) {
            return produceHttpResponse(e);
        }
    }

    @POST
    @Path("/setmappings")
    public String SetMappings(
            @QueryParam(value = "username") final String username,
            @QueryParam(value = "password") final String password,
            final String content) throws RDFParseException, IOException, RDFHandlerException {
        if(!StaticJsonHelper.isJsonLdValid(content))
            return addHtmlBody("no valid JsonLd!");
        String admins = DataIdConfig.getInstance().get("adminName");
        String pass = DataIdConfig.getInstance().get("password");
        if(!admins.equals(username) || !pass.equals(password))
            return addHtmlBody("Username or password not recognized!");

        try {
            StaticJsonHelper.writeJsonContent(DataIdConfig.getInstance().getMappingConfigPath(), content);
        } catch (IOException e) {
            return addHtmlBody(produceHttpResponse(e));
        }
        return addHtmlBody("<h1>mappings were updated</h1>");
    }

    @POST
    @Path("/prettyprintid")
    public String prettyPrintId(@QueryParam(value = "format") final String format, final String ttl) {
        try {
            IdPart dataid = new IdPart(ttl);
            RDFFormat f = null;
            if(format != null)
                for(Object fo : RDFFormat.values().toArray())
                    if(((RDFFormat)fo).getName().equalsIgnoreCase(format))
                        f = (RDFFormat)fo;
            if(format == null)
                f = StaticFunctions.getRDFFormat(ttl);
            String ret = dataid.toSerialization(f);
            return ret;
        } catch (Exception e) {
            return ttl;
        }
    }

    @POST
    @Path("/publishlinkset")
    public String PublishLinkSet(final String linkSet) {
        try {
            //TODO mayba add linkset uri?
            IdPart dataid = new IdPart(linkSet);
            graph.enterLinkSet(dataid);
        } catch (Exception e) {
            return addHtmlBody(produceHttpResponse(e));
        }
        return addHtmlBody(produceHttpResponse("linkset published"));
    }

    @POST
    @Path("/dataidtojson")
    @Produces("application/json")
    public String DataIdToJson(final String ttl) {
        try {
            IdPart dataid = new IdPart(ttl);
            ObjectMapper mapper = new ObjectMapper();
            mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
            if(dataid.getPartType() == IdPart.DataIdParts.DataId)
                return mapper.writeValueAsString(dataid);
            else
                return addHtmlBody(produceHttpResponse("no dataid found"));
        } catch (Exception e) {
            return addHtmlBody(produceHttpResponse(e));
        }
    }
    
    @POST
    @Path("/executeTextSearch")
    public String executeTextSearch(final String searchQueryJson) throws DataIdInputException, JsonProcessingException, DataIdSolrException {
        if (null == searchQueryJson || searchQueryJson.isEmpty()) {
        	return "";
        }
        
        DataIdSolrHandler solrHandler = null;
    	try {
    		solrHandler = new DataIdSolrHandler();
    		
    		String result = solrHandler.search(searchQueryJson);    		
    		return result;
    	} finally {
    		if (null != solrHandler) {
    			solrHandler.close();
    			solrHandler = null;
    		}
    	}
    }
    
    /**
     * This method can be used to store data id information in SOLR
     * @param ttl
     */
    protected void addDataIdToSolr(final String ttlDataId, final String dataIdBaseUri) {
    	if (null == ttlDataId) {
    		/// @TODO mullekay: add logging message
    		System.err.println("Was not able to add data id file");
    		return;
    	}
    	
    	// create solr handler instance
    	DataIdSolrHandler solrHandler = null;
    	try {
    		solrHandler = new DataIdSolrHandler();
    		
    		Map<String, Entry<String, String>> ontologyMap = DataIdConfig.getInstance().getOntologies();
    		if (null == ontologyMap || ontologyMap.isEmpty()) {
    			System.err.println("No ontology mappings stored in config");
    			return;
    		}
    		
    		Entry<String, String> dataIdOntologyEntry = ontologyMap.get("dataid");
    		if (null == dataIdOntologyEntry) {
    			/// TODO mullekay: Logger
    			System.err.println("Was not able to find dataid ontology settings in config");
    			return;
    		}
    		
    		// get data id data
    		String dataIdBaseUrl = dataIdOntologyEntry.getKey();
    		String ontologyUrl = dataIdOntologyEntry.getValue();
    		
    		// TODO kmuler: put into cache
    		Collection<String> solrFieldNames = solrHandler.getKnownSolrFieldNames();
    		
			// convert Data ID data into data which can be understood by SOLR
			DataIdSolrConverterUtils dataIt2SolrConverter = new DataIdSolrConverterUtils(
									dataIdBaseUrl, ontologyUrl, solrFieldNames);
			dataIt2SolrConverter.addDataIdModel(dataIdBaseUri, ttlDataId);
			
			// get documents for each data id
			Map<String, List<DataIdSolrDocument>> results = dataIt2SolrConverter.getJsonSolrDocuments();
			
			for (String key : results.keySet()) {
				/// @TODO mullekay: Add debug logger
				//System.out.println("Result: " + key + "\n");
				solrHandler.addDataIdSolrDocuments(results.get(key));
			}
			
    	} catch (Exception e) {
    		throw new RuntimeException(e);
    	} finally {
    		if (null != solrHandler) {
    			solrHandler.close();
    			solrHandler = null;
    		}
    	}		
    }

    @POST
    @Path("/publishdataid")
    public String PublishDataId(final String ttl) throws DataIdInputException {
        IdPart dataid = new IdPart(ttl);
        try {
            graph.enterDataId(dataid.toSerialization(RDFFormat.TURTLE), dataid.getId().stringValue(), dataid.getPrevVersion().stringValue());
            
            this.addDataIdToSolr(ttl, null);
/*            List<Dataset> sets = proc.parseToDataHubDataset(ttl);

            JsonLdOptions opt = new JsonLdOptions();
            opt.setBase("http://someuri.org");
            opt.setUseNativeTypes(true);
            opt.setCompactArrays(true);
            opt.setExpandContext(proc.getMappings().getRdfContext().getMap());

            for(Dataset set : sets)
            {
                try {
                    RDFDataset rdfd = (RDFDataset) JsonLdProcessor.toRDF(set.getGraph(), opt);
                    String innerId = RDFDatasetUtils.toNQuads(rdfd);
                    graph.enterDataId(innerId, set.getDataIdUri(), set.getVersion());
                    response.append(produceHttpResponse("Dataset " + set.getDataIdUri() + " was published correctly"));
                } catch (Exception e) {
                    response.append(produceHttpResponse("Dataset " + set.getDataIdUri() + " encountered an error: " + e.getMessage()));
                }
            }*/
        } catch (Exception e) {
            addHtmlBody(produceHttpResponse(e));
        }

        return addHtmlBody("DataID has been saved");
    }

	@POST
	@Path("/publishtodatahub")
	public String PublishToDatahub(
			@QueryParam(value = "organization") final String organization,
			@QueryParam(value = "apikey") final String apiKey,
            @QueryParam(value = "datasetid") final String datasetId,
			@DefaultValue("false") @QueryParam(value = "isprivate") final boolean isprivate,
			final String dataid)
	{

        String res = checkParameters(organization, apiKey, dataid, datasetId);
        if(res != null)
            return res;

        try(CkanRestClient client = Main.CreateCkanRestClient(apiKey))
        {
            List<Dataset> sets = createDatahubDatasets(client, organization, dataid, datasetId, isprivate);

            if(sets == null || sets.size() == 0)
            return addHtmlBody(produceHttpResponse(new DatahubError("no datasets found")));

            for(Dataset set : sets) {
                ValidCkanResponse ckanRes=null;
                if (getDataset(datasetId == null ? set.getId() : datasetId, apiKey) == null)
                    ckanRes = client.CreateDataset(set);
                else
                    ckanRes = client.UpdateDataset(set);

                if (!(ckanRes instanceof Dataset)) //not!
                    return addHtmlBody(produceHttpResponse((DatahubError) ckanRes));
            }
            return addHtmlBody(produceHttpResponse(sets));
		} catch (Exception e) {
            return addHtmlBody(produceHttpResponse(e));
        }
    }

	private String checkParameters(final String organization, final String apiKey, final String dataid, final String datahubId)
	{
		if(apiKey == null)
			return addHtmlBody(produceHttpResponse(new DatahubError("parameter 'apikey' is missing - the datahub apikey can be found in the user-profile view")));
		if(datahubId == null)
			return addHtmlBody(produceHttpResponse(new DatahubError("parameter 'datasetId' is missing - this is the internal id of the dataset of datahub")));
        if(datahubId.contains(" "))
            return addHtmlBody(produceHttpResponse(new DatahubError("parameter 'datasetId' has an invalid whitespace - a datahub-id of a dataset has non!")));
		if(organization == null)
			return addHtmlBody(produceHttpResponse(new DatahubError("parameter 'organization' is missing - the internal name of an organization the dataset is published under")));
		if(dataid == null)
			return addHtmlBody(produceHttpResponse(new DatahubError("the DataId informations are missing - provide the DataId as data to this post")));
		return null;
	}

	private List<Dataset> createDatahubDatasets(final CkanRestClient client, final String organization, final String dataid, final String datahubId, final boolean isprivate)
            throws DataHubMappingException, IOException, DatahubError, DataIdInputException {

        List<Dataset> sets = proc.parseToDataHubDataset(dataid);
		for (Dataset set : sets) {
			String owner_org = client.GetOrganizationId(organization);
			set.setOwner_org(owner_org);
			set.setPrivate(isprivate);
            if(datahubId != null) {
                set.setName(datahubId.toLowerCase());
                if(set.getTitle() == null)
                    set.setTitle(datahubId);
            }
		}
		return sets;
	}

    private String produceHttpResponse(Iterable<Dataset> sets)
    {
        String html = "<p>all sets are deployed:";

        for(Dataset set : sets)
                html += "<p><a href=\"http://datahub.io/dataset/" + set.getName() + "\">http://datahub.io/dataset/" + set.getName() + "</a></p>";
        return html + "</p>";
    }

    private String produceHttpResponse(String s)
    {
        return "<p>" + s + "</p>";
    }

    private String produceHttpResponse(DatahubError ex)
    {
        ex.printStackTrace();
        String html = "<p>An error occurred: \\n" + ex.getMessage() + "</p>";
        return html;
    }

    private String produceHttpResponse(Exception ex)
    {
        ex.printStackTrace();
        String html = "<p>An error occurred: \\n" + ex.getMessage() + "</p>";
        return html;
    }

    private String addHtmlBody(String innerHtml)
    {
        return "<html><body>" + innerHtml + "</body></html>";
    }
}
