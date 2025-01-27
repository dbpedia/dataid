package org.aksw.dataid.ontology;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.aksw.dataid.wrapper.InternalLieteralImpl;
import org.aksw.dataid.wrapper.ModelWrapper;
import org.aksw.dataid.wrapper.OntoPropery;
import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;

import javax.naming.directory.InvalidAttributesException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Chile on 3/10/2015.
 */
@JsonPropertyOrder({ "title", "identifier", "label", "comment", "description", "issued", "modified", "language", "keywords" })
public class DataIdDataset extends DataIdPart {

    @OntoPropery(property = "http://purl.org/dc/terms/title", maxCard = 1, minCard = 1)
    private InternalLieteralImpl title;
    @OntoPropery(property = "http://purl.org/dc/terms/description", maxCard = 1, recommended = true)
    private InternalLieteralImpl description;
    @OntoPropery(property = "http://purl.org/dc/terms/modified", maxCard = 1, recommended = true, derivable = true)
    private InternalLieteralImpl modified;
    @OntoPropery(property = "http://purl.org/dc/terms/issued", maxCard = 1, recommended = true, derivable = true)
    private InternalLieteralImpl issued;
    @OntoPropery(property = "http://purl.org/dc/terms/language", maxCard = 1, recommended = true, derivable = true)
    private InternalLieteralImpl language;
    @OntoPropery(property = "http://purl.org/dc/terms/accrualPeriodicity", maxCard = 1)
    private URI accrualPeriodicity;
    @OntoPropery(property = "http://www.w3.org/ns/dcat#theme", recommended = true)
    private List<URI> themes;
    @OntoPropery(property = "http://www.w3.org/ns/dcat#keyword", minCard = 1)
    private List<InternalLieteralImpl> keywords;
    @OntoPropery(property = "http://dataid.dbpedia.org/ns/core#associatedAgent", minCard = 1, derivable = true)
    private List<Agent> associatedAgent;
    @OntoPropery(property = "http://dataid.dbpedia.org/ns/core#distribution", minCard = 1, alternative = "http://rdfs.org/ns/void#subset")
    private List<Distribution> distributions;
    @OntoPropery(property = "http://www.w3.org/ns/dcat#landingPage", minCard = 1)
    private List<URI> landingPage;
    @OntoPropery(property = "http://rdfs.org/ns/void#sparqlEndpoint")
    private List<URI> sparqlEndpoint;
    @OntoPropery(property = "http://rdfs.org/ns/void#triples", maxCard = 1, recommended = true)
    private InternalLieteralImpl triples;
    @OntoPropery(property = "http://rdfs.org/ns/void#properties", maxCard = 1)
    private InternalLieteralImpl properties;
    @OntoPropery(property = "http://rdfs.org/ns/void#entities", maxCard = 1)
    private InternalLieteralImpl entities;
    @OntoPropery(property = "http://rdfs.org/ns/void#documents", maxCard = 1)
    private InternalLieteralImpl documents;
    @OntoPropery(property = "http://rdfs.org/ns/void#distinctSubjects", maxCard = 1)
    private InternalLieteralImpl distinctSubjects;
    @OntoPropery(property = "http://rdfs.org/ns/void#distinctObjects", maxCard = 1)
    private InternalLieteralImpl distinctObjects;
    @OntoPropery(property = "http://rdfs.org/ns/void#classes", maxCard = 1)
    private InternalLieteralImpl classes;
    @OntoPropery(property = "http://rdfs.org/ns/void#rootResource", maxCard = 1, recommended = true)
    private Distribution rootResource;
    @OntoPropery(property = "http://dataid.dbpedia.org/ns/core#containsLinks", recommended = true)
    private List<LinkSet> linksets;
    @OntoPropery(property = "http://dataid.dbpedia.org/ns/core#latestVersion", maxCard = 1, recommended = true)
    private DataIdDataset latestVersion;
    @OntoPropery(property = "http://dataid.dbpedia.org/ns/core#nextVersion", maxCard = 1)
    private DataIdDataset nextVersion;
    @OntoPropery(property = "http://dataid.dbpedia.org/ns/core#previousVersion", maxCard = 1)
    private DataIdDataset previousVersion;
    @OntoPropery(property = "http://rdfs.org/ns/void#subset")
    private List<DataIdDataset> subsets;
    @OntoPropery(property = "http://purl.org/dc/terms/license", minCard = 1, maxCard = 1)
    private URI license;
    @OntoPropery(property = "http://dataid.dbpedia.org/ns/core#licenseName", maxCard = 1, recommended = true)
    private InternalLieteralImpl licanseName;
    @OntoPropery(property = "http://purl.org/dc/terms/rights", maxCard = 1)
    private InternalLieteralImpl rights;

    public InternalLieteralImpl getTitle() {
        return title;
    }

    public void setTitle(InternalLieteralImpl title) {
        this.title = title;
    }

    public InternalLieteralImpl getDescription() {
        return description;
    }

    public void setDescription(InternalLieteralImpl description) {
        this.description = description;
    }

    public InternalLieteralImpl getModified() {
        return modified;
    }

    public void setModified(InternalLieteralImpl modified) {
        this.modified = modified;
    }

    public InternalLieteralImpl getIssued() {
        return issued;
    }

    public void setIssued(InternalLieteralImpl issued) {
        this.issued = issued;
    }

    public InternalLieteralImpl getLanguage() {
        return language;
    }

    public void setLanguage(InternalLieteralImpl language) {
        this.language = language;
    }

    public String getAccrualPeriodicity() {
        return accrualPeriodicity != null ? accrualPeriodicity.stringValue() : null;
    }

    public void setAccrualPeriodicity(String accrualPeriodicity) {
        this.accrualPeriodicity = new URIImpl(accrualPeriodicity);
    }

    public List<String> getThemes() {
        if(themes == null)
            return null;
        List<String> zw = new ArrayList<String>();
        for(URI uri : themes)
            zw.add(uri.stringValue());
        return zw;
    }

    public void setThemes(List<String> themes) {
        this.themes = new ArrayList<URI>();
        for(String uri : themes)
            this.themes.add(new URIImpl(uri));
    }

    public List<InternalLieteralImpl> getKeywords() {
        return keywords;
    }

    public void setKeywords(List<InternalLieteralImpl> keywords) {
        this.keywords = keywords;
    }

    public List<Distribution> getDistributions() {
        return distributions;
    }

    public void setDistributions(List<Distribution> distributions) {
        this.distributions = distributions;
    }

    public List<String> getLandingPages() {
        if(landingPage == null)
            return null;
        List<String> zw = new ArrayList<String>();
        for(URI uri : landingPage)
            zw.add(uri.stringValue());
        return zw;
    }

    public void setLandingPages(List<String> landingPage) {
        this.landingPage = new ArrayList<URI>();
        for(String uri : landingPage)
            this.landingPage.add(new URIImpl(uri));
    }

    public List<String> getSparqlEndpoints() {
        if(sparqlEndpoint == null)
            return null;
        List<String> zw = new ArrayList<String>();
        for(URI uri : sparqlEndpoint)
            zw.add(uri.stringValue());
        return zw;
    }

    public void setSparqlEndpoints(List<String> endpoints) {
        this.sparqlEndpoint = new ArrayList<URI>();
        for(String uri : endpoints)
            this.sparqlEndpoint.add(new URIImpl(uri));
    }

    public List<Agent> getAssociatedAgent() {
        return associatedAgent;
    }

    public void setAssociatedAgent(List<Agent> associatedAgent) {
        this.associatedAgent = associatedAgent;
    }


    public InternalLieteralImpl getTriples() {
        return triples;
    }

    public void setTriples(InternalLieteralImpl triples) {
        this.triples = triples;
    }

    public InternalLieteralImpl getProperties() {
        return properties;
    }

    public void setProperties(InternalLieteralImpl properties) {
        this.properties = properties;
    }

    public InternalLieteralImpl getEntities() {
        return entities;
    }

    public void setEntities(InternalLieteralImpl entities) {
        this.entities = entities;
    }

    public InternalLieteralImpl getDocuments() {
        return documents;
    }

    public void setDocuments(InternalLieteralImpl documents) {
        this.documents = documents;
    }

    public InternalLieteralImpl getDistinctSubjects() {
        return distinctSubjects;
    }

    public void setDistinctSubjects(InternalLieteralImpl distinctSubjects) {
        this.distinctSubjects = distinctSubjects;
    }

    public InternalLieteralImpl getDistinctObjects() {
        return distinctObjects;
    }

    public void setDistinctObjects(InternalLieteralImpl distinctObjects) {
        this.distinctObjects = distinctObjects;
    }

    public InternalLieteralImpl getClasses() {
        return classes;
    }

    public void setClasses(InternalLieteralImpl classes) {
        this.classes = classes;
    }

    public Distribution getRootResource() {
        return rootResource;
    }

    public void setRootResource(Distribution rootResource) {
        this.rootResource = rootResource;
    }

    public List<LinkSet> getLinksets() {
        return linksets;
    }

    public void setLinksets(List<LinkSet> linksets) {
        this.linksets = linksets;
    }

    public DataIdDataset getLatestVersion() {
        return latestVersion;
    }

    public void setLatestVersion(DataIdDataset latestVersion) {
        this.latestVersion = latestVersion;
    }

    public DataIdDataset getNextVersion() {
        return nextVersion;
    }

    public void setNextVersion(DataIdDataset nextVersion) {
        this.nextVersion = nextVersion;
    }

    public DataIdDataset getPreviousVersion() {
        return previousVersion;
    }

    public void setPreviousVersion(DataIdDataset previousVersion) {
        this.previousVersion = previousVersion;
    }

    public List<DataIdDataset> getSubsets() {
        return subsets;
    }

    public void setSubsets(List<DataIdDataset> subsets) {
        this.subsets = subsets;
    }

    public String getLicense() {
        return license != null ? license.stringValue() : null;
    }

    public void setLicense(String license) {
        this.license = new URIImpl(license);
    }

    public InternalLieteralImpl getLicanseName() {
        return licanseName;
    }

    public void setLicanseName(InternalLieteralImpl licanseName) {
        this.licanseName = licanseName;
    }

    public InternalLieteralImpl getRights() {
        return rights;
    }

    public void setRights(InternalLieteralImpl rights) {
        this.rights = rights;
    }
}
