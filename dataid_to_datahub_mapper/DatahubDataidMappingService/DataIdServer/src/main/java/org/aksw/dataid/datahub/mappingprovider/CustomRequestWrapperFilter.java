package org.aksw.dataid.datahub.mappingprovider;

import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerRequestFilter;

import javax.ws.rs.container.PreMatching;


/**
 * Created by Chile on 1/19/2016.
 */
@PreMatching
public class CustomRequestWrapperFilter implements ContainerRequestFilter {

    @Override
    public ContainerRequest filter(ContainerRequest containerRequest) {
        return containerRequest;
    }
}
