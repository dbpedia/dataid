package org.aksw.dataid.errors;

import org.aksw.rdfunit.enums.RLOGLevel;
import org.aksw.rdfunit.model.interfaces.results.RLOGTestCaseResult;

/**
 * Created by Chile on 4/6/2015.
 */
public class ErrorWarning {

    public enum DataIdErrorType{
        Error,
        Warning
    }

    private String resource;
    private String msg;
    private DataIdErrorType errorType;

    public ErrorWarning(){}

    public ErrorWarning(RLOGTestCaseResult r)
    {
        this.resource = r.getFailingResource();
        this.msg = r.getMessage();
        if(r.getSeverity() == RLOGLevel.ERROR || r.getSeverity() == RLOGLevel.FATAL)
            this.errorType = DataIdErrorType.Error;
        else
            this.errorType = DataIdErrorType.Warning;
    }

    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public DataIdErrorType getErrorType() {
        return errorType;
    }

    public void setErrorType(DataIdErrorType errorType) {
        this.errorType = errorType;
    }

    @Override
    public String toString()
    {
        if(getErrorType() == null)
            return "";
        if(getErrorType() == DataIdErrorType.Error)
            return errorType.toString() + " at resource " + getResource() + " - " + getMsg();
        else
            return errorType.toString() + " at resource " + getResource() + " - " + getMsg();
    }
}