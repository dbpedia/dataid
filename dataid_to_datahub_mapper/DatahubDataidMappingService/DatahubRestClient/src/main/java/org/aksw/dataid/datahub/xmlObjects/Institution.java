package org.aksw.dataid.datahub.xmlObjects;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import java.util.Date;
import java.util.List;

/**
 * Created by ciro on 30.01.16.
 */
public class Institution {

    private final String namespace = "r3d";       //TODO

    @JacksonXmlProperty(namespace = namespace, localName = "institutionName")
    private Literal institutionName;
    @JacksonXmlElementWrapper(namespace = namespace, localName = "institutionAdditionalName", useWrapping = false)
    private List<Literal> institutionAdditionalName;
    @JacksonXmlProperty(namespace = namespace, localName = "institutionCountry")
    private String institutionCountry;                                              //url
    @JacksonXmlElementWrapper(namespace = namespace, localName = "responsibilityType", useWrapping = false)
    private List<String> responsibilityType;
    @JacksonXmlProperty(namespace = namespace, localName = "institutionType")
    private String institutionType;
    @JacksonXmlProperty(namespace = namespace, localName = "institutionURL")
    private String institutionURL;                                                  //url
    @JacksonXmlProperty(namespace = namespace, localName = "institutionIdentifier")
    private String institutionIdentifier;
    @JacksonXmlProperty(namespace = namespace, localName = "responsibilityStartDate")
    private Date responsibilityStartDate;
    @JacksonXmlProperty(namespace = namespace, localName = "responsibilityEndDate")
    private Date responsibilityEndDate;
    @JacksonXmlProperty(namespace = namespace, localName = "institutionContact")
    private String institutionContact;

    public String getNamespace() {
        return namespace;
    }

    public Literal getInstitutionName() {
        return institutionName;
    }

    public void setInstitutionName(Literal institutionName) {
        this.institutionName = institutionName;
    }

    public List<Literal> getInstitutionAdditionalName() {
        return institutionAdditionalName;
    }

    public void setInstitutionAdditionalName(List<Literal> institutionAdditionalName) {
        this.institutionAdditionalName = institutionAdditionalName;
    }

    public String getInstitutionCountry() {
        return institutionCountry;
    }

    public void setInstitutionCountry(String institutionCountry) {
        this.institutionCountry = institutionCountry;
    }

    public List<String> getResponsibilityType() {
        return responsibilityType;
    }

    public void setResponsibilityType(List<String> responsibilityType) {
        this.responsibilityType = responsibilityType;
    }

    public String getInstitutionType() {
        return institutionType;
    }

    public void setInstitutionType(String institutionType) {
        this.institutionType = institutionType;
    }

    public String getInstitutionURL() {
        return institutionURL;
    }

    public void setInstitutionURL(String institutionURL) {
        this.institutionURL = institutionURL;
    }

    public String getInstitutionIdentifier() {
        return institutionIdentifier;
    }

    public void setInstitutionIdentifier(String institutionIdentifier) {
        this.institutionIdentifier = institutionIdentifier;
    }

    public Date getResponsibilityStartDate() {
        return responsibilityStartDate;
    }

    public void setResponsibilityStartDate(Date responsibilityStartDate) {
        this.responsibilityStartDate = responsibilityStartDate;
    }

    public Date getResponsibilityEndDate() {
        return responsibilityEndDate;
    }

    public void setResponsibilityEndDate(Date responsibilityEndDate) {
        this.responsibilityEndDate = responsibilityEndDate;
    }

    public String getInstitutionContact() {
        return institutionContact;
    }

    public void setInstitutionContact(String institutionContact) {
        this.institutionContact = institutionContact;
    }
}