package org.aksw.dataid.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.aksw.dataid.jsonutils.StaticJsonHelper;

import java.util.*;

/**
 * Created by Chile on 8/16/2015.
 */
public class DataIdConfig {

    private static String mainPath;
    private static String mainConfigPath;
    private static JsonNode mainConfigFile;

    public static void initDataIdConfig(String mPath)
    {
        mainPath = mPath;
        mainConfigPath = mainPath + "/MainConfig.json";
        System.out.println(mainConfigPath);
        mainConfigFile = StaticJsonHelper.getJsonContent(mainConfigPath);
    }

    public static String getMappingConfigPath()
    {
        String zw = mainConfigFile.get("mappingConfigPath").asText();
        return zw.startsWith("/") ? (mainPath + zw) : (mainPath + "/" + zw);
    }

    public static String getVirtuosoHost()
    {
        return mainConfigFile.get("virtuosoHost").toString().replace("\"", "");
    }
    public static Integer getVirtuosoPort()
    {
        return Integer.parseInt(mainConfigFile.get("virtuosoPort").toString());
    }
    public static String getVirtuosoUser()
    {
        return mainConfigFile.get("virtuosoUser").toString().replace("\"", "");
    }
    public static String getVirtuosoPassword()
    {
        return mainConfigFile.get("virtuosoPass").toString().replace("\"", "");
    }

    public static String get(String key)
    {
        return mainConfigFile.get(key).asText().replace("\"", "");
    }

    public static Iterator<Map.Entry<String, JsonNode>> getActionMap()
    {
        return mainConfigFile.get("ckanActionMap").fields();
    }

    private static Map<String, Map.Entry<String, String>> ontoMap = null;
    public static Map<String, Map.Entry<String, String>> getOntologies()
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

    private static Map<String, List<String>> exceptions = null;
    public static Map<String, List<String>> getExceptions()
    {
        try {
            TypeReference<Map<String, List<String>>> typeRef = new TypeReference<Map<String, List<String>>>(){};
            return StaticJsonHelper.castJsonToObject(mainConfigFile.get("exceptionMap"), typeRef);
        } catch (Exception e) {
            return null;
        }
    }
}
