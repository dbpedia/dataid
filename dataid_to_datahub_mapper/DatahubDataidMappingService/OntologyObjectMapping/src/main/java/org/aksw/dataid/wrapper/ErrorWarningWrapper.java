package org.aksw.dataid.wrapper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Chile on 4/6/2015.
 */
public class ErrorWarningWrapper {
    private List<ErrorWarning> errors = new ArrayList<ErrorWarning>();
    private List<ErrorWarning> warnings = new ArrayList<ErrorWarning>();

    public List<ErrorWarning> getErrors() {
        return errors;
    }

    public void setErrors(List<ErrorWarning> errors) {
        this.errors = errors;
    }

    public List<ErrorWarning> getWarnings() {
        return warnings;
    }

    public void setWarnings(List<ErrorWarning> warnings) {
        this.warnings = warnings;
    }

    public void addError(String dataIdPart, String property, String msg)
    {
        ErrorWarning e = new ErrorWarning();
        e.setErrorType(ErrorWarning.DataIdErrorType.Error);
        e.setOnProperty(property);
        e.setDataIdPart(dataIdPart);
        e.setMsg(msg);
        errors.add(e);
    }

    public void addWarning(String dataIdPart, String property, String msg)
    {
        ErrorWarning e = new ErrorWarning();
        e.setErrorType(ErrorWarning.DataIdErrorType.Warning);
        e.setOnProperty(property);
        e.setDataIdPart(dataIdPart);
        e.setMsg(msg);
        warnings.add(e);
    }
}
