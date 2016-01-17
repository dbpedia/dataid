package org.aksw.dataid.errors;

import java.math.BigInteger;

/**
 * Created by Chile on 1/17/2016.
 */
public class DataIdServiceException extends Exception {
    private BigInteger serviceReference;

    public DataIdServiceException(String message) { super(message); }
    public DataIdServiceException(String message, Throwable cause) { super(message, cause); }
    public DataIdServiceException(Throwable cause) { super(cause); }


}
