package aksw.dataid.datahub.propertymapping;

import java.io.FileNotFoundException;
import java.net.URL;
import java.util.List;

import org.codehaus.jackson.JsonNode;

import aksw.dataid.datahub.jsonobjects.Dataset;
import aksw.dataid.datahub.jsonobjects.DatasetRelationship;
import aksw.dataid.datahub.jsonutils.JsonFileManager;
import aksw.dataid.datahub.restclient.CkanRestClient;


public class TestMapping {
	public static void main(String[] args) 
	{
		String main = System.getProperty("user.dir");
		String mainConfigPath = main + "\\bin\\MainConfig.Json";
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
		String content = mainFileManager.getFileContent().get("datahubActionUri").asText();
		CkanRestClient client = new CkanRestClient(content);
		Dataset set = client.GetDataset(args[0]);
		PropertyMapper propMapper = new PropertyMapper(mapperConfigPath);
		JsonNode orgigin = StaticHelper.mapper.valueToTree(set);
		System.out.println(orgigin.toString());
		JsonNode dataid = propMapper.DatahubToDataid(set);
		System.out.println(dataid.toString());
		JsonNode datahub = propMapper.DataidToDatahub(dataid);
		System.out.println(datahub.toString());
	}

}
