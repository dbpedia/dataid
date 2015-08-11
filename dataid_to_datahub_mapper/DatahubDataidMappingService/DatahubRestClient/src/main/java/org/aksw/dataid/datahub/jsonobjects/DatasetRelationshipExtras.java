package org.aksw.dataid.datahub.jsonobjects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Date;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DatasetRelationshipExtras 
{
	private Date revision_timestamp;
	private String subject_package_id;
	private String object_package_id;
	private String revision_id;
	
	public String getRevision_id() {
		return revision_id;
	}
	public void setRevision_id(String revision_id) {
		this.revision_id = revision_id;
	}
	public Date getRevision_timestamp() {
		return revision_timestamp;
	}
	public void setRevision_timestamp(Date revision_timestamp) {
		this.revision_timestamp = revision_timestamp;
	}
	public String getSubject_package_id() {
		return subject_package_id;
	}
	public void setSubject_package_id(String subject_package_id) {
		this.subject_package_id = subject_package_id;
	}
	public String getObject_package_id() {
		return object_package_id;
	}
	public void setObject_package_id(String object_package_id) {
		this.object_package_id = object_package_id;
	}
}
