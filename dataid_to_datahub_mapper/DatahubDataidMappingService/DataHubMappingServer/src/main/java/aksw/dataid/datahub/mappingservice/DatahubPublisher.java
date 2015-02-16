package aksw.dataid.datahub.mappingservice;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import aksw.dataid.datahub.propertymapping.DataHubMappingException;
import org.apache.http.client.HttpResponseException;

import aksw.dataid.datahub.cli.DataIdProcesser;
import aksw.dataid.datahub.jsonobjects.DatahubError;
import aksw.dataid.datahub.jsonobjects.Dataset;
import aksw.dataid.datahub.jsonobjects.ValidCkanResponse;
import aksw.dataid.datahub.jsonutils.StaticJsonHelper;
import aksw.dataid.datahub.restclient.CkanRestClient;
import aksw.dataid.datahub.restclient.DatahubException;

@Path("/dataid/datahubpublisher")
public class DatahubPublisher 
{
    
    @GET
    @Path("/getdataset")
    @Produces("text/plain")
    @Consumes("text/plain")
    public String getDataset(@QueryParam(value = "id") final String id, @QueryParam(value = "apiKey") final String apiKey) 
    {
		final CkanRestClient client = Main.CreateCkanRestClient(apiKey);
    	Dataset set = null;
		try {
			set = client.GetDataset(id);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DatahubException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return set.getName();
    }

    @GET
    @Path("/getmappings")
    @Produces("text/html")
    public String getMappings()
    {
		try {
            DataIdProcesser proc = new DataIdProcesser(Main.getMappingConfigPath());
            String f = this.getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
            f = f.substring(1, f.indexOf("target")) + "webcontent/MappingsPage.html";
            String content = new String(Files.readAllBytes(Paths.get(f)));
            content = content.replace("$content", StaticJsonHelper.getPrettyContent(proc.getMappingConfig()));
			return content;
		} catch (DataHubMappingException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "an error occurred";
    }

    @POST
    @Path("/setmappings")
    public String SetMappings(
            @QueryParam(value = "username") final String username,
            @QueryParam(value = "password") final String password,
            final String content)
    {
        if(!StaticJsonHelper.isJsonLdValid(content))
            return "no valid JsonLd!";
        String admins = Main.getMainConfigFile().get("adminName").toString().replace("\"", "");
        String pass = Main.getMainConfigFile().get("password").toString().replace("\"", "");
        if(!admins.equals(username) || !pass.equals(password))
            return "Username or password not recognized!";

        try {
            StaticJsonHelper.writeJsonContent(Main.getMappingConfigPath(), content);
        } catch (IOException e) {
            return e.getMessage();
        }
        return "mappings were updated";
    }
	
	@POST
	@Path("/updatedataset")
	public String UpdateDataset(
		//	@QueryParam(value = "datasetname") final String datasetid,
			@QueryParam(value = "organization") final String organization,
			@QueryParam(value = "apikey") final String apiKey,
			@DefaultValue("false") @QueryParam(value = "isprivate") final boolean isprivate,
			final String dataid) 
	{
		
		String res = checkParameters(organization, apiKey, dataid);
		if(res != null)
			return res;
		
		final CkanRestClient client = Main.CreateCkanRestClient(apiKey);
		
		List<Dataset> sets = null;
		try {
			sets = CreateDatahubDataset(client, organization,	dataid, isprivate);
		} catch (DataHubMappingException e1) {
			return produceHttpResponse(e1);
		} catch (IOException e1) {
			return produceHttpResponse(e1);
		} catch (DatahubException e1) {
			return produceHttpResponse(e1);
		}
		
		if(sets == null || sets.size() == 0)
			return produceHttpResponse(new DatahubError("no datasets found"));

        try {
            for(Dataset set : sets)
        	{
	        	ValidCkanResponse ckanRes = client.UpdateDataset(set);
	        	if(!(ckanRes instanceof Dataset)) //not!
	        		return produceHttpResponse((DatahubError) ckanRes);
            }
			return produceHttpResponse(sets);
				
		} catch (HttpResponseException e) {
			return produceHttpResponse(e);
		} catch (IOException e) {
			return produceHttpResponse(e);
		} 
 
	}
	
	@POST
	@Path("/createdataset")
	public String CreateDataset(
			//@QueryParam(value = "datasetname") final String datasetid,
			@QueryParam(value = "organization") final String organization,
			@QueryParam(value = "apikey") final String apiKey,
			@DefaultValue("false") @QueryParam(value = "isprivate") final boolean isprivate,
			final String dataid) 
	{

		String res = checkParameters(organization, apiKey, dataid);
		if(res != null)
			return res;
		
		final CkanRestClient client = Main.CreateCkanRestClient(apiKey);
		
		List<Dataset> sets = null;
		try {
			sets = CreateDatahubDataset(client, organization,	dataid, isprivate);
		} catch (DataHubMappingException e1) {
			return produceHttpResponse(e1);
		} catch (IOException e1) {
			return produceHttpResponse(e1);
		} catch (DatahubException e1) {
			return produceHttpResponse(e1);
		}
		
		if(sets == null || sets.size() == 0)
			return produceHttpResponse(new DatahubError("no datasets found"));
        
        try {
        	for(Dataset set : sets)
        	{
	        	ValidCkanResponse ckanRes = client.CreateDataset(set);
	        	if(!(ckanRes instanceof Dataset)) //not!
	        		return produceHttpResponse(new DatahubError((DatahubError) ckanRes));
        	}
			return produceHttpResponse(sets);
				
		} catch (HttpResponseException e) {
			return produceHttpResponse(e);
		} catch (IOException e) {
			return produceHttpResponse(e);
		} 
 
	}

	private String checkParameters(final String organization, final String apiKey, final String dataid)
	{
		if(apiKey == null)
			return produceHttpResponse(new DatahubError("parameter 'apikey' is missing - the datahub apikey can be found in the user-profile view"));
//		if(datasetid == null)
//			return produceHttpResponse(new DatahubError("parameter 'datasetname' is missing - this is the internal name of the dataset"));
		if(organization == null)
			return produceHttpResponse(new DatahubError("parameter 'organization' is missing - the internal name of an organization the dataset is published under"));
		if(dataid == null)
			return produceHttpResponse(new DatahubError("the DataId informations are missing - provide the DataId as data to this post"));
		return null;
	}

	private List<Dataset> CreateDatahubDataset(final CkanRestClient client, final String organization, final String dataid, final boolean isprivate) 
			throws DataHubMappingException, IOException, DatahubException {
		DataIdProcesser proc = new DataIdProcesser(Main.getMappingConfigPath());
    	List<Dataset> sets = proc.GetDataHubDataset(dataid);
		for (Dataset set : sets) {
			String owner_org = client.GetOrganizationId(organization);
			set.setOwner_org(owner_org);
			set.setPrivate(isprivate);
		}
		return sets;
	}

    private String produceHttpResponse(Iterable<Dataset> sets)
    {
        String html = "<html><body><p>all sets are deployed:";

        for(Dataset set : sets)
                html += "<p><a href=\"http://datahub.io/dataset/" + set.getName() + "\">http://datahub.io/dataset/" + set.getName() + "</a></p>";
        return html + "</p></body></html>";
    }

    private String produceHttpResponse(Exception ex)
    {
        String html = "<html><body><p>An error occurred: \\n" + ex.getMessage() + "</p></body></html>";
        return html;
    }
}
