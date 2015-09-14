package org.aksw.dataid.rdfunit;

import java.util.*;

import com.hp.hpl.jena.rdf.model.Model;
import org.aksw.jena_sparql_api.model.QueryExecutionFactoryModel;
import org.aksw.rdfunit.RDFUnitConfiguration;
import org.aksw.rdfunit.elements.implementations.PatternBasedTestCaseImpl;
import org.aksw.rdfunit.elements.interfaces.TestCase;
import org.aksw.rdfunit.enums.TestCaseExecutionType;
import org.aksw.rdfunit.exceptions.TestCaseExecutionException;
import org.aksw.rdfunit.exceptions.UndefinedSerializationException;
import org.aksw.rdfunit.io.reader.RDFReaderException;
import org.aksw.rdfunit.sources.SchemaSource;
import org.aksw.rdfunit.sources.TestSource;
import org.aksw.rdfunit.statistics.DatasetStatistics;
import org.aksw.rdfunit.tests.TestSuite;
import org.aksw.rdfunit.tests.results.DatasetOverviewResults;
import org.aksw.rdfunit.validate.ParameterException;
import org.aksw.rdfunit.validate.wrappers.RDFUnitStaticValidator;
import org.aksw.rdfunit.validate.wrappers.RDFUnitTestSuiteGenerator;

/**
 * User: Dimitris Kontokostas
 * Description
 * Created: 6/19/14 1:37 PM
 */
public class DataIdValidator {

    private final List<SchemaSource> ontologySource = new ArrayList<>();
    private final Map<String, List<String>> exceptions = new HashMap<>();

    public DataIdValidator(Map<String, Map.Entry<String, String>> ontologySources, Map<String, List<String>> exceptions) throws RDFReaderException {

        this.exceptions.putAll(exceptions);
        RDFUnitTestSuiteGenerator.Builder tgBuilder = new RDFUnitTestSuiteGenerator.Builder();
        for(String prefix : ontologySources.keySet())
        {
            tgBuilder.addLocalResource(prefix, ontologySources.get(prefix).getValue());
        }
        RDFUnitStaticValidator.initWrapper(tgBuilder.build());
    }

    public RDFUnitConfiguration getConfiguration(String type, String source, String inputFormat, String outputFormat, TestCaseExecutionType testCaseType) throws ParameterException {

        boolean isText = type.equals("text");

        String datasetName = source;
        if (isText) {
            datasetName = "custom-text";
        }

        RDFUnitConfiguration configuration = new RDFUnitConfiguration(datasetName, "../data/");
        //configuration.setTestCacheEnabled(false);
        //configuration.setSchemata(ontologySource);

        if (isText) {
            try {
                configuration.setCustomTextSource(source, inputFormat);
            } catch (UndefinedSerializationException e) {
                throw new ParameterException(inputFormat, e);
            }

        }

        try {
            configuration.setOutputFormatTypes(Arrays.asList(outputFormat));
        } catch (UndefinedSerializationException e) {
            throw new ParameterException(e.getMessage(), e);
        }

        try {
            configuration.setTestCaseExecutionType(testCaseType);
        } catch (Exception e) {
            configuration.setTestCaseExecutionType( TestCaseExecutionType.aggregatedTestCaseResult);
        }

        if (configuration.getTestCaseExecutionType().equals(TestCaseExecutionType.extendedTestCaseResult) && outputFormat.equals("html")) {
            throw new ParameterException("Annotated results cannot be displayed in HTML, please select an RDF format (e.g. turtle)");
        }

        // test input if it reads data
        configuration.getTestSource().getExecutionFactory();
        getTestSuite();
        return configuration;
    }

    public TestSuite getTestSuite() {    //final RDFUnitConfiguration configuration, final TestSource testSource) {
        TestSuite ts = RDFUnitStaticValidator.getTestSuite();
        if(exceptions != null) {
            List<TestCase> removees = new ArrayList<>();
            Iterator<TestCase> tCases = ts.getTestCases().iterator();
            while (tCases.hasNext()) {
                TestCase tc = tCases.next();
                if(!(tc instanceof PatternBasedTestCaseImpl)) // not!
                    continue;
                if(exceptions.keySet().contains(((PatternBasedTestCaseImpl) tc).getAutoGeneratorURI()))
                {
                    for(String e : exceptions.get(((PatternBasedTestCaseImpl) tc).getAutoGeneratorURI())) {
                        if (tc.getSparqlWhere().contains(e))
                            removees.add(tc);
                    }
                }
            }
            for (TestCase tc : removees)
                ts.getTestCases().remove(tc);
        }
        return ts;
    }

    public Model validate(final RDFUnitConfiguration configuration, final TestSource testSource, final TestSuite testSuite) throws TestCaseExecutionException {
        DatasetOverviewResults overviewResults = new DatasetOverviewResults();
        Model model = RDFUnitStaticValidator.validate(configuration.getTestCaseExecutionType(), testSource, testSuite, overviewResults);
        System.out.println(overviewResults.toString());
        return model;
    }
}


