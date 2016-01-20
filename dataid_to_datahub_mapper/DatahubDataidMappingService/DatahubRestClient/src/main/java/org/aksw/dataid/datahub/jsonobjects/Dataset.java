package org.aksw.dataid.datahub.jsonobjects;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Dataset implements ValidCkanResponse, MappingObject
{
    private String id;                  //ckan id -> no mapping
    private String name;                //ckan name (without spaces, lowercase)  -> no mapping
    private String title;               //dc:title
    private String revision_id;         //ckan internal -> no mapping
    private String maintainer;          //name of maintainer or contact -> mapping to maintainer role
    private String maintainer_email;    //mail of maintainer (replaceable by url e.g. to contact formular on dataid)
    private String license_id;          //id of ckan internal license id  //TODO mapping for this?
    private String license;             //dc:rights
    private String license_title;       //name of license, get it from virtuoso
    private String license_url;         //dc:license
    private Date metadata_created;      //internal/automated -> no mapping
    private Date metadata_modified;     //internal/automated -> no mapping
    private String author;              //name of publisher or creator -> mapping to creator role
    private String author_email;        //mail of creator (replaceable by url e.g. to contact formular on dataid)
    private String state;               //no equivalent
    private String version;             //dataid:version
    private String type;                //should be "dataset"
    private String notes;               //dc:description
    private Integer triples;            //void:triples
    private boolean isopen;
    private List<Tag> tags;             //dcat:keyword (no spaces)
    private List<Resource> resources;   //dact:distribution
    private String url;                 //dcat:landingPage
    private List<DatasetExtras> extras; //list of additional properties
    private List<DatasetRelationship> relationships_as_object;
    private List<DatasetRelationship> relationships_as_subject;
    private boolean isPrivate;          //make public or not
    private String owner_org;           //id of organisation
    private Integer num_resources;      //internal
    private Integer num_tags;           //internal

    //internal use!
    private boolean isUpdate = false;   //create or update
    private List<String> subsets = new ArrayList<>();
    private String dataIdUri;           //@id -> the uri of the dataset
    private List<LinkedHashMap<String, Object>> graph = new ArrayList<LinkedHashMap<String, Object>>();

    public Dataset()
    {
        //common values
        this.isopen = true;

        //TODO switch back to false when done with debugging!
        this.isPrivate = true;
        this.state = "active";
        this.type = "dataset";

        extras = new ArrayList<DatasetExtras>();
        tags = new ArrayList<Tag>();
        resources = new ArrayList<Resource>();
        relationships_as_object = new ArrayList<DatasetRelationship>();
        relationships_as_subject = new ArrayList<DatasetRelationship>();
    }

    public List<DatasetRelationship> getRelationships_as_object() {
        return relationships_as_object;
    }

    public void setRelationships_as_object(
            List<DatasetRelationship> relationships_as_object) {
        this.relationships_as_object = relationships_as_object;
    }

    public List<DatasetRelationship> getRelationships_as_subject() {
        return relationships_as_subject;
    }

    public void setRelationships_as_subject(
            List<DatasetRelationship> relationships_as_subject) {
        this.relationships_as_subject = relationships_as_subject;
    }

    public boolean isPrivate() {
        return isPrivate;
    }

    public void setPrivate(boolean isPrivate) {
        this.isPrivate = isPrivate;
    }

    public String getOwner_org() {
        return owner_org;
    }

    public void setOwner_org(String owner_org) {
        this.owner_org = owner_org;
    }

    public Integer getNum_resources() {
        return num_resources;
    }

    public void setNum_resources(Integer num_resources) {
        this.num_resources = num_resources;
    }

    public Integer getNum_tags() {
        return num_tags;
    }

    public void setNum_tags(Integer num_tags) {
        this.num_tags = num_tags;
    }

    public List<DatasetExtras> getExtras() {
        return extras;
    }

    public void setExtras(List<DatasetExtras> extras) {
        this.extras = extras;
    }

    public void setId(String id) { this.id = id;  }

    public String getId() {
        return id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name.trim().toLowerCase().replaceAll("\\W", "_");
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setRevision_id(String revision_id) {
        this.revision_id = revision_id;
    }

    public String getRevision_id() {
        return revision_id;
    }

    public void setMaintainer(String maintainer) {
        this.maintainer = maintainer;
    }

    public String getMaintainer() {
        return maintainer;
    }

    public void setMaintainer_email(String maintainer_email) {
        this.maintainer_email = maintainer_email;
    }

    public String getMaintainer_email() {
        return maintainer_email;
    }

    public void setLicense_id(String license_id) {
        this.license_id = license_id;
    }

    public String getLicense_id() {
        return license_id;
    }

    public void setLicense(String license) {
        this.license = license;
    }

    public String getLicense() {
        return license;
    }

    public void setLicense_title(String license_title) {
        this.license_title = license_title;
    }

    public String getLicense_title() {
        return license_title;
    }

    public void setLicense_url(String license_url) {
        this.license_url = license_url;
    }

    public String getLicense_url() {
        return license_url;
    }

    public void setMetadata_created(Date metadata_created) {
        this.metadata_created = metadata_created;
    }

    public Date getMetadata_created() {
        return metadata_created;
    }

    public void setMetadata_modified(Date metadata_modified) {
        this.metadata_modified = metadata_modified;
    }

    public Date getMetadata_modified() {
        return metadata_modified;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor_email(String author_email) {
        this.author_email = author_email;
    }

    public String getAuthor_email() {
        return author_email;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getState() {
        return state;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getVersion() {
        return version;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getNotes() {
        return notes;
    }

    public void setIsopen(boolean isopen) {
        this.isopen = isopen;
    }

    public boolean isIsopen() {
        return isopen;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }

    public List<Tag> getTags() {
        return tags;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public List<Resource> getResources() {
        return resources;
    }

    public void setResources( List<Resource> resources ) {
        this.resources = resources;
    }

    @JsonIgnore
    public String getDatahubUrl() {return "http://datahub.io/dataset/" + getName(); }

    @JsonIgnore
    public List<String> getSubsets() {
        return subsets;
    }

    @JsonIgnore
    public void setSubsets(List<String> subsets) {
        this.subsets = subsets;
    }

    @JsonIgnore
    public Integer getTriples() {
        return triples;
    }

    @JsonIgnore
    public void setTriples(Integer triples) {
        this.triples = triples;
    }

    @JsonIgnore
    public String getDataIdUri() {
        return dataIdUri;
    }

    @JsonIgnore
    public boolean isUpdate() {
        return isUpdate;
    }

    @JsonIgnore
    public void setUpdate(boolean isUpdate) {
        this.isUpdate = isUpdate;
    }

    @JsonIgnore
    public void setDataIdUri(String dataIdUri) {
        this.dataIdUri = dataIdUri;
    }
    @Override
    public String toString() {
        return "<Dataset:" + this.getName() + " ," + this.getTitle() + "," + this.getAuthor() + ", " + this.getUrl() + ">";
    }

    @JsonIgnore
    @Override
    public List<LinkedHashMap<String, Object>> getGraph() {
        return graph;
    }

    @JsonIgnore
    @Override
    public void setGraph(List<LinkedHashMap<String, Object>> graph) {
        this.graph = graph;
    }

    @JsonIgnore
    @Override
    public void addChild(LinkedHashMap<String, Object> child) {
        if(!graph.contains(child)) {
            this.graph.add(child);
        }
    }
}
