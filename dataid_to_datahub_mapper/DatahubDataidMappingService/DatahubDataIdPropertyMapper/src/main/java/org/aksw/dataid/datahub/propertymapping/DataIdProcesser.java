package org.aksw.dataid.datahub.propertymapping;

import com.fasterxml.jackson.core.JsonParseException;
import com.github.jsonldjava.core.JsonLdError;
import com.github.jsonldjava.core.JsonLdOptions;
import com.github.jsonldjava.core.JsonLdProcessor;
import com.github.jsonldjava.utils.JsonUtils;
import com.hp.hpl.jena.rdf.model.Model;
import org.aksw.dataid.config.DataIdConfig;
import org.aksw.dataid.datahub.jsonobjects.Dataset;
import org.aksw.dataid.datahub.mappingobjects.DataId;
import org.aksw.dataid.datahub.mappingobjects.DataidInput;
import org.aksw.dataid.datahub.mappingobjects.MappingConfig;
import org.aksw.dataid.jsonutils.RdfXmlParser;
import org.aksw.dataid.jsonutils.StaticJsonHelper;
import org.aksw.dataid.jsonutils.TtlParser;
import org.aksw.rdfunit.RDFUnitConfiguration;
import org.aksw.rdfunit.enums.TestCaseExecutionType;
import org.aksw.rdfunit.exceptions.TestCaseExecutionException;
import org.aksw.rdfunit.io.format.SerialiazationFormatFactory;
import org.aksw.rdfunit.io.format.SerializationFormat;
import org.aksw.rdfunit.sources.TestSource;
import org.aksw.rdfunit.validate.ParameterException;

import java.io.IOException;
import java.util.*;

public class DataIdProcesser 
{
    private MappingConfig mappingConfig;
	private PropertyMapper mapper;
    private DataIdValidator validator;
	
	public DataIdProcesser(String mappingConfigPath, String ontologyPath) throws DataHubMappingException {
        mappingConfig = StaticJsonHelper.castJsonToObject(StaticJsonHelper.getJsonContent(mappingConfigPath).toString(), MappingConfig.class, "@graph");
        mappingConfig.setRdfContext(StaticJsonHelper.castJsonToObject(StaticJsonHelper.getJsonContent(mappingConfigPath).toString(), new HashMap<String, String>().getClass(), "@context"));
        if(mappingConfig == null)
            throw new DataHubMappingException("the mapping-config file could not be found");
        mapper = new PropertyMapper(StaticJsonHelper.getJsonContent(mappingConfigPath));
        validator = new DataIdValidator(DataIdConfig.getDataIdUri(), null);

        JsonLdProcessor.removeRDFParser("text/turtle");
        JsonLdProcessor.registerRDFParser("text/turtle", new TtlParser());
        JsonLdProcessor.registerRDFParser("rdf/xml", new RdfXmlParser());
	}

    public List<Dataset> parseToDataHubDataset(String sourceId) throws DataHubMappingException, DataIdInputException {
        List<Dataset> sets = new ArrayList<Dataset>();
        DataidInput inputType = getInputType(sourceId);
        if(inputType == DataidInput.NoDataId)
            throw new DataIdInputException("The provided input is not of the following formats: turtle, n-quads, RdfXml or json-ld");
        try {
            if(inputType == DataidInput.Turtle)
                sets = mapper.DataidToDatasets(parseDataIdFromTurtle(sourceId));
            else if(inputType == DataidInput.Nquads)
                sets = mapper.DataidToDatasets(parseDataIdFromNquads(sourceId));
            else if(inputType == DataidInput.JsonLd)
                sets = mapper.DataidToDatasets(parseDataIdFromJson(sourceId));
            else if(inputType == DataidInput.RdfXml)
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
		result = JsonLdProcessor.compact(result, mappingConfig.getRdfContext().getMap(), opt);
		return buildDataId(result);
	}

	private DataId buildDataId(Object result) {
		DataId id = new DataId();
		id.setRdfContext(this.mappingConfig.getRdfContext());
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
        return buildDataId(JsonLdProcessor.compact(result, this.mappingConfig.getRdfContext(), opt));
    }

    public Model validateDataId(final String sourceId) throws DataIdInputException {
        DataidInput inputFormat = getInputType(sourceId);
        try {
            SerializationFormat sf = null;
            for(SerializationFormat f : SerialiazationFormatFactory.getAllFormats())
            {
                if(f.getName().equals(inputFormat.name().toUpperCase()))
                {
                    sf = f;
                    break;
                }
            }
            if(sf == null)
                throw new DataIdInputException("unknown serialization format");

            //TODO RDFUnit
            RDFUnitConfiguration config = validator.getConfiguration("text", sourceId, sf.getName(),sf.getName(), TestCaseExecutionType.statusTestCaseResult);
            TestSource source = config.getTestSource();
            return validator.validate(config, source, validator.getTestSuite(config, source));

        } catch (ParameterException e) {
            throw new DataIdInputException(e);
        } catch (TestCaseExecutionException e) {
            throw new DataIdInputException(e);
        }
    }
	
	private DataidInput getInputType(String testString)
	{
		String test = testString.trim();
		if(!test.contains(DataIdConfig.getDataIdUri()))  //!not!
			return DataidInput.NoDataId;
		else if(StaticJsonHelper.isJsonLdValid(test))
			return DataidInput.JsonLd;
		else if(StaticJsonHelper.isTurtleValid(test))
			return DataidInput.Turtle;
		else if(StaticJsonHelper.isNquadValid(test))
			return DataidInput.Nquads;
        else if(test.replace(" ", "").contains("rdf:resource=\"" + DataIdConfig.getDataIdUri() + "\""))
            return DataidInput.RdfXml;
		return DataidInput.NoDataId;
	}

    public MappingConfig getMappingConfig() {
        return mappingConfig;
    }
}
