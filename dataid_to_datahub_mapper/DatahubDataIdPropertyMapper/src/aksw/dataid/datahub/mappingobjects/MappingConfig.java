package aksw.dataid.datahub.mappingobjects;

import java.util.Date;
import java.util.Map;


import org.codehaus.jackson.annotate.*;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MappingConfig 
{
	private String version;
	private Date created;
	private String author;
	private Map<String,DataIdProperty> dictionary;
	
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public Date getCreated() {
		return created;
	}
	public void setCreated(Date created) {
		this.created = created;
	}
	public String getAuthor() {
		return author;
	}
	public void setAuthor(String author) {
		this.author = author;
	}
	public Map<String, DataIdProperty> getDictionary() {
		return dictionary;
	}
	public void setDictionary(Map<String, DataIdProperty> dictionary) {
		this.dictionary = dictionary;
	}
	
	public DataIdProperty GetDataIdProperty(String dataDataIdProperty)
	{
		for(String key : dictionary.keySet())
		{
			DataIdProperty prop = dictionary.get(key);
			if(prop.getId().equals(dataDataIdProperty))
			{
				return prop;
			}
		}
		return null;
	}
	
	public DataIdProperty GetDataHubProperty(String dataDataIdProperty)
	{
		for(String key : dictionary.keySet())
		{
			DataIdProperty prop = dictionary.get(key);
			if(prop.getDatahub() != null && prop.getDatahub().equals(dataDataIdProperty))
			{
				return prop;
			}
		}
		return null;
	}
}
