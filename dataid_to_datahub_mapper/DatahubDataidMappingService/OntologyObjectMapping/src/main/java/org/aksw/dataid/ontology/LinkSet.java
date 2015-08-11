package org.aksw.dataid.ontology;

import org.aksw.dataid.wrapper.InternalLieteralImpl;
import org.aksw.dataid.wrapper.OntoPropery;
import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;

/**
 * Created by Chile on 3/9/2015.
 */
public class LinkSet extends DataIdPart{

    @OntoPropery(property = "http://rdfs.org/ns/void#linkPredicate", maxCard = 1)
    private URI linkPredicate;
    @OntoPropery(property = "http://rdfs.org/ns/void#subjectsTarget", maxCard = 1, minCard = 1)
    private URI subjectTarget;
    @OntoPropery(property = "http://rdfs.org/ns/void#objectsTarget", maxCard = 1, minCard = 1)
    private URI objectTarget;
    @OntoPropery(property = "http://rdfs.org/ns/void#exampleResource", maxCard = 1)
    private URI exampleResource;
    @OntoPropery(property = "http://rdfs.org/ns/void#triples", maxCard = 1, recommended = true)
    private InternalLieteralImpl triples;
    @OntoPropery(property = "http://purl.org/dc/terms/issued", maxCard = 1)
    private InternalLieteralImpl issued;
    @OntoPropery(property = "http://purl.org/dc/terms/modified", maxCard = 1, recommended = true)
    private InternalLieteralImpl modified;
    @OntoPropery(property = "http://dataid.dbpedia.org/ns/core#nextLinksetVersion", maxCard = 1, recommended = true)
    private LinkSet nextVersion;
    @OntoPropery(property = "http://dataid.dbpedia.org/ns/core#previousLinksetVersion", maxCard = 1, recommended = true)
    private LinkSet prevVersion;
    @OntoPropery(property = "http://dataid.dbpedia.org/ns/core#LatestLinksetVersion", maxCard = 1)
    private LinkSet latestVersion;

    public String getLinkPredicate() {
        if(linkPredicate != null)
            return linkPredicate.stringValue();
        return null;
    }

    public void setLinkPredicate(String linkPredicate) {
        this.linkPredicate = new URIImpl(linkPredicate);
    }

    public String getSubjectTarget() {
        if(subjectTarget == null)
            return null;
        return subjectTarget.stringValue();
    }

    public void setSubjectTarget(String subjectTarget) {
        this.subjectTarget = new URIImpl(subjectTarget);
    }

    public String getObjectTarget() {

        if(objectTarget == null)
            return null;
        return objectTarget.stringValue();
    }

    public void setObjectTarget(String objectTarget) {
        this.objectTarget =new URIImpl( objectTarget);
    }

    public String getExampleResource() {

        if(exampleResource == null)
            return null;
        return exampleResource.stringValue();
    }

    public void setExampleResource(String exampleResource) {
        this.exampleResource = new URIImpl(exampleResource);
    }

    public InternalLieteralImpl getTriples() {
        return triples;
    }

    public void setTriples(InternalLieteralImpl triples) {
        this.triples = triples;
    }

    public InternalLieteralImpl getIssued() {
        return issued;
    }

    public void setIssued(InternalLieteralImpl issued) {
        this.issued = issued;
    }

    public InternalLieteralImpl getModified() {
        return modified;
    }

    public void setModified(InternalLieteralImpl modified) {
        this.modified = modified;
    }

    public LinkSet getNextVersion() {
        return nextVersion;
    }

    public void setNextVersion(LinkSet nextVersion) {
        this.nextVersion = nextVersion;
    }

    public LinkSet getPrevVersion() {
        return prevVersion;
    }

    public void setPrevVersion(LinkSet prevVersion) {
        this.prevVersion = prevVersion;
    }

    public LinkSet getLatestVersion() {
        return latestVersion;
    }

    public void setLatestVersion(LinkSet latestVersion) {
        this.latestVersion = latestVersion;
    }
}
