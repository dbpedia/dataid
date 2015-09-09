package org.aksw.dataid.wrapper;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.aksw.dataid.statics.StaticContent;
import org.aksw.dataid.statics.StaticFunctions;
import org.openrdf.model.Literal;
import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;

import java.lang.reflect.InvocationTargetException;
import java.math.BigInteger;
import java.text.ParseException;
import java.util.*;


/**
 * Created by Chile on 3/12/2015.
 */
public class InternalLieteralImpl {

    private static final Map<URI, Class<?>> xsdTypeMap = new HashMap<URI, Class<?>>();

    private Map<String, String> valueMap = new HashMap<String, String>();
    private URI datatype;

    public InternalLieteralImpl(){
        if(xsdTypeMap.size() == 0)
            fillTypeMap();
        this.setDatatype("http://www.w3.org/2001/XMLSchema#string");
    }

    public InternalLieteralImpl(Map<String, String> langMap)
    {
        this();
        this.setValueMap(langMap);
    }

    public InternalLieteralImpl(Literal lit)
    {
        this();
        this.datatype = lit.getDatatype();
        if(lit.getLanguage() != null)
            valueMap.put(lit.getLanguage(), lit.getLabel());
        else
            valueMap.put("default", lit.getLabel());
    }

    @JsonIgnore
    public String getLabel()
    {
        return getLabel("default");
    }

    @JsonIgnore
    public String getLabel(String lang) {
        if(valueMap.size() > 0)
        {
            String en = valueMap.get(lang);
            if(en != null)
                return en;
        }
        return null;
    }

    @JsonIgnore
    public void setLabel(String label) {

        this.valueMap.put("default", label);
    }

    public String getDatatype() {

        if(this.isString())
            return StaticContent.xsdString.stringValue();
        else
            return datatype.stringValue();
    }

    public void setDatatype(String datatype) {

        this.datatype = new URIImpl(datatype);
    }

    public Map<String, String> getValueMap() {
        return valueMap;
    }

    public void setValueMap(Map<String, String> map)
    {
        this.valueMap = map;
    }


    public void addTranslation(String lab, String language)
    {
        if(language == null)
            this.valueMap.put("default", lab);
        else
            this.valueMap.put(language, lab);
    }

    public List<Literal> allValues()
    {
        List<Literal> vals = new ArrayList<Literal>();
        if(!isString())
            vals.add(toDataLiteral());
        else
            for(String lang : getValueMap().keySet())
                vals.add(toStringLiteral(lang));
        return vals;
    }

    public Literal toDataLiteral()
    {
        if(datatype != null)
            return new org.openrdf.model.impl.LiteralImpl(this.getLabel(), datatype);
        else
            return new org.openrdf.model.impl.LiteralImpl(this.getLabel(), StaticContent.xsdString);
    }

    public Literal toStringLiteral() {
        return new org.openrdf.model.impl.LiteralImpl(this.getLabel(), StaticContent.xsdString);
    }

    public Literal toStringLiteral(String lang)
    {
        if(valueMap.get(lang) != null)
            return new org.openrdf.model.impl.LiteralImpl(valueMap.get(lang), lang);
        else
            return new org.openrdf.model.impl.LiteralImpl(this.getLabel(), StaticContent.xsdString);
    }

    @JsonIgnore
    public boolean isString()
    {
        if(datatype == null || datatype.equals(StaticContent.xsdString) || datatype.equals(StaticContent.rdfsString))
            return true;
        else
            return false;
    }

    public <T extends Object> T value()
    {
        if(!isString())  //not a string
        {
            Class<?> type = xsdTypeMap.get(datatype);
            if (type == null)
                return (T) this.getLabel();   //has to be a string val

            try {
                if (type == Date.class)
                    return (T) StaticFunctions.ParseDate(this.getLabel());
                else
                    return (T) type.getConstructor(String.class).newInstance(this.getLabel());
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        else
            return (T) this.getLabel();
        return null;
    }

    private void fillTypeMap()
    {
        xsdTypeMap.put(new URIImpl("http://www.w3.org/2001/XMLSchema#string"), String.class);
        xsdTypeMap.put(new URIImpl("http://www.w3.org/2001/XMLSchema#time"), Date.class);
        xsdTypeMap.put(new URIImpl("http://www.w3.org/2001/XMLSchema#dateTime"), Date.class);
        xsdTypeMap.put(new URIImpl("http://www.w3.org/2001/XMLSchema#decimal"), Float.class);
        xsdTypeMap.put(new URIImpl("http://www.w3.org/2001/XMLSchema#double"), Double.class);
        xsdTypeMap.put(new URIImpl("http://www.w3.org/2001/XMLSchema#float"), Float.class);
        xsdTypeMap.put(new URIImpl("http://www.w3.org/2001/XMLSchema#int"), Integer.class);
        xsdTypeMap.put(new URIImpl("http://www.w3.org/2001/XMLSchema#long"), Long.class);
        xsdTypeMap.put(new URIImpl("http://www.w3.org/2001/XMLSchema#negativeInteger"), BigInteger.class);
        xsdTypeMap.put(new URIImpl("http://www.w3.org/2001/XMLSchema#integer"), BigInteger.class);
        xsdTypeMap.put(new URIImpl("http://www.w3.org/2001/XMLSchema#nonNegativeInteger"), BigInteger.class);
        xsdTypeMap.put(new URIImpl("http://www.w3.org/2001/XMLSchema#nonPositiveInteger"), BigInteger.class);
        xsdTypeMap.put(new URIImpl("http://www.w3.org/2001/XMLSchema#positiveInteger"), BigInteger.class);
        xsdTypeMap.put(new URIImpl("http://www.w3.org/2001/XMLSchema#short"), Short.class);
        xsdTypeMap.put(new URIImpl("http://www.w3.org/2001/XMLSchema#unsignedByte"), Short.class);
        xsdTypeMap.put(new URIImpl("http://www.w3.org/2001/XMLSchema#unsignedInt"), Long.class);
        xsdTypeMap.put(new URIImpl("http://www.w3.org/2001/XMLSchema#unsignedLong"), BigInteger.class);
        xsdTypeMap.put(new URIImpl("http://www.w3.org/2001/XMLSchema#unsignedShort"), Integer.class);
    }
}
