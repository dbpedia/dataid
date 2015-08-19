package org.aksw.dataid.datahub.mappingservice;

import com.sun.jersey.spi.resource.Singleton;
import org.aksw.dataid.config.DataIdConfig;
import org.aksw.dataid.jsonutils.StaticJsonHelper;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Singleton
@Path("/dataid/scripts")
public class DataIdInfo 
{
    @GET
    @Path("/ace.js")
    @Produces("application/javascript")
    public String getAce() {
        try {
            String path = StaticFunctions.getBasePath() + "webcontent/ace.js";
            return new String(Files.readAllBytes(Paths.get(path)));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }
}
