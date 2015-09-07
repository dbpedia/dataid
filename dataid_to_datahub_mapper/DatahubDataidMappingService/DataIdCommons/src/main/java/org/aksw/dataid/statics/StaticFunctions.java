package org.aksw.dataid.statics;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import org.aksw.dataid.errors.DataIdInputException;
import org.aksw.dataid.jsonutils.StaticJsonHelper;
import org.apache.xml.serializer.utils.Utils;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFParseException;

import java.io.IOException;

/**
 * Created by Chile on 9/7/2015.
 */
public class StaticFunctions {

    public static RDFFormat getRDFFormat(String input) throws DataIdInputException {
        input = input.trim();
        StringBuilder errorBuilder = new StringBuilder();
        try {
            if(StaticJsonHelper.isJsonLdValid(input))
                return RDFFormat.JSONLD;
        } catch (Exception e) {
            errorBuilder.append("JSONLD: " + e.getMessage() + "\n");
        }
        try {
        if(StaticJsonHelper.isTurtleValid(input))
            return RDFFormat.TURTLE;
        } catch (Exception e) {
            errorBuilder.append("TURTLE: " + e.getMessage() + "\n");
        }
        try {
        if(StaticJsonHelper.isNquadValid(input))
            return RDFFormat.NQUADS;
        } catch (Exception e) {
            errorBuilder.append("NQUADS: " + e.getMessage() + "\n");
        }
        try {
        if(StaticJsonHelper.isRdfXmlValid(input))
            return RDFFormat.RDFXML;
        } catch (Exception e) {
            errorBuilder.append("RDFXML: " + e.getMessage() + "\n");
        }
        throw new DataIdInputException("Serialization format could not be determined. Please check the following parser errors:\n" + errorBuilder.toString());
    }
}
