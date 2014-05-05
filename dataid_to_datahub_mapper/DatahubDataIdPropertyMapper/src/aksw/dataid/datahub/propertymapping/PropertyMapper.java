package aksw.dataid.datahub.propertymapping;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.JavaType;

import aksw.dataid.datahub.jsonobjects.*;
import aksw.dataid.datahub.jsonutils.JsonFileManager;
import aksw.dataid.datahub.mappingobjects.DataIdProperty;
import aksw.dataid.datahub.mappingobjects.MappingConfig;

import com.fasterxml.jackson.core.JsonParseException;
import com.github.jsonldjava.core.Context;
import com.github.jsonldjava.core.JsonLdOptions;
import com.github.jsonldjava.utils.JsonUtils;

public class PropertyMapper 
{
	private MappingConfig mappingConfig;
	private ObjectMapper mapper = new ObjectMapper();
	private Context jsonLdContext = new Context();
	private List<LinkedHashMap> dataIdDataset;
	
	
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
    		mappingConfig = castJsonToObject(fileManager.getFileContent(), MappingConfig.class, "@graph");
    		for(Map<String,DataIdProperty> dictionary : mappingConfig.getDataHubMapping().values())
    		{
	    		for(String key : dictionary.keySet())
	    		{
	    			if(dictionary.get(key).getDataHub() == null)
	    			{	
	    				dictionary.get(key).setDataHub(key);
	    			}
	    			else if(dictionary.get(key).getDataHub().contains("{"))
	    			{
	    				
	    			}
	    		}
    		}
    		
    		try {
				String cont = fileManager.getFileContent().get("@context").toString();
				Object lala = JsonUtils.fromString(cont);
				jsonLdContext = new Context(((Map<String, Object>) lala), new JsonLdOptions("http://lalaland.io"));
			} catch (JsonParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
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
    
    private LinkedHashMap getObjectMapById(String id)
    {
    	if(dataIdDataset != null)
    	{
    		for(LinkedHashMap map : dataIdDataset)
    		{
    			if(map.containsKey("@id") && map.get("@id").equals(id))
    			{
    				return map;
    			}
    		}
    	}
    	return null;
    }
    
    private String followReferenceChain(List<String> chain, LinkedHashMap context)
    {
    	for (int i =0; i< chain.size();i++ )
    	{
    		if(i<chain.size()-1)
    			context = getObjectMapById(extractRealValue(context.get(chain.get(i))));
    		else
    			return extractRealValue(context.get(chain.get(i)));
    	}
    	return null;
    }
    
    public JsonNode DataidToDatahub(List<LinkedHashMap> dataIdObject) throws DataHubMappingException
    {
    	dataIdDataset = dataIdObject;
		Dataset set = new Dataset();
		LinkedHashMap dataset = null;
		
		boolean datasetfound = false;
		for(LinkedHashMap map : dataIdObject)
		{
			if(map.containsKey("@type"))
			{
				//mapping for dataset and resources
				if((String.valueOf((map.get("@type"))).contains("dcat:Dataset")))
				{
					set = fillObjectWithMapValues(set, map);
					datasetfound = true;
				}
				//handling linksets
				if((String.valueOf((map.get("@type"))).contains("void:Linkset")))
				{
					String key = "links: " + extractRealValue(map.get("void:objectTarget"));
					String value = extractRealValue(map.get("void:triples"));
					setDatasetExtra(set, key, value);
				}
			}
		}
		if(!datasetfound)
			throw new DataHubMappingException("no dataset found!");
		addTriples(set);
    	return mapper.convertValue(set, JsonNode.class);
    }
    
	private void addTriples(Dataset set) {
		int triples =0;
		for(Resource r : set.getResources())
		{
			if(r.getTriples() != null)
				triples += r.getTriples();
		}
		setDatasetExtra(set, "triples", String.valueOf(triples));
	}
	
	private void setDatasetExtra(Dataset set, String key, String value) {
		DatasetExtras ex = new DatasetExtras();
		ex.setKey(key);
		ex.setValue(value);
		set.getExtras().add(ex);
	}
    
	private <T> T fillObjectWithMapValues(T set, LinkedHashMap dataset) 
	{
		//take care of id link
		if(dataset.keySet().size() == 1 && dataset.get("@id") != null)
		{
			String id = String.valueOf(dataset.get("@id"));
			LinkedHashMap zw = getObjectMapById(id);
			if(zw != null)
				dataset = zw;
		}
		String dictionary = "dataset";
		if(set.getClass() == Resource.class)
			dictionary = "resource";
		for(Object field : mappingConfig.getDataHubMapping().get(dictionary).keySet())
    	{
    		DataIdProperty dataId = mappingConfig.GetPropertyByDataHub(dictionary, field.toString());
    		if(dataId == null)
    			continue;
			SetGenericProperty(set, dataId, dataset);
    	}
    	return set;
	}
	
	@SuppressWarnings("unchecked")
	private <T> List<T> getGenericList(Class<T> listClass, List<Object> fieldValue)
	{
		List<T> list = new ArrayList<T>();
		for(Object ele : fieldValue)
		{
			T set = null;
			try {
				set = listClass.newInstance();
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(set.getClass() == Tag.class)
			{
				for(Object tag : fieldValue)
				{
					Tag t = new Tag();
					t.setDisplay_name(String.valueOf(tag));
					t.setState("active");
					t.setName(String.valueOf(tag));
					set = (T) t;
				}
			}
			else if(ele.getClass() == LinkedHashMap.class)
				set = fillObjectWithMapValues(set, (LinkedHashMap)ele);
			else
				set = (T) ele;
			
			if(set.getClass() == Resource.class)
				((Resource)set).setPosition(list.size());
			list.add(set);
		}
		return list;
	}
    
	@SuppressWarnings("unchecked")
	public boolean SetGenericProperty(Object target, DataIdProperty fieldProperty, LinkedHashMap context) 
	{
		Object fieldValue = context.get(fieldProperty.getId());
		String stringValue = null;
				
		if(fieldProperty.getReferenceChain() != null)
			stringValue = followReferenceChain((List<String>)fieldProperty.getReferenceChain(), context);
		else
			stringValue = extractRealValue(fieldValue);
        try {
            Field field = null;
        	if(fieldProperty.isReadOnly())
        	{	return false;    }
        	else if(fieldProperty.isList())
        	{
        		field = getField(target.getClass(), fieldProperty);
        		ParameterizedType genericType = (ParameterizedType) field.getGenericType();
	            field.set(target, getGenericList((Class<?>) genericType.getActualTypeArguments()[0], (List<Object>)fieldValue));
        	}
        	else if(fieldProperty.isAdditionalKey())
        	{
        		field = Dataset.class.getDeclaredField("extras");
        		field.setAccessible(true);
        		setDatasetExtra(((Dataset)target), fieldProperty.getDataHub(), stringValue);
        	}
        	else
        	{
        		field = getField(target.getClass(), fieldProperty);
	            Object value = castToValue(stringValue, field.getType(), null);
	            field.set(target, value);
        	}
            return true;

        }catch (Exception e) {
    	    return false;
        }
	}
	
	private String extractRealValue(Object value)
	{
		if(value == null)
			return null;
		if(value.getClass() == LinkedHashMap.class)
		{
			if(((LinkedHashMap)value).keySet().contains("@id"))
				return ((LinkedHashMap)value).get("@id").toString();
			if(((LinkedHashMap)value).keySet().contains("@value"))
				return ((LinkedHashMap)value).get("@value").toString();
			return null;
		}
		else if(value.getClass() == List.class)
		{
			return extractRealValue(((List<LinkedHashMap>)value).get(0));
		}
		else
			return String.valueOf(value);
	}

	@SuppressWarnings("unchecked")
	public <T> T castToValue(String fieldValue, Class<T> type, ParameterizedType listType) 
	{
		ObjectMapper mapper = new ObjectMapper();
		if(type == String.class)
		{
			if(!fieldValue.startsWith("\"")) //not!
				fieldValue = "\"" + fieldValue + "\"";
		}
		T value = null;
			try {
				if(listType != null)
				{
					Class<?> t = (Class<?>) listType.getActualTypeArguments()[0];
					JavaType javaType = mapper.getTypeFactory().constructParametricType(List.class,t);
					value = mapper.readValue(fieldValue, javaType);
				}
				else if(type == Date.class)
				{
					if(fieldValue.contains("-"))
					{
						SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
						try {
							value = (T) sdf.parse(fieldValue);
						} catch (ParseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
				else
				{
					value = mapper.readValue(fieldValue, type);
					
				}
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
		return value;
	}

	public Field getField(Class<?> targetClass, DataIdProperty fieldProperty)
	{
		Field field = null;
		try {
			field = targetClass.getDeclaredField(fieldProperty.getDataHub());
			field.setAccessible(true);
		} catch (NoSuchFieldException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return field;
	}
	
	public Context getJsonLdContext() {
		return jsonLdContext;
	}
}
