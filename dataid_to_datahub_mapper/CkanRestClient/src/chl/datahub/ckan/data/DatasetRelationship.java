package chl.datahub.ckan.data;

import java.util.List;

import org.codehaus.jackson.annotate.*;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DatasetRelationship 
{
	private String subject;
	private String object;
	private String comment;
	private String id;
	private String type;
	private List<DatasetRelationshipExtras> extras;
	
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
}
