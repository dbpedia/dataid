package org.aksw.dataid.ontology;

import org.aksw.dataid.wrapper.ErrorWarningWrapper;
import org.aksw.dataid.wrapper.InternalLieteralImpl;
import org.aksw.dataid.wrapper.OntoPropery;

import java.util.List;

/**
 * Created by Chile on 3/10/2015.
 */
public class DataId extends DataIdPart{

    private static InternalLieteralImpl preamble;

    @OntoPropery(property = "http://xmlns.com/foaf/0.1/topic", minCard = 1)
    private List<DataIdDataset> topic;
    @OntoPropery(property = "http://purl.org/dc/terms/modified", maxCard = 1, recommended = true)
    private InternalLieteralImpl modified;
    @OntoPropery(property = "http://purl.org/dc/terms/issued", maxCard = 1, recommended = true)
    private InternalLieteralImpl issued;
    @OntoPropery(property = "http://www.w3.org/ns/dcat#associatedAgent", minCard = 1, maxCard = 1)
    private Agent associatedAgent;

    public List<DataIdDataset> getTopic() {
        return topic;
    }

    public void setTopic(List<DataIdDataset> topic) {
        this.topic = topic;
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

    public Agent getAssociatedAgent() {
        return associatedAgent;
    }

    public void setAssociatedAgent(Agent associatedAgent) {
        this.associatedAgent = associatedAgent;
    }

    @Override  //sets preamble as comment
    public InternalLieteralImpl getComment()
    {
        return DataId.preamble;
    }

    //do this when starting the framework
    public static void setPreamble(InternalLieteralImpl preamble) {
        DataId.preamble = preamble;
    }
}
