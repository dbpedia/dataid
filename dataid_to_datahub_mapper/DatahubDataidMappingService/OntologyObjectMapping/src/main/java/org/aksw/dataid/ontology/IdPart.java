package org.aksw.dataid.ontology;

import org.aksw.dataid.config.DataIdConfig;
import org.aksw.dataid.errors.DataIdInputException;
import org.aksw.dataid.errors.ErrorWarningWrapper;
import org.aksw.dataid.jsonutils.StaticJsonHelper;
import org.aksw.dataid.rdfunit.DataIdValidator;
import org.aksw.dataid.rdfunit.JenaModelEvaluator;
import org.aksw.dataid.statics.StaticContent;
import org.aksw.dataid.statics.StaticFunctions;
import org.aksw.jena_sparql_api.model.QueryExecutionFactoryModel;
import org.aksw.rdfunit.RDFUnitConfiguration;
import org.aksw.rdfunit.enums.TestCaseExecutionType;
import org.aksw.rdfunit.io.format.SerializationFormat;
import org.aksw.rdfunit.io.reader.RDFReaderException;
import org.aksw.rdfunit.io.writer.RDFStreamWriter;
import org.aksw.rdfunit.sources.TestSource;
import org.openrdf.model.*;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFHandlerException;

import java.io.ByteArrayOutputStream;
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
    private static DataIdValidator validator;

    private ErrorWarningWrapper errorswarnings = new ErrorWarningWrapper();
    private Model graph;
    private Resource id;
    private Set<URI> types;
    private DataIdParts partType;
    private String jsonLdErrorReport;

    public IdPart(Model m) throws DataIdInputException {
        loadModel(m);
        //TODO multiple DataIds???
        this.id = getRoots().iterator().next();
        setTypes();
        init();
    }

    public IdPart(Model m, Resource id)
    {
        loadModel(m);
        this.id = id;
        setTypes();
        init();
    }

    public IdPart(String s) throws DataIdInputException {
        loadModel(s);
        this.id = getRoots().iterator().next();
        setTypes();
        init();
    }
    public IdPart(String s, Resource id) throws DataIdInputException {
        loadModel(s);
        this.id = id;
        setTypes();
        init();
    }

    private void setTypes() {
        types = new HashSet<>();
        for (Value o : graph.filter(this.id, StaticContent.a, null).objects())
            if (o.getClass().isAssignableFrom(URI.class))
                types.add((URI) o);

        partType = getDataIdPartType(getTypes());
    }

    private static void init()
    {
        try {
            validator = new DataIdValidator(DataIdConfig.getOntologies(), DataIdConfig.getExceptions());
        } catch (RDFReaderException e) {
            e.printStackTrace();
        }
    }

    private Set<Resource> getRoots() throws DataIdInputException {
        Set<Resource> subjects = graph.filter(null, StaticContent.a, new URIImpl("http://rdfs.org/ns/void#DatasetDescription")).subjects();
        if(subjects.size() == 0)
            throw new DataIdInputException("no root element (void:DatasetDescription) was found");
        return subjects;
    }

    private void loadModel(Model m)
    {
        this.graph = m;
    }

    private void loadModel(String ttl) throws DataIdInputException {
        this.graph = StaticFunctions.parseModel(ttl);
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
        Model m = StaticFunctions.createDefaultModel(StaticContent.getRdfContext());
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

    public String toSerialization(RDFFormat format) throws RDFHandlerException, DataIdInputException {
        return StaticFunctions.writeSerialization(graph, format);
    }

    public boolean validate() throws DataIdInputException {

        try{
            String ttl = this.toSerialization(RDFFormat.TURTLE);
            SerializationFormat sf = StaticJsonHelper.getSerialization(ttl);
            RDFUnitConfiguration config = validator.getConfiguration("text", ttl, sf.getName(), sf.getName(), TestCaseExecutionType.extendedTestCaseResult);
            TestSource source = config.getTestSource();
            System.out.println("query size: " + ((QueryExecutionFactoryModel) source.getExecutionFactory()).getModel().size());
            com.hp.hpl.jena.rdf.model.Model m = validator.validate(config, source, validator.getTestSuite());
            ErrorWarningWrapper ew = new JenaModelEvaluator(m).getErrorWarnings();
            this.errorswarnings.addAll(ew.getErrors());
            this.errorswarnings.addAll(ew.getWarnings());
            final ByteArrayOutputStream os = new ByteArrayOutputStream();
            new RDFStreamWriter(os, "JSON-LD").write(m);
            this.jsonLdErrorReport = os.toString("UTF8");
            if(ew.getErrors().size() == 0)
                return true;
            else
                return false;
        } catch (Exception e) {
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

    public String getJsonLdErrorReport() throws DataIdInputException {
        if(jsonLdErrorReport ==  null)
            validate();
        return jsonLdErrorReport;
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



    public static IdPart.DataIdParts getDataIdPartType(Set<URI> types)
    {
        IdPart.DataIdParts partType = null;
        for(URI uri : types)
            if(uri.stringValue().toLowerCase().contains(StaticContent.dataIdStump) || uri.stringValue().toLowerCase().contains(StaticContent.DataIdUri))
                switch(uri.stringValue().toLowerCase().trim()){
                    case StaticContent.LinksetUri:
                        partType = IdPart.DataIdParts.Linkset;
                        break;
                    case StaticContent.DatasetUri:
                        partType = IdPart.DataIdParts.Dataset;
                        break;
                    case StaticContent.DistributionUri:
                        partType = IdPart.DataIdParts.Distribution;
                        break;
//                    case StaticContent.dcatDistributionUri:
//                        partType = IdPart.DataIdParts.Distribution;
//                        break;
                    case StaticContent.DataIdUri:
                        partType = IdPart.DataIdParts.DataId;
                        break;
                    case StaticContent.PublisherUri:
                        partType = IdPart.DataIdParts.Publisher;
                        break;
                    case StaticContent.MaintainerUri:
                        partType = IdPart.DataIdParts.Maintainer;
                        break;
                    case StaticContent.CreatorUri:
                        partType = IdPart.DataIdParts.Creator;
                        break;
                    case StaticContent.ContactUri:
                        partType = IdPart.DataIdParts.Contact;
                        break;
                    case StaticContent.ContributorUri:
                        partType = IdPart.DataIdParts.Contributor;
                        break;
                    case StaticContent.AgentdUri:
                        partType = IdPart.DataIdParts.Agent;
                        break;
                    default:
                        partType = IdPart.DataIdParts.Entity;
                        //TODO Entity, Activity!
                }
        return partType;
    }
}
