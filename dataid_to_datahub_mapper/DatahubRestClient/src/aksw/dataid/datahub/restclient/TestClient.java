package aksw.dataid.datahub.restclient;

import java.io.FileNotFoundException;
import java.util.List;

import aksw.dataid.datahub.jsonobjects.Dataset;
import aksw.dataid.datahub.jsonobjects.DatasetRelationship;
import aksw.dataid.datahub.jsonutils.JsonFileManager;
//import aksw.dataid.datahub.mappingobjects.MappingConfig;


public class TestClient {

	/**
	 * @param args
	 */
	public static void main(String[] args) 
	{
		CkanRestClient client = new CkanRestClient("http://datahub.io/api/action");
		Dataset set = client.GetDataset("chiletestseta");
		List<DatasetRelationship> shipList = client.GetRelationships("chiletestseta");
//		DatasetRelationship ship = new DatasetRelationship();
//		ship.setComment("tja noch ein versuch");
//		ship.setObject("chiletestseta");
//		ship.setSubject("chiletestseta");
//		ship.setType("parent_of");
//		for(DatasetRelationship ship : set)
		JsonFileManager manager = new JsonFileManager();
		try {
			manager.LoadJsonFile("/MappingConfig.json", true);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//MappingConfig config = manager.GetMappingConfig();
		//System.out.println(config.getDictionary().get("dthub:url").getComment());
	}

}
