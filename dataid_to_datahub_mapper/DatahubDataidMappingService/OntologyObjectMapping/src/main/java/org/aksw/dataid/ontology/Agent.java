package org.aksw.dataid.ontology;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.aksw.dataid.wrapper.InternalLieteralImpl;
import org.aksw.dataid.wrapper.OntoPropery;
import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;

/**
 * Created by Chile on 3/12/2015.
 */
@JsonPropertyOrder({ "agentName", "identifier", "label", "comment", "agentRole", "agentMail", "agentUrl" })
public class Agent extends DataIdPart
{
    public enum AgentRole{
        Contact,
        Creator,
        Contributor,
        Maintainer,
        Publisher,
        Agent
    }

    @OntoPropery(property = "http://dataid.dbpedia.org/ns/core#agentName", maxCard = 1, minCard = 1)
    private InternalLieteralImpl agentName;
    @OntoPropery(property = "http://dataid.dbpedia.org/ns/core#agentMail", maxCard = 1, minCard = 1)
    private InternalLieteralImpl agentMail;
    @OntoPropery(property = "http://dataid.dbpedia.org/ns/core#agentUrl", maxCard = 1)
    private URI agentUrl;
    @OntoPropery(property = "http://dataid.dbpedia.org/ns/core#agentRole", maxCard = 1, minCard = 1)
    private AgentRole agentRole;  //special Property, will be mapped to Subclasses of dataid:Agent

    public InternalLieteralImpl getAgentName() {
        return agentName;
    }

    public void setAgentName(InternalLieteralImpl agentName) {
        this.agentName = agentName;
    }

    public InternalLieteralImpl getAgentMail() {
        return agentMail;
    }

    public void setAgentMail(InternalLieteralImpl agentMail) {
        this.agentMail = agentMail;
    }

    public String getAgentUrl() {
        if(agentUrl == null)
            return null;
        return agentUrl.stringValue();
    }

    public void setAgentUrl(String agentUrl) {
        this.agentUrl = new URIImpl(agentUrl);
    }

    public String getAgentRole() {
        return agentRole.toString();
    }

    public void setAgentRole(String agentRole) {
        this.agentRole = AgentRole.valueOf(agentRole);
    }
}
