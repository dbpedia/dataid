
package aksw.dataid.datahub.mappingservice;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import aksw.dataid.datahub.cli.DataIdProcesser;
import aksw.dataid.datahub.jsonobjects.Dataset;
import aksw.dataid.datahub.propertymapping.DataHubMappingException;
import aksw.dataid.datahub.propertymapping.StaticHelper;
import aksw.dataid.datahub.restclient.CkanRestClient;

// The Java class will be hosted at the URI path "/myresource"
@Path("/dataid/datahubpublisher")
public class DatahubPublisher 
{

    @GET 
    @Produces("text/plain")
    public String getIt() {
        return "Got it!";
    }
    
    @GET
    @Path("/getdataset")
    @Produces("text/plain")
    @Consumes("text/plain")
    public String getDataset(@QueryParam(value = "id") final String id) 
    {
    	Dataset set = Main.getClient().GetDataset(id);
        return set.getName();
    }
    
    @GET
    @Path("/update")
    @Produces("text/plain")
    public String updateDataset(
    		@QueryParam(value = "name") final String name,
    		@QueryParam(value = "dataidurl") final String url,
    		@QueryParam(value = "organization") final String organization,
    		@QueryParam(value = "ckanapikey") final String ckanapikey) 
    {
    	String dataId = StaticHelper.readUrl("https://raw.githubusercontent.com/dbpedia/dataId/master/dataid_to_datahub_mapper/DatahubDataIdPropertyMapper/src/dbpedia_complete_id.ttl");
    	DataIdProcesser proc;
    	Dataset set = null;
		try {
			proc = new DataIdProcesser(dataId, Main.getMappingConfigPath());
	    	set = proc.createDataset();
		} catch (DataHubMappingException e) {
			return e.getMessage();
		}
		Main.getClient().setApiKey(ckanapikey);
		String owner_org = Main.getClient().GetOrganizationId(organization);
        set.setName(name);
        set.setOwner_org(owner_org);
        if(Main.getClient().UpdateDataset(name, set))
        	return "datset was updated";
        else
        	return "update failed";
    }
}
