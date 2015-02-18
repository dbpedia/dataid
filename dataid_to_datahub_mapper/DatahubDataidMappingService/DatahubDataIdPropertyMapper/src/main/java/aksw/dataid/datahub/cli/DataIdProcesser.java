package aksw.dataid.datahub.cli;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import aksw.dataid.datahub.jsonutils.RdfXmlParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.core.JsonParseException;
import com.github.jsonldjava.core.JsonLdError;
import com.github.jsonldjava.core.JsonLdOptions;
import com.github.jsonldjava.core.JsonLdProcessor;
import com.github.jsonldjava.utils.JsonUtils;

import aksw.dataid.datahub.jsonobjects.Dataset;
import aksw.dataid.datahub.jsonutils.StaticJsonHelper;
import aksw.dataid.datahub.mappingobjects.DataId;
import aksw.dataid.datahub.propertymapping.DataHubMappingException;
import aksw.dataid.datahub.propertymapping.DataIdInputException;
import aksw.dataid.datahub.propertymapping.PropertyMapper;

public class DataIdProcesser 
{
	private JsonNode mappingConfig;
	private PropertyMapper mapper;
	
	public DataIdProcesser(String mappingConfigPath) throws DataHubMappingException, IOException {
		mappingConfig = StaticJsonHelper.getJsonContent(mappingConfigPath);
	    if(mappingConfig == null)
            throw new DataHubMappingException("the mapping-config file could not be found");
        mapper = new PropertyMapper(mappingConfig);
	}
	
	private List<LinkedHashMap> getDataIdFromJson(String sourceId, JsonLdOptions opt) throws JsonLdError, JsonParseException, IOException
	{
		Object result =null;
		result = JsonUtils.fromString(sourceId);
		result = JsonLdProcessor.compact(result, mapper.getJsonLdContext(), opt);
		return (List<LinkedHashMap>) ((Map<String, Object>) result).get("@graph");
	}
		
	private DataId getDataId(String sourceId, JsonLdOptions opt) throws JsonLdError
	{
		Object result =null;
		result = JsonLdProcessor.fromRDF(sourceId, opt);
		result = JsonLdProcessor.compact(result, mapper.getJsonLdContext(), opt);
		return buildDataId(result);
	}

	private DataId buildDataId(Object result) {
		DataId id = new DataId();
		id.setDataIdBody((List<LinkedHashMap<String, Object>>) ((Map<String, Object>) result).get("@graph"));
		id.setRdfContext((Map<String, String>) ((Map<String, Object>) result).get("@context"));
		return id;
	}
	
	private DataId getDataIdFromTurtle(String sourceId) throws JsonLdError
	{
		JsonLdOptions opt = new JsonLdOptions();
		opt.format = "text/turtle";
        opt.setBase("http://someuri.org");
		opt.setUseNativeTypes(true);
		opt.setCompactArrays(true);
		
		return getDataId(sourceId, opt);
	}
	
	private DataId getDataIdFromNquads(String sourceId) throws JsonLdError
	{
		JsonLdOptions opt = new JsonLdOptions();
        opt.format = "application/nquads";
        opt.setBase("http://someuri.org");
		opt.setUseNativeTypes(true);
		opt.setCompactArrays(true);
		
		return getDataId(sourceId, opt);
	}

    private DataId getDataIdFromRdfXml(String sourceId) throws JsonLdError
    {
        JsonLdProcessor.registerRDFParser("rdf/xml", new RdfXmlParser());
        JsonLdOptions opt = new JsonLdOptions();
        opt.format = "rdf/xml";
        opt.setBase("http://someuri.org");
        opt.setUseNativeTypes(true);
        opt.setCompactArrays(true);

        return getDataId(sourceId, opt);
    }

	public List<Dataset> GetDataHubDataset(String sourceId) throws DataHubMappingException, DataIdInputException {
		List<Dataset> sets = new ArrayList<Dataset>();
		DataidInput inputType = getInputType(sourceId);
		if(inputType == DataidInput.None)
			throw new DataIdInputException("The provided input is not of the following formats: turtle, n-quads, RdfXml or json-ld");
		try {
			if(inputType == DataidInput.Turtle)
				sets = mapper.DataidToDatahub(getDataIdFromTurtle(sourceId));
			else if(inputType == DataidInput.Nquads)
				sets = mapper.DataidToDatahub(getDataIdFromNquads(sourceId));
			else if(inputType == DataidInput.JsonLd)
				sets = mapper.DataidToDatahub(buildDataId(sourceId));
            else if(inputType == DataidInput.RdfXml)
                sets = mapper.DataidToDatahub(getDataIdFromRdfXml(sourceId));
        } catch (DataHubMappingException e) {
			throw new DataHubMappingException( "An Exception while mapping properties ocurred: " + e.getMessage() );
		} catch (JsonLdError e) {
			throw new DataIdInputException("Error while trying to parse DataId file: " + e.getMessage());
		}
		return sets;
	}
	
	private DataidInput getInputType(String testString)
	{
		String test = testString.trim();
		if(!test.contains("http://dataid.dbpedia.org/ns/core#"))  //!not!
			return DataidInput.NoDataId;
		if(test.trim().startsWith("{") && test.contains("\"@context\"") && StaticJsonHelper.isJsonLdValid(test))
			return DataidInput.JsonLd;
		if(test.contains("@prefix") && StaticJsonHelper.isTurtleValid(test))
			return DataidInput.Turtle;
		if(test.contains("http://dataid.dbpedia.org/ns/core#Dataset") && StaticJsonHelper.isNquadValid(test))
			return DataidInput.Nquads;
        if(test.replace(" ", "").contains("rdf:resource=\"http://dataid.dbpedia.org/ns/core#Dataset\""))
            return DataidInput.RdfXml;
		return DataidInput.None;
	}

	public JsonNode getMappingConfig() {
		return mappingConfig;
	}

	public PropertyMapper getMapper() {
		return mapper;
	}
}
