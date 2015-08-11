package org.aksw.dataid.ontology;

import org.aksw.dataid.wrapper.ErrorWarningWrapper;
import org.aksw.dataid.wrapper.InternalLieteralImpl;
import org.aksw.dataid.wrapper.ModelWrapper;
import org.aksw.dataid.wrapper.OntoPropery;
import org.openrdf.model.Literal;
import org.openrdf.model.Model;
import org.openrdf.model.Resource;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.rio.RDFHandlerException;

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
    private Resource id;
    private Map<OntoPropery, Field> annos;
    private ErrorWarningWrapper errorswarnings = new ErrorWarningWrapper();

    @OntoPropery(property = "http://www.w3.org/2000/01/rdf-schema#label", maxCard = 1, recommended = true)
    private InternalLieteralImpl label;
    @OntoPropery(property = "http://www.w3.org/2000/01/rdf-schema#comment", maxCard = 1)
    private InternalLieteralImpl comment;

    public String toTurtle() throws RDFHandlerException {
        return ModelWrapper.writeTurtle(toTransitiveModel());
    }

    public Map<OntoPropery, Field> annotations()
    {
        if(annos == null) {
            annos = new HashMap<OntoPropery, Field>();
            for (Field field : this.getClass().getDeclaredFields()) {
                Annotation[] annotations = field.getDeclaredAnnotations();
                for (Annotation anno : annotations)
                    if (OntoPropery.class.equals(anno.annotationType()))
                        annos.put((OntoPropery) anno, field);
            }
        }
        return annos;
    }

    public Resource uriId()
    {
        return id;
    }

    public void setUriId(Resource id)
    {
        this.id = id;
    }

    public void wasSuccessfullyLoaded(boolean result)
    {
        this.loaded = result;
    }

    public boolean wasSuccessfullyLoaded()
    {
        return loaded;
    }

    public Model toFlatModel()
    {
        return toModel(false);
    }

    public Model toTransitiveModel()
    {
        return toModel(true);
    }

    private Model toModel(boolean trans)
    {
        Model model = ModelWrapper.createDefaultModel();
        model.add(this.uriId(), ModelWrapper.a, ModelWrapper.GetDataIdUri(ModelWrapper.dataIdStump, "Dataset"));

        for(Annotation anno : this.annotations().keySet())
        {
            try {
                Field field = this.annotations().get(anno);
                if (InternalLieteralImpl.class.isAssignableFrom(field.getType()))
                    for (Literal lit : ((InternalLieteralImpl) field.get(this)).allValues())
                        model.add(this.uriId(), new URIImpl(((OntoPropery)anno).property()), lit);
                else if(DataIdPart.class.isAssignableFrom(field.getType())) {
                    model.add(this.uriId(), new URIImpl(((OntoPropery) anno).property()), ((DataIdPart) field.get(this)).uriId());
                    if(trans)   //add all triples of submodel
                        model.addAll(((DataIdPart) field.get(this)).toTransitiveModel());
                }
                else if(List.class.isAssignableFrom(field.getType()))
                {
                    ParameterizedType stringListType = (ParameterizedType) field.getGenericType();
                    Class<?> genericListClass = (Class<?>) stringListType.getActualTypeArguments()[0];

                    List<Object> list = (List<Object>) field.get(this);
                    if(list != null)
                    {
                        for(Object obj : list)
                        {
                            if(InternalLieteralImpl.class.isAssignableFrom(genericListClass))
                                for (Literal lit : ((InternalLieteralImpl) obj).allValues())
                                    model.add(this.uriId(), new URIImpl(((OntoPropery)anno).property()), lit);
                            else if(DataIdPart.class.isAssignableFrom(genericListClass))
                                model.add(this.uriId(), new URIImpl(((OntoPropery)anno).property()), ((DataIdPart)obj).uriId());
                        }
                    }
                }
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
        }
        return model;
    }

    public void checkInstanceAgainstOntology() {
        for(OntoPropery anno : this.annotations().keySet())
        {
            try {
                Field f = this.annotations().get(anno);
                f.setAccessible(true);
                Object val = f.get(this);
                if(anno.minCard() > 0 && val == null)
                    this.errorswarnings.addError(this.id.stringValue(), anno.property(), "has no value but minCardinality > 0");
                else if(anno.recommended() && val == null)
                    this.errorswarnings.addWarning(this.id.stringValue(), anno.property(), " is recommended but was not provided");

                if(val != null && val.getClass().isAssignableFrom(List.class))
                {
                    if(anno.maxCard() > ((List<?>)val).size())
                        this.errorswarnings.addError(this.id.stringValue(), anno.property(), "has more values than allowed by maxCardinality");
                    if(anno.minCard() < ((List<?>)val).size())
                        this.errorswarnings.addError(this.id.stringValue(), anno.property(), "has less values than allowed by minCardinality");
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    public ErrorWarningWrapper getErrorswarnings() {
        return errorswarnings;
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
}
