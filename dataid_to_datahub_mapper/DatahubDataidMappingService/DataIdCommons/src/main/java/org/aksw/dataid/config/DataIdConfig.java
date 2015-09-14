package org.aksw.dataid.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import org.aksw.dataid.jsonutils.StaticJsonHelper;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.*;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;

/**
 * Created by Chile on 8/16/2015.
 */
public class DataIdConfig {

    private static String mainPath;
    private static String mainConfigPath;
    private static JsonNode mainConfigFile;

    public static void initDataIdConfig(String mainConfigPath, String mainPath) throws FileNotFoundException {
        DataIdConfig.mainPath = mainPath;
        DataIdConfig.mainConfigPath = mainConfigPath;
        System.out.println(mainConfigPath);
        InputStream inputStream = new FileInputStream(mainConfigPath);
        String content = StaticJsonHelper.GetStringFromInputStream(inputStream);
        mainConfigFile = StaticJsonHelper.convertStringToJsonNode(content);
        mainConfigFile = StaticJsonHelper.convertStringToJsonNode(replacePlaceholder(content));
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

    public static Map<String, List<String>> getExceptions()
    {
        try {
            TypeReference<Map<String, List<String>>> typeRef = new TypeReference<Map<String, List<String>>>(){};
            return StaticJsonHelper.castJsonToObject(mainConfigFile.get("exceptionMap"), typeRef);
        } catch (Exception e) {
            return null;
        }
    }

    private static String replacePlaceholder(String input)
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
