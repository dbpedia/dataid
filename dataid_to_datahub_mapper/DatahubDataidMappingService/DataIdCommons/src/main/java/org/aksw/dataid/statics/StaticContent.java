package org.aksw.dataid.statics;

import org.aksw.dataid.config.DataIdConfig;
import org.aksw.dataid.config.MappingConfig;
import org.aksw.dataid.config.RdfContext;
import org.aksw.dataid.jsonutils.StaticJsonHelper;

import java.util.HashMap;

/**
 * Created by Chile on 8/16/2015.
 */
public class StaticContent {
    private static RdfContext rdfContext;
    private static MappingConfig mappings;
    public static RdfContext getRdfContext() {
        return rdfContext;
    }

    public static RdfContext setRdfContext(String mappingConfigPath) {
        StaticContent.rdfContext = new RdfContext(StaticJsonHelper.castJsonToObject(StaticJsonHelper.getJsonContent(mappingConfigPath).toString(), new HashMap<String, String>().getClass(), "@context"));
        return rdfContext;
    }

    public static MappingConfig getMappings() {
        return mappings;
    }

    public static MappingConfig setMappings(String mappingConfigPath) {
        StaticContent.mappings = StaticJsonHelper.castJsonToObject(StaticJsonHelper.getJsonContent(mappingConfigPath).toString(), MappingConfig.class, "@graph");
        setRdfContext(mappingConfigPath);
        StaticContent.mappings.setRdfContext(StaticContent.getRdfContext());
        return StaticContent.mappings;
    }

}
