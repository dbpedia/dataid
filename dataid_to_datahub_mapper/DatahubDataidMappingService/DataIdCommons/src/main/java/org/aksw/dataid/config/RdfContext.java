package org.aksw.dataid.config;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by Chile on 3/7/2015.
 */
public class RdfContext implements Iterable<String>
{
    private Map<String, String> rdfContext = new HashMap<String, String>();

    public RdfContext(Map<String, String> rdfContext)
    {
        this.rdfContext = rdfContext;
    }

    public String getUri(String prefix)
    {
        return rdfContext.get(prefix);
    }

    public String getPrefix(String uri)
    {
        for(String key : rdfContext.keySet())
            if(uri.trim().toLowerCase().equals(rdfContext.get(key).trim().toLowerCase()))
                return key;
        return null;
    }

    public String encode(String uri)
    {
        for(String key : rdfContext.keySet())
        {
            if(uri.toLowerCase().contains(rdfContext.get(key).toLowerCase().trim()))
            {
                return uri.replace(rdfContext.get(key).trim(), key + ":");
            }
        }
        return null;
    }

    public String decode(String encoded)
    {
        String prefix = encoded.substring(0, encoded.indexOf(':')).trim();
        String longform = rdfContext.get(prefix);
        if(longform == null)
            return null;
        return encoded.replace(prefix + ":", longform);
    }

    public String getOther(String xxx)
    {
        if(xxx.contains("/"))
            return getPrefix(xxx);
        else
            return getUri(xxx);
    }

    public String toggle(String xxx)
    {
        if(xxx.contains("/"))
            return encode(xxx);
        else
            return decode(xxx);
    }

    public boolean iriContains(String value, String prefix, String postfix)
    {
        String longPrefix = null;
        if(prefix.contains("/"))
            longPrefix = prefix;
        else
            prefix = prefix.replace(":", "");

        if(longPrefix == null)
            longPrefix = getUri(prefix);

        prefix = getPrefix(longPrefix);
        if(value.contains(prefix + ":" + postfix) || value.contains(longPrefix + postfix))
            return true;
        else
            return false;
    }

    public boolean isNull()
    {
        if(rdfContext == null)
            return true;
        else
            return false;
    }

    public Map<String, String> getMap()
    {
        return this.rdfContext;
    }

    @Override
    public Iterator<String> iterator() {
        return rdfContext.keySet().iterator();
    }
}
