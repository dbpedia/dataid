package org.aksw.dataid.virtuoso;

import com.fasterxml.jackson.databind.JsonNode;
import org.aksw.dataid.errors.DataIdInputException;
import org.aksw.dataid.jsonutils.StaticJsonHelper;
import org.aksw.dataid.ontology.IdPart;
import org.aksw.dataid.statics.StaticContent;
import org.aksw.dataid.statics.StaticFunctions;
import org.apache.commons.collections15.map.HashedMap;
import org.openrdf.model.*;
import org.openrdf.model.impl.LiteralImpl;
import org.openrdf.model.impl.TreeModel;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.rio.RDFFormat;

import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by Chile on 10/1/2015.
 */
public class VirtuosoDataIdBranch {

    private Connection conn;

    private String uri;
    private String name;
    private Integer uribra;
    private int head;
    private int loadedDelta;
    private boolean isMaster;
    private String contact;
    private Date lastChange;
    private String key;

    private Map<Integer, Model> deltas= new HashedMap<Integer, Model>();

    public VirtuosoDataIdBranch(Integer uribra, Connection conn) throws DataIdInputException {
        this.conn = conn;
        resolveUriBranchFromDB(uribra);
        loadBranchInfoFromDB();
        loadVersion(getHead());
    }

    public VirtuosoDataIdBranch(String uri, String branch, Connection conn) throws DataIdInputException {
        this.conn = conn;
        resolveUriFromDB(uri, branch);
        loadBranchInfoFromDB();
        loadVersion(getHead());
    }

    public VirtuosoDataIdBranch(String uri, String branch, Connection conn, int version) throws DataIdInputException {
        this.uri = uri;
        this.name = branch;
        this.conn = conn;
        loadBranchInfoFromDB();
        loadVersion(version);
    }

    /**
     * Special constructor for freshly created branched -> no deltas to load here!
     * @param uri
     * @param branch
     * @param uribra
     * @param conn
     * @throws DataIdInputException
     */
    protected VirtuosoDataIdBranch(String uri, String branch, Integer uribra, Connection conn) throws DataIdInputException {
        this.conn = conn;
        resolveIdentifierFromDB(uribra, uri, branch);
        loadBranchInfoFromDB();
    }

    public VirtuosoDataIdBranch(IdPart dataId, Connection conn) throws DataIdInputException {
        this.uri = dataId.getId().stringValue();
        this.conn = conn;
        String hexId = java.net.URI.create(dataId.getThisVersion()).getFragment();
        if(hexId == null)
            hexId = dataId.getThisVersion();
        this.uribra = VirtuosoDataIdDelta.GetUriBranchOfDeltaId(hexId);
        loadVersion(VirtuosoDataIdDelta.GetVersionOfDeltaId(hexId));
        resolveUriBranchFromDB(this.uribra);
    }

    public VirtuosoDataIdDelta insertDelta(IdPart dataId, String key, String agent) throws DataIdInputException {
        return insertDelta(dataId, key, agent, 1);
    }

    public VirtuosoDataIdDelta insertDelta(IdPart dataId, String key, String agent, int isHead) throws DataIdInputException {
        String parentVersion = null;
        try {
            //get parent uri
            parentVersion = dataId.getPrevVersion() != null ? VirtuosoDataIdDelta.GetFullHexDeltaId(new java.net.URI(dataId.getPrevVersion()).getFragment(), 0) : null;
        } catch (URISyntaxException e) {
            // we do nothing about it
        }
        //calculate delta
        VirtuosoDataIdDelta delta = null;
        if(parentVersion != null)
            delta = new VirtuosoDataIdDelta(getVersion(VirtuosoDataIdDelta.GetVersionOfDeltaId(parentVersion)), dataId, agent);
        else
            delta = new VirtuosoDataIdDelta(IdPart.GetEmptyId(),dataId, agent);

        if(delta.getDeletions().size() == 0 && delta.getAdditions().size() == 0)
            throw new DataIdInputException("No Changes to DataID found -> not updating!");

        try (Statement stmt = conn.createStatement()) {
            //make entry for new delta in delta table -> get deltaId
            String query = "SELECT DB.DBA.DataIdAddDelta(" + this.uribra + ", '" + key + "', " + delta.getAdditions().size() + ", " + delta.getDeletions().size() + ", " + isHead + ", " + (agent != null ? ("'" + agent + "')") : ")");
            ResultSet set = stmt.executeQuery(query);
            set.next();
            delta.setIdentifier(set.getString(1));

        }
        catch (SQLException e) {
            throw new DataIdInputException(e);
        }

        //update 'thisVersion'
        if(parentVersion != null)
            delta.getDeletions().add(new URIImpl(uri), new URIImpl(StaticContent.thisDataIdVersion), new URIImpl(uri + "#" + delta.getDelIdentifier()));
        delta.getAdditions().add(new URIImpl(uri), new URIImpl(StaticContent.thisDataIdVersion), new URIImpl(uri + "#" + delta.getIdentifier()));
        try (Statement stmt = conn.createStatement()) {
            //enter additions
            String inserTtlp = "TTLP('" + StaticFunctions.writeSerialization(delta.getAdditions(), RDFFormat.TURTLE) + "','', '" + (uri + "#" + delta.getIdentifier()) + "', 17)";
            stmt.execute(inserTtlp);
        }
        catch(Exception e) {
            throw new DataIdInputException(e);
        }
        try (Statement stmt = conn.createStatement()) {
            //enter deletions
            String inserTtlp = "TTLP('" + StaticFunctions.writeSerialization(delta.getDeletions(), RDFFormat.TURTLE) + "','', '" + (uri + "#" + delta.getDelIdentifier()) + "', 17)";
            stmt.execute(inserTtlp);
        }
        catch(Exception e) {
            throw new DataIdInputException(e);
        }
        try (Statement stmt = conn.createStatement()) {
            //update latest version in first delta (0)
            String inserTtlp = "SELECT DB.DBA.DataIdReplaceLatestVersion('" + uri + "', '" + delta.getIdentifier() + "' )";
            ResultSet set = stmt.executeQuery(inserTtlp);
            set.next();
            if( Boolean.parseBoolean(set.getString(1)))
            {
                deltas.put(delta.getDelta(), delta.getAdditions());
                deltas.put(delta.getDelta() + 1, delta.getDeletions());
                return delta;
            }
            else
                throw new DataIdInputException("something went wrong, version has not been saved");
        }
        catch(Exception e) {
            throw new DataIdInputException(e);
        }
    }

    private void resolveUriBranchFromDB(Integer uribra) throws DataIdInputException {
        try (Statement stmt = conn.createStatement()) {
            ResultSet set = stmt.executeQuery("Call DB.DBA.DataIdResolveUriBranch(" + uribra + ")");
            set.next();
            resolveIdentifierFromDB(uribra, set.getString("uri"), set.getString("branch"));
        }
        catch(Exception e) {
            throw new DataIdInputException(e);
        }
    }


    private void resolveUriFromDB(String uri, String branch) throws DataIdInputException {
        try (Statement stmt = conn.createStatement()) {
            ResultSet set = stmt.executeQuery("Call DB.DBA.DataIdResolveUri('" + uri + "', '" + branch + "')");
            set.next();
            resolveIdentifierFromDB(Integer.parseInt(set.getString("uribra")), uri, branch);
        }
        catch(Exception e) {
            throw new DataIdInputException(e);
        }
    }

    private void resolveIdentifierFromDB(Integer uribra, String uri, String branch) throws DataIdInputException {
        this.uribra = uribra;
        this.uri = uri;
        this.name = branch;
    }

    private void loadBranchInfoFromDB() throws DataIdInputException {
        try (Statement stmt = conn.createStatement()) {
            conn.commit();
            String query = "Call DB.DBA.DataIdGetBranchInfo('" + uri + "', '" + name + "' )";

            ResultSet set = stmt.executeQuery(query);
            set.next();
            this.head = VirtuosoDataIdDelta.GetVersionOfDeltaId(set.getLong("head"));
            this.isMaster = set.getInt("master") != 0;
            this.contact = set.getString("contact");
            String zw = set.getString("changed");
            this.lastChange = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(zw.substring(0, zw.lastIndexOf('.')));
        }
        catch(Exception e) {
            throw new DataIdInputException(e);
        }
    }

    public void loadVersion(int delta) throws DataIdInputException {
        Model result;
        try (Statement stmt = conn.createStatement()) {
            ResultSet set = stmt.executeQuery("SELECT DB.DBA.DataIdGetVersion('" + uri + "', '" + name + "', " + delta + " )");
            set.next();
            result = transformJsonResultSet(StaticJsonHelper.convertStringToJsonNode(set.getString(1)));
        }
        catch(Exception e) {
            throw new DataIdInputException(e);
        }
        for(Resource res : result.contexts())
        {
            int del = VirtuosoDataIdDelta.GetVersionOfDeltaId(res.stringValue());
            deltas.put(del, result.filter(null, null, null, res));
        }
        this.loadedDelta = delta;
    }

    public IdPart getVersion(final int delta) throws DataIdInputException {
        if(delta % 2 == 1)
            throw new DataIdInputException("version number has to be a non odd number");

        Model model = new TreeModel();
        for(Namespace ns : StaticContent.getRdfContext().getNamespaces())
        {
            model.setNamespace(ns);
        }
        for(int i =0; i <= delta; i++)
        {
            if(deltas.keySet().contains(i)) {
                if (i % 2 == 0) {
                    for (org.openrdf.model.Statement st : deltas.get(i)) {
                        model.add(st.getSubject(), st.getPredicate(), st.getObject());
                    }
                } else {

                    for (org.openrdf.model.Statement st : deltas.get(i)) {
                        model.remove(st.getSubject(), st.getPredicate(), st.getObject());
                    }
                }
            }
        }
        if(model.size() == 0)
            throw new DataIdInputException("Version was not found: " + uri + ", branch: " + name + ", version: " + delta);

        return new IdPart(model);
    }

    private Model transformJsonResultSet(JsonNode jsonResult)
    {
        Model model = StaticFunctions.createDefaultModel(StaticContent.getRdfContext());
        if(jsonResult.hasNonNull("results"))
            jsonResult = jsonResult.get("results");
        if(jsonResult.hasNonNull("bindings"))
            jsonResult = jsonResult.get("bindings");

        Iterator<JsonNode> zw = jsonResult.elements();
        while(zw.hasNext())
        {
            final JsonNode res = zw.next();
            model.add(new org.openrdf.model.Statement() {
                @Override
                public Resource getSubject() {
                    return (Resource)evaluateResultNode(res.get("s"));
                }

                @Override
                public URI getPredicate() {
                    return (URI)evaluateResultNode(res.get("p"));
                }

                @Override
                public Value getObject() {
                    return evaluateResultNode(res.get("o"));
                }

                @Override
                public Resource getContext() {
                    return (URI)evaluateResultNode(res.get("g"));
                }
            });
        }
        return model;
    }

    private Value evaluateResultNode(JsonNode node)
    {
        //TODO Blank Nodes
        if(node.get("type").asText().equals("uri"))
            return new URIImpl(node.get("value").asText());
        if(node.get("type").asText().equals("typed-literal")) //not!
        {
            return new LiteralImpl(node.get("value").asText(), new URIImpl(node.get("datatype").asText()));
        }
        if(node.get("type").asText().equals("literal") && node.hasNonNull("xml:lang"))
        {
            return new LiteralImpl(node.get("value").asText(), node.get("xml:lang").asText());
        }
        return new LiteralImpl(node.get("value").asText());
    }

    public String getContact() {
        return contact;
    }

    public int getHead() {
        return head;
    }

    public int getLoadedDelta() {
        return loadedDelta;
    }

    public boolean isMaster() {
        return isMaster;
    }

    public Date getLastChange() {
        return lastChange;
    }

    public String getName() {
        return name;
    }

    public String getUri() {
        return uri;
    }

    public Integer getUribra() {
        return uribra;
    }

    public int getDeltaSize()
    {
        int zw = 0;
        for(int i =0; i < deltas.size(); i++)
        {
            if(i % 2== 0)
                zw += deltas.get(i).size();
            else
                zw -= deltas.get(i).size();
        }
        return zw;
    }

    protected void setConn(Connection conn) {
        this.conn = conn;
    }

    protected void setContact(String contact) {
        this.contact = contact;
    }

    protected void setHead(int head) {
        this.head = head;
    }

    protected void setMaster(boolean isMaster) {
        this.isMaster = isMaster;
    }

    protected void setName(String name) {
        this.name = name;
    }

    protected void setUri(String uri) {
        this.uri = uri;
    }

    protected void setUribra(Integer uribra) {
        this.uribra = uribra;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
