package org.aksw.dataid.datahub.xmlObjects;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import org.aksw.dataid.statics.StaticContent;

/**
 * Created by ciro on 31.01.16.
 */
public class MetaStandard{

    @JacksonXmlProperty(namespace = StaticContent.Re3DataNamespace, localName = "metadataStandardName")
    private Literal metadataStandardName;
    @JacksonXmlProperty(namespace = StaticContent.Re3DataNamespace, localName = "metadataStandardURL")
    private String metadataStandardURL;

    public Literal getMetadataStandardName() {
        return metadataStandardName;
    }

    public void setMetadataStandardName(Literal metadataStandardName) {
        this.metadataStandardName = metadataStandardName;
    }

    public String getMetadataStandardURL() {
        return metadataStandardURL;
    }

    public void setMetadataStandardURL(String metadataStandardURL) {
        this.metadataStandardURL = metadataStandardURL;
    }
}
