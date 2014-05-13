package aksw.dataid.datahub.cli;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.JsonNode;

import com.github.jsonldjava.core.Context;
import com.github.jsonldjava.core.JsonLdError;
import com.github.jsonldjava.core.JsonLdOptions;
import com.github.jsonldjava.core.JsonLdProcessor;

import aksw.dataid.datahub.jsonobjects.Dataset;
import aksw.dataid.datahub.jsonutils.StaticJsonHelper;
import aksw.dataid.datahub.propertymapping.DataHubMappingException;
import aksw.dataid.datahub.propertymapping.PropertyMapper;
import aksw.dataid.datahub.propertymapping.StaticHelper;

public class DataIdProcesser 
{
	String sourceId;
	List<LinkedHashMap> processedId;
	PropertyMapper mapper;
	public DataIdProcesser(String dataid, String mappingConfigPath) throws DataHubMappingException
	{

		JsonNode mappingConfig = StaticJsonHelper.getJsonContent(mappingConfigPath);
	    mapper = new PropertyMapper(mappingConfig);
	    sourceId = dataid;
	    try {
			processedId = getDataIdAsJson();
		} catch (JsonLdError e) {
			throw new DataHubMappingException("Error while trying to parse DataId file: " + e.getMessage());
		}
	}
		
	private List<LinkedHashMap> getDataIdAsJson() throws JsonLdError
	{
		JsonLdOptions opt = new JsonLdOptions();
		opt.setFormat("text/turtle");
		opt.setUseNativeTypes(true);
		opt.setCompactArrays(true);
		
		Object result =null;
		result = JsonLdProcessor.fromRDF(sourceId, opt);
		result = JsonLdProcessor.compact(result, mapper.getJsonLdContext(), opt);
		return (List<LinkedHashMap>) ((Map<String, Object>) result).get("@graph");
	}

	public Dataset createDataset() throws DataHubMappingException {
		Dataset set = null;
		try {
			set = mapper.DataidToDatahub(getDataIdAsJson());
		} catch (DataHubMappingException e) {
			throw new DataHubMappingException( "An Exception while mapping properties ocured: " + e.getMessage() );
		} catch (JsonLdError e) {
			throw new DataHubMappingException("Error while trying to parse DataId file: " + e.getMessage());
		}
		return set;
	}
}
