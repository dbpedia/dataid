package org.aksw.dataid.datahub.propertymapping;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.aksw.dataid.config.DataIdConfig;
import org.aksw.dataid.datahub.jsonobjects.*;
import org.aksw.dataid.datahub.mappingobjects.DataId;
import org.aksw.dataid.datahub.mappingobjects.DataIdBody;
import org.aksw.dataid.config.DataIdProperty;
import org.aksw.dataid.config.MappingConfig;
import org.aksw.dataid.errors.DataHubMappingException;
import org.aksw.dataid.config.RdfContext;
import org.aksw.dataid.errors.DataIdInputException;
import org.aksw.dataid.errors.DataIdServiceException;
import org.aksw.dataid.statics.StaticContent;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class PropertyMapper 
{
	private MappingConfig mappingConfig;
	private Map<String,String> contextSynchronization = new HashMap<String,String>();
	private DataId currentId;
	private String dataIdPrefix;
	
	
    public PropertyMapper(JsonNode mappingContent) {
    	if(mappingContent != null)
    	{
            mappingConfig = StaticContent.getMappings();
    		this.dataIdPrefix = StaticContent.getRdfContext().getPrefix(DataIdConfig.getOntologies().get("dataid").getKey());
    		
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
    	}
    }
    
    private <E extends Map<String, Object>> E getObjectMapById(String id) throws DataIdInputException {
    	if(id == null || currentId.getDataIdBody() != null)
    	{
    		for(DataIdBody.DataIdObject map : currentId.getDataIdBody().getBody())
    		{
    			if(map.containsKey("@id") && map.get("@id").equals(id))
    			{
    				return (E) map;
    			}
    		}
    	}
        throw new DataIdInputException("An instance could not be found for this id: " + id);
    }
    
    private <E extends Map<String, Object>> String followReferenceChain(List<String> chain, E context, MappingObject set) throws DataIdInputException {
    	for (int i =0; i< chain.size();i++ )
    	{
    		if(i<chain.size()-1) {
                context = getObjectMapById(extractRealValue(context.get(chain.get(i))));
            }
    		else
                if(context != null && context.keySet().contains(chain.get(i))) {
                    if(context.getClass().isAssignableFrom(DataIdBody.DataIdObject.class))
                        set.addChild(((DataIdBody.DataIdObject) context).getMap());
                    else if(context.getClass().isAssignableFrom(LinkedHashMap.class))
                        set.addChild(((LinkedHashMap<String, Object>) context));
                    return extractRealValue(context.get(chain.get(i)));
                }
    	}
    	return null;
    }
    
    public List<Dataset> DataidToDatasets(DataId dataIdObject) throws DataIdServiceException {
    	currentId = dataIdObject;
    	this.synchronizeRdfContexts(dataIdObject.getRdfContext());
		List<Dataset> sets = new ArrayList<Dataset>();

		for(DataIdBody.DataIdObject map : dataIdObject.getDataIdBody().getBody())
		{
			if(map.containsKey("@type"))
			{
				//mapping for dataset and resources
				if(StaticContent.getRdfContext().iriContains(String.valueOf((map.get("@type"))), dataIdPrefix, "Dataset"))
				{
					Dataset set = new Dataset();
					sets.add(fillObjectWithMapValues(set, map));
					//addTriples(set);
                    for(Resource r : set.getResources())
                        for(LinkedHashMap<String, Object> o : r.getGraph())
                            set.addChild(o);

				}
			}
		}
        for(Dataset set : sets)
        {
            List<Resource> newResources= new ArrayList<>();

            for(int i =0; i < set.getSubsets().size(); i++)
            {
                String subset = set.getSubsets().get(i);
                for(Dataset sub : sets) {
                    if(sub.getDataIdUri().trim().equals(subset.trim()))
                    {
                        Resource subjRes = new Resource();
                        //subjRes.setPackage_id(set.getId());
                        subjRes.setUrl(sub.getDatahubUrl());
                        subjRes.setDescription("Sub-Dataset of " + set.getTitle());
                        subjRes.setFormat("HTML");
                        subjRes.setName(sub.getTitle());
                        subjRes.setDistributionType("void:subset");
                        newResources.add(subjRes);

                        Resource objRes = new Resource();
                        //objRes.setPackage_id(sub.getId());
                        objRes.setUrl(set.getDatahubUrl());
                        objRes.setDescription("Parent-Dataset");
                        objRes.setFormat("HTML");
                        objRes.setName(set.getTitle());
                        objRes.setDistributionType("void:subset");
                        sub.getResources().add(objRes);
                    }
                }
            }
            for(int i =0; i < set.getResources().size(); i++)
            {
                newResources.add(set.getResources().get(i));
            }
            set.setResources(newResources);
        }
		if(sets.size() == 0)
			throw new DataIdInputException("no dataset found!");
    	return sets;
    }
	
	private void setDatasetExtra(Dataset set, String key, String value) {
		DatasetExtras ex = new DatasetExtras();
		ex.setKey(key);
		ex.setValue(value);
		set.getExtras().add(ex);
	}
    
	private <T extends MappingObject, E extends Map<String, Object>> T fillObjectWithMapValues(T set, E dataContext) throws DataIdServiceException {
		//take care of id link
		if(dataContext.keySet().size() == 1 && dataContext.get("@id") != null)
		{
			String id = String.valueOf(dataContext.get("@id"));
			DataIdBody.DataIdObject zw = getObjectMapById(id);
			if(zw != null) {
                dataContext = (E)zw;
            }
		}
        if(dataContext.getClass() == DataIdBody.DataIdObject.class)
            set.addChild(((DataIdBody.DataIdObject) dataContext).getMap());
        else if(dataContext.getClass().isAssignableFrom(LinkedHashMap.class))
            set.addChild(((LinkedHashMap<String, Object>) dataContext));

        DataIdProperty.MappingDictionaryType dictType = DataIdProperty.MappingDictionaryType.Dataset;
		if(set.getClass() == Resource.class)
            dictType = DataIdProperty.MappingDictionaryType.Resource;

		for(Object field : mappingConfig.getDataHubMapping().get(dictType.toString().toLowerCase()).keySet())
    	{
    		DataIdProperty dataIdProp = mappingConfig.GetPropertyByDataHub(dictType, field.toString());
    		if(dataIdProp == null)
    			continue;
    		if(dataIdProp.isAlternative()) {
                dataIdProp.setAlternative(true);
                continue;
            }

            dataIdProp.setDictionary(dictType);
            //add links
            if(StaticContent.getRdfContext().iriContains(dataIdProp.getType(), dataIdPrefix, "Linkset")) {
                if(dataContext.get(dataIdProp.getDataIdRef()) != null)
                {
                    LinkedHashMap<String, Object> link = getSubGraphMap(((LinkedHashMap<String, Object>) dataContext.get(dataIdProp.getDataIdRef())).get("@id").toString());
                    String voidPrefix = StaticContent.getRdfContext().getPrefix("http://rdfs.org/ns/void#");
                    String target = ((LinkedHashMap<String, Object>) link.get(voidPrefix + ":target")).get("@id").toString();
                    String shorty = "links:" + target.substring(0, target.lastIndexOf('.')).replace("http://", "");
                    setDatasetExtra((Dataset) set, shorty, link.get(voidPrefix + ":triples").toString());
                    set.addChild(link);
                }
            }
            else
            {
                SetGenericProperty(set, dataIdProp, dataContext);
            }
    	}
    	return set;
	}

	@SuppressWarnings("unchecked")
	private <T, E extends Map<String, Object>> List<T> getGenericList(Class<T> listClass, Object fieldValue, Dataset set) throws IllegalAccessException, InstantiationException, DataIdServiceException {
		List<LinkedHashMap> value = new ArrayList<LinkedHashMap>();
		if(fieldValue.getClass() == LinkedHashMap.class)
		{
			value.add((LinkedHashMap) fieldValue);
		}
        else if(List.class.isAssignableFrom(fieldValue.getClass()))
        {
            value = (List<LinkedHashMap>) fieldValue;
        }
		else
			throw new DataIdInputException("a list item was out of format");
		
		List<T> list = new ArrayList<T>();
		for(Object ele : value)
		{

            T inst = listClass.newInstance();
            //if(MappingObject.class.isAssignableFrom(inst.getClass()))
            //    inst = (T) (fillObjectWithMapValues((MappingObject) inst, (LinkedHashMap<String, Object>)ele));

			if(inst.getClass() == Tag.class)
			{
                Tag t = new Tag();
                t.setDisplay_name(String.valueOf(((LinkedHashMap<String, Object>)ele).get("@value")));
                t.setState("active");
                t.setName(String.valueOf(((LinkedHashMap<String, Object>)ele).get("@value")));
                inst = (T) t;
				
			}
            else if(inst.getClass() == String.class)
            {
                inst = (T) extractRealValue(ele);
            }
			else if(ele.getClass() == LinkedHashMap.class)
                inst = (T) fillObjectWithMapValues((MappingObject) inst, (LinkedHashMap<String, Object>)ele);
			else
                inst = (T) ele;
			
/*			if(inst.getClass() == Resource.class) {
                ((Resource) inst).setPosition(list.size());
                //set.addChild((LinkedHashMap<String, Object>)ele);
            }*/
			list.add(inst);

		}
		return list;
	}
    
	@SuppressWarnings("unchecked")
	public <T extends MappingObject, E extends Map<String, Object>> boolean SetGenericProperty(T target, DataIdProperty fieldProperty, E context) throws DataIdServiceException {
        if (fieldProperty == null || fieldProperty.isReadOnly()) {
            return false;
        }
        String fieldString = fieldProperty.getDataIdRef();

        Object fieldValue = context.get(fieldString);
        //if(fieldValue == null)
            //fieldValue = fieldProperty.getReferenceChain() == null ? fieldValue : fieldProperty.getReferenceChain().get(0);

        if(fieldValue == null)
        {
            return SetGenericProperty(target, getAlternative(target, fieldProperty, context), context);
        }

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
                    setGenericproperty(target, zw, context, obj, getStringValue(zw, context, obj, target));
                    count++;
                }
            }
            else
                setGenericproperty(target, fieldProperty, context, ((List) fieldValue).get(0), getStringValue(fieldProperty, context, ((List) fieldValue).get(0), target));

            return true;
        }
        stringValue = getStringValue(fieldProperty, context, fieldValue, target);

        if(fieldValue == null || stringValue == null)
        {
            return SetGenericProperty(target, getAlternative(target, fieldProperty, context), context);
        }

        if (!setGenericproperty(target, fieldProperty, context, fieldValue, stringValue))  //not!!
            return false;

        return true;
	}

    private <T extends MappingObject, E extends Map<String, Object>> DataIdProperty getAlternative(T target, DataIdProperty fieldProperty, E context) {
        if (fieldProperty.getHasAlternative() != null) {
            DataIdProperty altProp = mappingConfig.GetPropertyByDataHub(fieldProperty.getDictionary(), fieldProperty.getHasAlternative());
            if(altProp == null)
                return null;
            altProp.setDataHub(fieldProperty.getDataHub());
            return altProp;
        }
        else
            return null;
    }

    private <E extends Map<String, Object>> String getStringValue(DataIdProperty fieldProperty, E context, Object fieldValue, MappingObject set) throws DataIdInputException {
        String stringValue;//follow reference chain
        if (fieldProperty.getReferenceChain() != null)
            stringValue = followReferenceChain((List<String>) fieldProperty.getReferenceChain(), context, set);
        else
            stringValue = extractRealValue(fieldValue);

        //take care of default values
        if (stringValue == null && fieldProperty.getDefaultValue() != null)
            stringValue = fieldProperty.getDefaultValue();
        return stringValue;
    }

    private  <T extends MappingObject, E extends Map<String, Object>> boolean setGenericproperty(T target, DataIdProperty fieldProperty, E context, Object fieldValue, String stringValue) throws DataIdServiceException {
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
                List<DataHubListObject> value = getGenericList(t, fieldValue, (Dataset) target);

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
                catch(NoSuchFieldException e)
                {}
            } else if (stringValue != null) {
                field = getField(target.getClass(), fieldProperty);
                Object value = castToValue(stringValue, field.getType(), null);
                field.set(target, value);
            }

        }catch(Exception e) {
            throw new DataIdServiceException(e);
        }
        return true;
    }
	
	public static String extractRealValue(Object value)
	{
		if(value == null)
			return null;
		else if(value.getClass() == LinkedHashMap.class)
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
        else if(Map.Entry.class.isAssignableFrom(value.getClass()))
		{
			return extractRealValue(((Map.Entry)value).getValue());
		}
		else
			return String.valueOf(value);
	}

    public static List<String> GetEntriesOfJsonLdNode(Object genericValue)
    {
        List<String> retList = new ArrayList<>();
        if (genericValue == null)
            return retList;
        else if (LinkedHashMap.class.isAssignableFrom(genericValue.getClass())) {
            for (Object sub : ((LinkedHashMap) genericValue).entrySet()) {
                retList.add(PropertyMapper.extractRealValue(sub));
            }
        }
        else if (List.class.isAssignableFrom(genericValue.getClass())) {
            for (Object sub : ((List) genericValue)) {
                retList.add(PropertyMapper.extractRealValue(sub));
            }
        }
        else
            retList.add(PropertyMapper.extractRealValue(genericValue));
        return  retList;
    }

    private LinkedHashMap<String, Object> getSubGraphMap(String key)
    {
        for(DataIdBody.DataIdObject map: this.currentId.getDataIdBody().getBody())
        {
            if(map.get("@id").toString().equals(key))
                return map.getMap();
        }
        return null;
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

	public void synchronizeRdfContexts(RdfContext dataIdContext) throws DataIdInputException
	{
        if(dataIdContext == null || dataIdContext.isNull())
            return;
		this.contextSynchronization.clear();
		for(String prefix : dataIdContext)
		{
			String postfix = dataIdContext.getUri(prefix);
			String configPrefix = StaticContent.getRdfContext().getPrefix(postfix);
			if(configPrefix != null)
				this.contextSynchronization.put(prefix,  configPrefix);
		}
	}

	public MappingConfig getMappingConfig() {
		return mappingConfig;
	}
	
}
