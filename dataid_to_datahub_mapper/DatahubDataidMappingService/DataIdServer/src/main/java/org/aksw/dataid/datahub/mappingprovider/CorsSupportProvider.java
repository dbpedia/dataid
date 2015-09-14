package org.aksw.dataid.datahub.mappingprovider;

import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerResponse;
import com.sun.jersey.spi.container.ContainerResponseFilter;

/**
 * Created by Chile on 9/9/2015.
 */
public class CorsSupportProvider implements ContainerResponseFilter {
    @Override
    public ContainerResponse filter(ContainerRequest request, ContainerResponse response) {
        // instead of "*" you should provide a specific domain name for security reasons
        response.getHttpHeaders().add("Access-Control-Allow-Origin", "*");
        response.getHttpHeaders().add("Access-Control-Allow-Methods", "PUT, GET, POST, DELETE, OPTIONS");
        response.getHttpHeaders().add("Access-Control-Allow-Headers", "Content-Type");
        return response;
    }
}
