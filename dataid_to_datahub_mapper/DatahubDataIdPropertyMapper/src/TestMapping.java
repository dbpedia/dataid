
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.List;

import org.codehaus.jackson.JsonNode;

import aksw.dataid.datahub.jsonobjects.Dataset;
import aksw.dataid.datahub.jsonobjects.DatasetRelationship;
import aksw.dataid.datahub.jsonutils.JsonFileManager;
import aksw.dataid.datahub.propertymapping.PropertyMapper;
import aksw.dataid.datahub.restclient.CkanRestClient;


public class TestMapping {
	public static void main(String[] args) 
	{
		URL main = TestMapping.class.getResource("TestMapping.class");
		String mainConfigPath = main.getPath() + "/MainConfig.Json";
		String mapperConfigPath = main.getPath();
		
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
		
		String mappingConfig = mainFileManager.getFileContent().get("mappingConfigPath").toString();
		mapperConfigPath = mappingConfig.startsWith("/") ? mapperConfigPath + mappingConfig : mappingConfig;
		CkanRestClient client = new CkanRestClient(mainFileManager.getFileContent().get("datahubActionUri").toString());
		Dataset set = client.GetDataset(args[0]);
		PropertyMapper propMapper = new PropertyMapper(mappingConfig);
		JsonNode dataid = propMapper.DatahubToDataid(set);
		System.out.println(dataid.toString());
		JsonNode datahub = propMapper.DataidToDatahub(dataid);
		System.out.println(datahub.toString());
	}

}
