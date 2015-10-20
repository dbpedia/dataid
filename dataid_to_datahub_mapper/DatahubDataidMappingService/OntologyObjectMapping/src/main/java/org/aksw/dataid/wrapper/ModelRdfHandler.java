package org.aksw.dataid.wrapper;

import org.aksw.dataid.statics.StaticContent;
import org.aksw.dataid.statics.StaticFunctions;
import org.openrdf.model.Model;
import org.openrdf.model.Statement;
import org.openrdf.rio.RDFHandler;
import org.openrdf.rio.RDFHandlerException;

/**
 * Created by Chile on 8/18/2015.
 */
public class ModelRdfHandler implements RDFHandler
{
    private Model model;
    private boolean loaded = false;

    @Override
    public void startRDF() throws RDFHandlerException {
        model = StaticFunctions.createDefaultModel(StaticContent.getRdfContext());
        loaded = false;
    }

    @Override
    public void endRDF() throws RDFHandlerException {
        loaded = true;
    }

    @Override
    public void handleNamespace(String s, String s1) throws RDFHandlerException {
        model.setNamespace(s, s1);
    }

    @Override
    public void handleStatement(Statement statement) throws RDFHandlerException {
        model.add(statement);
    }

    @Override
    public void handleComment(String s) throws RDFHandlerException {
    }

    public Model getModel() {
        if(loaded)
            return model;
        else
            return null;
    }
}