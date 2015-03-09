package aksw.dataid.datahub.mappingobjects;

import aksw.dataid.datahub.jsonobjects.Dataset;
import aksw.dataid.datahub.propertymapping.StaticHelper;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class DataId 
{
	private Map<String, String> rdfContext;
	private List<LinkedHashMap<String, Object>> dataIdBody;
	
	public Map<String, String> getRdfContext() {
		return rdfContext;
	}
	public void setRdfContext(Map<String, String> rdfContext) {
		this.rdfContext = rdfContext;
	}
	public List<LinkedHashMap<String, Object>> getDataIdBody() {
		return dataIdBody;
	}
	public void setDataIdBody(List<LinkedHashMap<String, Object>> dataIdBody) {
        this.dataIdBody = dataIdBody;
	}
}
