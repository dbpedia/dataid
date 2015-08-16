package org.aksw.dataid.dmpExtension;

import org.aksw.dataid.ontology.DataIdDataset;
import org.aksw.dataid.wrapper.InternalLieteralImpl;
import org.aksw.dataid.wrapper.OntoPropery;
import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;

/**
 * Created by Chile on 8/7/2015.
 */
public class DmpDataset extends DataIdDataset {

    //DMP additions
    @OntoPropery(property = "http://dataid.dbpedia.org/ns/core#usefulness", maxCard = 1, minCard = 1, ontoUsage = OntoPropery.OntologyUsage.DataIdDmp)
    private InternalLieteralImpl usefulness;
    @OntoPropery(property = "http://dataid.dbpedia.org/ns/core#similarData", maxCard = 1, minCard = 1, ontoUsage = OntoPropery.OntologyUsage.DataIdDmp)
    private InternalLieteralImpl similarData;
    @OntoPropery(property = "http://dataid.dbpedia.org/ns/core#reuseAndIntegration", maxCard = 1, minCard = 1, ontoUsage = OntoPropery.OntologyUsage.DataIdDmp)
    private InternalLieteralImpl reuseAndIntegration;
    @OntoPropery(property = "http://dataid.dbpedia.org/ns/core#additionalSoftware", maxCard = 1, minCard = 1, ontoUsage = OntoPropery.OntologyUsage.DataIdDmp)
    private InternalLieteralImpl additionalSoftware;
    @OntoPropery(property = "http://dataid.dbpedia.org/ns/core#growth", maxCard = 1, minCard = 1, ontoUsage = OntoPropery.OntologyUsage.DataIdDmp)
    private InternalLieteralImpl growth;
    @OntoPropery(property = "http://dataid.dbpedia.org/ns/core#preservation", maxCard = 1, minCard = 1, ontoUsage = OntoPropery.OntologyUsage.DataIdDmp)
    private InternalLieteralImpl preservation;
    @OntoPropery(property = "http://dataid.dbpedia.org/ns/core#openness", maxCard = 1, minCard = 1, ontoUsage = OntoPropery.OntologyUsage.DataIdDmp)
    private InternalLieteralImpl openness;
    @OntoPropery(property = "http://dataid.dbpedia.org/ns/core#repositoryUrl", maxCard = 1, minCard = 1, ontoUsage = OntoPropery.OntologyUsage.DataIdDmp)
    private URI repositoryUrl;
    @OntoPropery(property = "http://dataid.dbpedia.org/ns/core#archiveLink", maxCard = 1, minCard = 1, ontoUsage = OntoPropery.OntologyUsage.DataIdDmp)
    private URI archiveLink;


    public InternalLieteralImpl getUsefulness() {
        return usefulness;
    }

    public void setUsefulness(InternalLieteralImpl usefulness) {
        this.usefulness = usefulness;
    }

    public InternalLieteralImpl getSimilarData() {
        return similarData;
    }

    public void setSimilarData(InternalLieteralImpl similarData) {
        this.similarData = similarData;
    }

    public InternalLieteralImpl getReuseAndIntegration() {
        return reuseAndIntegration;
    }

    public void setReuseAndIntegration(InternalLieteralImpl reuseAndIntegration) {
        this.reuseAndIntegration = reuseAndIntegration;
    }

    public InternalLieteralImpl getAdditionalSoftware() {
        return additionalSoftware;
    }

    public void setAdditionalSoftware(InternalLieteralImpl additionalSoftware) {
        this.additionalSoftware = additionalSoftware;
    }

    public InternalLieteralImpl getGrowth() {
        return growth;
    }

    public void setGrowth(InternalLieteralImpl growth) {
        this.growth = growth;
    }

    public InternalLieteralImpl getPreservation() {
        return preservation;
    }

    public void setPreservation(InternalLieteralImpl preservation) {
        this.preservation = preservation;
    }

    public InternalLieteralImpl getOpenness() {
        return openness;
    }

    public void setOpenness(InternalLieteralImpl openness) {
        this.openness = openness;
    }

    public String getRepositoryUrl() {
        return repositoryUrl != null ? repositoryUrl.stringValue() : null;
    }

    public void setRepositoryUrl(String repositoryUrl) {
        this.repositoryUrl = new URIImpl(repositoryUrl);
    }

    public String getArchiveLink() {
        return archiveLink != null ? archiveLink.stringValue() : null;
    }

    public void setArchiveLink(String archiveLink) {
        this.archiveLink = new URIImpl(archiveLink);
    }
}
