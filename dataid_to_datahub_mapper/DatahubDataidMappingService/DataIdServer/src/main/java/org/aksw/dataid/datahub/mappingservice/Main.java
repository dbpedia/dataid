
package org.aksw.dataid.datahub.mappingservice;

import com.fasterxml.jackson.databind.JsonNode;
import com.sun.jersey.api.container.grizzly2.GrizzlyServerFactory;
import com.sun.jersey.api.core.PackagesResourceConfig;
import com.sun.jersey.api.core.ResourceConfig;
import org.aksw.dataid.config.DataIdConfig;
import org.aksw.dataid.datahub.mappingprovider.CorsSupportProvider;
import org.aksw.dataid.datahub.mappingprovider.DataIdInputExceptionProvider;
import org.aksw.dataid.datahub.restclient.CkanRestClient;
import org.aksw.dataid.virtuoso.VirtuosoDataIdGraph;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.http.server.NetworkListener;
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
    private static VirtuosoDataIdGraph graph;
    private static HttpServer httpServer;

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

    public static URI Base_Uri;
    
    protected static HttpServer configureServer() throws IOException , SQLException{
        graph = new VirtuosoDataIdGraph(DataIdConfig.getVirtuosoHost(), DataIdConfig.getVirtuosoPort(), DataIdConfig.getVirtuosoUser(), DataIdConfig.getVirtuosoPassword());
        ResourceConfig rc = new PackagesResourceConfig(Main.class.getPackage().getName(), DataIdInputExceptionProvider.class.getPackage().getName());
        rc.getContainerResponseFilters().add(new CorsSupportProvider());
        Base_Uri = UriBuilder.fromUri("http://" + DataIdConfig.get("ipaddress") + "/").port(Integer.parseInt(DataIdConfig.get("port").toString())).build();
        System.out.println("Starting grizzly2...");
        httpServer = GrizzlyServerFactory.createHttpServer(Base_Uri, rc);
        StaticHttpHandler statichandler = new StaticHttpHandler(mainPath);
        httpServer.getServerConfiguration().addHttpHandler(statichandler, "/" + DataIdConfig.get("contentDir").toString());

        return httpServer;
    }
    
    public static void main(String[] args) throws IOException, SQLException {
        assert args.length == 2;

        DataIdConfig.initDataIdConfig(args[0], args[1]);
        mainPath = args[1];
        configureServer();
        httpServer.start();
        System.out.println(String.format("Jersey app started with WADL available at %sapplication.wadl\nHit enter to stop it...", Base_Uri));
        System.in.read();
        httpServer.stop();
    }

    public static VirtuosoDataIdGraph getGraph() {
        return graph;
    }

    public static String getMainPath() {
        return mainPath;
    }
}
