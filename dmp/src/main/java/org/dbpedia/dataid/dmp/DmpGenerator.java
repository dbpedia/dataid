package org.dbpedia.dataid.dmp;


import org.apache.jena.rdf.model.*;
import org.apache.jena.sparql.vocabulary.FOAF;
import org.apache.jena.vocabulary.DCTerms;
import org.apache.jena.vocabulary.RDF;

import java.io.FileWriter;
import java.io.Writer;

public class DmpGenerator {


    private static final String DATA_ID_NS = "http://dataid.dbpedia.org/ns/core#";
    private static final Property  dataIdStatement = getDataIDProperty("statement");

    private DmpGenerator() { }


    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            throw new IllegalArgumentException("Input not correct");
        }

        Model m = ModelFactory.createDefaultModel();
        m.read(args[0]);

        Resource datasetDataIdResource = m.listResourcesWithProperty(RDF.type, getDataIDClass("DataId"))
            .toList().stream().findFirst().orElseThrow(IllegalArgumentException::new);

        Resource mainDataset = datasetDataIdResource.getPropertyResourceValue(FOAF.primaryTopic);
        
        StringBuilder sb = new StringBuilder();

        sb.append("\n<h2>Dataset reference and name</h2>");
        sb.append("<ul>");
        asHtmlListItem(sb, "Name", getSingleValueAsTextForProperty(mainDataset, DCTerms.title));
        asHtmlListItem(sb, "Metadata IRI", asHtmlLink(datasetDataIdResource.getURI()));
        asHtmlListItem(sb, "Homepage", asHtmlLink(getSingleValueAsTextForProperty(mainDataset, getProperty("http://www.w3.org/ns/dcat#landingPage"))));//Homepage
        //Maintainer
        //Publisher
        sb.append("</ul>");

        sb.append("\n<h2>Dataset Metadata description</h2>");
        sb.append("<ul>");
        asHtmlListItem(sb, "Description", getSingleValueAsTextForProperty(mainDataset, DCTerms.description));
        asHtmlListItem(sb, "Usefulness", getStatementTextFor(mainDataset, getDataIDProperty("usefulness")));
        asHtmlListItem(sb, "Similar Data", getStatementTextFor(mainDataset, getDataIDProperty("similarData")));
        asHtmlListItem(sb, "Re-use and integration", getStatementTextFor(mainDataset, getDataIDProperty("reuseAndIntegration")));
        sb.append("</ul>");

        sb.append("\n<h2>Standards and metadata</h2>");
        sb.append("<ul>");
        sb.append("<li>Dataset Metadata description is done in Linked Data using DataID, a metadata description vocabulary based on DCAT. DMP reports are automatically generated and maintained up to date using this metadata</li>");//Vocabularies and Ontologies used
        //vocabularies and ontologies used
        sb.append("</ul>");

        sb.append("\n<h2>Data sharing</h2>");
        sb.append("<ul>");
        //license
        asHtmlListItem(sb, "License", getStatementTextFor(mainDataset, DCTerms.rights));
        if (mainDataset.hasProperty(DCTerms.license)) {
            asHtmlListItem(sb, "ODRL license description", asHtmlLink(getSingleValueAsTextForProperty(mainDataset, DCTerms.license)));
        }
        asHtmlListItem(sb, "Openness", getStatementTextFor(mainDataset, getDataIDProperty("openness")));
        //software necessary
        //repository

        sb.append("</ul>");

        sb.append("\n<h2>Archiving and preservation</h2>");
        sb.append("<ul>");
        //preservation
        asHtmlListItem(sb, "Groth", getStatementTextFor(mainDataset, getDataIDProperty("growth")));
        //archive

        sb.append("</ul>");


        Writer w = new FileWriter(args[0] + ".html");
        w.write(sb.toString());
        w.close();
        

    }


    private static Property getProperty(String iri){
        return ResourceFactory.createProperty(iri);
    }

    private static Property getDataIDProperty(String pname) {
        return getProperty(DATA_ID_NS + pname);
    }

    private static Resource getDataIDClass(String name) {
        return ResourceFactory.createResource(DATA_ID_NS + name);
    }

    private static String getSingleValueAsTextForProperty(Resource subject, Property predicate) {
        RDFNode n = subject.getProperty(predicate).getObject();
        if (n.isLiteral())
            return n.asLiteral().getLexicalForm();
        else
            return n.toString();
    }

    private static String getStatementTextFor(Resource subject, Property predicate) {
        return subject.getProperty(predicate).getObject().asResource().getProperty(dataIdStatement).getObject().asLiteral().getLexicalForm();
    }

    private static String asHtmlLink(String iri, String text) {
        return "<a href=\"" + iri + "\">" + text + "</a>";
    }

    private static String asHtmlLink(String iri) {
        return asHtmlLink(iri, iri);
    }

    private static void asHtmlListItem(StringBuilder sb, String title, String value) {
        sb
                .append("\n<li>")
                .append("<strong>").append(title).append("</strong>: ")
                .append(value)
                .append("</li>");

    }
}


