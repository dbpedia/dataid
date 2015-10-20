package org.aksw.dataid.datahub.mappingservice;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.aksw.dataid.errors.DataIdInputException;
import org.aksw.dataid.ontology.IdPart;
import org.aksw.dataid.virtuoso.VirtuosoDataIdBranch;
import org.aksw.dataid.virtuoso.VirtuosoDataIdDelta;
import org.aksw.dataid.virtuoso.VirtuosoDataIdGraph;
import org.openrdf.rio.RDFFormat;

import javax.ws.rs.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Path("dataid/graph")
public class DataIdGraphEndpoint {
    private VirtuosoDataIdGraph graph;

    public DataIdGraphEndpoint()
    {
        graph = Main.getGraph();
    }

    @POST
    @Path("/validationresulttable")
    @Produces("text/html")
    public String validationresulttable(@QueryParam(value = "url") final String url, String result ) throws IOException {
        if(result == null)
            result = new String(Files.readAllBytes(Paths.get(url)));
        try {
            String path = Main.getMainPath() + "/html/ValidationResultTable.html";
            String content = new String(Files.readAllBytes(Paths.get(path))).replace("$result", result);
            return content;
        } catch (IOException e) {
            return e.getMessage();
        }
    }

    @POST
    @Path("/validateid")
    public String validateDataId(final String ttl) throws DataIdInputException, JsonProcessingException {
        IdPart dataid = new IdPart(ttl);
        dataid.validate();
        return dataid.getJsonLdErrorReport();
    }

    @POST
    @Path("/isIdValid")
    public String isIdValid(final String ttl)
    {
        try {
            IdPart dataid = new IdPart(ttl);
            return new Boolean(dataid.validate()).toString();
        } catch (Exception e) {
            return e.getMessage();
        }
    }


    @GET
    @Path("/dataidvalidator")
    @Produces("text/html")
    public String validator()
    {
        try {
            String path = Main.getMainPath() + "/html/DataIdResultPage.html";
            String content = new String(Files.readAllBytes(Paths.get(path)));
            return content;
        } catch (IOException e) {
            return e.getMessage();
        }
    }

    @GET
    @Path("/getlicenses")
    @Produces("text/plain")
    public String getLicenses() throws DataIdInputException {
       return graph.getLicenses();
    }

    @GET
    @Path("/getlanguages")
    @Produces("text/plain")
    public String getLanguages() throws DataIdInputException {
        return graph.getLangs();
    }

    @GET
    @Path("/getmimes")
    @Produces("text/plain")
    public String getMimes() throws DataIdInputException {
        return graph.getMimes();
    }

    @GET
    @Path("/getstoreddataset")
    @Produces("text/plain")
    @Consumes("text/plain")
    public String getStoredDataset(@QueryParam(value = "title") final String uri,
                                   @QueryParam(value = "branch") final String branch) throws DataIdInputException {
        try {

            VirtuosoDataIdBranch bbranch = graph.getDataIdBranch(uri, branch);
            String id =  bbranch.getVersion(bbranch.getHead()).toSerialization(RDFFormat.TURTLE);
//            Dataset set = proc.parseToDataHubDataset(id).get(0);
//            //TODO DataHub??
            return id;
        } catch (Exception e) {
            throw new DataIdInputException(e);
        }
    }

    @POST
    @Path("/publishlinkset")
    public String PublishLinkSet(final String linkSet) {
        try {
            //TODO mayba add linkset uri?
            IdPart dataid = new IdPart(linkSet);
            graph.enterLinkSet(dataid);
        } catch (Exception e) {
            return e.getMessage();
        }
        return "Linkset published";
    }

    @POST
    @Path("/dataidtojson")
    @Produces("application/json")
    public String DataIdToJson(final String ttl) {
        try {
            IdPart dataid = new IdPart(ttl);
            ObjectMapper mapper = new ObjectMapper();
            mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
            if(dataid.getPartType() == IdPart.DataIdParts.DataId)
                return mapper.writeValueAsString(dataid);
            else
                return "DataID not found";
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    @POST
    @Path("/publishdataid")
    public String PublishDataId(final String ttl) throws DataIdInputException {
        IdPart dataid = new IdPart(ttl);
        //TODO aktivate if RDFUnit is solved
        //if(dataid.validate()) {
        try {
            graph.enterDataId(dataid);
/*            List<Dataset> sets = proc.parseToDataHubDataset(ttl);

            JsonLdOptions opt = new JsonLdOptions();
            opt.setBase("http://someuri.org");
            opt.setUseNativeTypes(true);
            opt.setCompactArrays(true);
            opt.setExpandContext(proc.getMappings().getRdfContext().getMap());

            for(Dataset set : sets)
            {
                try {
                    RDFDataset rdfd = (RDFDataset) JsonLdProcessor.toRDF(set.getGraph(), opt);
                    String innerId = RDFDatasetUtils.toNQuads(rdfd);
                    graph.enterDataId(innerId, set.getDataIdUri(), set.getVersion());
                    response.append(produceHttpResponse("Dataset " + set.getDataIdUri() + " was published correctly"));
                } catch (Exception e) {
                    response.append(produceHttpResponse("Dataset " + set.getDataIdUri() + " encountered an error: " + e.getMessage()));
                }
            }*/
        } catch (Exception e) {
            throw new DataIdInputException(e);
        }
        return "DataID has been saved";
        //}
        //throw new DataIdInputException("DataId is not valid!");
    }


    @GET
    @Path("/getheadversion")
    @Produces("text/plain")
    public String getHeadVersion(
            @QueryParam(value = "uri") final String uri,
            @QueryParam(value = "branch") final String branch) throws DataIdInputException{
        VirtuosoDataIdBranch bbranch = graph.getDataIdBranch(uri, branch);
        return bbranch.getVersion(bbranch.getHead()).toSerialization(RDFFormat.JSONLD);
    }

    @GET
    @Path("/getlatestversion")
    @Produces("text/plain")
    public String getLatestVersion(@QueryParam(value = "uri") final String uri) throws DataIdInputException{
        VirtuosoDataIdBranch bra =  graph.getMasterBranch(uri);
        return bra.getVersion(bra.getHead()).toSerialization(RDFFormat.JSONLD);
    }

    @GET
    @Path("/getversion")
    @Produces("text/plain")
    public String getVersion(
            @QueryParam(value = "uri") final String uri,
            @QueryParam(value = "branch") final String branch,
            @QueryParam(value = "version") final int version) throws DataIdInputException{
        IdPart id =  graph.getDataIdBranch(uri, branch).getVersion(version);
        return id.toSerialization(RDFFormat.JSONLD);
    }

    @POST
    @Path("/createnewbranch")
    @Produces("text/plain")
    public String createNewBranch(
            @QueryParam(value = "uri") final String uri,
            @QueryParam(value = "branch") final String branch,
            @QueryParam(value = "contact") final String contact,
            @DefaultValue("1") @QueryParam(value = "isMaster") final int isMaster,
            @DefaultValue("") @QueryParam(value = "parent") final String parent,
            final String ttl) throws DataIdInputException{
        //TODO add Datahub and other support
        IdPart dataid = new IdPart(ttl);
        if(dataid.validate())
        {
            //TODO check isMaster
            VirtuosoDataIdBranch bra = graph.createNewDataIdBranch(uri, branch, parent, contact, isMaster);
            bra.insertDelta(dataid, bra.getKey(), contact, 1);
            return bra.getKey();
        }
        throw new DataIdInputException("provided DataId was not valid");
    }

    @POST
    @Path("/addversion")
    @Produces("text/plain")
    public String addVersion(
            @QueryParam(value = "uri") final String uri,
            @QueryParam(value = "branch") final String branch,
            @QueryParam(value = "key") final String key,
            @QueryParam(value = "agent") final String agent,
            @DefaultValue("1") @QueryParam(value = "isHead") final int isHead,
            final String ttl) throws DataIdInputException{
        VirtuosoDataIdBranch bbranch = graph.getDataIdBranch(uri, branch);
        IdPart dataid = new IdPart(ttl);
        VirtuosoDataIdDelta delta = bbranch.insertDelta(dataid, key, agent, isHead);
        return delta.getDelIdentifier();
    }
}