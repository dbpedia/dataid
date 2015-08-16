package org.aksw.dataid.wrapper;

import com.sun.javaws.exceptions.InvalidArgumentException;
import org.aksw.dataid.ontology.*;
import org.openrdf.model.*;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.rio.RDFHandler;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFParseException;
import org.openrdf.rio.turtle.TurtleParser;
import org.openrdf.rio.turtle.TurtleWriter;

import javax.naming.directory.InvalidAttributesException;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by Chile on 3/9/2015.
 */
public class ModelWrapper {
    public static final URI a = new URIImpl("http://www.w3.org/1999/02/22-rdf-syntax-ns#type");
    public static final URI xsdString = new URIImpl("http://www.w3.org/2001/XMLSchema#string");
    public static final URI rdfsString = new URIImpl("http://www.w3.org/1999/02/22-rdf-syntax-ns#langString");
    public static final String dataIdStump = "http://dataid.dbpedia.org/ns/core#";
    public static final String xmlStump = "http://www.w3.org/2001/XMLSchema#";
    public static final String voidStump = "http://rdfs.org/ns/void#";
    public static final String foafStump = "http://xmlns.com/foaf/0.1/";
    public static final String purlStump = "http://purl.org/dc/terms/";
    public static final String dcatStump = "http://www.w3.org/ns/dcat#";

    private static final SimpleDateFormat dayDateFormatter = new SimpleDateFormat("yyyy-MM-dd");
    private static final SimpleDateFormat secDateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static final SimpleDateFormat msDateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    private static ModelWrapper instance;
    public static final String LinksetUri = dataIdStump + "Linkset";
    public static final String DatasetUri = dataIdStump + "Dataset";
    public static final String DistributionUri = dataIdStump + "Distribution";
    public static final String dcatDistributionUri = dcatStump + "Distribution";
    public static final String DataIdUri = voidStump + "DatasetDescription";
    public static final String AgentdUri = dataIdStump + "Agent";
    public static final String PublisherUri = dataIdStump + "Publisher";
    public static final String MaintainerUri = dataIdStump + "Maintainer";
    public static final String CreatorUri = dataIdStump + "Creator";
    public static final String ContactUri = dataIdStump + "Contact";
    public static final String ContributorUri = dataIdStump + "Contributor";

    private static Map<Resource, DataIdPart> objectMap;
    private static RdfContext rdfContext;
    private static Model model;
    private static OntoPropery.OntologyUsage usage;

    public static ModelWrapper getInstance() throws Exception {
        if(objectMap == null)
            throw new Exception("model not loaded");
        if(instance == null)
            instance = new ModelWrapper();
        return instance;
    }

    public static URI GetDataIdUri(String stump, String property)
    {
        return new URIImpl(stump + property);
    }

    public static URI GetDataIdUri(String uri)
    {
        return new URIImpl(uri);
    }

    public static boolean CompareToUri(URI uri, String uri2)
    {
        return CompareToUri(uri, GetDataIdUri(uri2));
    }

    public static boolean CompareToUri(URI uri, URI uri2)
    {
        if(uri.equals(uri2))
            return true;
        else
            return false;
    }

    public static <T extends Object> List<T> CreateGenericList(Class<T> type)
    {
        return new ArrayList<T>();
    }

    public static String GetDataIdType(Model model, Resource subject)
    {
        for(Statement st : model.filter(subject, ModelWrapper.a, null))
            if(st.getObject().stringValue().contains(dataIdStump)
                    || st.getObject().stringValue().contains(dcatDistributionUri)
                    || st.getObject().stringValue().contains(DataIdUri))
                return st.getObject().stringValue();
        return null;
    }

    public static Date ParseDate(String date) throws ParseException {
        date = date.replace("  ", " ");
        if (date.contains("."))
            return msDateFormatter.parse(date);
        else if (date.contains(":"))
            return secDateFormatter.parse(date);
        else if (date.contains("-"))
            return dayDateFormatter.parse(date);
        else
            throw new ParseException("date format not rcognized : 'yyyy-MM-dd (HH:mm:ss.(SSS))'", 0);
    }

    public static String FormatDate(Date date){
        return secDateFormatter.format(date);
    }

    public static void loadModel(RdfContext context, String ttl) throws RDFParseException, RDFHandlerException {
        loadModel(context, ttl, OntoPropery.OntologyUsage.DataIdCore);
    }
        public static void loadModel(RdfContext context, String ttl, OntoPropery.OntologyUsage u) throws RDFParseException, RDFHandlerException {
        rdfContext = context;
        model = parseModel(ttl);
        usage = u;
        objectMap = new HashMap<Resource, DataIdPart>();

        for(Statement st: model.filter(null, ModelWrapper.a, GetDataIdUri(DataIdUri)))
        {
            URI dataIdUri = ((URI)(st.getSubject()));
            DataIdPart p = null;
            try {
                p = getDataIdPart(dataIdUri);
                objectMap.put(dataIdUri, p);
                //p.resolveAllParentReferences();
            } catch (InvalidAttributesException e) {
                e.printStackTrace();
            }
            //String[] zw = p.errorsAndWarnings();
        }
    }

    public static RdfContext getRdfContext() {
        return instance.rdfContext;
    }

    public static void setRdfContext(RdfContext rdfContext) {
        instance.rdfContext = rdfContext;
    }

    public static String writeTurtle(Model model) throws RDFHandlerException {
        StringWriter sw = new StringWriter();
        TurtleWriter ttlWriter = new TurtleWriter(sw);

        ttlWriter.startRDF();
        for(org.openrdf.model.Statement st : model) {
            ttlWriter.handleStatement(st);
        }
        ttlWriter.endRDF();
        return sw.toString();
    }

    public static Model createDefaultModel()
    {
        Model model = new org.openrdf.model.impl.TreeModel();
        for(String key : instance.rdfContext)
            model.setNamespace(key, instance.rdfContext.getUri(key));
        return model;
    }
    public static Model parseModel(String ttl) throws RDFParseException, RDFHandlerException
    {
        ModelRdfHandler handler = new ModelRdfHandler();
        StringReader sr = new StringReader(ttl);
        TurtleParser parser = new TurtleParser();
        parser.setRDFHandler(handler);
        parser.setPreserveBNodeIDs(true);
        try {
            parser.parse(sr, "http://dataid.dbpedia.org/ns/core#");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return handler.getModel();
    }

    public static Model getModel() {
        return model;
    }

    public static void setModel(Model m)
    {
        model = m;
    }

    public static List<DataId> getAllDataIds()
    {
        List<DataId> ids = new ArrayList<DataId>();
        if(model != null)
        {
            for(DataIdPart part : objectMap.values())
            {
                if(part != null && DataId.class.isAssignableFrom(part.getClass()))
                    ids.add((DataId) part);
            }
        }
        return ids;
    }

    public static DataIdPart getDataIdPart(Resource uri) throws InvalidAttributesException {
        DataIdPart part = objectMap.get(uri);
        Model partModel = model.filter(uri, null, null);

        Model subsetParent = model.filter(null, new URIImpl(voidStump + "subset"), uri);
        Model dataidParent = model.filter(null, new URIImpl(foafStump + "topic"), uri);
        Model distributionParent = model.filter(null, new URIImpl(dcatStump + "distribution"), uri);

        if(part == null)
        {
            try{
                String type = GetDataIdType(partModel, uri);
                if(type == null)
                    throw new InvalidAttributesException("could not find valid type for " + uri);
                switch(type) {
                    case LinksetUri:
                        part = new LinkSet();
                        part = parseDataIdPart(partModel, part);
                        break;
                    case DatasetUri:
                        part = new DataIdDataset();
                        if(subsetParent.size() > 0)
                            part.setParent(subsetParent.subjectURI());
                        else if(dataidParent.size() > 0)
                            part.setParent(dataidParent.subjectURI());
                        part = parseDataIdPart(partModel, part);
                        break;
                    case DistributionUri:  //TODO ???
                        part = new Distribution();
                        part = parseDataIdPart(partModel, part);
                        break;
                    case dcatDistributionUri:
                        part = new Distribution();
                        if(distributionParent.size() > 0)
                            part.setParent(distributionParent.subjectURI());
                        part = parseDataIdPart(partModel, part);
                        break;
                    case DataIdUri:
                        part = new DataId();
                        part = parseDataIdPart(partModel, part);
                        break;
                    case PublisherUri:
                    case MaintainerUri:
                    case CreatorUri:
                    case ContactUri:
                    case ContributorUri:
                    case AgentdUri:
                        part = new Agent();
                        switch(type)
                        {
                            case PublisherUri: ((Agent) part).setAgentRole("Publisher"); break;
                            case MaintainerUri: ((Agent) part).setAgentRole("Maintainer"); break;
                            case CreatorUri: ((Agent) part).setAgentRole("Creator"); break;
                            case ContactUri: ((Agent) part).setAgentRole("Contact"); break;
                            case ContributorUri: ((Agent) part).setAgentRole("Contributor"); break;
                            case AgentdUri: ((Agent) part).setAgentRole("Agent"); break;
                        }
                        part = parseDataIdPart(partModel, part);
                        break;
                }
            } catch (IllegalAccessException e) {
                //TODO
                e.printStackTrace();
            } catch (InstantiationException e) {
                //TODO
                e.printStackTrace();
            }
        }
        objectMap.put(uri, part);
        return part;
    }

    public static <T extends DataIdPart> DataIdPart parseDataIdPart(Model model, T inst) throws IllegalAccessException, InstantiationException, InvalidAttributesException {

        inst.setIdentifier(model.iterator().next().getSubject().stringValue());

        Map<OntoPropery, Field> annos = inst.annotations();

        for(Statement st : model)
        {
            for(OntoPropery anno : annos.keySet())
            {
                if(ModelWrapper.CompareToUri(st.getPredicate(), ModelWrapper.GetDataIdUri(anno.property())))
                {
                    Object val = InsertProperty(inst, st.getObject(), anno);
                    Field field = annos.get(anno);
                    field.setAccessible(true);
                    field.set(inst, val);
                    break;
                }
            }
        }

        for(OntoPropery anno : annos.keySet())
        {
            if(anno.derivable() && inst.getParent() != null) {
                Object val = annos.get(anno).get(inst);
                if(val == null || (List.class.isAssignableFrom(val.getClass())
                        && (((List<?>)val).size() == 0 || ((List<?>)val).get(0) == null)))
                    inst.getUnresolvedValues().add(new LazyParentReference(inst.getParent(), anno, inst));
            }
        }
        inst.checkInstanceAgainstOntology(usage);
        return inst;
    }

    public static OntoPropery.OntologyUsage getUsage() {
        return usage;
    }

    static Object InsertProperty(DataIdPart inst, Value object, OntoPropery anno) throws IllegalAccessException, InvalidAttributesException {
        Object retVal = null;
        Field field = inst.annotations().get(anno);
        field.setAccessible(true);
        if(DataIdPart.class.isAssignableFrom(field.getType())) {
            if (BNode.class.isAssignableFrom(object.getClass()))
                retVal = field.getType().cast(getDataIdPart((BNode)object));
            else
                retVal = field.getType().cast(getDataIdPart(new URIImpl(object.stringValue())));
        }
        else if(URI.class.isAssignableFrom(field.getType()))
            retVal = field.getType().cast(new URIImpl(object.stringValue()));
        else if(List.class.isAssignableFrom(field.getType()))
        {
            ParameterizedType stringListType = (ParameterizedType) field.getGenericType();
            Class<?> genericListClass = (Class<?>) stringListType.getActualTypeArguments()[0];

            List<Object> list = null;
            if (field.get(inst) == null)
                list = (List<Object>) ModelWrapper.CreateGenericList(genericListClass);
            else
                list = (List<Object>) field.get(inst);
            if(DataIdPart.class.isAssignableFrom(genericListClass))
            {
                if (BNode.class.isAssignableFrom(object.getClass())) {
                    list.add(getDataIdPart((BNode) object));
                }
                else {
                    list.add(getDataIdPart(new URIImpl(object.stringValue())));
                }
            }
            else if(URI.class.isAssignableFrom(genericListClass))
                list.add(new URIImpl(object.stringValue()));
            else //Literal
                list.add(new InternalLieteralImpl((Literal)object));
            retVal = list;
        }
        else //Literal!
        {
            InternalLieteralImpl lit = (InternalLieteralImpl) field.get(inst);
            if(lit != null && lit.isString())
            {
                lit.addTranslation(((Literal) object).getLabel(), ((Literal) object).getLanguage());
                retVal = lit;
            }
            retVal = new InternalLieteralImpl((Literal) object);
        }
        return retVal;
    }

    public static class ModelRdfHandler implements RDFHandler
    {
        private Model model;
        private boolean loaded = false;

        @Override
        public void startRDF() throws RDFHandlerException {
            model = createDefaultModel();
            loaded = false;
        }

        @Override
        public void endRDF() throws RDFHandlerException {
            loaded = true;
        }

        @Override
        public void handleNamespace(String s, String s1) throws RDFHandlerException {
            model.setNamespace(s, s1);
        }

        @Override
        public void handleStatement(Statement statement) throws RDFHandlerException {
            model.add(statement);
        }

        @Override
        public void handleComment(String s) throws RDFHandlerException {
        }

        public Model getModel() {
            if(loaded)
                return model;
            else
                return null;
        }
    }
}
