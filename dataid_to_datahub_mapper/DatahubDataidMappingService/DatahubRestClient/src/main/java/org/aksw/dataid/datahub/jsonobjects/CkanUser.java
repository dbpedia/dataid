package org.aksw.dataid.datahub.jsonobjects;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by Chile on 1/18/2016.
 */
public class CkanUser implements ValidCkanResponse, MappingObject{

    private String name;
    private String email;
    private String password;
    private String id;
    private String fullname;
    private String about;
    private String openid;

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


    private List<LinkedHashMap<String, Object>> graph = new ArrayList<LinkedHashMap<String, Object>>();
    @Override
    public List<LinkedHashMap<String, Object>> getGraph() {
        return graph;
    }

    @Override
    public void setGraph(List<LinkedHashMap<String, Object>> graph) {
        this.graph = graph;
    }

    @Override
    public void addChild(LinkedHashMap<String, Object> child) {
        this.graph.add(child);
    }
}
