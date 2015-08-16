package org.aksw.dataid.ontology;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.aksw.dataid.wrapper.*;
import org.openrdf.model.Literal;
import org.openrdf.model.Model;
import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.rio.RDFHandlerException;

import javax.naming.directory.InvalidAttributesException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Chile on 3/11/2015.
 */
public abstract class DataIdPart
{
    private boolean loaded;
    private Map<OntoPropery, Field> annos;
    private ErrorWarningWrapper errorswarnings = new ErrorWarningWrapper();

    private URI parent;

    private List<LazyParentReference> unresolvedValues = new ArrayList<LazyParentReference>();

    @OntoPropery(property = "http://www.w3.org/2000/01/rdf-schema#label", maxCard = 1, recommended = true)
    private InternalLieteralImpl label;
    @OntoPropery(property = "http://www.w3.org/2000/01/rdf-schema#comment", maxCard = 1)
    private InternalLieteralImpl comment;
    @OntoPropery(property = "http://purl.org/dc/terms/identifier", maxCard = 1, minCard = 1)
    private URI identifier;

    public String toTurtle(OntoPropery.OntologyUsage usage) throws RDFHandlerException {
        return ModelWrapper.writeTurtle(toTransitiveModel(usage));
    }

    public Map<OntoPropery, Field> annotations()
    {
        if(annos == null) {
            annos = new HashMap<OntoPropery, Field>();
            for (Field field : this.getClass().getDeclaredFields()) {
                Annotation[] annotations = field.getDeclaredAnnotations();
                for (Annotation anno : annotations)
                    if (OntoPropery.class.equals(anno.annotationType())) {
                        field.setAccessible(true);
                        annos.put((OntoPropery) anno, field);
                    }
            }
        }
        return annos;
    }

    public OntoPropery getAnnotation(String uri)
    {
        for(OntoPropery anno : this.annotations().keySet())
        {
            if(anno.property().toLowerCase().trim().equals(uri.toLowerCase().trim()))
                return anno;
        }
        return null;
    }

    private Object getTransitiveValue(OntoPropery anno, Field f)
    {
        try {
            DataIdPart parent = ModelWrapper.getDataIdPart(this.parent);
            Object val = f.get(this);
            if(val == null && anno.derivable())
                val = parent.getTransitiveValue(anno, f);
            return val;
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvalidAttributesException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void wasSuccessfullyLoaded(boolean result)
    {
        this.loaded = result;
    }

    public boolean wasSuccessfullyLoaded()
    {
        return loaded;
    }

    public Model toFlatModel(OntoPropery.OntologyUsage usage)
    {
        return toModel(usage, false);
    }

    public Model toTransitiveModel(OntoPropery.OntologyUsage usage)
    {
        return toModel(usage, true);
    }

    private Model toModel(OntoPropery.OntologyUsage usage, boolean trans)
    {
        Model model = ModelWrapper.createDefaultModel();
        model.add(this.identifier, ModelWrapper.a, ModelWrapper.GetDataIdUri(ModelWrapper.dataIdStump, "Dataset"));

        try {
            for(OntoPropery anno : this.annotations().keySet())
                {
                    if(anno.ontoUsage() == OntoPropery.OntologyUsage.DataIdDmp && usage != OntoPropery.OntologyUsage.DataIdDmp)
                        continue;
                    Field field = this.annotations().get(anno);
                    Object val = getTransitiveValue(anno, field);
                    if(val != field.get(this))  //property was derived from parent
                        errorswarnings.addWarning(this.identifier.toString(), anno.property(), " was inherited from parent!");
                    if (InternalLieteralImpl.class.isAssignableFrom(field.getType()))
                        for (Literal lit : ((InternalLieteralImpl) val).allValues())
                            model.add(this.identifier, new URIImpl(anno.property()), lit);
                    else if(DataIdPart.class.isAssignableFrom(field.getType())) {
                        model.add(this.identifier, new URIImpl(anno.property()), ((DataIdPart) val).identifier);
                        if(trans)   //add all triples of submodel
                            model.addAll(((DataIdPart) val).toTransitiveModel(usage));
                    }
                    else if(List.class.isAssignableFrom(field.getType()))
                    {
                        ParameterizedType stringListType = (ParameterizedType) field.getGenericType();
                        Class<?> genericListClass = (Class<?>) stringListType.getActualTypeArguments()[0];

                        List<Object> list = (List<Object>) val;
                        if(list != null)
                        {
                            for(Object obj : list)
                            {
                                if(InternalLieteralImpl.class.isAssignableFrom(genericListClass))
                                    for (Literal lit : ((InternalLieteralImpl) obj).allValues())
                                        model.add(this.identifier, new URIImpl(((OntoPropery)anno).property()), lit);
                                else if(DataIdPart.class.isAssignableFrom(genericListClass))
                                    model.add(this.identifier, new URIImpl(((OntoPropery)anno).property()), ((DataIdPart)obj).identifier);
                            }
                        }
                    }
                }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return model;
    }

    public void checkInstanceAgainstOntology(OntoPropery.OntologyUsage usage) {
        for(OntoPropery anno : this.annotations().keySet())
        {
            if(anno.ontoUsage() == OntoPropery.OntologyUsage.DataIdDmp && usage != OntoPropery.OntologyUsage.DataIdDmp)
                continue;
            try {
                Field f = this.annotations().get(anno);
                f.setAccessible(true);
                Object val = f.get(this);
                addNoTripleError(anno, null, val);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    private void addNoTripleError(OntoPropery anno, OntoPropery original, Object val) throws IllegalAccessException {
        if(val == null) {
            if(!anno.alternative().equals("none"))  //not!
            {
                OntoPropery o = getAnnotation(anno.alternative());
                Field f = this.annotations().get(o);
                f.setAccessible(true);
                Object v = f.get(this);
                addNoTripleError(o, anno, v);
                return;
            }
            if(anno.minCard() > 0)
                this.errorswarnings.addError(this.identifier.stringValue(), original == null ? anno.property() : original.property(), "has no value but minCardinality > 0");
            else if(anno.recommended())
                this.errorswarnings.addWarning(this.identifier.stringValue(), original == null ? anno.property() : original.property(), " is recommended but was not provided");
        }
        else{
            if(val.getClass().isAssignableFrom(List.class)) {
                if(!anno.alternative().equals("none") && ((List<?>) val).size() == 0)  //not!
                {
                    OntoPropery o = getAnnotation(anno.alternative());
                    Field f = this.annotations().get(o);
                    f.setAccessible(true);
                    Object v = f.get(this);
                    addNoTripleError(o, anno, v);
                    return;
                }
                if (anno.minCard() > ((List<?>) val).size())
                    this.errorswarnings.addError(this.identifier.stringValue(), original == null ? anno.property() : original.property(), " has more values than allowed by maxCardinality");
                else if(anno.maxCard() < ((List<?>)val).size())
                    this.errorswarnings.addError(this.identifier.stringValue(), original == null ? anno.property() : original.property(), "has more values than allowed by maxCardinality");
                else if (anno.recommended() && ((List<?>) val).size() == 0)
                    this.errorswarnings.addError(this.identifier.stringValue(), original == null ? anno.property() : original.property(), " is recommended but was not provided");
            }
        }
    }

    public InternalLieteralImpl getComment() {
        return comment;
    }

    public void setComment(InternalLieteralImpl comment) {
        this.comment = comment;
    }

    public InternalLieteralImpl getLabel() {
        return label;
    }

    public void setLabel(InternalLieteralImpl label) {
        this.label = label;
    }

    public String getIdentifier()
    {
        return this.identifier.stringValue();
    }

    public void setIdentifier(String id)
    {
        this.identifier = new URIImpl(id);
    }

    public ErrorWarningWrapper getErrorswarnings() {
        return errorswarnings;
    }

    public URI getParent() {
        return parent;
    }

    public void setParent(URI parent) {
        this.parent = parent;
    }

    @JsonIgnore
    public List<LazyParentReference> getUnresolvedValues() {
        return unresolvedValues;
    }

    public void resolveAllParentReferences() throws InvalidAttributesException {
        for (LazyParentReference ref : unresolvedValues) {
            ref.resolveParentReference();
        }
        unresolvedValues.clear();

        try {
            for (OntoPropery anno : this.annotations().keySet()) {
                Field f = annotations().get(anno);
                f.setAccessible(true);
                Object val = f.get(this);
                if (val != null && DataIdPart.class.isAssignableFrom(f.getType())) {
                    ((DataIdPart) val ).resolveAllParentReferences();
                } else if (val != null && List.class.isAssignableFrom(f.getType())) {
                    ParameterizedType stringListType = (ParameterizedType) f.getGenericType();
                    Class<?> genericListClass = (Class<?>) stringListType.getActualTypeArguments()[0];
                    if (DataIdPart.class.isAssignableFrom(genericListClass)) {
                        for (DataIdPart p : (List<DataIdPart>) val) {
                            p.resolveAllParentReferences();
                        }
                    }
                }
            }
        }
        catch(IllegalAccessException e)
        {
            e.printStackTrace();
        }
    }
}
