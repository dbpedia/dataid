package chl.datahub.ckan.restclient;

import java.util.List;

import chl.datahub.ckan.data.Dataset;
import chl.datahub.ckan.data.DatasetRelationship;

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
			System.out.println(String.valueOf(set.getId()));
	}

}
