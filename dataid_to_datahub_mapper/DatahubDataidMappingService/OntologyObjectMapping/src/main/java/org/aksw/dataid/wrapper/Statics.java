package org.aksw.dataid.wrapper;

import org.aksw.dataid.config.DataIdConfig;
import org.aksw.dataid.config.RdfContext;
import org.aksw.dataid.errors.DataIdInputException;
import org.aksw.dataid.jsonutils.StaticJsonHelper;
import org.aksw.dataid.ontology.IdPart;
import org.openrdf.model.*;
import org.openrdf.model.impl.TreeModel;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFParseException;
import org.openrdf.rio.RDFParser;
import org.openrdf.rio.helpers.BasicWriterSettings;
import org.openrdf.rio.turtle.TurtleWriter;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * Created by Chile on 8/18/2015.
 */
public class Statics {

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

    public static Model createDefaultModel(RdfContext rdfContext)
    {
        TreeModel model = new TreeModel();
        return addRdfContext(model, rdfContext);
    }

    private static Model addRdfContext(Model model, RdfContext context)
    {
        for(String key : context)
            model.setNamespace(key, context.getUri(key));
        return model;
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
        for(Statement st : model.filter(subject, a, null))
            if(st.getObject().stringValue().contains(dataIdStump)
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

    public static Model parseModel(String ttl) throws DataIdInputException {
        ModelRdfHandler handler = new ModelRdfHandler();
        StringReader sr = new StringReader(ttl);
        RDFParser parser = StaticJsonHelper.getRdfParser(ttl);
        parser.setRDFHandler(handler);
        parser.setPreserveBNodeIDs(true);
        try {
            parser.parse(sr, DataIdConfig.getDataIdUri());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (RDFHandlerException e) {
            e.printStackTrace();
        } catch (RDFParseException e) {
            e.printStackTrace();
        }
        return handler.getModel();
    }

    public static String writeTurtle(Model m) throws RDFHandlerException {
        StringWriter sw = new StringWriter();
        TurtleWriter ttlWriter = new TurtleWriter(sw);
        ttlWriter.getWriterConfig().set(BasicWriterSettings.PRETTY_PRINT, true);
        ttlWriter.startRDF();
        for(Namespace ns : m.getNamespaces()) {
            ttlWriter.handleNamespace(ns.getPrefix(), ns.getName());
        }
        for(org.openrdf.model.Statement st : m) {
            ttlWriter.handleStatement(st);
        }
        ttlWriter.endRDF();
        return sw.toString();
    }

    public static IdPart.DataIdParts getDataIdPartType(Set<URI> types)
    {
        IdPart.DataIdParts partType = null;
        for(URI uri : types)
            if(uri.stringValue().toLowerCase().contains(Statics.dataIdStump) || uri.stringValue().toLowerCase().contains(Statics.DataIdUri))
                switch(uri.stringValue().toLowerCase().trim()){
                    case Statics.LinksetUri:
                        partType = IdPart.DataIdParts.Linkset;
                        break;
                    case Statics.DatasetUri:
                        partType = IdPart.DataIdParts.Dataset;
                        break;
                    case Statics.DistributionUri:
                        partType = IdPart.DataIdParts.Distribution;
                        break;
//                    case Statics.dcatDistributionUri:
//                        partType = IdPart.DataIdParts.Distribution;
//                        break;
                    case Statics.DataIdUri:
                        partType = IdPart.DataIdParts.DataId;
                        break;
                    case Statics.PublisherUri:
                        partType = IdPart.DataIdParts.Publisher;
                        break;
                    case Statics.MaintainerUri:
                        partType = IdPart.DataIdParts.Maintainer;
                        break;
                    case Statics.CreatorUri:
                        partType = IdPart.DataIdParts.Creator;
                        break;
                    case Statics.ContactUri:
                        partType = IdPart.DataIdParts.Contact;
                        break;
                    case Statics.ContributorUri:
                        partType = IdPart.DataIdParts.Contributor;
                        break;
                    case Statics.AgentdUri:
                        partType = IdPart.DataIdParts.Agent;
                        break;
                    default:
                        partType = IdPart.DataIdParts.Entity;
                    //TODO Entity, Activity!
                }
        return partType;
    }
}
