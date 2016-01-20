package org.aksw.dataid.datahub.propertymapping;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonNode;
import com.github.jsonldjava.core.JsonLdError;
import com.github.jsonldjava.core.JsonLdOptions;
import com.github.jsonldjava.core.JsonLdProcessor;
import com.github.jsonldjava.utils.JsonUtils;
import org.aksw.dataid.config.DataIdConfig;
import org.aksw.dataid.datahub.jsonobjects.*;
import org.aksw.dataid.datahub.mappingobjects.DataId;
import org.aksw.dataid.config.MappingConfig;
import org.aksw.dataid.datahub.restclient.CkanRestClient;
import org.aksw.dataid.errors.DataHubMappingException;
import org.aksw.dataid.errors.DataIdInputException;
import org.aksw.dataid.errors.DataIdServiceException;
import org.aksw.dataid.jsonutils.RdfXmlParser;
import org.aksw.dataid.jsonutils.StaticJsonHelper;
import org.aksw.dataid.jsonutils.TtlParser;
import org.aksw.dataid.rdfunit.DataIdValidator;
import org.aksw.dataid.statics.StaticContent;
import org.aksw.dataid.virtuoso.VirtuosoDataIdGraph;
import org.aksw.rdfunit.io.format.SerializationFormat;

import java.io.IOException;
import java.lang.reflect.Array;
import java.sql.SQLException;
import java.util.*;

public class DataIdProcesser 
{
    private MappingConfig mappings;
	private PropertyMapper mapper;
    private VirtuosoDataIdGraph virtuoso;
	
	public DataIdProcesser(String mappingConfigPath) throws DataHubMappingException, SQLException {
        mappings = StaticContent.getMappings();
        if(mappings == null)
            throw new DataHubMappingException("the mapping-config file could not be found");
        mapper = new PropertyMapper(StaticJsonHelper.getJsonContent(mappingConfigPath));

        JsonLdProcessor.removeRDFParser("text/turtle");
        JsonLdProcessor.registerRDFParser("text/turtle", new TtlParser());
        JsonLdProcessor.registerRDFParser("rdf/xml", new RdfXmlParser());

        virtuoso = new VirtuosoDataIdGraph(DataIdConfig.getVirtuosoHost(), DataIdConfig.getVirtuosoPort(), DataIdConfig.getVirtuosoUser(), DataIdConfig.getVirtuosoPassword());
	}

    public List<Dataset> parseToDataHubDataset(String sourceId) throws DataIdServiceException {
        List<Dataset> sets = new ArrayList<Dataset>();
        SerializationFormat sf = StaticJsonHelper.getSerialization(sourceId);
        if(sf.getName() == "NONE")
            throw new DataIdInputException("The provided input is not of the following formats: turtle, n-quads, RdfXml or json-ld");
        try {
            if(sf.getName() == "TURTLE")
                sets = mapper.DataidToDatasets(parseDataIdFromTurtle(sourceId));
            else if(sf.getName() == "N-QUADS")
                sets = mapper.DataidToDatasets(parseDataIdFromNquads(sourceId));
            else if(sf.getName() == "JSON-LD")
                sets = mapper.DataidToDatasets(parseDataIdFromJson(sourceId));
            else if(sf.getName() == "RDF/XML")
                sets = mapper.DataidToDatasets(parseDataIdFromRdfXml(sourceId));
        } catch (DataHubMappingException e) {
            throw new DataHubMappingException( "An Exception while mapping properties ocurred: " + e.getMessage() );
        } catch (JsonLdError e) {
            throw new DataIdInputException("Error while trying to parse DataId file: " + e.getMessage());
        } catch (JsonParseException e) {
            throw new DataIdInputException("Error while trying to parse DataId file: " + e.getMessage());
        } catch (IOException e) {
            throw new DataIdInputException("IOException: " + e.getMessage());
        }
        return sets;
    }

	private DataId parseDataId(String sourceId, JsonLdOptions opt) throws JsonLdError
	{
		Object result =null;
		result = JsonLdProcessor.fromRDF(sourceId, opt);
		result = JsonLdProcessor.compact(result, StaticContent.getRdfContext().getMap(), opt);
		return buildDataId(result);
	}

	private DataId buildDataId(Object result) {
		DataId id = new DataId();
		id.setRdfContext(StaticContent.getRdfContext());
        id.setDataIdBody((List<LinkedHashMap<String, Object>>) ((Map<String, Object>) result).get("@graph"));
		return id;
	}
	
	private DataId parseDataIdFromTurtle(String sourceId) throws JsonLdError, DataIdInputException {

        //validateDataId(sourceId, "TURTLE");

        JsonLdOptions opt = new JsonLdOptions();
		opt.format = "text/turtle";
        opt.setBase("http://someuri.org");
		opt.setUseNativeTypes(true);
		opt.setCompactArrays(true);

		return parseDataId(sourceId, opt);
	}

    private DataId parseDataIdFromNquads(String sourceId) throws JsonLdError, DataIdInputException {
        //validateDataId(sourceId, "N-TRIPLE");

		JsonLdOptions opt = new JsonLdOptions();
        opt.format = "application/nquads";
        opt.setBase("http://someuri.org");
		opt.setUseNativeTypes(true);
		opt.setCompactArrays(true);
		
		return parseDataId(sourceId, opt);
	}

    private DataId parseDataIdFromRdfXml(String sourceId) throws JsonLdError, DataIdInputException {
        //validateDataId(sourceId, "RDF/XML");

        JsonLdOptions opt = new JsonLdOptions();
        opt.format = "rdf/xml";
        opt.setBase("http://someuri.org");
        opt.setUseNativeTypes(true);
        opt.setCompactArrays(true);

        return parseDataId(sourceId, opt);
    }

    private DataId parseDataIdFromJson(String sourceId) throws JsonLdError, IOException, DataIdInputException {
        //validateDataId(sourceId, "JSON-LD");

        JsonLdOptions opt = new JsonLdOptions();
        opt.format = "jsonld";
        opt.setBase("http://someuri.org");
        opt.setUseNativeTypes(true);
        opt.setCompactArrays(true);
        Object result =null;
        result = JsonUtils.fromString(sourceId);
        return buildDataId(JsonLdProcessor.compact(result, StaticContent.getRdfContext(), opt));
    }

    private List<Dataset> createDatahubDatasets(final String dataid, final String excemptions, final boolean isprivate)
            throws DataIdServiceException {
        List<String> exemptionList = Arrays.asList(excemptions.replace(" ","").split(","));
        List<Dataset> sets = new ArrayList<>();
        for (Dataset set : parseToDataHubDataset(dataid)) {
            if(exemptionList.contains(set.getDataIdUri().trim()))
                continue;
            if(set.getLicense_url() != null && set.getLicense_url().length() > 0)
                set.setLicense_title(virtuoso.getLicenseName(set.getLicense_url()));
            set.setOwner_org(DataIdConfig.get("datahubOwnerDataId"));
            set.setPrivate(isprivate);
            sets.add(set);
        }
        return sets;
    }

    public ValidCkanResponse[] DeleteDatasets(String apiKey, String dataid) throws DataIdServiceException {
        try(CkanRestClient client = createCkanRestClient(apiKey))
        {
            List<Dataset> sets = createDatahubDatasets(dataid, "", true);

            if(sets == null || sets.size() == 0)
                throw new DatahubError("no datasets found");

            HashMap<Dataset, ValidCkanResponse> responseMap = deleteDatasets(client, sets);

            int countErrors = 0;
            for(ValidCkanResponse res : responseMap.values())
                if(res instanceof DatahubError)
                    countErrors ++;
            if(countErrors != 0)
                return responseMap.values().toArray(new ValidCkanResponse[responseMap.values().size()]);

            return sets.toArray(new ValidCkanResponse[sets.size()]);
        } catch (IOException e) {
            throw new DatahubError(e);
        }
    }

    public ValidCkanResponse[] PublishDatasets(String apiKey, String excemptions, boolean isprivate, String dataid) throws DataIdServiceException {
        try(CkanRestClient client = createCkanRestClient(apiKey))
        {
            List<Dataset> sets = createDatahubDatasets(dataid, excemptions, isprivate);

            if(sets == null || sets.size() == 0)
                throw new DatahubError("no datasets found");

            HashMap<Dataset, ValidCkanResponse> responseMap = insertDatasets(client, sets);

            int countErrors = 0;
            for(ValidCkanResponse res : responseMap.values())
                if(res instanceof DatahubError)
                    countErrors ++;
            if(countErrors != 0)
                return responseMap.values().toArray(new ValidCkanResponse[responseMap.values().size()]);

            updateRelationships(client, sets);

            return sets.toArray(new ValidCkanResponse[sets.size()]);
        } catch (IOException e) {
            throw new DatahubError(e);
        }
    }

    private HashMap<Dataset, ValidCkanResponse> deleteDatasets(CkanRestClient client, Collection<Dataset> sets) throws IOException, DatahubError {
        HashMap<Dataset, ValidCkanResponse> erroniousResponseMap = new HashMap<>();
        for(Dataset set : sets) {
            IdMappingObject zw = new IdMappingObject();
            zw.setId(set.getName());
            ValidCkanResponse ckanRes = client.GenericAction(CkanRestClient.CkanAction.DeleteDataset, zw);

            if (ckanRes instanceof Dataset) {
                set.setId(((Dataset) ckanRes).getId());
            }
            erroniousResponseMap.put(set, ckanRes);
        }
        return erroniousResponseMap;
    }

    private HashMap<Dataset, ValidCkanResponse> insertDatasets(CkanRestClient client, Collection<Dataset> sets) throws IOException, DatahubError {
        HashMap<Dataset, ValidCkanResponse> erroniousResponseMap = new HashMap<>();
        for(Dataset set : sets) {
            ValidCkanResponse ckanRes=null;
            Dataset zw = client.GetDataset(set.getName());
            String dataHubDataIdIdentifier = DataIdConfig.get("datahubOwnerDataId");
            if (zw != null && zw.getOwner_org().equals(dataHubDataIdIdentifier))
                ckanRes = client.GenericAction(CkanRestClient.CkanAction.UpdateDataset, set);
            else
                ckanRes = client.GenericAction(CkanRestClient.CkanAction.CreateDataset, set);

            if (ckanRes instanceof Dataset) {
                set.setId(((Dataset) ckanRes).getId());
            }
            erroniousResponseMap.put(set, ckanRes);
        }
        return erroniousResponseMap;
    }

    private List<ValidCkanResponse> updateRelationships(CkanRestClient client, List<Dataset> oldSets) throws IOException, DatahubError {

        List<ValidCkanResponse> reList = new ArrayList<>();
        for(Dataset set : oldSets)
        {
            for(int i =0; i < set.getSubsets().size(); i++)
            {
                String subset = set.getSubsets().get(i);
                for(Dataset sub : oldSets) {
                    if(sub.getDataIdUri().trim().equals(subset.trim()))
                    {
                        DatasetRelationship rel = new DatasetRelationship();
                        rel.setSubject(set.getName());
                        rel.setObject(sub.getName());
                        rel.setType("parent_of");
                        rel.setComment("void:subset");
                        reList.add(client.GenericAction(CkanRestClient.CkanAction.UpdateDatasetRelationship, rel));
                    }
                }
            }
        }
        return reList;
    }

    public MappingConfig getMappings() {
        return mappings;
    }

    public CkanRestClient createCkanRestClient(String apiKey) {
        String dataHubUrl = DataIdConfig.get("datahubActionUri");
        int timeout = Integer.parseInt(DataIdConfig.get("ckanTimeOut"));
        Map<String, String> actions = new HashMap<String, String>();
        Iterator<Map.Entry<String, JsonNode>> i = DataIdConfig.getActionMap();
        for(Map.Entry<String, JsonNode> key; i.hasNext();)
        {
            key = i.next();
            actions.put(key.getKey(), key.getValue().asText());
        }

        CkanRestClient client = new CkanRestClient(dataHubUrl, apiKey, actions, timeout);
        return client;
    }
}
