
package org.aksw.dataid.datahub.mappingservice;

import com.fasterxml.jackson.databind.JsonNode;
import com.sun.jersey.api.container.grizzly2.GrizzlyServerFactory;
import com.sun.jersey.api.core.PackagesResourceConfig;
import com.sun.jersey.api.core.ResourceConfig;
import org.aksw.dataid.config.DataIdConfig;
import org.aksw.dataid.datahub.restclient.CkanRestClient;
import org.aksw.dataid.virtuoso.VirtuosoDataIdGraph;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.http.server.StaticHttpHandler;

import javax.ws.rs.core.UriBuilder;
import java.io.IOException;
import java.net.URI;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


public class Main {
	private static String mainPath;
	private static String mainConfigPath;
    private static VirtuosoDataIdGraph graph;

    public static CkanRestClient CreateCkanRestClient(String apiKey) {
		String dataHubUrl = DataIdConfig.get("datahubActionUri");
		int timeout = Integer.parseInt(DataIdConfig.get("ckanTimeOut"));
		Map<String, String> actions = new HashMap<String, String>();
		Iterator<Map.Entry<String, JsonNode>> i = DataIdConfig.getActionMap();
		for(Map.Entry<String, JsonNode> key; i.hasNext();)
		{
			key = i.next();
			actions.put(key.getKey(), key.getValue().asText());
		}
		
		CkanRestClient client = new CkanRestClient(dataHubUrl, apiKey, actions, timeout);
		return client;
	}
	
    private static int getPort(int defaultPort) {
        //grab port from environment, otherwise fall back to default port 9998
        String httpPort = System.getProperty("jersey.test.port");
        if (null != httpPort) {
            try {
                return Integer.parseInt(httpPort);
            } catch (NumberFormatException e) {
            }
        }
        return defaultPort;
    }

    private static URI getBaseURI() {
        return UriBuilder.fromUri("http://localhost/").port(getPort(9997)).build();
    }

    public static final URI BASE_URI = getBaseURI();
    
    protected static HttpServer startServer() throws IOException {
        final Map<String, Object> initParams = new HashMap<String, Object>();

        initParams.put("com.sun.jersey.config.property.packages", Main.class.getPackage().getName());
        ResourceConfig rc = new PackagesResourceConfig(Main.class.getPackage().getName());
        System.out.println("Starting grizzly2...");
        return GrizzlyServerFactory.createHttpServer(BASE_URI, rc);
    }
    
    public static void main(String[] args) throws IOException, SQLException {
        mainPath = Main.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        mainPath = mainPath.substring(1, mainPath.indexOf("DatahubDataidMappingService") + 27) + "/DataIdServer/webcontent";
        DataIdConfig.initDataIdConfig(mainPath);
        graph = new VirtuosoDataIdGraph(DataIdConfig.getVirtuosoHost(), DataIdConfig.getVirtuosoPort(), DataIdConfig.getVirtuosoUser(), DataIdConfig.getVirtuosoPassword());
        HttpServer httpServer = startServer();
        StaticHttpHandler statichandler = new StaticHttpHandler("C:\\Users\\Chile\\workspace\\DatahubDataidMappingService\\DataIdServer\\webcontent");
        httpServer.getServerConfiguration().addHttpHandler(statichandler, "/static");
        System.out.println(String.format("Jersey app started with WADL available at "
                        + "%sapplication.wadl\nHit enter to stop it...",
                BASE_URI));
        System.in.read();
        httpServer.stop();
    }

    public static VirtuosoDataIdGraph getGraph() {
        return graph;
    }
}
