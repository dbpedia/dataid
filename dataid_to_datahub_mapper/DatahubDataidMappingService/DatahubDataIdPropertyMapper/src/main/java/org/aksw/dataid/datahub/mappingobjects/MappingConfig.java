package org.aksw.dataid.datahub.mappingobjects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.aksw.dataid.wrapper.RdfContext;

import java.util.Date;
import java.util.Map;



@JsonIgnoreProperties(ignoreUnknown = true)
public class MappingConfig 
{
	private String version;
	private Date created;
	private String author;
	private Map<String, Map<String,DataIdProperty>> dataHubMapping;
    private RdfContext context;
	
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
				for(String ref : prop.getDataIdRefs())
				{
					//TODO proper comparison!!
					if(ref.equals(dataDataIdProperty))
					{
						return prop;
					}
				}

			}
		
		return null;
	}
	
	public DataIdProperty GetPropertyByDataHub(DataIdProperty.MappingDictionaryType dictionary, String dataDataIdProperty)
	{
		DataIdProperty prop = dataHubMapping.get(dictionary.toString().toLowerCase()).get(dataDataIdProperty);
				return prop;
	}
	public Map<String, Map<String, DataIdProperty>> getDataHubMapping() {
		return dataHubMapping;
	}
	public void setDataHubMapping(
			Map<String, Map<String, DataIdProperty>> dataHubMapping) {
		this.dataHubMapping = dataHubMapping;
	}
	public RdfContext getRdfContext() {
		return context;
	}
	public void setRdfContext(Map<String, String> rdfContext) {
		this.context = new RdfContext(rdfContext);
	}
}
