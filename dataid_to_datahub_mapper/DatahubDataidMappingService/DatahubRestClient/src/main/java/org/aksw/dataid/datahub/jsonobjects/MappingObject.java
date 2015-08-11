package org.aksw.dataid.datahub.jsonobjects;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by Chile on 3/8/2015.
 */
public interface MappingObject {
    List<LinkedHashMap<String, Object>> getGraph();
    void setGraph(List<LinkedHashMap<String, Object>> graph);
    void addChild(LinkedHashMap<String, Object> child);
}
