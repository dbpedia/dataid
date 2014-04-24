package aksw.dataid.datahub.restclient;

import org.codehaus.jackson.map.MapperConfig;
import org.codehaus.jackson.map.PropertyNamingStrategy;
import org.codehaus.jackson.map.introspect.AnnotatedField;
import org.codehaus.jackson.map.introspect.AnnotatedMethod;

public class JsonLdNamingStrategy  extends PropertyNamingStrategy
{
	  @Override
	  public String nameForField(MapperConfig config, AnnotatedField field, String defaultName) {
	     return convert(defaultName);

	  }
	  @Override
	  public String nameForGetterMethod(MapperConfig config, AnnotatedMethod method, String defaultName) {
	     return convert(defaultName);
	  }

	  @Override
	  public String nameForSetterMethod(MapperConfig config, AnnotatedMethod method, String defaultName) {
	   String a = convert(defaultName); 
	   return a;
	  }

	  public String convert(String defaultName )
	  {
		  if(defaultName == "@id")
			  return "id";
		  else
			  return defaultName;
	  }

}
