package org.aksw.dataid.datahub.mappingobjects;

import org.aksw.dataid.config.RdfContext;
import org.aksw.dataid.datahub.propertymapping.PropertyMapper;

import java.util.*;

/**
 * Created by Chile on 3/8/2015.
 */
public class DataIdBody{

    public class DataIdObject implements Map<String, Object>
    {
        private LinkedHashMap<String, Object> map = new LinkedHashMap<String, Object>();

        public DataIdObject(LinkedHashMap<String, Object> map)
        {
            this.map = map;
        }

        @Override
        public int size() {
            return map.size();
        }

        @Override
        public boolean isEmpty() {
            return map.isEmpty();
        }

        @Override
        public boolean containsKey(Object key) {
            if(map.containsKey(key) || map.containsKey(context.toggle(key.toString())))
                return true;
            return false;
        }

        @Override
        public boolean containsValue(Object value) {
            return map.containsValue(value);
        }

        @Override
        public Object get(Object key) {
            Object ret = map.get(key);
            if(ret == null)
                ret = map.get(context.toggle(key.toString()));
            return ret;
        }

        @Override
        public Object put(String key, Object value) {
            return map.put(key, value);
        }

        @Override
        public Object remove(Object key) {
            Object ret = map.remove(key);
            if(ret == null)
                ret = map.remove(context.toggle(key.toString()));
            return ret;
        }

        @Override
        public void putAll(Map<? extends String, ?> m) {
            map.putAll(m);
        }

        @Override
        public void clear() {
            map.clear();
        }

        @Override
        public Set<String> keySet() {
            return map.keySet();
        }

        @Override
        public Collection<Object> values() {
            return map.values();
        }

        @Override
        public Set<Entry<String, Object>> entrySet() {
            return map.entrySet();
        }

        @Override
        public boolean equals(Object o) {
            return map.equals(o);
        }

        @Override
        public int hashCode() {
            return map.hashCode();
        }

        public LinkedHashMap<String, Object> getMap() {
            return map;
        }
    }

    private List<DataIdObject> graph;
    private RdfContext context;

    private Map<String,LinkedHashMap<String, Object>> retMap = new HashMap<>();
    private Map<String, ArrayList<Map.Entry<String, String>>> agents = new HashMap<>();

    public DataIdBody(List<LinkedHashMap<String, Object>> graph, RdfContext context)
    {
        addRoleAgentMap(graph);
        this.graph = new ArrayList<DataIdObject>();
        for(LinkedHashMap<String, Object> map : graph)
        {
            this.graph.add(new DataIdObject(map));
        }
        this.context = context;
    }

    public DataIdBody(RdfContext context)
    {
        this.graph = new ArrayList<DataIdObject>();
        this.context = context;
    }

    private void addRoleAgentMap(List<LinkedHashMap<String, Object>> graph)
    {
        for(LinkedHashMap<String, Object> map : graph)
        {
            filterAuthorityContexts(map);

            if(map.get("@type").equals("dataid:DataId") || map.get("@type").equals("dataid:Dataset") || map.get("@type").equals("dataid:SingleFile")
                    || map.get("@type").equals("dataid:SparqlENdpoint") || map.get("@type").equals("dataid:Directory") || map.get("@type").equals("dataid:ServiceEndpoint"))
            {
                retMap.put(map.get("@id").toString(), map);
            }
        }
        String dataidUri = null;
        for(LinkedHashMap<String, Object> map : graph)
        {
            if(map.get("@type").equals("dataid:DataId"))
            {
                dataidUri = map.get("@id").toString();
                break;
            }
        }
        if(dataidUri == null)
            return;
        for(Map.Entry<String, LinkedHashMap<String, Object>> ent : retMap.entrySet())
        {
            for(Map.Entry<String, String> zw : agents.get(dataidUri))
                ent.getValue().put(zw.getKey(), zw.getValue());
        }
        for(LinkedHashMap<String, Object> map : graph) {
            if (map.get("@type").equals("dataid:Dataset")) {
                insertAgentsInDataset(map.get("@id").toString());
            }
        }
    }

    private void filterAuthorityContexts(LinkedHashMap<String, Object> map) {
        if(map.get("@type").equals("dataid:AuthorityEntityContext"))
        {
            for(String ent : PropertyMapper.GetEntriesOfJsonLdNode(map.get("dataid:authorizedFor")))
            {
                insertAuthorities(map, ent);
            }
        }
    }

    private void insertAuthorities(LinkedHashMap<String, Object> map, String authFor)
    {
        for(String ent : PropertyMapper.GetEntriesOfJsonLdNode(map.get("dataid:authorityAgentRole")))
        {
            if(agents.get(authFor) == null)
                agents.put(authFor, new ArrayList<Map.Entry<String, String>>());
            agents.get(authFor).add(new AbstractMap.SimpleEntry<String, String>(ent, PropertyMapper.extractRealValue(map.get("dataid:authorizedAgent"))));
        }
    }

    private void insertAgentsInDataset(String datasetUri)
    {
        if(agents.get(datasetUri) != null) {
            for (Map.Entry<String, String> zw : agents.get(datasetUri)) {
                insertAgentsInDataset(PropertyMapper.extractRealValue(datasetUri), zw.getKey(), zw.getValue());

                for(String ent : PropertyMapper.GetEntriesOfJsonLdNode(retMap.get(datasetUri).get("void:subset")))
                {
                    insertAgentsInDataset(ent, zw.getKey(), zw.getValue());
                }
            }
        }
    }

    private void insertAgentsInDataset(String datasetUri, String role, String agent)
    {
        LinkedHashMap<String, Object> dat = retMap.get(datasetUri);
        if(dat != null)
            dat.put(role, agent);
        else
            return;

        for(String ent : PropertyMapper.GetEntriesOfJsonLdNode(dat.get("dcat:distribution")))
        {
            insertAgentsInDataset(ent, role, agent);
        }
    }

    public void addDataIdObject(LinkedHashMap<String, Object> map)
    {
        graph.add(new DataIdObject(map));
    }

    public List<DataIdObject> getBody()
    {
        return graph;
    }
}
