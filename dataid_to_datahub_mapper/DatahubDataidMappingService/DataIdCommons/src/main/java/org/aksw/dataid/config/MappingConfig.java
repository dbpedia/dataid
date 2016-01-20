package org.aksw.dataid.config;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.aksw.dataid.config.RdfContext;

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

    protected MappingConfig(){}
	
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

	public void setRdfContext(RdfContext rdfContext) {
		this.context = rdfContext;
	}
}
