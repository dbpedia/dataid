package aksw.dataid.datahub.propertymapping;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.JsonNode;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.github.jsonldjava.core.Context;
import com.github.jsonldjava.core.JsonLdApi;
import com.github.jsonldjava.core.JsonLdError;
import com.github.jsonldjava.core.JsonLdOptions;
import com.github.jsonldjava.core.JsonLdProcessor;
import com.github.jsonldjava.core.RDFDataset;
import com.github.jsonldjava.core.RDFDatasetUtils;
import com.github.jsonldjava.utils.JsonUtils;

import aksw.dataid.datahub.jsonobjects.Dataset;
import aksw.dataid.datahub.jsonobjects.DatasetRelationship;
import aksw.dataid.datahub.jsonutils.JsonFileManager;
import aksw.dataid.datahub.restclient.CkanRestClient;


public class TestMapping {
	public static void main(String[] args) 
	{
		String main = System.getProperty("user.dir");
		String mainConfigPath = main + "\\bin\\MainConfig.Json";
		String dataidTurtle = main + "\\bin\\dbpedia_complete_id.ttl";
		String mapperConfigPath = main;
		
		if(args.length < 1)
		{
			System.out.println("provide a dataset-id");
			return;
		}
		
		JsonFileManager mainFileManager = new JsonFileManager();
		try {
			mainFileManager.LoadJsonFile(mainConfigPath);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		String mappingConfig = mainFileManager.getFileContent().get("mappingConfigPath").asText();
		mapperConfigPath = mappingConfig.startsWith("\\") ? mapperConfigPath + mappingConfig : mappingConfig;
		PropertyMapper propMapper = new PropertyMapper(mapperConfigPath);
		
		String dataIdContentTurtle = StaticHelper.readFile(dataidTurtle);
//		RDFDataset inputRdf = null;
//		try {
//			inputRdf = RDFDatasetUtils.parseNQuads(dataIdContentTurtle);
//		} catch (JsonLdError e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		JsonLdApi ldApi = new JsonLdApi();
		JsonLdOptions opt = new JsonLdOptions();
		opt.setFormat("text/turtle");
		opt.setUseNativeTypes(true);
		opt.setCompactArrays(true);
		//opt.useNamespaces = true;
		Object result =null;
		try {
			result = JsonLdProcessor.fromRDF(dataIdContentTurtle, opt);
		} catch (JsonLdError e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			result = JsonLdProcessor.compact(result, propMapper.getJsonLdContext(), opt);
		} catch (JsonLdError e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		List<LinkedHashMap> list = (List<LinkedHashMap>) ((Map<String, Object>) result).get("@graph");
		JsonNode node = null;
		try {
			node = propMapper.DataidToDatahub(list);
		} catch (DataHubMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(node.toString());
//		try {
//			System.out.println(JsonUtils.toPrettyString(result));
//		} catch (JsonGenerationException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		String content = mainFileManager.getFileContent().get("datahubActionUri").asText();
//		CkanRestClient client = new CkanRestClient(content);
//		Dataset set = client.GetDataset(args[0]);
//		JsonNode orgigin = StaticHelper.mapper.valueToTree(set);
//		System.out.println(orgigin.toString());
//		JsonNode dataid = propMapper.DatahubToDataid(set);
//		System.out.println(dataid.toString());
	}

}
