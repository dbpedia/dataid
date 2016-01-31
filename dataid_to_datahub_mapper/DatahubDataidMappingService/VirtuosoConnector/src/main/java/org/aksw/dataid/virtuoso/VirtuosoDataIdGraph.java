package org.aksw.dataid.virtuoso;

import org.aksw.dataid.classes.PrefixTree;
import org.aksw.dataid.config.DataIdConfig;
import org.aksw.dataid.errors.DataIdInputException;
import org.aksw.dataid.ontology.IdPart;
import org.apache.commons.lang3.StringUtils;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFHandlerException;
//import virtuoso.jdbc4.VirtuosoDataSource;

import java.sql.*;
import java.util.*;
import java.util.Date;
import java.util.concurrent.Executor;


/**
 * Created by Chile on 3/4/2015.
 */
public class VirtuosoDataIdGraph {

    //private static VirtuosoDataSource dSource = new VirtuosoDataSource();
    private static Connection conn;

    private Map<String, Map<String, Map.Entry<VirtuosoDataIdBranch, Date>>> branchCache = new HashMap<String, Map<String, Map.Entry<VirtuosoDataIdBranch, Date>>>();
    private int cacheSize = 0;

    public VirtuosoDataIdGraph(final String host, final int port, final String username, final String password) throws SQLException {

        if(conn == null) {
//            dSource.setPortNumber(port);
//            dSource.setServerName(host);
//            dSource.setUser(username);
//            dSource.setPassword(password);
            conn =  new Connection() {
                @Override
                public Statement createStatement() throws SQLException {
                    return null;
                }

                @Override
                public PreparedStatement prepareStatement(String sql) throws SQLException {
                    return null;
                }

                @Override
                public CallableStatement prepareCall(String sql) throws SQLException {
                    return null;
                }

                @Override
                public String nativeSQL(String sql) throws SQLException {
                    return null;
                }

                @Override
                public void setAutoCommit(boolean autoCommit) throws SQLException {

                }

                @Override
                public boolean getAutoCommit() throws SQLException {
                    return false;
                }

                @Override
                public void commit() throws SQLException {

                }

                @Override
                public void rollback() throws SQLException {

                }

                @Override
                public void close() throws SQLException {

                }

                @Override
                public boolean isClosed() throws SQLException {
                    return false;
                }

                @Override
                public DatabaseMetaData getMetaData() throws SQLException {
                    return null;
                }

                @Override
                public void setReadOnly(boolean readOnly) throws SQLException {

                }

                @Override
                public boolean isReadOnly() throws SQLException {
                    return false;
                }

                @Override
                public void setCatalog(String catalog) throws SQLException {

                }

                @Override
                public String getCatalog() throws SQLException {
                    return null;
                }

                @Override
                public void setTransactionIsolation(int level) throws SQLException {

                }

                @Override
                public int getTransactionIsolation() throws SQLException {
                    return 0;
                }

                @Override
                public SQLWarning getWarnings() throws SQLException {
                    return null;
                }

                @Override
                public void clearWarnings() throws SQLException {

                }

                @Override
                public Statement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException {
                    return null;
                }

                @Override
                public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
                    return null;
                }

                @Override
                public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
                    return null;
                }

                @Override
                public Map<String, Class<?>> getTypeMap() throws SQLException {
                    return null;
                }

                @Override
                public void setTypeMap(Map<String, Class<?>> map) throws SQLException {

                }

                @Override
                public void setHoldability(int holdability) throws SQLException {

                }

                @Override
                public int getHoldability() throws SQLException {
                    return 0;
                }

                @Override
                public Savepoint setSavepoint() throws SQLException {
                    return null;
                }

                @Override
                public Savepoint setSavepoint(String name) throws SQLException {
                    return null;
                }

                @Override
                public void rollback(Savepoint savepoint) throws SQLException {

                }

                @Override
                public void releaseSavepoint(Savepoint savepoint) throws SQLException {

                }

                @Override
                public Statement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
                    return null;
                }

                @Override
                public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
                    return null;
                }

                @Override
                public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
                    return null;
                }

                @Override
                public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws SQLException {
                    return null;
                }

                @Override
                public PreparedStatement prepareStatement(String sql, int[] columnIndexes) throws SQLException {
                    return null;
                }

                @Override
                public PreparedStatement prepareStatement(String sql, String[] columnNames) throws SQLException {
                    return null;
                }

                @Override
                public Clob createClob() throws SQLException {
                    return null;
                }

                @Override
                public Blob createBlob() throws SQLException {
                    return null;
                }

                @Override
                public NClob createNClob() throws SQLException {
                    return null;
                }

                @Override
                public SQLXML createSQLXML() throws SQLException {
                    return null;
                }

                @Override
                public boolean isValid(int timeout) throws SQLException {
                    return false;
                }

                @Override
                public void setClientInfo(String name, String value) throws SQLClientInfoException {

                }

                @Override
                public void setClientInfo(Properties properties) throws SQLClientInfoException {

                }

                @Override
                public String getClientInfo(String name) throws SQLException {
                    return null;
                }

                @Override
                public Properties getClientInfo() throws SQLException {
                    return null;
                }

                @Override
                public Array createArrayOf(String typeName, Object[] elements) throws SQLException {
                    return null;
                }

                @Override
                public Struct createStruct(String typeName, Object[] attributes) throws SQLException {
                    return null;
                }

                @Override
                public void setSchema(String schema) throws SQLException {

                }

                @Override
                public String getSchema() throws SQLException {
                    return null;
                }

                @Override
                public void abort(Executor executor) throws SQLException {

                }

                @Override
                public void setNetworkTimeout(Executor executor, int milliseconds) throws SQLException {

                }

                @Override
                public int getNetworkTimeout() throws SQLException {
                    return 0;
                }

                @Override
                public <T> T unwrap(Class<T> iface) throws SQLException {
                    return null;
                }

                @Override
                public boolean isWrapperFor(Class<?> iface) throws SQLException {
                    return false;
                }
            }   ;                         //dSource.getConnection();
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
            try (Statement stmt = conn.createStatement()) {
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
            try (Statement stmt = conn.createStatement()) {
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

    public String getLicenseName(String uri) throws DataIdInputException {
        if(langString == null) {
            try (Statement stmt = conn.createStatement()) {
                ResultSet set = stmt.executeQuery(DataIdConfig.getLicenseNameQuery());
                set.next();
                return set.getString(1);
            } catch (SQLException e) {
                throw new DataIdInputException(e);
            }
        }
        return "";
    }

    private String langString;
    public String getLangs() throws DataIdInputException {
        if(langString == null) {
            try (Statement stmt = conn.createStatement()) {
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
            try (Statement stmt = conn.createStatement()) {
                ResultSet set = stmt.executeQuery(DataIdConfig.getMimeQuery());
                set.next();
                mimeResult = set.getString(1);
            } catch (SQLException e) {
                throw new DataIdInputException(e);
            }
        }
        return mimeResult;
    }

    public int getCacheSize() {
        return cacheSize;
    }
}
