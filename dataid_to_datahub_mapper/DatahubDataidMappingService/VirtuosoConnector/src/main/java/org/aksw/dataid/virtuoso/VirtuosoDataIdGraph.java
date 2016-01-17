package org.aksw.dataid.virtuoso;

import org.aksw.dataid.classes.PrefixTree;
import org.aksw.dataid.config.DataIdConfig;
import org.aksw.dataid.errors.DataIdInputException;
import org.aksw.dataid.ontology.IdPart;
import org.apache.commons.collections15.MultiMap;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFHandlerException;
import virtuoso.jdbc4.VirtuosoDataSource;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;


/**
 * Created by Chile on 3/4/2015.
 */
public class VirtuosoDataIdGraph {

    private static VirtuosoDataSource dSource = new VirtuosoDataSource();
    private static Connection conn;

    private Map<String, Map<String, Map.Entry<VirtuosoDataIdBranch, Date>>> branchCache = new HashMap<String, Map<String, Map.Entry<VirtuosoDataIdBranch, Date>>>();
    private int cacheSize = 0;

    public VirtuosoDataIdGraph(final String host, final int port, final String username, final String password) throws SQLException {

        if(conn == null) {
            dSource.setPortNumber(port);
            dSource.setServerName(host);
            dSource.setUser(username);
            dSource.setPassword(password);
            conn = dSource.getConnection();
            conn.setAutoCommit(true);
            conn.setHoldability(ResultSet.CLOSE_CURSORS_AT_COMMIT); //for faster execution
        }
    }

    public VirtuosoDataIdBranch getMasterBranch(String uri) throws DataIdInputException {
        String branch;
        try(Statement stmt = conn.createStatement()){
            ResultSet set = stmt.executeQuery("SELECT branch FROM DB.DBA.DataIdBranches WHERE uri = '" + uri + "' AND master = 1");

            if(set.next()) {
                branch = set.getString(1);
            }
            else
                throw new DataIdInputException("No DataID with this id was found");
        }
        catch(SQLException e) {
            throw new DataIdInputException(e);
        }
        return getDataIdBranch(uri, branch);
    }

    public VirtuosoDataIdBranch getDataIdBranch(String uri, String bra) throws DataIdInputException {
        VirtuosoDataIdBranch branch;
        if(!branchCache.keySet().contains(uri)) //not!
        {
            branchCache.put(uri, new HashMap<String, Map.Entry<VirtuosoDataIdBranch, Date>>());
            branch = new VirtuosoDataIdBranch(uri, bra, conn);
            branchCache.get(uri).put(bra, new AbstractMap.SimpleEntry<>(branch , new Date())) ;
            cacheSize += branch.getDeltaSize();
        }
        else
        {
            if(branchCache.get(uri).containsKey(bra))
                branch =  branchCache.get(uri).get(bra).getKey();
            else
                branch = new VirtuosoDataIdBranch(uri, bra, conn);
        }
        cleanBranchCache();
        return branch;
    }

    private void cleanBranchCache()
    {
        if(cacheSize > DataIdConfig.getCacheTripleSize()) {
            Map<String,String> remove = new HashMap<String, String>();
            for (Map.Entry<String, Map<String, Map.Entry<VirtuosoDataIdBranch, Date>>> ddd : branchCache.entrySet()) {
                for(Map.Entry<String, Map.Entry<VirtuosoDataIdBranch, Date>> ent : ddd.getValue().entrySet()) {
                    if (new Date().getTime() - ent.getValue().getValue().getTime() > DataIdConfig.getBranchCacheTimeOut()) {
                        remove.put(ddd.getKey(), ent.getKey());
                    }
                }
            }
            for (Map.Entry<String, String> rem : remove.entrySet()) {
                cacheSize -= branchCache.get(rem.getKey()).get(rem.getValue()).getKey().getDeltaSize();
                branchCache.get(rem.getKey()).remove(rem.getValue());
            }
        }
    }

    public VirtuosoDataIdBranch createNewDataIdBranch(String uri, String name, String parentVersion, String contact, int isMaster) throws DataIdInputException {
        String key;
        Integer uribra;
        if(uriAndBranchValid(uri, name))
        {
            try(Statement stmt = conn.createStatement()){
                ResultSet set = stmt.executeQuery("SELECT DB.DBA.DataIdNewBranch('" + uri + "', '" + name + "', '" + contact + "', '" + parentVersion + "', " + isMaster + ")");
                set.next();
                uribra = Integer.parseInt(set.getString(1).substring(set.getString(1).indexOf('#')+1));
                key = set.getString(1).substring(0, set.getString(1).indexOf('#'));
            }
            catch(SQLException e) {
                throw new DataIdInputException(e);
            }
            VirtuosoDataIdBranch br = new VirtuosoDataIdBranch(uri, name, uribra, conn);
            br.setKey(key);
            return br;
        }
        return null;
    }

    public boolean uriAndBranchValid(final String uri, final String branchName) throws DataIdInputException {
        try(Statement stmt = conn.createStatement()){
            ResultSet set = stmt.executeQuery("SELECT DB.DBA.DataIdIsValidUriAndBranch('" + uri + "', '" + branchName + "')");
            set.next();
            return Boolean.parseBoolean(set.getString(1));
        }
        catch(SQLException e) {
            throw new DataIdInputException(e);
        }
    }

    public boolean enterDataId(final IdPart dataID) throws DataIdInputException {
        return false;
    }

    public boolean enterLinkSet(IdPart part) throws SQLException, RDFHandlerException, DataIdInputException {
        try (Statement stmt = conn.createStatement()) {
            //TODO!!!
            if(part.getPartType() == IdPart.DataIdParts.Linkset) {
                String inserTtlp = "TTLP('" + part.toSerialization(RDFFormat.TURTLE) + "','', '" + DataIdConfig.getDataIdGraoh() + "', 17)";
                if (stmt.execute(inserTtlp))
                    return true;
            }
        }
        return false;
    }

    private String licenseString;
    public String getLicenses() throws DataIdInputException {
        if(licenseString == null) {
            try (Statement stmt = VirtuosoDataIdGraph.getConn().createStatement()) {
                ResultSet set = stmt.executeQuery(DataIdConfig.getLicenseQuery());
                set.next();
                licenseString = set.getString(1);
            } catch (SQLException e) {
                throw new DataIdInputException(e);
            }
        }
        return licenseString;
    }

    private PrefixTree<Map.Entry<String, String>> tree;
    public PrefixTree<Map.Entry<String, String>> getMagicNumbers() throws DataIdInputException {
        if(tree == null) {
            tree = new PrefixTree<>();
            try (Statement stmt = VirtuosoDataIdGraph.getConn().createStatement()) {
                ResultSet set = stmt.executeQuery(DataIdConfig.getMagicNumbers());
                while(set.next())
                {
                    String pad = "xx ";
                    String magic = set.getString("m");
                    String offset = set.getString("o");
                    int off = (offset == null || offset.trim().equals("")) ? 0 : Integer.parseInt(offset);
                    magic = StringUtils.leftPad(magic, off*pad.length() + magic.length(), pad);
                    tree.insert(magic, new AbstractMap.SimpleEntry<String, String>(set.getString("e"), set.getString("mime")));
                }
            } catch (SQLException e) {
                throw new DataIdInputException(e);
            }
        }
        return tree;
    }

    private String langString;
    public String getLangs() throws DataIdInputException {
        if(langString == null) {
            try (Statement stmt = VirtuosoDataIdGraph.getConn().createStatement()) {
                ResultSet set = stmt.executeQuery(DataIdConfig.getLanguageQuery());
                set.next();
                langString = set.getString(1);
            } catch (SQLException e) {
                throw new DataIdInputException(e);
            }
        }
        return langString;
    }

    private String mimeResult;
    public String getMimes() throws DataIdInputException {
        if(mimeResult == null) {
            try (Statement stmt = VirtuosoDataIdGraph.getConn().createStatement()) {
                ResultSet set = stmt.executeQuery(DataIdConfig.getMimeQuery());
                set.next();
                mimeResult = set.getString(1);
            } catch (SQLException e) {
                throw new DataIdInputException(e);
            }
        }
        return mimeResult;
    }

    public static Connection getConn() {
        return conn;
    }

    public int getCacheSize() {
        return cacheSize;
    }
}
