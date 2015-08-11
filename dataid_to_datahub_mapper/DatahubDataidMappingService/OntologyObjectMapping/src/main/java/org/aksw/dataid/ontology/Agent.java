package org.aksw.dataid.ontology;

import org.aksw.dataid.wrapper.InternalLieteralImpl;
import org.aksw.dataid.wrapper.OntoPropery;
import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;

/**
 * Created by Chile on 3/12/2015.
 */
public class Agent extends DataIdPart
{
    public enum AgentRole{
        Contact,
        Creator,
        Contributor,
        Maintainer,
        Publisher
    }

    @OntoPropery(property = "http://dataid.dbpedia.org/ns/core#agentName", maxCard = 1, minCard = 1)
    private InternalLieteralImpl name;
    @OntoPropery(property = "http://dataid.dbpedia.org/ns/core#agentMail", maxCard = 1, minCard = 1)
    private InternalLieteralImpl mailAddress;
    @OntoPropery(property = "http://dataid.dbpedia.org/ns/core#agentUrl", maxCard = 1)
    private URI url;
    @OntoPropery(property = "http://dataid.dbpedia.org/ns/core#agentRole", maxCard = 1, minCard = 1)
    private AgentRole agentRole;  //special Property, will be mapped to Subclasses of dataid:Agent

    public InternalLieteralImpl getName() {
        return name;
    }

    public void setName(InternalLieteralImpl name) {
        this.name = name;
    }

    public InternalLieteralImpl getMailAddress() {
        return mailAddress;
    }

    public void setMailAddress(InternalLieteralImpl mailAddress) {
        this.mailAddress = mailAddress;
    }

    public String getUrl() {
        if(url == null)
            return null;
        return url.stringValue();
    }

    public void setUrl(String url) {
        this.url = new URIImpl(url);
    }

    public String getAgentRole() {
        return agentRole.toString();
    }

    public void setAgentRole(String agentRole) {
        this.agentRole = AgentRole.valueOf(agentRole);
    }
}
