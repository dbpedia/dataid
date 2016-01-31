package org.aksw.dataid.datahub.xmlObjects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import org.aksw.dataid.statics.StaticContent;

import java.util.List;

/**
 * Created by ciro on 31.01.16.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
@JacksonXmlRootElement(namespace=StaticContent.Re3DataNamespace, localName="re3data")
public class Re3Data {
    @JacksonXmlProperty(namespace = StaticContent.Re3DataNamespace, localName = "repository")
    private Repository repositories;

    public Repository getRepositories() {
        return repositories;
    }

    public void setRepositories(Repository repositories) {
        this.repositories = repositories;
    }
}
