package org.aksw.dataid.datahub.jsonobjects;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by Chile on 1/18/2016.
 */
public class IdMappingObject implements MappingObject {

    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public List<LinkedHashMap<String, Object>> getGraph() {
        return null;
    }

    @Override
    public void setGraph(List<LinkedHashMap<String, Object>> graph) {

    }

    @Override
    public void addChild(LinkedHashMap<String, Object> child) {

    }
}
