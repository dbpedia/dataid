package org.aksw.dataid.datahub.jsonobjects;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Resource  implements DataHubListObject, MappingObject
{
    private String id;                  //datahub id -> no mapping
    private String resource_group_id;   //datahub internal -> no mapping
    private String name;                //dc:title
    private String description;         //dc:description
    private Date created;               //dc:issued
    private String url;                 //dcat:downloadURL / dcat:accessURL
    private Integer size;               //dcat:byteSize
    private Integer position;           //internal -> position in resource lost //TODO remember to put DataId link first
    private Date last_modified;         //dc:modified
    private String hash;                //dataid:checksum / dataid:md5
    private String format;              //dc:format  //TODO needs additional work -> http://docs.ckan.org/en/ckan-1.7.2/domain-model-resource.html
    private String mimetype;            //dcat:mimeType
    private String mimetype_inner;      //dc:format
    private String state;               //no equivalent

    //internal use!
    private String resourceUri;         //@id
    private String distributionType;    //@type
    private List<LinkedHashMap<String, Object>> graph = new ArrayList<LinkedHashMap<String, Object>>();

	public Resource() 
	{
        size = 0;
		state = "active";
	}
	
    public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

    public String getResource_group_id() {
        return resource_group_id;
    }

    public void setResource_group_id(String resource_group_id) {
        this.resource_group_id = resource_group_id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Date getCreated() {
        return created;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public int getSize() {
        return size;
    }

    public int getPosition() {
        return position;
    }

    /**
     * type of the resource by ckan
     * @return ckan resource type
     */
    public String getResource_type() {
        if(distributionType.equals("dataid:SingleFile") || distributionType.equals("dataid:Directory"))
            return "file";
        else
            return "api";
    }

    public void setLast_modified(Date last_modified) {
        this.last_modified = last_modified;
    }

    public Date getLast_modified() {
        return last_modified;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getHash() {
        return hash;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getFormat() {
        return format;
    }

    public void setMimetype(String mimetype) {
        this.mimetype = mimetype;
    }

    public String getMimetype() {
        return mimetype;
    }

    public void setMimetype_inner(String mimetype_inner) {
        this.mimetype_inner = mimetype_inner;
    }

    public String getMimetype_inner() {
        return mimetype_inner;
    }

	public void setSize(Integer size) {
		this.size = size;
	}

	public void setPosition(Integer position) {
		this.position = position;
	}

    @JsonIgnore
    public String getDistributionType() {
        return distributionType;
    }

    @JsonIgnore
    public void setDistributionType(String distributionType) {
        this.distributionType = distributionType;
    }

    @JsonIgnore
    public String getResourceUri() {
        return resourceUri;
    }

    @JsonIgnore
    public void setResourceUri(String resourceUri) {
        this.resourceUri = resourceUri;
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
