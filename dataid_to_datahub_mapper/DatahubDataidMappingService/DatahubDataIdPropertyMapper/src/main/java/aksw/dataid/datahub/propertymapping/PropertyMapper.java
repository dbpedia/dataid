package aksw.dataid.datahub.propertymapping;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;

import aksw.dataid.datahub.jsonobjects.*;
import aksw.dataid.datahub.mappingobjects.DataId;
import aksw.dataid.datahub.mappingobjects.DataIdProperty;
import aksw.dataid.datahub.mappingobjects.MappingConfig;


import com.fasterxml.jackson.databind.ObjectMapper;
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

    private static List<String> ImplicitAlternativeProps;
	
	
    public PropertyMapper(JsonNode mappingContent) throws IOException {
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
            String cont = mappingContent.get("@context").toString();
            jsonLdContext = new Context(((Map<String, Object>) JsonUtils.fromString(cont)), new JsonLdOptions("http://someuri.io"));

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
                if(context.keySet().contains(chain.get(i)))
    			    return extractRealValue(context.get(chain.get(i)));
    	}
    	return null;
    }
    
    public List<Dataset> DataidToDatahub(DataId dataIdObject) throws DataHubMappingException, DataIdInputException
    {
    	currentId = dataIdObject;
    	this.synchronizeRdfContexts(dataIdObject.getRdfContext());
		List<Dataset> sets = new ArrayList<Dataset>();
        ImplicitAlternativeProps = new ArrayList<String>();

		for(LinkedHashMap map : dataIdObject.getDataIdBody())
		{
			if(map.containsKey("@type"))
			{
				//mapping for dataset and resources
				if(containsIri(String.valueOf((map.get("@type"))), dataIdPrefix, "Dataset")) //type.contains("http://dataid.dbpedia.org/ns/core#Dataset") || type.contains("dataid:Dataset"))
				{
					Dataset set = new Dataset();
					sets.add(fillObjectWithMapValues(set, map));
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
    
	private <T extends ValidCkanResponse> T fillObjectWithMapValues(T set, LinkedHashMap dataset)
	{
		//take care of id link
		if(dataset.keySet().size() == 1 && dataset.get("@id") != null)
		{
			String id = String.valueOf(dataset.get("@id"));
			LinkedHashMap zw = getObjectMapById(id);
			if(zw != null)
				dataset = zw;
		}
        DataIdProperty.MappingDictionaryType dictType = DataIdProperty.MappingDictionaryType.Dataset;
		if(set.getClass() == Resource.class)
            dictType = DataIdProperty.MappingDictionaryType.Resource;
		for(Object field : mappingConfig.getDataHubMapping().get(dictType.toString().toLowerCase()).keySet())
    	{
    		DataIdProperty dataId = mappingConfig.GetPropertyByDataHub(dictType, field.toString());
    		if(dataId == null)
    			continue;
    		if(ImplicitAlternativeProps.contains(dataId.getDataHub()) || dataId.isAlternative()) {
                dataId.setAlternative(true);
                continue;
            }
    		dataId.setDictionary(dictType);
			SetGenericProperty(set, dataId, dataset);
    	}
    	return set;
	}
	
	@SuppressWarnings("unchecked")
	private <T extends DataHubListObject> List<DataHubListObject> getGenericList(Class<T> listClass, Object fieldValue) throws IllegalAccessException, InstantiationException {
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
			T set = listClass.newInstance();

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
	public <T extends ValidCkanResponse> boolean SetGenericProperty(T target, DataIdProperty fieldProperty, LinkedHashMap context)
	{
        for(String fieldString : fieldProperty.getDataIdRefs()) {
            if (fieldProperty.isReadOnly()) {
                return false;
            }

            //extract value
            Object fieldValue = context.get(fieldString);
            if(fieldValue == null)
                return false;
            String stringValue = null;

            //multiple property occurrences -> if visible add additional keys
            if (!fieldProperty.isList() && List.class.isAssignableFrom(fieldValue.getClass())) {
                if(fieldProperty.isAdditionalKey() || Arrays.asList("url", "author", "author_email", "maintainer", "maintainer_email").contains(fieldProperty.getDataHub()))
                {
                    int count =0;
                    for (Object obj : (List) fieldValue) {
                        DataIdProperty zw = (DataIdProperty) fieldProperty.clone();

                        if(count > 0) {
                            zw.setAdditionalKey(true);
                            zw.setDataHub(fieldProperty.getDataHub() + "_" + count);
                        }
                        setGenericproperty(target, zw, context, obj, getStringValue(zw, context, obj));
                        count++;
                    }
                }
                else
                    setGenericproperty(target, fieldProperty, context, ((List) fieldValue).get(0), getStringValue(fieldProperty, context, ((List) fieldValue).get(0)));

                return true;
            }
            stringValue = getStringValue(fieldProperty, context, fieldValue);

            if (!setGenericproperty(target, fieldProperty, context, fieldValue, stringValue))  //not!!
                return false;
        }
        return true;
	}

    private String getStringValue(DataIdProperty fieldProperty, LinkedHashMap context, Object fieldValue) {
        String stringValue;//follow reference chain
        if (fieldProperty.getReferenceChain() != null)
            stringValue = followReferenceChain((List<String>) fieldProperty.getReferenceChain(), context);
        else
            stringValue = extractRealValue(fieldValue);

        //take care of default values
        if (stringValue == null && fieldProperty.getDefaultValue() != null)
            stringValue = fieldProperty.getDefaultValue();
        return stringValue;
    }

    private  <T extends ValidCkanResponse> boolean setGenericproperty(T target, DataIdProperty fieldProperty, LinkedHashMap context, Object fieldValue, String stringValue) {
        //take care of default values
        if (stringValue == null && fieldProperty.getDefaultValue() != null)
            stringValue = fieldProperty.getDefaultValue();

        Field field = null;
        //create a Field and set value to it
        try {
            if (fieldProperty.isList()) {
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
            } else if (fieldProperty.isAdditionalKey()) {
                field = Dataset.class.getDeclaredField("extras");
                field.setAccessible(true);
                setDatasetExtra(((Dataset) target), fieldProperty.getDataHub(), stringValue);

                //TODO check the following lines -> adds a property after adding additional key (if prop exists)
                try {
                    field = getField(target.getClass(), fieldProperty);
                    Object value = castToValue(stringValue, field.getType(), null);
                    field.set(target, value);
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }
            } else if (stringValue != null) {
                field = getField(target.getClass(), fieldProperty);
                Object value = castToValue(stringValue, field.getType(), null);
                field.set(target, value);
            }

            //follow alternatives
            try {
                field.get(target);    //alas, if field == null field.get throws Nullpointer
            } catch (NullPointerException e) {
                if (fieldProperty.getHasAlternative() != null) {
                    DataIdProperty altProp = mappingConfig.GetPropertyByDataHub(fieldProperty.getDictionary(), fieldProperty.getHasAlternative());
                    ImplicitAlternativeProps.add(altProp.getDataHub());
                    altProp.setDataHub(fieldProperty.getDataHub());
                    return SetGenericProperty(target, altProp, context);
                }
            }
        }catch(Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
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
		else if(List.class.isAssignableFrom(value.getClass()))
		{
			return extractRealValue(((List<LinkedHashMap>)value).get(0));
		}
		else
			return String.valueOf(value);
	}

	@SuppressWarnings("unchecked")
	public <T> T castToValue(String fieldValue, Class<T> type, ParameterizedType listType) throws ParseException, IOException {
		ObjectMapper mapper = new ObjectMapper();
		if(type == String.class)
		{
			if(!fieldValue.startsWith("\"")) //not!
				fieldValue = "\"" + fieldValue + "\"";
		}
		T value = null;
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
                value = (T) sdf.parse(fieldValue);
            }
        }
        else
        {
            value = mapper.readValue(fieldValue, type);

        }
		return value;
	}

	public Field getField(Class<?> targetClass, DataIdProperty fieldProperty) throws NoSuchFieldException, SecurityException
	{
		Field field = null;
        field = targetClass.getDeclaredField(fieldProperty.getDataHub());
        field.setAccessible(true);
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
