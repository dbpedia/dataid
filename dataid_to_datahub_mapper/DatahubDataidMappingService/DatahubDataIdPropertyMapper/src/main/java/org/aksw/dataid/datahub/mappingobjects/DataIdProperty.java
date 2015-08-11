package org.aksw.dataid.datahub.mappingobjects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DataIdProperty
{
    public enum MappingDictionaryType{
        Dataset,
        Resource
    }

	private String dataIdRef; //dataHub property name
	private String type;
	private String comment;
	private String addedBy;
	private Date issued;
	private String dataHub;
	private String hasAlternative;
	private MappingDictionaryType dictionary = MappingDictionaryType.Dataset;
	private boolean isAlternative = false;		//indicates this property as an alternative mapping, and is therefore ignored as a standard mapping property
	private boolean isReadOnly = false; 	//if property is read-only in Datahub.io - optional in mapping.json
	private boolean isList = false;			//if property is a list on Datahub.io - optional - mapped to list element!
	private boolean additionalKey = false;  //indicates an additional key-value pair is to be used to map this data-id property at datahub.io - optional
	private boolean isReverseProp = false;	//a given dataid property is used in reverse (by adding a ^) - optional
	//if property is not part of dataset-object we need a chain like; "dc:publisher, foaf:mbox" to link foaf:mbox to dataset- optional
	private List<String> referenceChain = null;
    private String defaultValue;            //should provide a json-string which will be inserted as default value if null
	
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
	public List<String> getDataIdRefs()
	{
		String[] reffs = getDataIdRef().replace(";", ",").replace(" ", "").split(",");
		return Arrays.asList(reffs);
	}
	public String getDataIdRef() {
		return dataIdRef;
	}
	public void setDataIdRef(String id) {
		this.dataIdRef = id;
	}
	@JsonProperty("@id")
	public String getDataHub() {
		return dataHub;
	}
	@JsonProperty("@id")
	public void setDataHub(String dataHub) {
		this.dataHub = dataHub;
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
    public String getDefaultValue() { return defaultValue; }
    public void setDefaultValue(String defaultValue) {  this.defaultValue = defaultValue; }

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
	public MappingDictionaryType getDictionary() {
		return dictionary;
	}
	public void setDictionary(MappingDictionaryType dictionary) {
		this.dictionary = dictionary;
	}
	public String getHasAlternative() {
		return hasAlternative;
	}
	public void setHasAlternative(String hasAlternative) {
		this.hasAlternative = hasAlternative;
	}
	@JsonProperty("isAlternative")
	public boolean isAlternative() {
		return isAlternative;
	}
	@JsonProperty("isAlternative")
	public void setAlternative(boolean isAlternative) {
		this.isAlternative = isAlternative;
	}

    @Override
    public Object clone() {
        DataIdProperty clone = new DataIdProperty();
        clone.setAddedBy(this.getAddedBy());
        clone.setAdditionalKey(this.isAdditionalKey());
        clone.setAlternative(this.isAlternative());
        clone.setComment(this.getComment());
        clone.setDataHub(this.getDataHub());
        clone.setDataIdRef(this.dataIdRef);
        clone.setDefaultValue(this.getDefaultValue());
        clone.setDictionary(this.getDictionary());
        clone.setHasAlternative(this.getHasAlternative());
        clone.setIssued(this.getIssued());
        clone.setList(this.isList);
        clone.setReadOnly(this.isReadOnly);
        clone.setReferenceChain(this.getReferenceChain());
        clone.setReverseProp(this.isReverseProp);
        clone.setType(this.getType());

        return clone;
    }
}
