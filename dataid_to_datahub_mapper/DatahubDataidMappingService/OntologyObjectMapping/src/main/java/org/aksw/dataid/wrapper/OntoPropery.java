package org.aksw.dataid.wrapper;

import org.aksw.dataid.ontology.DataIdPart;
import org.openrdf.model.URI;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by Chile on 3/10/2015.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface OntoPropery
{
    public enum OntologyUsage{
        DataIdCore,     //core DataId ontology
        DataIdDmp       //Data Management Plan extension
    }

    String property();                                                  //property resource uri
    boolean recommended() default false;                                //property is recommended for usage (but not mandatory)
    OntologyUsage ontoUsage() default OntologyUsage.DataIdCore;         //property belongs to the core DataId ontology
    int maxCard() default 0;                                            //max amount of triples with this predicate
    int minCard() default 0;                                            //min ----"-----
    String alternative() default "none";                                //alternative to this property (incl. all restrictions)
    boolean derivable() default false;                                  //this property can be derived from a parent DataIdPart
}
