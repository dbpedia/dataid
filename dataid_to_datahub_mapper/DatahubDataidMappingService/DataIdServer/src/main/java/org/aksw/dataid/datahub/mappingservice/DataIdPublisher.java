package org.aksw.dataid.datahub.mappingservice;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.github.jsonldjava.core.*;
import com.hp.hpl.jena.rdf.model.Model;
import com.sun.javaws.exceptions.InvalidArgumentException;
import org.aksw.dataid.config.DataIdConfig;
import org.aksw.dataid.datahub.jsonobjects.DatahubError;
import org.aksw.dataid.datahub.jsonobjects.Dataset;
import org.aksw.dataid.datahub.jsonobjects.ValidCkanResponse;
import org.aksw.dataid.datahub.xmlObjects.Repository;
import org.aksw.dataid.jsonutils.StaticJsonHelper;
import org.aksw.dataid.datahub.propertymapping.DataHubMappingException;
import org.aksw.dataid.datahub.propertymapping.DataIdInputException;
import org.aksw.dataid.datahub.propertymapping.DataIdProcesser;
import org.aksw.dataid.datahub.restclient.CkanRestClient;
import org.aksw.dataid.ontology.DataId;
import org.aksw.dataid.ontology.LinkSet;
import org.aksw.dataid.virtuoso.VirtuosoDataIdGraph;
import org.aksw.dataid.wrapper.ModelWrapper;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFParseException;

import javax.naming.directory.InvalidAttributesException;
import javax.ws.rs.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.List;

@Path("dataid/publisher")
public class DataIdPublisher
{
    private VirtuosoDataIdGraph graph;
    private DataIdProcesser proc;

    public DataIdPublisher() throws SQLException, DataHubMappingException {
        graph = Main.getGraph();
        proc = new DataIdProcesser(DataIdConfig.getMappingConfigPath(), DataIdConfig.getOntologyPath());
        ModelWrapper.setRdfContext(proc.getMappingConfig().getRdfContext());
    }

    @GET
    @Path("/deserializerepo")
    @Produces("text/plain")
    @Consumes("text/plain")
    public String deserializeRepo(final String repoXml) throws IOException {

        XmlMapper mapper = new XmlMapper();
        Repository repo = mapper.readValue(repoXml, Repository.class);
        return repo.getRe3dataIdentifier();
    }

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
        public Dataset getStoredDataset(@QueryParam(value = "title") final String title) throws DatahubError, RDFHandlerException {
        try {
            String id =  graph.getDataIdFile(title, proc.getMappingConfig().getRdfContext());
            Dataset set = proc.parseToDataHubDataset(id).get(0);
            return set;
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (DataHubMappingException e) {
            e.printStackTrace();
        } catch (DataIdInputException e) {
            e.printStackTrace();
        }
        return null;
    }

    @GET
    @Path("/getmappings")
    @Produces("text/html")
    public String getMappings()
    {
		try {
            String f = this.getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
            f = f.substring(1, f.indexOf("target")) + "webcontent/MappingsPage.html";
            String content = new String(Files.readAllBytes(Paths.get(f)));
            content = content.replace("$content", StaticJsonHelper.getPrettyContent(StaticJsonHelper.getJsonContent(DataIdConfig.getMappingConfigPath())));
			return content;
		} catch (IOException e) {
            return addHtmlBody(produceHttpResponse(e));
		}
    }

    @POST
    @Path("/validateid")
    public String validateDataId(final String dataId)
    {
        try {
            Model m = proc.validateDataId(dataId);
            return produceHttpResponse("DataId validated");
        } catch (DataIdInputException e) {
            return produceHttpResponse(e);
        }
    }

    @POST
    @Path("/setmappings")
    public String SetMappings(
            @QueryParam(value = "username") final String username,
            @QueryParam(value = "password") final String password,
            final String content)
    {
        if(!StaticJsonHelper.isJsonLdValid(content))
            return addHtmlBody("no valid JsonLd!");
        String admins = DataIdConfig.get("adminName");
        String pass = DataIdConfig.get("password");
        if(!admins.equals(username) || !pass.equals(password))
            return addHtmlBody("Username or password not recognized!");

        try {
            StaticJsonHelper.writeJsonContent(DataIdConfig.getMappingConfigPath(), content);
        } catch (IOException e) {
            return addHtmlBody(produceHttpResponse(e));
        }
        return addHtmlBody("<h1>mappings were updated</h1>");
    }

    @POST
    @Path("/publishlinkset")
    public String PublishLinkSet(final String linkSet) {
        try {
            ModelWrapper.loadModel(proc.getMappingConfig().getRdfContext(), linkSet);
            LinkSet links = (LinkSet) ModelWrapper.getDataIdPart(ModelWrapper.getModel().subjectURI());
            graph.enterLinkSet(links);
        } catch (SQLException e) {
            return addHtmlBody(produceHttpResponse(e));
        } catch (RDFHandlerException e) {
            return addHtmlBody(produceHttpResponse(e));
        } catch (RDFParseException e) {
             return addHtmlBody(produceHttpResponse(e));
        } catch (InvalidAttributesException e) {
            return addHtmlBody(produceHttpResponse(e));
        }
        return addHtmlBody(produceHttpResponse("linkset published"));
    }

    @POST
    @Path("/dataidtojson")
    @Produces("application/json")
    public String DataIdToJson(final String dataid) {
        try {
            ModelWrapper.loadModel(proc.getMappingConfig().getRdfContext(), dataid);
            ObjectMapper mapper = new ObjectMapper();
            mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
            List<DataId> ids = ModelWrapper.getAllDataIds();
            if(ids.size() > 0)
                return mapper.writeValueAsString(ids.get(0));
            else
                return addHtmlBody(produceHttpResponse("no datasets found"));
        } catch (RDFParseException e) {
            return addHtmlBody(produceHttpResponse(e));
        } catch (RDFHandlerException e) {
            return addHtmlBody(produceHttpResponse(e));
        } catch (JsonProcessingException e) {
            return addHtmlBody(produceHttpResponse(e));
        }
    }

    @POST
    @Path("/publishdataid")
    public String PublishDataId(final String dataid){
        StringBuilder response = new StringBuilder();
        try {
            ModelWrapper.loadModel(proc.getMappingConfig().getRdfContext(), dataid);
            List<Dataset> sets = proc.parseToDataHubDataset(dataid);

            JsonLdOptions opt = new JsonLdOptions();
            opt.setBase("http://someuri.org");
            opt.setUseNativeTypes(true);
            opt.setCompactArrays(true);
            opt.setExpandContext(proc.getMappingConfig().getRdfContext().getMap());

            for(Dataset set : sets)
            {
                try {
                    RDFDataset rdfd = (RDFDataset) JsonLdProcessor.toRDF(set.getGraph(), opt);
                    String innerId = RDFDatasetUtils.toNQuads(rdfd);
                    graph.enterDataId(innerId, set.getDataIdUri(), set.getVersion());
                    response.append(produceHttpResponse("Dataset " + set.getDataIdUri() + " was published correctly"));
                } catch (SQLException e) {
                    response.append(produceHttpResponse("Dataset " + set.getDataIdUri() + " encountered an error: " + e.getMessage()));
                } catch (InvalidArgumentException e) {
                    response.append(produceHttpResponse("Dataset " + set.getDataIdUri() + " encountered an error: " + e.getMessage()));
                } catch (JsonLdError jsonLdError) {
                    response.append(produceHttpResponse("Dataset " + set.getDataIdUri() + " encountered an error: " + jsonLdError.getMessage()));
                }
            }
        } catch (DataIdInputException e) {
            addHtmlBody(produceHttpResponse(e));
        } catch (DataHubMappingException e) {
            addHtmlBody(produceHttpResponse(e));
        } catch (RDFHandlerException e) {
            addHtmlBody(produceHttpResponse(e));
        } catch (RDFParseException e) {
            addHtmlBody(produceHttpResponse(e));
        }

        return addHtmlBody(response.toString());
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
		} catch (DatahubError e) {
            return addHtmlBody(produceHttpResponse(e));
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
