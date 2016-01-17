package org.aksw.dataid.errors;

/**
 * Created by Chile on 10/28/2015.
 */
public class InnerFutureException extends Exception {
    public InnerFutureException() { super(); }
    public InnerFutureException(String message) { super(message); }
    public InnerFutureException(String message, Throwable cause) { super(message, cause); }
    public InnerFutureException(Throwable cause) { super(cause); }
}
