package org.aksw.dataid.rdfunit;

import org.aksw.dataid.errors.ErrorWarning;
import org.aksw.dataid.errors.ErrorWarningWrapper;
import org.aksw.rdfunit.enums.RLOGLevel;
import org.apache.jena.rdf.model.Model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Chile on 8/16/2015.
 */
public class JenaModelEvaluator {

    private Model model;
    private ErrorWarningWrapper ew;
    public JenaModelEvaluator(Model m)
    {
        this.model = m;
    }

/*    public List<RLOGTestCaseResult> getAllErrors()
    {
        List<Statement> zw = filterModel(null, "http://persistence.uni-leipzig.org/nlp2rdf/ontologies/rlog#level", null);
        List<RLOGTestCaseResult> ret = new ArrayList<RLOGTestCaseResult>();
        Resource error = new ResourceImpl("http://persistence.uni-leipzig.org/nlp2rdf/ontologies/rlog#ERROR");
        Resource fatal = new ResourceImpl("http://persistence.uni-leipzig.org/nlp2rdf/ontologies/rlog#FATAL");
        for(Statement st : zw)
        {
            if(st.getObject().asResource().equals(error) || st.getObject().asResource().equals(fatal))
            ret.add(getLogInstance(st.getSubject().getURI()));
        }
        return ret;
    }

    public List<RLOGTestCaseResult> getAllWarnings()
    {
        List<Statement> zw = filterModel(null, "http://persistence.uni-leipzig.org/nlp2rdf/ontologies/rlog#level", null);
        List<RLOGTestCaseResult> ret = new ArrayList<RLOGTestCaseResult>();
        Resource warning = new ResourceImpl("http://persistence.uni-leipzig.org/nlp2rdf/ontologies/rlog#WARNING");
        for(Statement st : zw)
        {
            if(st.getObject().asResource().equals(warning))
                ret.add(getLogInstance(st.getSubject().getURI()));
        }
        return ret;
    }

    public List<Statement> filterModel(String subject, String predicate, String object)
    {
        ResourceImpl s = subject == null ? null : new ResourceImpl(subject);
        PropertyImpl p = predicate == null ? null : new PropertyImpl(predicate);
        RDFNode o = object == null ? null : new  ResourceImpl(object);
        return model.listStatements(new SimpleSelector(s, p, o)).toList();
    }

    public RLOGTestCaseResult getLogInstance(String uri)
    {
        String msg = filterModel(uri, "http://persistence.uni-leipzig.org/nlp2rdf/ontologies/rlog#message", null).get(0).getString();
        String res = filterModel(uri, "http://persistence.uni-leipzig.org/nlp2rdf/ontologies/rlog#resource", null).get(0).getResource().getURI();
        String levUri = filterModel(uri, "http://persistence.uni-leipzig.org/nlp2rdf/ontologies/rlog#level", null).get(0).getResource().getURI();
        levUri = levUri.substring(levUri.lastIndexOf("#")+1);
        return new RLOGTestCaseResult(null, res, msg, RLOGLevel.valueOf(levUri));
    }

    public ErrorWarningWrapper getErrorWarnings()
    {
        ew = new ErrorWarningWrapper();
        for(RLOGTestCaseResult r : getAllErrors())
        {
            ew.addError(r);
        }
        for(RLOGTestCaseResult r : getAllWarnings())
        {
            ew.addWarning(r);
        }
        return ew;
    }*/
}
