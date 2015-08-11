
package org.aksw.dataid.datahub.mappingservice;

import com.fasterxml.jackson.databind.JsonNode;
import com.sun.jersey.api.container.grizzly2.GrizzlyWebContainerFactory;
import org.aksw.dataid.datahub.jsonutils.StaticJsonHelper;
import org.aksw.dataid.datahub.restclient.CkanRestClient;
import org.aksw.dataid.virtuoso.VirtuosoDataIdGraph;
import org.glassfish.grizzly.http.server.HttpServer;

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
	private static String mappingConfigPath;
    private static String ontologyPath;
    private static JsonNode mainConfigFile;
    private static VirtuosoDataIdGraph graph;

    public static CkanRestClient CreateCkanRestClient(String apiKey) {
		String dataHubUrl = mainConfigFile.get("datahubActionUri").asText();
		int timeout = Integer.parseInt(mainConfigFile.get("ckanTimeOut").asText());
		Map<String, String> actions = new HashMap<String, String>();
		Iterator<Map.Entry<String, JsonNode>> i = mainConfigFile.get("ckanActionMap").fields();
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
        final Map<String, String> initParams = new HashMap<String, String>();

        initParams.put("com.sun.jersey.config.property.packages", Main.class.getPackage().getName());

        System.out.println("Starting grizzly2...");
        return GrizzlyWebContainerFactory.create(BASE_URI, initParams);
    }
    
    public static void main(String[] args) throws IOException, SQLException {
        mainPath = Main.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        mainPath = mainPath.substring(1, mainPath.indexOf("target")) + "webcontent";
        mainConfigPath = mainPath + "/MainConfig.json";
        ontologyPath = mainPath + "/ontology.ttl";
		mainConfigFile = StaticJsonHelper.getJsonContent(mainConfigPath);
		String zw = mainConfigFile.get("mappingConfigPath").asText();
		mappingConfigPath = zw.startsWith("/") ? (mainPath + zw) : (mainPath + "/" + zw);
        String virtuosoHost = mainConfigFile.get("virtuosoHost").toString().replace("\"", "");
        Integer virtuosoPort = Integer.parseInt(mainConfigFile.get("virtuosoPort").toString());
        String virtuosoUser = mainConfigFile.get("virtuosoUser").toString().replace("\"", "");
        String virtuosoPass = mainConfigFile.get("virtuosoPass").toString().replace("\"", "");
        graph = new VirtuosoDataIdGraph(virtuosoHost, virtuosoPort, virtuosoUser, virtuosoPass);
        HttpServer httpServer = startServer();
        System.out.println(String.format("Jersey app started with WADL available at "
                        + "%sapplication.wadl\nHit enter to stop it...",
                BASE_URI));
        System.in.read();
        httpServer.stop();
    }

	public static String getMappingConfigPath() {
		return mappingConfigPath;
	}

    public static JsonNode getMainConfigFile() { return mainConfigFile; }

    public static String getOntologyPath() {
        return ontologyPath;
    }

    public static VirtuosoDataIdGraph getGraph() {
        return graph;
    }
}
