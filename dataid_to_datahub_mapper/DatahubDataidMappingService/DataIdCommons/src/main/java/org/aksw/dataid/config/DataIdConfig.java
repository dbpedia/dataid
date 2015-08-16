package org.aksw.dataid.config;

import com.fasterxml.jackson.databind.JsonNode;
import org.aksw.dataid.jsonutils.StaticJsonHelper;

import java.util.Iterator;
import java.util.Map;

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

    public static String getOntologyPath()
    {
        return mainPath + "/ontology.ttl";
    }

    public static String getDataIdUri()
    {
        return get("dataIdUri");
    }

    public static String get(String key)
    {
        return mainConfigFile.get(key).asText().replace("\"", "");
    }

    public static Iterator<Map.Entry<String, JsonNode>> getActionMap()
    {
        return mainConfigFile.get("ckanActionMap").fields();
    }
}
