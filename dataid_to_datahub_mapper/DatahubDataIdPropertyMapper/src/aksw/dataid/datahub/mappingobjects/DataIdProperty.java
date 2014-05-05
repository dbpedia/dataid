package aksw.dataid.datahub.mappingobjects;

import java.util.Date;
import java.util.List;

import org.codehaus.jackson.annotate.*;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DataIdProperty 
{
	private String id;
	private String type;
	private String comment;
	private String addedBy;
	private Date issued;
	private String dataHub;
	private boolean isReadOnly = false; 	//if property is read-only in Datahub.io - optional in mapping.json
	private boolean isList = false;			//if property is a list on Datahub.io - optional - mapped to list element!
	private boolean additionalKey = false;  //indicates an additional key-value pair is to be used to map this data-id property at datahub.io - optional
	private boolean isReverseProp = false;	//a given dataid property is used in reverse (by adding a ^) - optional
	//if property is not part of dataset-object we need a chain like; "dc:publisher, foaf:mbox" to link foaf:mbox to dataset- optional
	private List<String> referenceChain = null; 
	
	public List<String> getReferenceChain() {
		return referenceChain;
	}
	public void setReferenceChain(List<String> referenceChain) {
		this.referenceChain = referenceChain;
	}
	@JsonProperty("isList")
	public boolean isList() {
		return isList;
	}
	@JsonProperty("isList")
	public void setList(boolean isList) {
		this.isList = isList;
	}
	public boolean isReadOnly() {
		return isReadOnly;
	}
	public void setReadOnly(boolean readOnly) {
		this.isReadOnly = readOnly;
	}
	@JsonProperty("@id")
	public String getId() {
		return id;
	}
	@JsonProperty("@id")
	public void setId(String id) {
		this.id = id;
	}
	@JsonProperty("@type")
	public String getType() {
		return type;
	}
	@JsonProperty("@type")
	public void setType(String type) {
		this.type = type;
	}
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	public String getAddedBy() {
		return addedBy;
	}
	public void setAddedBy(String addedBy) {
		this.addedBy = addedBy;
	}
	public Date getIssued() {
		return issued;
	}
	public void setIssued(Date issued) {
		this.issued = issued;
	}

	public boolean isAdditionalKey() {
		return additionalKey;
	}
	public void setAdditionalKey(boolean additionalKey) {
		this.additionalKey = additionalKey;
	}
	public boolean isReverseProp() {
		return isReverseProp;
	}
	public void setReverseProp(boolean isReverseProp) {
		this.isReverseProp = isReverseProp;
	}
	public String getDataHub() {
		return dataHub;
	}
	public void setDataHub(String dataHub) {
		this.dataHub = dataHub;
	}
}
