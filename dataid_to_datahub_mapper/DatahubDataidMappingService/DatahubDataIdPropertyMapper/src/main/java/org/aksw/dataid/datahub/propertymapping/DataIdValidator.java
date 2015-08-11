package org.aksw.dataid.datahub.propertymapping;

import com.hp.hpl.jena.rdf.model.Model;
import org.aksw.rdfunit.RDFUnitConfiguration;
import org.aksw.rdfunit.enums.TestCaseExecutionType;
import org.aksw.rdfunit.exceptions.TestCaseExecutionException;
import org.aksw.rdfunit.exceptions.UndefinedSerializationException;
import org.aksw.rdfunit.sources.Source;
import org.aksw.rdfunit.tests.TestSuite;
import org.aksw.rdfunit.validate.ParameterException;
import org.aksw.rdfunit.validate.wrappers.RDFUnitStaticWrapper;

import java.util.Arrays;

/**
 * User: Dimitris Kontokostas
 * Description
 * Created: 6/19/14 1:37 PM
 */
public class DataIdValidator {

    public DataIdValidator(final String ontologyUri)
    {
        RDFUnitStaticWrapper.initWrapper(ontologyUri);
    }

    protected RDFUnitConfiguration getConfiguration(String datasetname, String type) throws ParameterException {

        if (type == null || !(type.equals("text") || type.equals("uri"))) {
            throw new ParameterException("'t' must be one of text or uri");
        }

        if (datasetname == null || datasetname.isEmpty()){
            throw new ParameterException("'s' must be defined and not empty");
        }

        boolean isText = type.equals("text");

        String datasetName = datasetname;
        if (isText) {
            datasetName = "custom-text";
        }

        String inputFormat = "turtle";

        String outputFormat = "turtle";

        RDFUnitConfiguration configuration = new RDFUnitConfiguration(datasetName, "../data/");

        if (isText) {
            try {
                configuration.setCustomTextSource(datasetname, inputFormat);
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
            configuration.setTestCaseExecutionType( TestCaseExecutionType.aggregatedTestCaseResult);
        } catch (Exception e) {
            configuration.setTestCaseExecutionType( TestCaseExecutionType.aggregatedTestCaseResult);
        }

        if (configuration.getTestCaseExecutionType().equals(TestCaseExecutionType.extendedTestCaseResult) && outputFormat.equals("html")) {
            throw new ParameterException("Annotated results cannot be displayed in HTML, please select an RDF format (e.g. turtle)");
        }

        // test input if it reads data
        //configuration.getTestSource().getExecutionFactory();

        return configuration;
    }

    protected TestSuite getTestSuite(final RDFUnitConfiguration configuration, final Source dataset) {
        return RDFUnitStaticWrapper.getTestSuite();
    }

    protected Model validate(final RDFUnitConfiguration configuration, final Source dataset, final TestSuite testSuite) throws TestCaseExecutionException {
        return RDFUnitStaticWrapper.validate(configuration.getTestCaseExecutionType(), dataset, testSuite);
    }
}


