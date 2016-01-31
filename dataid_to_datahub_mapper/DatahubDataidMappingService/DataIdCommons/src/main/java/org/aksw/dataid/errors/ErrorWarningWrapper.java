package org.aksw.dataid.errors;



import org.aksw.rdfunit.model.interfaces.results.RLOGTestCaseResult;

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

    public List<ErrorWarning> getWarnings() {
        return warnings;
    }

    public void addError(RLOGTestCaseResult result)
    {
        errors.add(new ErrorWarning(result));
    }

    public void addError(String dataIdPart, String msg)
    {
        ErrorWarning e = new ErrorWarning();
        e.setErrorType(ErrorWarning.DataIdErrorType.Error);
        e.setResource(dataIdPart);
        e.setMsg(msg);
        errors.add(e);
    }

    public void addWarning(RLOGTestCaseResult result)
    {
        warnings.add(new ErrorWarning(result));
    }

    public void addWarning(String dataIdPart, String msg)
    {
        ErrorWarning e = new ErrorWarning();
        e.setErrorType(ErrorWarning.DataIdErrorType.Warning);
        e.setResource(dataIdPart);
        e.setMsg(msg);
        warnings.add(e);
    }

    public void addAll(Iterable<ErrorWarning> el)
    {
        for(ErrorWarning e : el)
        {
            if(e.getErrorType() == ErrorWarning.DataIdErrorType.Warning)
                warnings.add(e);
            else if(e.getErrorType() == ErrorWarning.DataIdErrorType.Error)
                errors.add(e);
        }
    }
}
