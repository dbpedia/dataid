package org.aksw.dataid.datahub.jsonobjects;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DatasetRelationship implements MappingObject
{
	private String subject;
	private String object;
	private String comment;
	private String id;
	private String type;
	private List<DatasetRelationshipExtras> extras;

    private List<LinkedHashMap<String, Object>> graph = new ArrayList<LinkedHashMap<String, Object>>();
	
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	@JsonIgnore
	public String getId() {
		return id;
	}
	@JsonProperty
	public void setId(String id) {
		this.id = id;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	@JsonProperty
	public String getSubject() {
		return subject;
	}
	@JsonIgnore
	public void setSubject(String subject) {
		this.subject = subject;
	}
	@JsonProperty
	public String getObject() {
		return object;
	}
	@JsonIgnore
	public void setObject(String object) {
		this.object = object;
	}
	@JsonIgnore
	public List<DatasetRelationshipExtras> getExtras() {
		return extras;
	}
	@JsonProperty
	public void setExtras(List<DatasetRelationshipExtras> extras) {
		this.extras = extras;
	}

    @Override
    public List<LinkedHashMap<String, Object>> getGraph() {
        return graph;
    }

    @Override
    public void setGraph(List<LinkedHashMap<String, Object>> graph) {
        this.graph = graph;
    }

    @Override
    public void addChild(LinkedHashMap<String, Object> child) {
        if(!graph.contains(child)) {
            this.graph.add(child);
        }
    }
}
