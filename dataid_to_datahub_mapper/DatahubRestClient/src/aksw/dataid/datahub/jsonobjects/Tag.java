package aksw.dataid.datahub.jsonobjects;

import java.util.Date;

public class Tag 
{
	private String vocabulary_id;
	private String display_name;
	private String name;
	private Date revision_timestamp;
	private String state;
	private String id;
	
	public String getVocabulary_id() {
		return vocabulary_id;
	}
	public void setVocabulary_id(String vocabulary_id) {
		this.vocabulary_id = vocabulary_id;
	}
	public String getDisplay_name() {
		return display_name;
	}
	public void setDisplay_name(String display_name) {
		this.display_name = display_name;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Date getRevision_timestamp() {
		return revision_timestamp;
	}
	public void setRevision_timestamp(Date revision_timestamp) {
		this.revision_timestamp = revision_timestamp;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
}
