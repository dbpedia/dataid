package org.aksw.dataid.wrapper;

import com.sun.javaws.exceptions.InvalidArgumentException;

import javax.naming.directory.InvalidAttributesException;

/**
 * Created by Chile on 4/6/2015.
 */
public class ErrorWarning {

    public enum DataIdErrorType{
        Error,
        Warning
    }

    private String dataIdPart;
    private String onProperty;
    private String msg;
    private DataIdErrorType errorType;

    public String getOnProperty() {
        return onProperty;
    }

    public void setOnProperty(String onProperty) {
        this.onProperty = onProperty;
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

    public String getDataIdPart() {
        return dataIdPart;
    }

    public void setDataIdPart(String dataIdPart) {
        this.dataIdPart = dataIdPart;
    }

    @Override
    public String toString()
    {
        if(errorType == null)
            return null;
        if(errorType == DataIdErrorType.Error)
            return errorType.toString() + " at " + dataIdPart + " on property " + onProperty + " - " + msg;
        else
            return errorType.toString() + " for " + dataIdPart + " on property " + onProperty + " - " + msg;
    }
}