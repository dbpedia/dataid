package aksw.dataid.datahub.jsonutils;

import com.github.jsonldjava.core.JsonLdError;
import com.github.jsonldjava.core.RDFDataset;
import com.github.jsonldjava.core.RDFParser;
import org.openrdf.model.Statement;
import org.openrdf.model.impl.BNodeImpl;
import org.openrdf.model.impl.LiteralImpl;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.rio.ParserConfig;
import org.openrdf.rio.RDFHandler;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFParseException;
import org.openrdf.rio.rdfxml.RDFXMLParser;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.IOException;
import java.io.StringReader;


/**
 * Created by Chile on 2/18/2015.
 */
public class RdfXmlParser implements RDFParser, RDFHandler {

    private RDFDataset currentSet;

    @Override
    public synchronized RDFDataset parse(Object input) throws JsonLdError {
        if (String.class.isAssignableFrom(input.getClass())) {
            StringReader reader = new StringReader(input.toString());
            RDFXMLParser p = new RDFXMLParser();
            p.setRDFHandler(this);
            p.setPreserveBNodeIDs(true);
            try {
                p.parse(reader, "http://someuri.org");
            } catch (IOException e) {
                throw new JsonLdError(JsonLdError.Error.INVALID_INPUT, e.getMessage());
            } catch (RDFParseException e) {
                throw new JsonLdError(JsonLdError.Error.PARSE_ERROR, e.getMessage());
            } catch (RDFHandlerException e) {
                throw new JsonLdError(JsonLdError.Error.PARSE_ERROR, e.getMessage());
            }
            return currentSet;
        } else {
            throw new JsonLdError(JsonLdError.Error.INVALID_INPUT,
                    "RdfXmlParser expected string input.");
        }
    }

    @Override
    public void startRDF() throws RDFHandlerException {
        currentSet = new RDFDataset();
    }

    @Override
    public void endRDF() throws RDFHandlerException {
    }

    @Override
    public void handleNamespace(String space, String prefix) throws RDFHandlerException {
        currentSet.setNamespace(prefix, space);
    }

    @Override
    public void handleStatement(Statement statement) throws RDFHandlerException {
        if(LiteralImpl.class.isAssignableFrom(statement.getObject().getClass()))
            currentSet.addTriple(statement.getSubject().toString(), statement.getPredicate().stringValue(), statement.getObject().stringValue(),
                    ((LiteralImpl)statement.getObject()).getDatatype().toString(), ((LiteralImpl)statement.getObject()).getLanguage());
        else
            currentSet.addTriple(statement.getSubject().toString(), statement.getPredicate().stringValue(), statement.getObject().toString());

    }

    @Override
    public void handleComment(String s) throws RDFHandlerException {
    }
}
