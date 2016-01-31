package org.aksw.dataid.datahub.mappingservice;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.aksw.dataid.config.DataIdConfig;
import org.aksw.dataid.datahub.jsonobjects.DatahubError;
import org.aksw.dataid.datahub.jsonobjects.Dataset;
import org.aksw.dataid.datahub.jsonobjects.ValidCkanResponse;
import org.aksw.dataid.datahub.xmlObjects.Re3Data;
import org.aksw.dataid.datahub.xmlObjects.Repository;
import org.aksw.dataid.errors.DataIdInputException;
import org.aksw.dataid.errors.DataIdServiceException;
import org.aksw.dataid.jsonutils.StaticJsonHelper;
import org.aksw.dataid.errors.DataHubMappingException;
import org.aksw.dataid.datahub.propertymapping.DataIdProcesser;
import org.aksw.dataid.datahub.restclient.CkanRestClient;
import org.aksw.dataid.statics.StaticFunctions;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFParseException;

import javax.ws.rs.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;

@Path("dataid/publisher")
public class DataIdPublisher
{
    private DataIdProcesser proc;

    public DataIdPublisher() throws SQLException, DataHubMappingException {
        proc = new DataIdProcesser(DataIdConfig.getMappingConfigPath());
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
		try(CkanRestClient client = proc.createCkanRestClient(apiKey))
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

    @POST
    @Path("/deserializerepo")
    public String deserializeRepo(final String repoXml) throws IOException {

        XmlMapper mapper = new XmlMapper();
        Re3Data repo = mapper.readValue(repoXml.replaceAll("re3data\\.orgIdentifier", "re3dataorgIdentifier"), Re3Data.class);
        return repo.getRepositories().getRe3dataIdentifier();
    }

    @GET
    @Path("/getmappings")
    @Produces("text/html")
    public String getMappings()
    {
		try {
            String path = Main.getMainPath() + "/html/MappingsPage.html";
            String content = new String(Files.readAllBytes(Paths.get(path)));
            content = content.replace("$content", StaticJsonHelper.getPrettyContent(StaticJsonHelper.getJsonContent(DataIdConfig.getMappingConfigPath())));
			return content;
		} catch (IOException e) {
            return addHtmlBody(produceHttpResponse(e));
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
    @Path("/prettyprintid")
    public String prettyPrintId(@QueryParam(value = "format") final String format, final String ttl) {
        try {
            RDFFormat f = null;
            if(format != null)
                for(Object fo : RDFFormat.values().toArray())
                    if(((RDFFormat)fo).getName().equalsIgnoreCase(format))
                        f = (RDFFormat)fo;
            if(format == null)
                f = StaticFunctions.getRDFFormat(ttl);
            return StaticFunctions.writeSerialization(StaticFunctions.parseModel(ttl), f);
        } catch (Exception e) {
            return ttl;
        }
    }

    @POST
    @Path("/deletefromdatahub")
    public String DeleteFromDatahub(
            @QueryParam(value = "apikey") final String apiKey,
            final String dataid) throws DataIdServiceException {

        String res = checkParameters(apiKey, dataid);
        if(res != null)
            return res;

        return addHtmlBody(produceHttpResponse(proc.DeleteDatasets(apiKey, dataid)));
    }

	@POST
	@Path("/publishtodatahub")
	public String PublishToDatahub(
			@QueryParam(value = "organization") final String organization,
			@QueryParam(value = "apikey") final String apiKey,
            @DefaultValue("") @QueryParam(value = "excemptions") final String excemptions,
			@DefaultValue("false") @QueryParam(value = "isprivate") final boolean isprivate,
			final String dataid) throws DataIdServiceException {



        String res = checkParameters(organization, apiKey, dataid);
        if(res != null)
            return res;


        return addHtmlBody(produceHttpResponse(proc.PublishDatasets(apiKey, excemptions, isprivate, dataid)));
    }

    private String checkParameters(final String apiKey, final String dataid)
    {
        return checkParameters("dataid", apiKey, dataid);
    }
    private String checkParameters(final String organization, final String apiKey, final String dataid)
	{
		if(apiKey == null)
			return addHtmlBody(produceHttpResponse(new DatahubError("parameter 'apikey' is missing - the datahub apikey can be found in the user-profile view")));
		if(organization == null)
			return addHtmlBody(produceHttpResponse(new DatahubError("parameter 'organization' is missing - the internal name of an organization the dataset is published under")));
		if(dataid == null)
			return addHtmlBody(produceHttpResponse(new DatahubError("the DataId informations are missing - provide the DataId as data to this post")));
		return null;
	}

    private String produceHttpResponse(ValidCkanResponse... sets)
    {
        String html = "<p>all sets are deployed:";

        for(int i =0; i < sets.length; i++) {
            if(sets[i] instanceof Dataset) {
                Dataset set = ((Dataset) sets[i]);
                html += "<p><a href=\"http://datahub.io/dataset/" + set.getName() + "\">http://datahub.io/dataset/" + set.getName() + "</a></p>";
            }
            else {
                DatahubError err = (DatahubError) sets[i];
                html += "<p>An error (" + err.getType() + ") occurred: " + err.getMessage() + "</p>";
            }
        }
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
