package org.aksw.dataid.datahub.mappingprovider;

import org.aksw.dataid.errors.DataIdInputException;

import javax.inject.Singleton;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * Created by Chile on 8/19/2015.
 */
@Provider
@Singleton
public class DataIdInputExceptionProvider implements ExceptionMapper<DataIdInputException> {

    @Override
    public Response toResponse(DataIdInputException e) {
        e.printStackTrace();
        return Response.status(Response.Status.NOT_ACCEPTABLE).entity(e.getMessage()).build();
    }
}
