package aksw.dataid.datahub.propertymapping;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.JavaType;

import aksw.dataid.datahub.jsonobjects.*;
import aksw.dataid.datahub.jsonutils.JsonFileManager;
import aksw.dataid.datahub.mappingobjects.DataId;
import aksw.dataid.datahub.mappingobjects.DataIdProperty;
import aksw.dataid.datahub.mappingobjects.MappingConfig;

import com.fasterxml.jackson.core.JsonParseException;
import com.github.jsonldjava.core.Context;
import com.github.jsonldjava.core.JsonLdOptions;
import com.github.jsonldjava.utils.JsonUtils;

public class PropertyMapper 
{
	private MappingConfig mappingConfig;
	private Context jsonLdContext = new Context();
	private Map<String,String> contextSynchronization = new HashMap<String,String>();
	private DataId currentId;
	private String dataIdPrefix;
	
	
    public PropertyMapper(JsonNode mappingContent)
    {
    	if(mappingContent != null)
    	{
    		mappingConfig = StaticHelper.castJsonToObject(mappingContent.toString(), MappingConfig.class, "@graph");
    		mappingConfig.setRdfContext(StaticHelper.castJsonToObject(mappingContent.toString(), contextSynchronization.getClass(), "@context"));
    		this.dataIdPrefix = mappingConfig.getPrefix("http://dataid.dbpedia.org/ns/core#");
    		
    		for(Map<String,DataIdProperty> dictionary : mappingConfig.getDataHubMapping().values())
    		{
	    		for(String key : dictionary.keySet())
	    		{
	    			if(dictionary.get(key).getDataHub() == null)
	    			{	
	    				dictionary.get(key).setDataHub(key);
	    			}
	    		}
    		}
    		
    		try {
				String cont = mappingContent.get("@context").toString();
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

    
    private LinkedHashMap getObjectMapById(String id)
    {
    	if(currentId.getDataIdBody() != null)
    	{
    		for(LinkedHashMap map : currentId.getDataIdBody())
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
    
    public List<Dataset> DataidToDatahub(DataId dataIdObject) throws DataHubMappingException, DataIdInputException
    {
    	currentId = dataIdObject;
    	this.synchronizeRdfContexts(dataIdObject.getRdfContext());
		List<Dataset> sets = new ArrayList<Dataset>();
		LinkedHashMap dataset = null;
		
		boolean datasetfound = false;
		for(LinkedHashMap map : dataIdObject.getDataIdBody())
		{
			if(map.containsKey("@type"))
			{
				//mapping for dataset and resources
				if(containsIri(String.valueOf((map.get("@type"))), dataIdPrefix, "Dataset")) //type.contains("http://dataid.dbpedia.org/ns/core#Dataset") || type.contains("dataid:Dataset"))
				{
					Dataset set = new Dataset();
					sets.add(fillObjectWithMapValues(set, map));
					datasetfound = true;
					addTriples(set);
				}
			}
		}
		if(sets.size() == 0)
			throw new DataHubMappingException("no dataset found!");
    	return sets;
    }
    
	private void addTriples(Dataset set) {
		int triples =0;
		for(Resource r : set.getResources())
		{
			if(r.getTriples() != null)
				triples += r.getTriples();
		}
		if(triples > 0)
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
    		if(dataId.isAlternative())
    			continue;
    		dataId.setDictionary(dictionary);
			SetGenericProperty(set, dataId, dataset);
    	}
    	return set;
	}
	
	@SuppressWarnings("unchecked")
	private <T extends DataHubListObject> List<DataHubListObject> getGenericList(Class<T> listClass, Object fieldValue)
	{
		List<LinkedHashMap> value = null;
		if(fieldValue.getClass() == LinkedHashMap.class)
		{
			value = new ArrayList<LinkedHashMap>();
			value .add((LinkedHashMap) fieldValue);
		}
		else
			value = (List<LinkedHashMap>) fieldValue;
		
		List<T> list = new ArrayList<T>();
		for(Object ele : value)
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
					Tag t = new Tag();
					t.setDisplay_name(String.valueOf(ele));
					t.setState("active");
					t.setName(String.valueOf(ele));
					set = (T) t;
				
			}
			else if(ele.getClass() == LinkedHashMap.class)
				set = fillObjectWithMapValues(set, (LinkedHashMap)ele);
			else
				set = (T) ele;
			
			if(set.getClass() == Resource.class)
				((Resource)set).setPosition(list.size());
			list.add(set);
		}
		return (List<DataHubListObject>) list;
	}
    
	@SuppressWarnings("unchecked")
	public boolean SetGenericProperty(Object target, DataIdProperty fieldProperty, LinkedHashMap context) 
	{

        try {
			for(String fieldString : fieldProperty.getDataIdRefs())
			{
				Object fieldValue = context.get(fieldString);
				String stringValue = null;
						
				if(fieldProperty.getReferenceChain() != null)
					stringValue = followReferenceChain((List<String>)fieldProperty.getReferenceChain(), context);
				else
					stringValue = extractRealValue(fieldValue);
		            Field field = null;
		        	if(fieldProperty.isReadOnly())
		        	{	return false;    }
		        	else if(fieldProperty.isList())
		        	{
		        		field = getField(target.getClass(), fieldProperty);
		        		ParameterizedType genericType = (ParameterizedType) field.getGenericType();
		        		Class<DataHubListObject> t = (Class<DataHubListObject>) genericType.getActualTypeArguments()[0];
		        		List<DataHubListObject> value = getGenericList(t, fieldValue);
		        		
		        		try {
							List<DataHubListObject> zw = (List<DataHubListObject>) field.get(target);
							value.addAll(zw);
						} catch (Exception e) {
						}
						field.set(target, value);
		        	}
		        	else if(fieldProperty.isAdditionalKey())
		        	{
		        		field = Dataset.class.getDeclaredField("extras");
		        		field.setAccessible(true);
		        		setDatasetExtra(((Dataset)target), fieldProperty.getDataHub(), stringValue);
		        	}
		        	else if(stringValue != null)
		        	{
		        		field = getField(target.getClass(), fieldProperty);
			            Object value = castToValue(stringValue, field.getType(), null);
			            field.set(target, value);
		        	}
		        	
		        	try {
						field.get(target);	//alas, if field == null field.get throws Nullpointer
					} catch (NullPointerException e) {
						if(fieldProperty.getHasAlternative() != null)
			        	{
			        		DataIdProperty altProp = mappingConfig.GetPropertyByDataHub(fieldProperty.getDictionary(), fieldProperty.getHasAlternative());
			        		altProp.setDataHub(fieldProperty.getDataHub());
			        		return SetGenericProperty(target, altProp, context);
			        	}
					}
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
	
	public boolean containsIri(String value, String prefix, String postfix)
	{
		String longPrefix = null;
		if(prefix.contains("/"))
			longPrefix = prefix;
		else
			prefix = prefix.replace(":", "");
		
		if(longPrefix == null)
			longPrefix = mappingConfig.getRdfContext().get(prefix);
	
		prefix = getPrefix(longPrefix, currentId.getRdfContext());
		if(value.contains(prefix + ":" + postfix) || value.contains(longPrefix + postfix))
			return true;
		else
			return false;
	}
	
	public void synchronizeRdfContexts(Map<String, String> dataIdContext) throws DataIdInputException
	{
		this.contextSynchronization.clear();
		for(String prefix : dataIdContext.keySet())
		{
			String postfix = dataIdContext.get(prefix);
			String configPrefix = getPrefix(postfix, mappingConfig.getRdfContext());
			if(configPrefix != null)
				this.contextSynchronization.put(prefix,  configPrefix);
		}
	}
	
	private String getPrefix(String postfix, Map<String, String> context)
	{
		for(String prefix : context.keySet())
		{
			if(postfix.equals(context.get(prefix)))
				return prefix;
		}
		return null;
	}


	public MappingConfig getMappingConfig() {
		return mappingConfig;
	}
	
}
