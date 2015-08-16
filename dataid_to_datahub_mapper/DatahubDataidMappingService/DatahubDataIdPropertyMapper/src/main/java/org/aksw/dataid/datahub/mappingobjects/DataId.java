package org.aksw.dataid.datahub.mappingobjects;

import org.aksw.dataid.wrapper.RdfContext;

import java.util.LinkedHashMap;
import java.util.List;

public class DataId
{

	private RdfContext rdfContext;
	private DataIdBody dataIdBody;
	
	public RdfContext getRdfContext() {
		return rdfContext;
	}
	public void setRdfContext(RdfContext rdfContext) {
		this.rdfContext = rdfContext;
	}
	public DataIdBody getDataIdBody() {
		return dataIdBody;
	}
	public void setDataIdBody(List<LinkedHashMap<String, Object>> body) {
        this.dataIdBody = new DataIdBody(body, rdfContext);
	}

}
