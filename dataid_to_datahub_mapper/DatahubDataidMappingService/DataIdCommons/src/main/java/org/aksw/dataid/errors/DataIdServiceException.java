package org.aksw.dataid.errors;

import java.math.BigInteger;

/**
 * Created by Chile on 1/17/2016.
 */
public class DataIdServiceException extends Exception {

    private Long serviceEventId = null;

    public DataIdServiceException() { }
    public DataIdServiceException(String message) { super(message); }
    public DataIdServiceException(String message, Throwable cause) { super(message, cause); }
    public DataIdServiceException(Throwable cause) { super(cause); }

    public void setServiceEventId(long id) {
        serviceEventId = id;
    }

    @Override
    public String getMessage()
    {
        if(serviceEventId != null)
            return "An issue with your request occurred: \n" + super.getMessage() +
            "\n\nPlease cite this event id when reporting to DataIdHub maintainers: " + String.valueOf(serviceEventId);
        else
            return super.getMessage();
    }

}
