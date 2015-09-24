package org.aksw.dataid.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import org.aksw.dataid.jsonutils.StaticJsonHelper;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.*;
import java.util.regex.Matcher;

/**
 * Created by Chile on 8/16/2015.
 */
public class DataIdConfig {
	
	private static DataIdConfig instance;

    private String mainPath;
    private String mainConfigPath;
    private JsonNode mainConfigFile;
    
    /**
     * Standard constructor of singleton
     */
    private DataIdConfig() {
    	
    }
    
    public static DataIdConfig getInstance() {
    	if (null == instance) {
    		instance = new DataIdConfig();
    	}
    	
    	return instance;
    }

    public static void initDataIdConfig(String mainConfigPath, String mainPath) throws FileNotFoundException {
    	DataIdConfig configInstance = getInstance();
        
    	configInstance.mainPath = mainPath;
    	configInstance.mainConfigPath = mainConfigPath;
        System.out.println(mainConfigPath);
        InputStream inputStream = new FileInputStream(mainConfigPath);
        String content = StaticJsonHelper.GetStringFromInputStream(inputStream);
        configInstance.mainConfigFile = StaticJsonHelper.convertStringToJsonNode(content);
        
        String withoutPlaceholder = configInstance.replacePlaceholder(content);
        configInstance.mainConfigFile = StaticJsonHelper.convertStringToJsonNode(withoutPlaceholder);
    }

    public String getMappingConfigPath()
    {
        String zw = mainConfigFile.get("mappingConfigPath").asText();
        return zw.startsWith("/") ? (mainPath + zw) : (mainPath + "/" + zw);
    }

    public String getVirtuosoHost()
    {
        return mainConfigFile.get("virtuosoHost").toString().replace("\"", "");
    }
    public Integer getVirtuosoPort()
    {
        return Integer.parseInt(mainConfigFile.get("virtuosoPort").toString());
    }
    public String getVirtuosoUser()
    {
        return mainConfigFile.get("virtuosoUser").toString().replace("\"", "");
    }
    public String getVirtuosoPassword()
    {
        return mainConfigFile.get("virtuosoPass").toString().replace("\"", "");
    }

    public String get(String key)
    {
        return mainConfigFile.get(key).asText().replace("\"", "");
    }

    public Iterator<Map.Entry<String, JsonNode>> getActionMap()
    {
        return mainConfigFile.get("ckanActionMap").fields();
    }

    private Map<String, Map.Entry<String, String>> ontoMap = null;
    public Map<String, Map.Entry<String, String>> getOntologies()
    {
        if(ontoMap == null) {
            ontoMap = new HashMap<>();
            Iterator<Map.Entry<String, JsonNode>> iter = mainConfigFile.get("ontologyMap").fields();
            while (iter.hasNext()) {
                Map.Entry<String, JsonNode> zw = iter.next();
                ontoMap.put(zw.getKey(), new AbstractMap.SimpleEntry<String, String>(zw.getValue().get("uri").asText(), zw.getValue().get("dlUrl").asText()));
            }
        }
        return ontoMap;
    }

    public Map<String, List<String>> getExceptions()
    {
        try {
            TypeReference<Map<String, List<String>>> typeRef = new TypeReference<Map<String, List<String>>>(){};
            return StaticJsonHelper.castJsonToObject(mainConfigFile.get("exceptionMap"), typeRef);
        } catch (Exception e) {
            return null;
        }
    }

    private String replacePlaceholder(String input)
    {
        Matcher matcher = java.util.regex.Pattern.compile("\\{\\$[^\\}]+\\}").matcher(input);
        while(matcher.find())
        {
            String match = matcher.group();
            String replacement = match.replace("{", "").replace("}", "").replace("$", "");
            replacement = mainConfigFile.get(replacement).asText();
            input =  input.replace(match, replacement);
        }
        return input;
    }
}
