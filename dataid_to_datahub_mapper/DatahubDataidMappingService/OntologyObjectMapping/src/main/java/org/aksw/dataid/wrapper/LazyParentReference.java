package org.aksw.dataid.wrapper;

import org.aksw.dataid.ontology.DataIdPart;
import org.openrdf.model.URI;

import javax.naming.directory.InvalidAttributesException;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.List;

/**
 * Created by Chile on 4/17/2015.
 * Stores parent references for values which are derivable from parent DataIdParts.
 * For lazy loading after initial parsing.
 */
public class LazyParentReference {
    private URI parentRef;
    private OntoPropery annotation;
    private DataIdPart instance;

    public LazyParentReference(URI ref, OntoPropery anno, DataIdPart part)
    {
        this.parentRef = ref;
        this.annotation = anno;
        this.instance = part;
    }

    private Object resolveParentReferenceRecursively() throws InvalidAttributesException {
        DataIdPart part = ModelWrapper.getDataIdPart(parentRef);
        Object val = null;
        try {
            Field f = null;
            for(OntoPropery o : part.annotations().keySet())
                if(o.property().trim().equals(annotation.property().trim()))
                    f = part.annotations().get(o);
            if(f != null)
                val = f.get(part);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        if(val != null && val.getClass() == LazyParentReference.class)
            val = ((LazyParentReference) val).resolveParentReferenceRecursively();

        return val;
    }

    public void resolveParentReference() throws InvalidAttributesException {
        try {
            Field f = instance.annotations().get(annotation);
            f.setAccessible(true);
            if(List.class.isAssignableFrom(f.getType()))
            {
                ParameterizedType stringListType = (ParameterizedType) f.getGenericType();
                Class<?> genericListClass = (Class<?>) stringListType.getActualTypeArguments()[0];
                List<Object> list = (List<Object>) ModelWrapper.CreateGenericList(genericListClass); //we know this list is empty
                list.add(resolveParentReferenceRecursively());
                f.set(instance, list);
            }
            else
                f.set(instance, resolveParentReferenceRecursively());
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
