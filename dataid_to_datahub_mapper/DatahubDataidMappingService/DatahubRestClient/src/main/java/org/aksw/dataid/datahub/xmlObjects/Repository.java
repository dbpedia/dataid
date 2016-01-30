package org.aksw.dataid.datahub.xmlObjects;

import java.io.IOException;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlText;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

@JsonIgnoreProperties(ignoreUnknown = true)
@JacksonXmlRootElement(namespace="r3d", localName="repository")
public class Repository
{

    private final String namespace = "r3d";       //TODO
    public class UrlType
    {
        private String typName;
        private String urlName;
        private String type;
        private String url;

        @JsonIgnore
        public String getTypName() {
            return typName;
        }

        @JsonIgnore
        public void setTypName(String typName) {
            this.typName = typName;
        }

        @JsonIgnore
        public String getUrlName() {
            return urlName;
        }

        @JsonIgnore
        public void setUrlName(String urlName) {
            this.urlName = urlName;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }

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
    }

    public class MetaStandard{

        @JacksonXmlProperty(namespace = namespace, localName = "metadataStandardName")
        private String metadataStandardName;
        @JacksonXmlProperty(namespace = namespace, localName = "metadataStandardScheme", isAttribute = true)
        private String metadataStandardScheme;
        @JacksonXmlProperty(namespace = namespace, localName = "metadataStandardURL")
        private String metadataStandardURL;

        public String getMetadataStandardName() {
            return metadataStandardName;
        }

        public void setMetadataStandardName(String metadataStandardName) {
            this.metadataStandardName = metadataStandardName;
        }

        public String getMetadataStandardScheme() {
            return metadataStandardScheme;
        }

        public void setMetadataStandardScheme(String metadataStandardScheme) {
            this.metadataStandardScheme = metadataStandardScheme;
        }

        public String getMetadataStandardURL() {
            return metadataStandardURL;
        }

        public void setMetadataStandardURL(String metadataStandardURL) {
            this.metadataStandardURL = metadataStandardURL;
        }
    }

    public class PublicationSupport{
        @JacksonXmlProperty(namespace = namespace, localName = "citationGuidelineURL")
        private String citationGuidelineURL;	//url
        @JacksonXmlElementWrapper(namespace = namespace, localName = "aidSystem")
        private List<String> aidSystem;			//controlled vocab
        @JacksonXmlProperty(namespace = namespace, localName = "enhancedPublication")
        private Boolean enhancedPublication;

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

    public class UrlTypeSerializer extends JsonSerializer<UrlType> {
        public void serialize(UrlType value, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonProcessingException {
            jgen.writeObjectField(value.getTypName(), value.getType());
            jgen.writeObjectField(value.getUrlName(), value.getUrl());
        }
    }

    public class UrlTypeDeSerializer extends JsonDeserializer<UrlType> {

        @Override
        public UrlType deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
            UrlType zw = new UrlType();
            ObjectCodec oc = jsonParser.getCodec();
            JsonNode node = oc.readTree(jsonParser);
            Iterator<String> iter = node.fieldNames();
            while(iter.hasNext())
            {
                String name = iter.next();
                if(name.toLowerCase().equals("name") || name.toLowerCase().equals("type"))
                {
                    zw.setType(node.get(name).asText());
                    zw.setTypName(name);
                }
                else
                {
                    zw.setUrl(node.get(name).asText());
                    zw.setUrlName(name);
                }
            }

            return zw;
        }
    }

    @JacksonXmlProperty(namespace = namespace, localName = "re3dataIdentifier")
    private String re3dataIdentifier;			//url
    @JacksonXmlProperty(namespace = namespace, localName = "repositoryName")
    private Literal repositoryName;
    @JacksonXmlElementWrapper(namespace = namespace, localName = "additionalName")
    private List<Literal> additionalName;
    @JacksonXmlProperty(namespace = namespace, localName = "repositoryUrl")
    private String repositoryUrl;
    @JacksonXmlElementWrapper(namespace = namespace, localName = "repositoryIdentifier")
    private List<String> repositoryIdentifier;  //url
    @JacksonXmlProperty(namespace = namespace, localName = "description")
    private Literal description;
    @JacksonXmlElementWrapper(namespace = namespace, localName = "repositoryContact")
    private List<String> repositoryContact;
    @JacksonXmlElementWrapper(namespace = namespace, localName = "type")
    private List<String> type;
    @JacksonXmlProperty(namespace = namespace, localName = "size")
    private String size; 						//open format seems odd
    @JacksonXmlProperty(namespace = namespace, localName = "startDate")
    private Date startDate;
    @JacksonXmlProperty(namespace = namespace, localName = "endDate")
    private Date endDate;
    @JacksonXmlElementWrapper(namespace = namespace, localName = "repositoryLanguage")
    private List<String> repositoryLanguage;	//change to own format
    @JacksonXmlElementWrapper(namespace = namespace, localName = "subject")
    private List<String> subject;				//own format?
    @JacksonXmlProperty(namespace = namespace, localName = "missionStatementURL")
    private String missionStatementURL;			//url
    @JacksonXmlElementWrapper(namespace = namespace, localName = "contentType")
    private List<String> contentType;
    @JacksonXmlProperty(namespace = namespace, localName = "isDataProvider")
    private Boolean isDataProvider;				//to providerType>dataProvider</
    @JacksonXmlProperty(namespace = namespace, localName = "isServiceProvider")
    private Boolean isServiceProvider;			//to providerType>serviceProvider</
    @JacksonXmlElementWrapper(namespace = namespace, localName = "keyword")
    private List<String> keyword;
    @JacksonXmlElementWrapper(namespace = namespace, localName = "institution")
    private List<Institution> institution;      //create Institution type

    @JsonDeserialize(using=UrlTypeDeSerializer.class)
    @JsonSerialize(using=UrlTypeSerializer.class)
    @JacksonXmlElementWrapper(namespace = namespace, localName = "policy")
    private List<UrlType> policy;				//create policy type

    @JsonDeserialize(using=UrlTypeDeSerializer.class)
    @JsonSerialize(using=UrlTypeSerializer.class)
    @JacksonXmlProperty(namespace = namespace, localName = "databaseAccess")
    private UrlType databaseAccess;	            //create access type

    @JsonDeserialize(using=UrlTypeDeSerializer.class)
    @JsonSerialize(using=UrlTypeSerializer.class)
    @JacksonXmlElementWrapper(namespace = namespace, localName = "databaseLicense")
    private List<UrlType> databaseLicense;	    //create license type

    @JsonDeserialize(using=UrlTypeDeSerializer.class)
    @JsonSerialize(using=UrlTypeSerializer.class)
    @JacksonXmlElementWrapper(namespace = namespace, localName = "dataAccess")
    private List<UrlType> dataAccess;		    //create access type

    @JsonDeserialize(using=UrlTypeDeSerializer.class)
    @JsonSerialize(using=UrlTypeSerializer.class)
    @JacksonXmlElementWrapper(namespace = namespace, localName = "dataLicense")
    private List<UrlType> dataLicense;		    //create license type

    @JsonDeserialize(using=UrlTypeDeSerializer.class)
    @JsonSerialize(using=UrlTypeSerializer.class)
    @JacksonXmlElementWrapper(namespace = namespace, localName = "dataUpload")
    private List<UrlType> dataUpload;		    //create access type

    @JsonDeserialize(using=UrlTypeDeSerializer.class)
    @JsonSerialize(using=UrlTypeSerializer.class)
    @JacksonXmlElementWrapper(namespace = namespace, localName = "dataUploadLicense")
    private List<UrlType> dataUploadLicense;    //create license type
    @JacksonXmlElementWrapper(namespace = namespace, localName = "software")
    private List<String> software;				//controlled Vocab? check 3.0
    @JacksonXmlProperty(namespace = namespace, localName = "versioning")
    private Boolean versioning;

    @JsonDeserialize(using=UrlTypeDeSerializer.class)
    @JsonSerialize(using=UrlTypeSerializer.class)
    @JacksonXmlElementWrapper(namespace = namespace, localName = "api")
    private List<UrlType> api; 					//create API type
    @JacksonXmlElementWrapper(namespace = namespace, localName = "pidSystem")
    private List<String> pidSystem;				//controlled vocab

    @JsonUnwrapped
    @JacksonXmlProperty(namespace = namespace, localName = "pubSupport")
    private PublicationSupport pubSupport; 		//create ps type
    @JacksonXmlProperty(namespace = namespace, localName = "qualityManagement")
    private Boolean qualityManagement;			//yes no unknown!
    @JacksonXmlElementWrapper(namespace = namespace, localName = "certificate")
    private List<String> certificate;			//controlled vocab
    @JacksonXmlElementWrapper(namespace = namespace, localName = "metadataStandard")
    private List<MetaStandard> metadataStandard;//create type ms

    @JsonDeserialize(using=UrlTypeDeSerializer.class)
    @JsonSerialize(using=UrlTypeSerializer.class)
    @JacksonXmlElementWrapper(namespace = namespace, localName = "syndication")
    private List<UrlType> syndication;			//check 3.0
    @JacksonXmlProperty(namespace = namespace, localName = "remarks")
    private Literal remarks;
    @JacksonXmlProperty(namespace = namespace, localName = "entryDate")
    private Date entryDate;
    @JacksonXmlProperty(namespace = namespace, localName = "lastUpdate")
    private Date lastUpdate;

    public String getNamespace() {
        return namespace;
    }

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

    public List<UrlType> getApi() {
        return api;
    }

    public void setApi(List<UrlType> api) {
        this.api = api;
    }

    public PublicationSupport getPubSupport() {
        return pubSupport;
    }

    public void setPubSupport(PublicationSupport pubSupport) {
        this.pubSupport = pubSupport;
    }

    public List<MetaStandard> getMetadataStandard() {
        return metadataStandard;
    }

    public void setMetadataStandard(List<MetaStandard> metadataStandard) {
        this.metadataStandard = metadataStandard;
    }

    public List<UrlType> getSyndication() {
        return syndication;
    }

    public void setSyndication(List<UrlType> syndication) {
        this.syndication = syndication;
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

    public String getRepositoryUrl() {
        return repositoryUrl;
    }

    public void setRepositoryUrl(String repositoryUrl) {
        this.repositoryUrl = repositoryUrl;
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

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
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

    public Boolean getDataProvider() {
        return isDataProvider;
    }

    public void setDataProvider(Boolean dataProvider) {
        isDataProvider = dataProvider;
    }

    public Boolean getServiceProvider() {
        return isServiceProvider;
    }

    public void setServiceProvider(Boolean serviceProvider) {
        isServiceProvider = serviceProvider;
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
}