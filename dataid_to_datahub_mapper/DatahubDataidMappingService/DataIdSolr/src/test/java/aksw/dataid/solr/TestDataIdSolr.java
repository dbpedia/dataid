package aksw.dataid.solr;

import static org.junit.Assert.*;

import java.util.Collection;

import org.aksw.dataid.config.DataIdConfig;
import org.aksw.dataid.rdfunit.TestUtils;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * These tests can be used to check basic
 * solr functionality
 * 
 * @author kay
 *
 */
public class TestDataIdSolr {
	
	@BeforeClass
	public static void setupClass() {		
		TestUtils.init();
	}
	
	/**
	 * Check we can get registered solr field names
	 * @throws DataIdSolrException 
	 */
	@Test
	public void testBasic() throws DataIdSolrException {
		DataIdSolrHandler solrHandler = new DataIdSolrHandler();
		Collection<String> fieldNames = solrHandler.getKnownSolrFieldNames();
		
		assertNotNull("Has received field names", fieldNames);
		assertFalse("Has values", fieldNames.isEmpty());
		
		solrHandler.close();
	}

}
