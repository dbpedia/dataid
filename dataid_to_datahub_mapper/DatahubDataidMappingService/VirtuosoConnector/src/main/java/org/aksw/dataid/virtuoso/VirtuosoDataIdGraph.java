package org.aksw.dataid.virtuoso;

import com.hp.hpl.jena.datatypes.BaseDatatype;
import com.hp.hpl.jena.datatypes.RDFDatatype;
import com.hp.hpl.jena.datatypes.TypeMapper;
import com.sun.javaws.exceptions.InvalidArgumentException;
import org.aksw.dataid.config.DataIdConfig;
import org.aksw.dataid.errors.DataIdInputException;
import org.aksw.dataid.ontology.IdPart;
import org.aksw.dataid.statics.StaticContent;
import org.aksw.dataid.wrapper.InternalLieteralImpl;
import org.aksw.dataid.config.RdfContext;
import org.aksw.dataid.wrapper.Statics;
import org.openrdf.model.Model;
import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.StatementImpl;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.rio.RDFHandlerException;
import virtuoso.jdbc4.VirtuosoDataSource;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;


/**
 * Created by Chile on 3/4/2015.
 */
public class VirtuosoDataIdGraph {
    //constants
    private final String blankNodePrefix = "nodeID://";
    private final RDFDatatype typeResource = new BaseDatatype("http://www.w3.org/1999/02/22-rdf-syntax-ns#Resource");
    private final RDFDatatype typeBlank = new BaseDatatype("http://www.w3.org/1999/02/22-rdf-syntax-ns#Blank");

    private VirtuosoDataSource dSource = new VirtuosoDataSource();
    private Connection conn;
    private TypeMapper typeMapper = TypeMapper.getInstance();
    //TreeMap<DataId, NextKnownVersion>
    private TreeMap<URI, URI> knownDataIds = new TreeMap<URI, URI>();

    public VirtuosoDataIdGraph(final String host, final int port, final String username, final String password) throws SQLException {

        dSource.setPortNumber(port);
        dSource.setServerName(host);
        dSource.setUser(username);
        dSource.setPassword(password);
        conn = dSource.getConnection();
        conn.setAutoCommit(true);
        conn.setHoldability(ResultSet.CLOSE_CURSORS_AT_COMMIT); //for faster execution
        //DataId.setPreamble(this.getDataIdPreamble());
        typeMapper.registerDatatype(typeResource);
        typeMapper.registerDatatype(typeBlank);
    }

    private boolean isHigherVersion(URI thisVers, URI prvVers) throws SQLException {
        try (Statement stmt = conn.createStatement()) {
            ResultSet set = stmt.executeQuery(
                    "SPARQL \n" +
                            "PREFIX dataid: <" + DataIdConfig.getDataIdUri() + ">\n" +
                            "SELECT ?prevV \n" +
                            "FROM <http://dataid/store>\n" +
                            "{ <" + thisVers.stringValue() + "> a dataid:Dataste;\n" +
                            " dataid:priviousVersion+ ?prevV.}"
            );

            while (set.next()) {
                if(prvVers.stringValue().toLowerCase().trim().equals(set.getString(1).toLowerCase().trim()))
                    return true;
            }
            return false;
        }
    }

    public void enterDataId(final String dataID, final String dataiduri, final String prevId) throws SQLException, InvalidArgumentException {
        if(dataiduri == null)
            throw new InvalidArgumentException(new String[]{"please provide a dataiduri"});
        if(dataID == null)
            throw new InvalidArgumentException(new String[]{"please provide a DataId"});
        if(dataIdExits(dataiduri)) {
            if (isHigherVersion(knownDataIds.get(new URIImpl(dataiduri.toLowerCase().trim())), new URIImpl(prevId.toLowerCase().trim()))) {
                deletePreviousId(dataiduri);
                enterId(dataID, dataiduri, prevId);
            }
            else
                throw new InvalidArgumentException(new String[]{"a DataId with this uri does exist with a higher or equal version number"});
        }
        else
            enterId(dataID, dataiduri, prevId);
    }

    private void deletePreviousId(final String dataiduri) throws SQLException {
        String sparql = "SPARQL \n" +
                "PREFIX dataid: <" + DataIdConfig.getDataIdUri() + ">\n" +
                "WITH <http://dataid/store>\n" +
                "DELETE {?s ?p ?o}\n" +
                "WHERE {{SELECT ?s ?p ?o (?s as ?desc)\n" +
                "FROM <http://dataid/store>\n" +
                "{?s a void:DatasetDescription.\n" +
                "?s ?p ?o.\n" +
                "}}\n" +
                "UNION\n" +
                "{SELECT ?s ?p ?o ?desc\n" +
                "FROM <http://dataid/store>\n" +
                "{?desc a void:DatasetDescription.\n" +
                "?desc foaf:primaryTopic ?set.\n" +
                "?set (! dataid:pp)* ?s.\n" +
                "?s ?p ?o.}}\n" +
                "FILTER(?desc = <" + dataiduri + ">)}";

        try (Statement stmt = conn.createStatement()) {
            stmt.execute(sparql);
        }
    }

    private void enterId(final String dataID, final String dataiduri, final String prevVersion) throws SQLException {
        try (Statement stmt = conn.createStatement()) {
            String inserTtlp = "TTLP('" + dataID + "','', 'http://dataid/store', 17)";
            if(stmt.execute(inserTtlp))
                knownDataIds.put(new URIImpl(dataiduri.trim().toLowerCase()), new URIImpl(prevVersion.toLowerCase().trim()));
        }
    }

    public boolean dataIdExits(final String dataiduri) throws SQLException {
        if(knownDataIds == null)
        {
            knownDataIds = new TreeMap<URI, URI>();
            try (Statement stmt = conn.createStatement()) {
                ResultSet set = stmt.executeQuery(
                    "SPARQL \n" +
                        "PREFIX dataid: <" + DataIdConfig.getDataIdUri() + ">\n" +
                        "SELECT str(?dataiduri) str(?prevV) \n" +
                        "FROM <http://dataid/store>\n" +
                        "{\n" +
                        "?dataiduri a void:DatasetDescription.\n" +
                        "OPTIONAL\n" +
                        "{?set dataid:priviousVersion ?prevV.\n" +
                        "}}"
                );

                while (set.next()) {
                    knownDataIds.put(new URIImpl(set.getString(1).toLowerCase().trim()), new URIImpl(set.getString(2).toLowerCase().trim()));
                }
            }
        }
        if(knownDataIds.keySet().contains(dataiduri.toLowerCase().trim()))
            return true;
        else
            return false;
    }

    public InternalLieteralImpl getDataIdPreamble() throws SQLException {
        Map<String, String> preambles = new HashMap<String, String>();
        try (Statement stmt = conn.createStatement()) {
            ResultSet set = stmt.executeQuery("SPARQL " +
                "PREFIX dataid: <" + DataIdConfig.getDataIdUri() + ">\n" +
                    "SELECT (lang(?prea) as ?lang) (str(?prea) as ?preamble) \n" +
                    "FROM <http://dataid/store>\n" +
                    "{?x dataid:preamble ?prea.}");

            while (set.next()) {
                preambles.put(set.getString(1), set.getString(2));
            }
        }
        return new InternalLieteralImpl(preambles);
    }

    public URI getVersion(final String dataiduri) throws Exception {
        if(dataIdExits(dataiduri))
            return knownDataIds.get(new URIImpl(dataiduri.toLowerCase().trim()));
        throw new Exception("DataId does not (yet) exist!");
    }

    public String getDataIdFile(final String dataiduri, final RdfContext context) throws SQLException, RDFHandlerException, DataIdInputException {
        //create and execute query
        String sparql = "SPARQL \n" +
                "PREFIX dataid: <" + DataIdConfig.getDataIdUri() + ">\n" +
                "SELECT ?s ?p ?t ?o \n" +
                "FROM <http://dataid/store>\n" +
                "{{SELECT ?s ?p ?o (?s as ?desc)\n" +
                "FROM <http://dataid/store>\n" +
                "{?s a void:DatasetDescription.\n" +
                "?s ?p ?o.\n" +
                "}}\n" +
                "UNION\n" +
                "{SELECT ?s ?p ?o ?desc\n" +
                "FROM <http://dataid/store>\n" +
                "{?desc a void:DatasetDescription.\n" +
                "?desc foaf:primaryTopic ?set.\n" +
                "?set (! dataid:pp)* ?s.\n" +
                "?s ?p ?o.}}\n" +
                "BIND( if ( COALESCE(datatype(?o), \"kk\") != \"kk\",\n" +
                "             datatype(?o), if(ISURI(?o), <http://www.w3.org/1999/02/22-rdf-syntax-ns#Resource>, <http://www.w3.org/1999/02/22-rdf-syntax-ns#Blank>)) as ?t)\n" +
                "FILTER(?desc = <" + dataiduri + ">)}";

        StringBuilder sb = new StringBuilder();

        Model model = Statics.createDefaultModel(StaticContent.getRdfContext());
        try(Statement stmt = conn.createStatement()) {
            ResultSet set = stmt.executeQuery(sparql);

            ValueFactory vFactory = new org.openrdf.model.impl.ValueFactoryImpl();

            while (set.next()) {

                org.openrdf.model.Resource subject = null;
                if(set.getString(1).startsWith(blankNodePrefix)) {
                    sb.append(set.getString(1).replace(blankNodePrefix, "_:"));
                    subject = new org.openrdf.model.impl.BNodeImpl(set.getString(1).replace(blankNodePrefix, ""));
                }
                else {
                    sb.append("<" + set.getString(1) + ">");
                    subject = new URIImpl(set.getString(1));
                }

                sb.append(" <" + set.getString(2) + "> ");

                org.openrdf.model.Value object = null;
                RDFDatatype type = typeMapper.getSafeTypeByName(set.getString(3));
                if(type.equals(this.typeBlank)) {
                    sb.append(set.getString(4).replace(blankNodePrefix, "_:"));
                    object = vFactory.createBNode(set.getString(4).replace(blankNodePrefix, ""));
                }
                else if(type.equals(this.typeResource)) {
                    sb.append("<" + set.getString(4) + ">");
                    object = vFactory.createURI(set.getString(4));
                }
                else {
                    sb.append("\"" + set.getString(4).replaceAll("(\\r|\\n)", "") + "\"^^<" + set.getString(3) + ">");
                    object = vFactory.createLiteral(set.getString(4).replaceAll("(\\r|\\n)", ""), vFactory.createURI(set.getString(3)) );
                }
                sb.append(".\n");
                model.add(new StatementImpl(subject, vFactory.createURI(set.getString(2)), object));
            }
        }
        return Statics.writeTurtle(model);
    }

    public boolean enterLinkSet(IdPart part) throws SQLException, RDFHandlerException, DataIdInputException {
        try (Statement stmt = conn.createStatement()) {
            //TODO!!!
            if(part.getPartType() == IdPart.DataIdParts.Linkset) {
                String inserTtlp = "TTLP('" + part.toTurtle() + "','', 'http://dataid/store', 17)";
                if (stmt.execute(inserTtlp))
                    return true;
            }
        }
        return false;
    }
}
