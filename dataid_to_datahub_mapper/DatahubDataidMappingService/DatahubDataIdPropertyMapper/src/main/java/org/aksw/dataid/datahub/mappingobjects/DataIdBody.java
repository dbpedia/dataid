package org.aksw.dataid.datahub.mappingobjects;

import org.aksw.dataid.wrapper.RdfContext;

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
    private List<DataIdObject> list;
    private RdfContext context;

    public DataIdBody(List<LinkedHashMap<String, Object>> list, RdfContext context)
    {
        this.list = new ArrayList<DataIdObject>();
        for(LinkedHashMap<String, Object> map : list)
        {
            this.list.add(new DataIdObject(map));
        }
        this.context = context;
    }

    public DataIdBody(RdfContext context)
    {
        this.list = new ArrayList<DataIdObject>();
        this.context = context;
    }

    public void addDataIdObject(LinkedHashMap<String, Object> map)
    {
        list.add(new DataIdObject(map));
    }

    public List<DataIdObject> getBody()
    {
        return list;
    }
}
