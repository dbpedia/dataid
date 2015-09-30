package org.aksw.dataid.datahub.mappingservice;

import static org.junit.Assert.*;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.aksw.dataid.config.DataIdConfig;
import org.aksw.dataid.rdfunit.TestUtils;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import aksw.dataid.solr.DataIdSolrConverterUtils;
import aksw.dataid.solr.DataIdSolrDocument;
import aksw.dataid.solr.DataIdSolrHandler;

public class TestMappingServiceSolr {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		TestUtils.init();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	/**
	 * This method can be used to check whether it is possible
	 * to register a DataId in Apache SOLR
	 * 
	 * @throws Exception
	 */
	@Test
	public void testDataIdRegistration() throws Exception {
		DataIdPublisher publisher = new DataIdPublisher();
		publisher.addDataIdToSolr("src/test/exampleDataId.ttl", null);
	}
}
