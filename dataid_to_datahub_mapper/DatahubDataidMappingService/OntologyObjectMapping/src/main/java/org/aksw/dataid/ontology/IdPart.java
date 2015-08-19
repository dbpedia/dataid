package org.aksw.dataid.ontology;

import org.aksw.dataid.config.DataIdConfig;
import org.aksw.dataid.errors.DataIdInputException;
import org.aksw.dataid.errors.ErrorWarningWrapper;
import org.aksw.dataid.jsonutils.StaticJsonHelper;
import org.aksw.dataid.rdfunit.DataIdValidator;
import org.aksw.dataid.rdfunit.JenaModelEvaluator;
import org.aksw.dataid.statics.StaticContent;
import org.aksw.dataid.wrapper.Statics;
import org.aksw.rdfunit.RDFUnitConfiguration;
import org.aksw.rdfunit.enums.TestCaseExecutionType;
import org.aksw.rdfunit.exceptions.TestCaseExecutionException;
import org.aksw.rdfunit.io.format.SerializationFormat;
import org.aksw.rdfunit.sources.TestSource;
import org.aksw.rdfunit.validate.ParameterException;
import org.openrdf.model.*;
import org.openrdf.rio.RDFHandlerException;

import java.util.*;

/**
 * Created by Chile on 8/18/2015.
 */
public class IdPart
{
    public enum DataIdParts
    {
        DataId,
        Dataset,
        Distribution,
        Linkset,
        Activity,
        Entity,
        Agent,
        Contact,
        Creator,
        Contributor,
        Maintainer,
        Publisher
    }

    private static Map<Resource, IdPart> DataIdPartCache = new HashMap<Resource, IdPart>();
    private static DataIdValidator validator = new DataIdValidator(DataIdConfig.getDataIdUrl(), null);

    private ErrorWarningWrapper errorswarnings = new ErrorWarningWrapper();
    private Model graph;
    private Resource id;
    private Set<URI> types;
    private DataIdParts partType;

    public IdPart(Model m)
    {
        loadModel(m);
        this.id = getRoot();
        setTypes();
    }

    public IdPart(Model m, Resource id)
    {
        loadModel(m);
        this.id = id;
        setTypes();
    }

    public IdPart(String s) throws DataIdInputException {
        loadModel(s);
        this.id = getRoot();
        setTypes();
    }

    public IdPart(String s, Resource id) throws DataIdInputException {
        loadModel(s);
        this.id = id;
        setTypes();
    }

    private void setTypes() {
        types = new HashSet<>();
        for (Value o : graph.filter(this.id, Statics.a, null).objects())
            if (o.getClass().isAssignableFrom(URI.class))
                types.add((URI) o);

        partType = Statics.getDataIdPartType(getTypes());
    }

    //TODO all defined subjects minus subjects which are referenced as object -> get first???
    private Resource getRoot()
    {
        Set<Resource> subjects = graph.filter(null, Statics.a, null).subjects();
        Set<Resource> res = new HashSet<>();
        res.addAll(subjects);
        for(Resource r : subjects)
        {
            if(graph.filter(null, null, r).size() > 0)
                res.remove(r);
        }
        return res.iterator().next();
    }

    private void loadModel(Model m)
    {
        this.graph = m;
    }

    private void loadModel(String ttl) throws DataIdInputException {
        this.graph = Statics.parseModel(ttl);
    }

    public Set<Resource> getChildren()
    {
        Set<Resource> zw = new HashSet<>();
        for(Value st : graph.filter(this.id, null, null).objects())
        {
            if(st.getClass().isAssignableFrom(Resource.class))
                zw.add((Resource)st);
        }
        return zw;
    }

    public Set<Resource> getParents()
    {
        return graph.filter(null, null, this.id).subjects();
    }

    public IdPart getParent(Resource parentId)
    {
        if(getParents().contains(parentId))
            return getIdFromCache(parentId);
        return null;
    }

    public IdPart getChild(Resource childId)
    {
        if(getChildren().contains(childId))
            return getIdFromCache(childId);
        return null;
    }

    private IdPart getIdFromCache(Resource iid)
    {
        if(DataIdPartCache.keySet().contains(iid))
            return DataIdPartCache.get(iid);
        else
            return DataIdPartCache.put(iid, new IdPart(this.graph, iid));
    }

    private Model getSubModel(Resource root)
    {
        Model m = Statics.createDefaultModel(StaticContent.getRdfContext());
        Model r = graph.filter(root, null, null);
        Set<Value> objects = r.objects();
        Set<Value> done = new HashSet<>();
        m.addAll(r);
        while(objects.size() > 0)
        {
            Value zz = objects.iterator().next();
            if(!done.contains(zz) && zz.getClass().isAssignableFrom(Resource.class)) {
                Model t = graph.filter((Resource) zz, null, null);
                objects.addAll(t.objects());
                m.addAll(t);
            }
            if(objects.remove(zz))
                done.add(zz);
        }
        return m;
    }

    public String toTurtle() throws RDFHandlerException, DataIdInputException {
        return Statics.writeTurtle(graph);
    }

    public boolean validate() throws DataIdInputException {

        try{
            String ttl = this.toTurtle();
            SerializationFormat sf = StaticJsonHelper.getSerialization(ttl);
            RDFUnitConfiguration config = validator.getConfiguration("text", ttl, sf.getName(), sf.getName(), TestCaseExecutionType.extendedTestCaseResult);
            TestSource source = config.getTestSource();
            com.hp.hpl.jena.rdf.model.Model m = validator.validate(config, source, validator.getTestSuite(config, source));
            ErrorWarningWrapper ew = new JenaModelEvaluator(m).getErrorWarnings();
            this.errorswarnings.addAll(ew.getErrors());
            this.errorswarnings.addAll(ew.getWarnings());
            if(ew.getErrors().size() == 0)
                return true;
            else
                return false;
        } catch (ParameterException e) {
            throw new DataIdInputException(e);
        } catch (TestCaseExecutionException e) {
            throw new DataIdInputException(e);
        } catch (RDFHandlerException e) {
            throw new DataIdInputException(e);
        }
    }

    public ErrorWarningWrapper getErrorswarnings() {
        return errorswarnings;
    }

    public Resource getId() {
        return id;
    }

    public Set<URI> getTypes() {
        return types;
    }

    public DataIdParts getPartType() {
        return partType;
    }

    public boolean isOfType(String type)
    {
        for(URI t : types)
        {
            if(type.toLowerCase().trim().equals(t.stringValue().toLowerCase().trim()))
                return true;
        }
        return false;
    }
}
