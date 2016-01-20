package org.aksw.dataid.datahub.mappingprovider;

import org.aksw.dataid.datahub.mappingservice.Main;
import org.aksw.dataid.errors.DataIdServiceException;

import javax.inject.Singleton;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * Created by Chile on 8/19/2015.
 */
@Provider
@Singleton
public class DataIdInputExceptionProvider implements ExceptionMapper<DataIdServiceException> {


    @Override
    public Response toResponse(DataIdServiceException e) {
        e.setServiceEventId(Main.generateServiceEventId());  //TODO more exception logging
        return Response.status(Response.Status.NOT_ACCEPTABLE).entity(e.getMessage()).build();

    }
}
