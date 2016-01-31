package org.aksw.dataid.datahub.xmlObjects;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import org.aksw.dataid.statics.StaticContent;
@JsonIgnoreProperties(ignoreUnknown = true)
public class Repository
{
    @JacksonXmlProperty(namespace = StaticContent.Re3DataNamespace, localName = "re3dataorgIdentifier")
    private String re3dataIdentifier;			//url
    @JacksonXmlProperty(namespace = StaticContent.Re3DataNamespace, localName = "repositoryName")
    private Literal repositoryName;
    @JacksonXmlElementWrapper(namespace = StaticContent.Re3DataNamespace, localName = "additionalName", useWrapping = false)
    private List<Literal> additionalName;
    @JacksonXmlProperty(namespace = StaticContent.Re3DataNamespace, localName = "repositoryURL")
    private String repositoryURL;
    @JacksonXmlElementWrapper(namespace = StaticContent.Re3DataNamespace, localName = "repositoryIdentifier", useWrapping = false)
    private List<String> repositoryIdentifier;  //url
    @JacksonXmlProperty(namespace = StaticContent.Re3DataNamespace, localName = "description")
    private Literal description;
    @JacksonXmlElementWrapper(namespace = StaticContent.Re3DataNamespace, localName = "repositoryContact", useWrapping = false)
    private List<String> repositoryContact;
    @JacksonXmlElementWrapper(namespace = StaticContent.Re3DataNamespace, localName = "type", useWrapping = false)
    private List<String> type;
    @JacksonXmlProperty(namespace = StaticContent.Re3DataNamespace, localName = "size")
    private Literal size; 						//open format seems odd
    @JacksonXmlProperty(namespace = StaticContent.Re3DataNamespace, localName = "startDate")
    private Date startDate;
    @JacksonXmlProperty(namespace = StaticContent.Re3DataNamespace, localName = "endDate")
    private Date endDate;
    @JacksonXmlElementWrapper(namespace = StaticContent.Re3DataNamespace, localName = "repositoryLanguage", useWrapping = false)
    private List<String> repositoryLanguage;	//change to own format
    @JacksonXmlElementWrapper(namespace = StaticContent.Re3DataNamespace, localName = "subject", useWrapping = false)
    private List<String> subject;				//own format?
    @JacksonXmlProperty(namespace = StaticContent.Re3DataNamespace, localName = "missionStatementURL")
    private String missionStatementURL;			//url
    @JacksonXmlElementWrapper(namespace = StaticContent.Re3DataNamespace, localName = "contentType", useWrapping = false)
    private List<String> contentType;
    @JacksonXmlElementWrapper(namespace = StaticContent.Re3DataNamespace, localName = "providerType", useWrapping = false)
    private List<String> providerType;				//to providerType>dataProvider</
    @JacksonXmlElementWrapper(namespace = StaticContent.Re3DataNamespace, localName = "keyword", useWrapping = false)
    private List<String> keyword;
    @JacksonXmlElementWrapper(namespace = StaticContent.Re3DataNamespace, localName = "institution", useWrapping = false)
    private List<Institution> institution;      //create Institution type

    @JacksonXmlElementWrapper(namespace = StaticContent.Re3DataNamespace, localName = "policy", useWrapping = false)
    private List<UrlType> policy;				//create policy type

    @JacksonXmlProperty(namespace = StaticContent.Re3DataNamespace, localName = "databaseAccess")
    private UrlType databaseAccess;	            //create access type

    @JacksonXmlElementWrapper(namespace = StaticContent.Re3DataNamespace, localName = "databaseLicense", useWrapping = false)
    private List<UrlType> databaseLicense;	    //create license type

    @JacksonXmlElementWrapper(namespace = StaticContent.Re3DataNamespace, localName = "dataAccess", useWrapping = false)
    private List<UrlType> dataAccess;		    //create access type

    @JacksonXmlElementWrapper(namespace = StaticContent.Re3DataNamespace, localName = "dataLicense", useWrapping = false)
    private List<UrlType> dataLicense;		    //create license type

    @JacksonXmlElementWrapper(namespace = StaticContent.Re3DataNamespace, localName = "dataUpload", useWrapping = false)
    private List<UrlType> dataUpload;		    //create access type

    @JacksonXmlElementWrapper(namespace = StaticContent.Re3DataNamespace, localName = "dataUploadLicense", useWrapping = false)
    private List<UrlType> dataUploadLicense;    //create license type
    @JsonUnwrapped(prefix = StaticContent.Re3DataNamespace, suffix = "softwareName")
    @JacksonXmlElementWrapper(namespace = StaticContent.Re3DataNamespace, localName = "software")
    private List<String> software;				//controlled Vocab? check 3.0

    @JsonDeserialize(using = YesNoDeSerializer.class)
    @JsonSerialize(using = YesNoSerializer.class)
    @JacksonXmlProperty(namespace = StaticContent.Re3DataNamespace, localName = "versioning")
    private Boolean versioning;

    @JacksonXmlElementWrapper(namespace = StaticContent.Re3DataNamespace, localName = "api", useWrapping = false)
    private List<Literal> api; 					//create API type
    @JacksonXmlElementWrapper(namespace = StaticContent.Re3DataNamespace, localName = "pidSystem", useWrapping = false)
    private List<String> pidSystem;				//controlled vocab

    @JacksonXmlProperty(namespace = StaticContent.Re3DataNamespace, localName = "citationGuidelineURL")
    private String citationGuidelineURL;	//url
    @JacksonXmlElementWrapper(namespace = StaticContent.Re3DataNamespace, localName = "aidSystem", useWrapping = false)
    private List<String> aidSystem;			//controlled vocab

    @JsonDeserialize(using = YesNoDeSerializer.class)
    @JsonSerialize(using = YesNoSerializer.class)
    @JacksonXmlProperty(namespace = StaticContent.Re3DataNamespace, localName = "enhancedPublication")
    private Boolean enhancedPublication;

    @JsonDeserialize(using = YesNoDeSerializer.class)
    @JsonSerialize(using = YesNoSerializer.class)
    @JacksonXmlProperty(namespace = StaticContent.Re3DataNamespace, localName = "qualityManagement")
    private Boolean qualityManagement;			//yes no unknown!
    @JacksonXmlElementWrapper(namespace = StaticContent.Re3DataNamespace, localName = "certificate", useWrapping = false)
    private List<String> certificate;			//controlled vocab
    @JacksonXmlElementWrapper(namespace = StaticContent.Re3DataNamespace, localName = "metadataStandard", useWrapping = false)
    private List<MetaStandard> metadataStandard;//create type ms

    @JacksonXmlElementWrapper(namespace = StaticContent.Re3DataNamespace, localName = "syndication", useWrapping = false)
    private List<Literal> syndication;			//check 3.0
    @JacksonXmlProperty(namespace = StaticContent.Re3DataNamespace, localName = "remarks")
    private Literal remarks;
    @JacksonXmlProperty(namespace = StaticContent.Re3DataNamespace, localName = "entryDate")
    private Date entryDate;
    @JacksonXmlProperty(namespace = StaticContent.Re3DataNamespace, localName = "lastUpdate")
    private Date lastUpdate;

    public List<UrlType> getPolicy() {
        return policy;
    }

    public void setPolicy(List<UrlType> policy) {
        this.policy = policy;
    }

    public UrlType getDatabaseAccess() {
        return databaseAccess;
    }

    public void setDatabaseAccess(UrlType databaseAccess) {
        this.databaseAccess = databaseAccess;
    }

    public List<UrlType> getDatabaseLicense() {
        return databaseLicense;
    }

    public void setDatabaseLicense(List<UrlType> databaseLicense) {
        this.databaseLicense = databaseLicense;
    }

    public List<UrlType> getDataAccess() {
        return dataAccess;
    }

    public void setDataAccess(List<UrlType> dataAccess) {
        this.dataAccess = dataAccess;
    }

    public List<UrlType> getDataLicense() {
        return dataLicense;
    }

    public void setDataLicense(List<UrlType> dataLicense) {
        this.dataLicense = dataLicense;
    }

    public List<UrlType> getDataUpload() {
        return dataUpload;
    }

    public void setDataUpload(List<UrlType> dataUpload) {
        this.dataUpload = dataUpload;
    }

    public List<UrlType> getDataUploadLicense() {
        return dataUploadLicense;
    }

    public void setDataUploadLicense(List<UrlType> dataUploadLicense) {
        this.dataUploadLicense = dataUploadLicense;
    }

    public List<MetaStandard> getMetadataStandard() {
        return metadataStandard;
    }

    public void setMetadataStandard(List<MetaStandard> metadataStandard) {
        this.metadataStandard = metadataStandard;
    }

    public List<Institution> getInstitution() {
        return institution;
    }

    public void setInstitution(List<Institution> institution) {
        this.institution = institution;
    }

    public String getRe3dataIdentifier() {
        return re3dataIdentifier;
    }

    public void setRe3dataIdentifier(String re3dataIdentifier) {
        this.re3dataIdentifier = re3dataIdentifier;
    }

    public Literal getRepositoryName() {
        return repositoryName;
    }

    public void setRepositoryName(Literal repositoryName) {
        this.repositoryName = repositoryName;
    }

    public List<Literal> getAdditionalName() {
        return additionalName;
    }

    public void setAdditionalName(List<Literal> additionalName) {
        this.additionalName = additionalName;
    }

    public String getRepositoryURL() {
        return repositoryURL;
    }

    public void setRepositoryURL(String repositoryUrl) {
        this.repositoryURL = repositoryUrl;
    }

    public List<String> getRepositoryIdentifier() {
        return repositoryIdentifier;
    }

    public void setRepositoryIdentifier(List<String> repositoryIdentifier) {
        this.repositoryIdentifier = repositoryIdentifier;
    }

    public Literal getDescription() {
        return description;
    }

    public void setDescription(Literal description) {
        this.description = description;
    }

    public List<String> getRepositoryContact() {
        return repositoryContact;
    }

    public void setRepositoryContact(List<String> repositoryContact) {
        this.repositoryContact = repositoryContact;
    }

    public List<String> getType() {
        return type;
    }

    public void setType(List<String> type) {
        this.type = type;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public List<String> getRepositoryLanguage() {
        return repositoryLanguage;
    }

    public void setRepositoryLanguage(List<String> repositoryLanguage) {
        this.repositoryLanguage = repositoryLanguage;
    }

    public List<String> getSubject() {
        return subject;
    }

    public void setSubject(List<String> subject) {
        this.subject = subject;
    }

    public String getMissionStatementURL() {
        return missionStatementURL;
    }

    public void setMissionStatementURL(String missionStatementURL) {
        this.missionStatementURL = missionStatementURL;
    }

    public List<String> getContentType() {
        return contentType;
    }

    public void setContentType(List<String> contentType) {
        this.contentType = contentType;
    }

    public List<String> getKeyword() {
        return keyword;
    }

    public void setKeyword(List<String> keyword) {
        this.keyword = keyword;
    }

    public List<String> getSoftware() {
        return software;
    }

    public void setSoftware(List<String> software) {
        this.software = software;
    }

    public Boolean getVersioning() {
        return versioning;
    }

    public void setVersioning(Boolean versioning) {
        this.versioning = versioning;
    }

    public List<String> getPidSystem() {
        return pidSystem;
    }

    public void setPidSystem(List<String> pidSystem) {
        this.pidSystem = pidSystem;
    }

    public Boolean getQualityManagement() {
        return qualityManagement;
    }

    public void setQualityManagement(Boolean qualityManagement) {
        this.qualityManagement = qualityManagement;
    }

    public List<String> getCertificate() {
        return certificate;
    }

    public void setCertificate(List<String> certificate) {
        this.certificate = certificate;
    }

    public Literal getRemarks() {
        return remarks;
    }

    public void setRemarks(Literal remarks) {
        this.remarks = remarks;
    }

    public Date getEntryDate() {
        return entryDate;
    }

    public void setEntryDate(Date entryDate) {
        this.entryDate = entryDate;
    }

    public Date getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public void setSize(Literal size) {
        this.size = size;
    }

    public List<Literal> getApi() {
        return api;
    }

    public void setApi(List<Literal> api) {
        this.api = api;
    }

    public List<Literal> getSyndication() {
        return syndication;
    }

    public void setSyndication(List<Literal> syndication) {
        this.syndication = syndication;
    }

    public Literal getSize() {
        return size;
    }

    public List<String> getProviderType() {
        return providerType;
    }

    public void setProviderType(List<String> providerType) {
        this.providerType = providerType;
    }

    public String getCitationGuidelineURL() {
        return citationGuidelineURL;
    }

    public void setCitationGuidelineURL(String citationGuidelineURL) {
        this.citationGuidelineURL = citationGuidelineURL;
    }

    public List<String> getAidSystem() {
        return aidSystem;
    }

    public void setAidSystem(List<String> aidSystem) {
        this.aidSystem = aidSystem;
    }

    public Boolean getEnhancedPublication() {
        return enhancedPublication;
    }

    public void setEnhancedPublication(Boolean enhancedPublication) {
        this.enhancedPublication = enhancedPublication;
    }
}