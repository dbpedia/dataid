package aksw.dataid.datahub.propertymapping;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.JsonNodeFactory;
import org.codehaus.jackson.node.ObjectNode;
import org.codehaus.jackson.type.JavaType;

import aksw.dataid.datahub.jsonobjects.Dataset;
import aksw.dataid.datahub.jsonutils.JsonFileManager;
import aksw.dataid.datahub.mappingobjects.DataIdProperty;
import aksw.dataid.datahub.mappingobjects.MappingConfig;

import com.fasterxml.jackson.core.JsonParseException;

public class PropertyMapper 
{
	private MappingConfig mappingConfig;
	private ObjectMapper mapper = new ObjectMapper();
	
	
    public PropertyMapper(String mappingFilePath)
    {
    	JsonFileManager fileManager = new JsonFileManager();
    	try {
    		fileManager.LoadJsonFile(mappingFilePath, false);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	if(fileManager.getFileContent() != null)
    	{
    		mappingConfig = castJsonToObject(fileManager.getFileContent(), MappingConfig.class, "mapping");
    	}
    }
    private <T> T castJsonToObject(JsonNode jsonString, Class<T> clas)
    {
    	return castJsonToObject(jsonString, clas, "");
    }
    private <T> T castJsonToObject(JsonNode json, Class<T> clas, String subNode)
    {
		try {
			JsonNode node = mapper.readValue(json, JsonNode.class);
			JavaType mapping = mapper.getTypeFactory().constructType(clas);
			T config = mapper.readValue(node.get(subNode).toString(), mapping);
			return config;
		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
    }
    
    public JsonNode DataidToDatahub(JsonNode dataIdObject)
    {
		Dataset set = new Dataset();
    	
    	for(Iterator<String> fields = dataIdObject.getFieldNames(); fields.hasNext();)
    	{
    		String field = fields.next();
    		DataIdProperty dataId = mappingConfig.GetDataIdProperty(field);
    		if (dataId != null) {
				String jsonValue = dataIdObject.get(dataId.getId()).toString();
				StaticHelper.SetDatasetProperty(set, dataId, jsonValue);
			}
    	}
    	JsonNode outputObject = mapper.convertValue(set, JsonNode.class);
    	return outputObject;
    }
    
    public JsonNode DatahubToDataid(Dataset set)
    {
    	ObjectNode node = JsonNodeFactory.instance.objectNode();
    	JsonNode jsonTree = mapper.convertValue(set, JsonNode.class);
    	for(Iterator<String> fields = jsonTree.getFieldNames(); fields.hasNext();)
    	{
    		String field = fields.next();
    		DataIdProperty dataId = mappingConfig.GetDataHubProperty(field);
    		if (dataId != null) {
		    	JsonNode subTree = mapper.convertValue(StaticHelper.dynamicGet(set, field), JsonNode.class);
				node.putPOJO(dataId.getId(), subTree);
			}
    	}

    	return node;
    }
}
