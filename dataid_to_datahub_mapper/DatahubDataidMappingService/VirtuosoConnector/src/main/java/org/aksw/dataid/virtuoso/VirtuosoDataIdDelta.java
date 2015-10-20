package org.aksw.dataid.virtuoso;

import org.aksw.dataid.errors.DataIdInputException;
import org.aksw.dataid.ontology.IdPart;
import org.aksw.dataid.statics.StaticFunctions;
import org.apache.commons.validator.UrlValidator;
import org.openrdf.model.Model;
import org.openrdf.model.Statement;
import org.openrdf.model.impl.TreeModel;

import java.math.BigInteger;
import java.net.URI;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.*;

/**
 * Created by Chile on 10/1/2015.
 */
public class VirtuosoDataIdDelta {
    private Model additions = new TreeModel();
    private Model deletions = new TreeModel();
    private String version;
    private String identifier;
    private String delIdentifier;
    private Date inserted;
    private int uribra;
    private int delta;
    private String agent;

    protected VirtuosoDataIdDelta(IdPart parent, IdPart child)
    {
        calculateDelta(parent.getGraph(), child.getGraph());
    }

    protected VirtuosoDataIdDelta(IdPart parent, IdPart child, String agent)
    {
        calculateDelta(parent.getGraph(), child.getGraph());
        this.agent = agent;
    }

    public VirtuosoDataIdDelta(IdPart parent, IdPart child, String id, String agent)
    {
        this(id, agent);
        calculateDelta(parent.getGraph(), child.getGraph());
    }

    public VirtuosoDataIdDelta(String id, String agent)
    {
        this.setIdentifier(id);
        this.agent = agent;
    }

    public VirtuosoDataIdDelta(Model additions, Model deletions, String id, String agent)
    {
        this(id, agent);
        this.additions = additions;
        this.deletions = deletions;
    }


    protected VirtuosoDataIdDelta(Model additions, Model deletions, String agent)
    {
        this.agent = agent;
        this.additions = additions;
        this.deletions = deletions;
    }

    private void calculateDelta(Model parent, Model child)
    {
        for(Statement st : parent)
        {
            if(child.filter(st.getSubject(), st.getPredicate(), st.getObject()).size() == 0)
                deletions.add(st);
        }
        for(Statement st : child)
        {
            if(parent.filter(st.getSubject(), st.getPredicate(), st.getObject()).size() == 0)
                additions.add(st);
        }
    }

    public Model getAdditions() {
        return additions;
    }

    public String getAgent() {
        return agent;
    }

    public Model getDeletions() {
        return deletions;
    }

    public int getDelta() {
        return delta;
    }

    public Date getInserted() {
        return inserted;
    }

    public int getUribra() {
        return uribra;
    }

    public String getIdentifier() {
        return identifier;
    }

    public String getDelIdentifier() {
        return delIdentifier;
    }

    public void setAgent(String agent) {
        this.agent = agent;
    }

    protected void setIdentifier(String id) {
        this.version = id;
        this.identifier = VirtuosoDataIdDelta.GetFullHexDeltaId(id, 0);
        this.delIdentifier = VirtuosoDataIdDelta.GetFullHexDeltaId(id, 1);
        this.uribra = VirtuosoDataIdDelta.GetUriBranchOfDeltaId(id);
        this.inserted = VirtuosoDataIdDelta.GetEntryDateOfDeltaId(id);
        this.delta = VirtuosoDataIdDelta.GetVersionOfDeltaId(id);
    }

    public String getVersion() {
        return version;
    }

    public static int GetVersionOfDeltaId(long id)
    {
        byte[] binary = ByteBuffer.allocate(8).putLong(id).array();
        binary = Arrays.copyOfRange(binary, 7, 8);
        return new BigInteger(binary).intValue() << 1; //add hidden bit (always non odd number!)
    }

    public static int GetVersionOfDeltaId(String hex)
    {
        long id = Long.parseLong(StaticFunctions.getFragmentOfUri(hex), 16);
        return GetVersionOfDeltaId(id);
    }

    public static int GetUriBranchOfDeltaId(long id)
    {
        byte[] binary = ByteBuffer.allocate(8).putLong(id).array();
        binary = Arrays.copyOfRange(binary, 3, 7);
        return new BigInteger(binary).intValue() << 4 >> 4; //delete first 2 bits
    }

    public static int GetUriBranchOfDeltaId(String hex)
    {
        long id = Long.parseLong(StaticFunctions.getFragmentOfUri(hex), 16);
        return GetUriBranchOfDeltaId(id);
    }

    public static Date GetEntryDateOfDeltaId(long id)
    {
        byte[] binary = ByteBuffer.allocate(8).putLong(id).array();
        binary = Arrays.copyOfRange(binary, 0, 4);
        int minutes = new BigInteger(binary).intValue() >> 4;    //first 28 bit
        GregorianCalendar cal = new GregorianCalendar(2000, 0, 1, 0, 0, 0);
        cal.add(Calendar.MINUTE, minutes);
        return cal.getTime();
    }

    public static Date GetEntryDateOfDeltaId(String hex)
    {
        long id = Long.parseLong(StaticFunctions.getFragmentOfUri(hex), 16);
        return GetEntryDateOfDeltaId(id);
    }

    /**
     * hex version of id shiftet 1 to the left (restored hidden bit)
     * @param id
     * @return
     */
    public static String GetFullHexDeltaId(long id, int add)
    {
        byte[] binary = ByteBuffer.allocate(8).putLong(id).array();
        return Long.toHexString((new BigInteger(binary).longValue() << 1) + add);
    }

    public static String GetFullHexDeltaId(String hex, int add)
    {
        long id = Long.parseLong(StaticFunctions.getFragmentOfUri(hex), 16);
        return GetFullHexDeltaId(id, add);
    }
}
