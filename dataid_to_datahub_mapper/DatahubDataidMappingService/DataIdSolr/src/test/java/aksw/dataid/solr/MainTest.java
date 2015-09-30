package aksw.dataid.solr;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.aksw.dataid.rdfunit.TestUtils;
import org.apache.log4j.Logger;


public class MainTest {
	
	static Logger logger = Logger.getLogger(MainTest.class);
	
	public static void main(String[] args) throws Exception {
		logger.info("Hello World");
		
		TestUtils.init();
		
		DataIdSolrHandler solrHandler = null;
		try {
			//solrHandler = new DataIdSolrHandler("http://localhost:8983/solr/Test");
			solrHandler = new DataIdSolrHandler("http://vmdbpedia.informatik.uni-leipzig.de:8983/solr/dataidtest");
		
			// enable when you want to ingest new data
			Map<String, List<DataIdSolrDocument>> results = null;
			final boolean enableIngestion = true;
			final boolean enabledDeletion = false;
			if (enableIngestion) {
				
				Collection<String> fieldNames = solrHandler.getKnownSolrFieldNames();
				
				// convert Data ID data into data which can be understood by SOLR
				final String baseUrl = "http://dataid.dbpedia.org/lod/core/";
				DataIdSolrConverterUtils dataIt2SolrConverter = new DataIdSolrConverterUtils(										
										(String) null, 
										"src/test/exampleDataId.ttl",
										//"https://raw.githubusercontent.com/dbpedia/dataid/master/ontology/dataid.ttl",
										fieldNames);
				
				dataIt2SolrConverter.addDataIdModel(baseUrl, "src/test/exampleDataId.ttl");
				
				results = dataIt2SolrConverter.getJsonSolrDocuments();
				
				for (String key : results.keySet()) {
					logger.info("Result: " + key + "\n");
					solrHandler.addDataIdSolrDocuments(results.get(key));
				}
			}
			
			// try out search
			String[] filter = null;// {"title:data"};
			String response = solrHandler.search("linked label data petr", filter);
			System.out.println("Response header: " + response);
			
			if (enabledDeletion && null != results) {
				List<DataIdSolrDocument> list = results.get("dataid");
				for (DataIdSolrDocument dataid : list) {
					String id = dataid.getId();
					logger.info("Delete ID: " + id);
					
					solrHandler.deleteDataIdDocument(id);
				}
			}
		} finally {		
			if (null != solrHandler) {
				solrHandler.close();
			}
		}
		
		return;
	}

}
