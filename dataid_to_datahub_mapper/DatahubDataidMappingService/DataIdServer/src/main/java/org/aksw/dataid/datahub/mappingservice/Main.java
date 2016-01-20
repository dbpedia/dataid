
package org.aksw.dataid.datahub.mappingservice;

import com.sun.jersey.api.container.grizzly2.GrizzlyServerFactory;
import com.sun.jersey.api.core.PackagesResourceConfig;
import com.sun.jersey.api.core.ResourceConfig;
import org.aksw.dataid.config.DataIdConfig;
import org.aksw.dataid.datahub.mappingprovider.CorsSupportProvider;
import org.aksw.dataid.datahub.mappingprovider.CustomRequestWrapperFilter;
import org.aksw.dataid.datahub.mappingprovider.DataIdInputExceptionProvider;
import org.aksw.dataid.virtuoso.VirtuosoDataIdGraph;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.http.server.StaticHttpHandler;

import javax.ws.rs.core.UriBuilder;
import java.io.IOException;
import java.net.URI;
import java.sql.SQLException;
import java.util.Date;
import java.util.concurrent.atomic.AtomicLong;


public class Main {
	private static String mainPath;
    private static VirtuosoDataIdGraph graph;
    private static HttpServer httpServer;
    private static AtomicLong idProvider = new AtomicLong(new Date().getTime());

    private static final String JERSEY_SERVLET_CONTEXT_PATH = "";

	
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
        ResourceConfig rc = new PackagesResourceConfig(Main.class.getPackage().getName(), DataIdInputExceptionProvider.class.getPackage().getName(), CustomRequestWrapperFilter.class.getName());
        rc.getContainerRequestFilters().add(new CustomRequestWrapperFilter());
        rc.getContainerResponseFilters().add(new CorsSupportProvider());
        Base_Uri = UriBuilder.fromUri("http://" + DataIdConfig.get("ipaddress") + "/").port(Integer.parseInt(DataIdConfig.get("port"))).build();
        System.out.println("Starting grizzly2...");
        httpServer = GrizzlyServerFactory.createHttpServer(Base_Uri, rc);
        StaticHttpHandler statichandler = new StaticHttpHandler(mainPath);
        System.out.println(statichandler.isFileCacheEnabled());
        httpServer.getServerConfiguration().addHttpHandler(statichandler, "/" + DataIdConfig.get("contentDir"));

        return httpServer;
    }
    
    public static void main(String[] args) throws IOException, SQLException {
        assert args.length == 2;

        DataIdConfig.initDataIdConfig(args);
        mainPath = args[1];
        configureServer();
        httpServer.start();
        System.out.println(String.format("Jersey app started with WADL available at %sapplication.wadl\nHit enter to stop it...", Base_Uri));
        System.in.read();
        httpServer.stop();
    }

    public static long generateServiceEventId()
    {
        return idProvider.incrementAndGet();
    }

    public static VirtuosoDataIdGraph getGraph() {
        return graph;
    }

    public static String getMainPath() {
        return mainPath;
    }
}
