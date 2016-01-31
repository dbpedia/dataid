package org.aksw.dataid.datahub.xmlObjects;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlText;

import java.util.Date;

/**
 * Created by ciro on 31.01.16.
 */
public class Literal
{
    @JacksonXmlText
    private String label;
    @JacksonXmlProperty(isAttribute = true, localName = "language")
    private String lang;
    @JacksonXmlProperty(isAttribute = true, localName = "apiType")
    private String apiType;
    @JacksonXmlProperty(isAttribute = true, localName = "contentTypeScheme")
    private String contentTypeScheme;
    @JacksonXmlProperty(isAttribute = true, localName = "subjectScheme")
    private String subjectScheme;
    @JacksonXmlProperty(isAttribute = true, localName = "updated")
    private Date updated;
    @JacksonXmlProperty(isAttribute = true, localName = "syndicationType")
    private String syndicationType;
    @JacksonXmlProperty(isAttribute = true, localName = "metadataStandardScheme")
    private String metadataStandardScheme;

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public String getApiType() {
        return apiType;
    }

    public void setApiType(String apiType) {
        this.apiType = apiType;
    }

    public String getContentTypeScheme() {
        return contentTypeScheme;
    }

    public void setContentTypeScheme(String contentTypeScheme) {
        this.contentTypeScheme = contentTypeScheme;
    }

    public String getSubjectScheme() {
        return subjectScheme;
    }

    public void setSubjectScheme(String subjectScheme) {
        this.subjectScheme = subjectScheme;
    }

    public Date getUpdated() {
        return updated;
    }

    public void setUpdated(Date updated) {
        this.updated = updated;
    }

    public String getSyndicationType() {
        return syndicationType;
    }

    public void setSyndicationType(String syndicationType) {
        this.syndicationType = syndicationType;
    }

    public String getMetadataStandardScheme() {
        return metadataStandardScheme;
    }

    public void setMetadataStandardScheme(String metadataStandardScheme) {
        this.metadataStandardScheme = metadataStandardScheme;
    }
}