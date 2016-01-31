package org.aksw.dataid.datahub.xmlObjects;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import org.aksw.dataid.statics.StaticContent;

/**
 * Created by ciro on 31.01.16.
 */
public class UrlType
{
    @JacksonXmlProperty(namespace = StaticContent.Re3DataNamespace, localName = "policyName")
    private String policyName;
    @JacksonXmlProperty(namespace = StaticContent.Re3DataNamespace, localName = "policyURL")
    private String policyUrl;
    @JacksonXmlProperty(namespace = StaticContent.Re3DataNamespace, localName = "databaseAccessType")
    private String databaseAccessType;
    @JacksonXmlProperty(namespace = StaticContent.Re3DataNamespace, localName = "databaseAccessRestriction")
    private String databaseAccessRestriction;
    @JacksonXmlProperty(namespace = StaticContent.Re3DataNamespace, localName = "databaseLicenseName")
    private String databaseLicenseName;
    @JacksonXmlProperty(namespace = StaticContent.Re3DataNamespace, localName = "databaseLicenseURL")
    private String databaseLicenseURL;
    @JacksonXmlProperty(namespace = StaticContent.Re3DataNamespace, localName = "dataAccessType")
    private String dataAccessType;
    @JacksonXmlProperty(namespace = StaticContent.Re3DataNamespace, localName = "dataAccessRestriction")
    private String dataAccessRestriction;
    @JacksonXmlProperty(namespace = StaticContent.Re3DataNamespace, localName = "dataLicenseName")
    private String dataLicenseName;
    @JacksonXmlProperty(namespace = StaticContent.Re3DataNamespace, localName = "dataLicenseURL")
    private String dataLicenseURL;
    @JacksonXmlProperty(namespace = StaticContent.Re3DataNamespace, localName = "dataUploadType")
    private String dataUploadType;
    @JacksonXmlProperty(namespace = StaticContent.Re3DataNamespace, localName = "dataUploadRestriction")
    private String dataUploadRestriction;
    @JacksonXmlProperty(namespace = StaticContent.Re3DataNamespace, localName = "dataUploadLicenseName")
    private String dataUploadLicenseName;
    @JacksonXmlProperty(namespace = StaticContent.Re3DataNamespace, localName = "dataUploadLicenseURL")
    private String dataUploadLicenseURL;

    public String getPolicyName() {
        return policyName;
    }

    public void setPolicyName(String policyName) {
        this.policyName = policyName;
    }

    public String getPolicyUrl() {
        return policyUrl;
    }

    public void setPolicyUrl(String policyUrl) {
        this.policyUrl = policyUrl;
    }

    public String getDatabaseAccessType() {
        return databaseAccessType;
    }

    public void setDatabaseAccessType(String databaseAccessType) {
        this.databaseAccessType = databaseAccessType;
    }

    public String getDatabaseAccessRestriction() {
        return databaseAccessRestriction;
    }

    public void setDatabaseAccessRestriction(String databaseAccessRestriction) {
        this.databaseAccessRestriction = databaseAccessRestriction;
    }

    public String getDatabaseLicenseName() {
        return databaseLicenseName;
    }

    public void setDatabaseLicenseName(String databaseLicenseName) {
        this.databaseLicenseName = databaseLicenseName;
    }

    public String getDatabaseLicenseURL() {
        return databaseLicenseURL;
    }

    public void setDatabaseLicenseURL(String databaseLicenseURL) {
        this.databaseLicenseURL = databaseLicenseURL;
    }

    public String getDataAccessType() {
        return dataAccessType;
    }

    public void setDataAccessType(String dataAccessType) {
        this.dataAccessType = dataAccessType;
    }

    public String getDataAccessRestriction() {
        return dataAccessRestriction;
    }

    public void setDataAccessRestriction(String dataAccessRestriction) {
        this.dataAccessRestriction = dataAccessRestriction;
    }

    public String getDataLicenseName() {
        return dataLicenseName;
    }

    public void setDataLicenseName(String dataLicenseName) {
        this.dataLicenseName = dataLicenseName;
    }

    public String getDataLicenseURL() {
        return dataLicenseURL;
    }

    public void setDataLicenseURL(String dataLicenseURL) {
        this.dataLicenseURL = dataLicenseURL;
    }

    public String getDataUploadType() {
        return dataUploadType;
    }

    public void setDataUploadType(String dataUploadType) {
        this.dataUploadType = dataUploadType;
    }

    public String getDataUploadRestriction() {
        return dataUploadRestriction;
    }

    public void setDataUploadRestriction(String dataUploadRestriction) {
        this.dataUploadRestriction = dataUploadRestriction;
    }

    public String getDataUploadLicenseName() {
        return dataUploadLicenseName;
    }

    public void setDataUploadLicenseName(String dataUploadLicenseName) {
        this.dataUploadLicenseName = dataUploadLicenseName;
    }

    public String getDataUploadLicenseURL() {
        return dataUploadLicenseURL;
    }

    public void setDataUploadLicenseURL(String dataUploadLicenseURL) {
        this.dataUploadLicenseURL = dataUploadLicenseURL;
    }
}
