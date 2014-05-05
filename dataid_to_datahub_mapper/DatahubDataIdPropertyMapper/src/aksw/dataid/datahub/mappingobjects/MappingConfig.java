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
	private Map<String, Map<String,DataIdProperty>> dataHubMapping;
	
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
	
	public DataIdProperty GetPropertyByDataId(String dictionary, String dataDataIdProperty)
	{
			for(String key : dataHubMapping.get(dictionary).keySet())
			{
				DataIdProperty prop = dataHubMapping.get(dictionary).get(key);
				if(prop.getId().equals(dataDataIdProperty))
				{
					return prop;
				}
			}
		
		return null;
	}
	
	public DataIdProperty GetPropertyByDataHub(String dictionary, String dataDataIdProperty)
	{
		DataIdProperty prop = dataHubMapping.get(dictionary).get(dataDataIdProperty);
				return prop;

	}
	public Map<String, Map<String, DataIdProperty>> getDataHubMapping() {
		return dataHubMapping;
	}
	public void setDataHubMapping(
			Map<String, Map<String, DataIdProperty>> dataHubMapping) {
		this.dataHubMapping = dataHubMapping;
	}
}
