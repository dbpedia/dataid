package org.aksw.dataid.statics;

import org.aksw.dataid.config.DataIdConfig;
import org.aksw.dataid.config.MappingConfig;
import org.aksw.dataid.config.RdfContext;
import org.aksw.dataid.jsonutils.StaticJsonHelper;
import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;

import java.text.SimpleDateFormat;
import java.util.HashMap;

/**
 * Created by Chile on 8/16/2015.
 */
public class StaticContent {
    private static RdfContext rdfContext;
    private static MappingConfig mappings;

    public static final URI a = new URIImpl("http://www.w3.org/1999/02/22-rdf-syntax-ns#type");
    public static final URI xsdString = new URIImpl("http://www.w3.org/2001/XMLSchema#string");
    public static final URI rdfsString = new URIImpl("http://www.w3.org/1999/02/22-rdf-syntax-ns#langString");
    public static final String dataIdStump = "http://dataid.dbpedia.org/ns/core#";
    public static final String xmlStump = "http://www.w3.org/2001/XMLSchema#";
    public static final String voidStump = "http://rdfs.org/ns/void#";
    public static final String foafStump = "http://xmlns.com/foaf/0.1/";
    public static final String purlStump = "http://purl.org/dc/terms/";
    public static final String dcatStump = "http://www.w3.org/ns/dcat#";

    public static final SimpleDateFormat dayDateFormatter = new SimpleDateFormat("yyyy-MM-dd");
    public static final SimpleDateFormat secDateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public static final SimpleDateFormat msDateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
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

    public static RdfContext getRdfContext() {
        return rdfContext;
    }

    public static RdfContext setRdfContext(String mappingConfigPath) {
        StaticContent.rdfContext = new RdfContext(StaticJsonHelper.castJsonToObject(StaticJsonHelper.getJsonContent(mappingConfigPath), new HashMap<String, String>().getClass(), "@context"));
        return rdfContext;
    }

    public static MappingConfig getMappings() {
        return mappings;
    }

    public static MappingConfig setMappings(String mappingConfigPath) {
        StaticContent.mappings = StaticJsonHelper.castJsonToObject(StaticJsonHelper.getJsonContent(mappingConfigPath).toString(), MappingConfig.class, "@graph");
        setRdfContext(mappingConfigPath);
        StaticContent.mappings.setRdfContext(StaticContent.getRdfContext());
        return StaticContent.mappings;
    }

}
