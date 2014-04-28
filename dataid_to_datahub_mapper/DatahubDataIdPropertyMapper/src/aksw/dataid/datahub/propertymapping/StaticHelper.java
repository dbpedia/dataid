package aksw.dataid.datahub.propertymapping;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.JavaType;

import aksw.dataid.datahub.jsonobjects.DatahubResponse;
import aksw.dataid.datahub.jsonobjects.Dataset;
import aksw.dataid.datahub.jsonobjects.DatasetExtras;
import aksw.dataid.datahub.mappingobjects.DataIdProperty;

public class StaticHelper 
{
	public static ObjectMapper mapper = new ObjectMapper();
		
	@SuppressWarnings("unchecked")
	public static <E> E dynamicGet(Object object, String fieldName) 
	{
	    Class<?> clazz = object.getClass();
	    while (clazz != null) {
	        try {
	            Field field = clazz.getDeclaredField(fieldName);
	            field.setAccessible(true);
	            return (E) field.get(object);
	        } catch (NoSuchFieldException e) {
	            clazz = clazz.getSuperclass();
	        } catch (Exception e) {
	            throw new IllegalStateException(e);
	        }
	    }
	    return null;
	}
	
	@SuppressWarnings("unchecked")
	public static boolean SetDatasetProperty(Dataset set, DataIdProperty fieldProperty, String fieldValue) 
	{
	        try {
	            Field field = getField(fieldProperty);
	        	if(fieldProperty.isReadOnly())
	        	{	return false;    }
	        	else if(fieldProperty.isList())
	        	{
	        		ParameterizedType listType = (ParameterizedType) field.getGenericType();
	                Class<?> listClass = (Class<?>) listType.getRawType();//getActualTypeArguments()[0];
		            Object value = castToValue(fieldValue, listClass, listType);
		            field.set(set, value);
		            return true;
	        	}
	        	else if(fieldProperty.isAdditionalKey())
	        	{
	        		field = Dataset.class.getDeclaredField("extras");
	        		Method add = List.class.getDeclaredMethod("add",Object.class);
	        		DatasetExtras extras = new DatasetExtras();
	        		extras.setKey(fieldProperty.getId());
	        		extras.setValue(fieldValue);
	        		//TODO extras added?
	        		add.invoke(field.get(set), extras);
		            return true;
	        	}
	        	else
	        	{
		            Object value = castToValue(fieldValue, field.getType(), null);
		            field.set(set, value);
		            return true;
	        	}

	        }catch (Exception e) {
	    	    return false;
	        }
	}

	private static <T> T castToValue(String fieldValue, Class<T> type, ParameterizedType listType) 
	{
		ObjectMapper mapper = new ObjectMapper();
		T value = null;
			try {
				if(listType != null)
				{
					Class<?> t = (Class<?>) listType.getActualTypeArguments()[0];
					JavaType javaType = mapper.getTypeFactory().constructParametricType(List.class,t);
					value = mapper.readValue(fieldValue, javaType);
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

	private static Field getField(DataIdProperty fieldProperty)
	{
		Field field = null;
		try {
			field = Dataset.class.getDeclaredField(fieldProperty.getDatahub());
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
}
