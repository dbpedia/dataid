package aksw.dataid.datahub.mappingservice;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import com.sun.jersey.spi.resource.Singleton;

@Singleton
@Path("/dataid")
public class DataIdInfo 
{
    @GET 
    // The Java method will produce content identified by the MIME Media
    // type "text/plain"
    @Produces("text/plain")
    public String dataIdInfo() {
        return "DataId is some great shit!";
    }
}
