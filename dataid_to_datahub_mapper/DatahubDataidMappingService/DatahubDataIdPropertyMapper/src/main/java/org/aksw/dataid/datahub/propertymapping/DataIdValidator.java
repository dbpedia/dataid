package org.aksw.dataid.datahub.propertymapping;

import com.hp.hpl.jena.rdf.model.Model;
import org.aksw.rdfunit.RDFUnitConfiguration;
import org.aksw.rdfunit.enums.TestCaseExecutionType;
import org.aksw.rdfunit.exceptions.TestCaseExecutionException;
import org.aksw.rdfunit.exceptions.UndefinedSerializationException;
import org.aksw.rdfunit.sources.TestSource;
import org.aksw.rdfunit.tests.TestSuite;
import org.aksw.rdfunit.validate.ParameterException;
import org.aksw.rdfunit.validate.wrappers.RDFUnitStaticWrapper;
import org.aksw.rdfunit.validate.ws.RDFUnitWebService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;

/**
 * User: Dimitris Kontokostas
 * Description
 * Created: 6/19/14 1:37 PM
 */
public class DataIdValidator {


    public DataIdValidator(String ontologyUri, String dlUrl) {
        RDFUnitStaticWrapper.initWrapper(ontologyUri, dlUrl);
    }

    protected RDFUnitConfiguration getConfiguration(String type, String source, String inputFormat, String outputFormat, TestCaseExecutionType testCaseType) throws ParameterException {

/*        String type = httpServletRequest.getParameter("t");
        if (type == null || !(type.equals("text") || type.equals("uri"))) {
            throw new ParameterException("'t' must be one of text or uri");
        }

        String source = httpServletRequest.getParameter("s");
        if (source == null || source.isEmpty()){
            throw new ParameterException("'s' must be defined and not empty");
        }



        String inputFormat = "";
        if (isText) {
            inputFormat = httpServletRequest.getParameter("i");
            if (inputFormat == null || inputFormat.isEmpty()) {
                throw new ParameterException("'i' must be defined when -t = 'text'");
            }
        }

        String outputFormat = httpServletRequest.getParameter("o");
        if (outputFormat == null || outputFormat.isEmpty()){
            outputFormat = "html";
        }*/

        boolean isText = type.equals("text");

        String datasetName = source;
        if (isText) {
            datasetName = "custom-text";
        }

        RDFUnitConfiguration configuration = new RDFUnitConfiguration(datasetName, "../data/");

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

        return configuration;
    }

    protected TestSuite getTestSuite(final RDFUnitConfiguration configuration, final TestSource testSource) {
        return RDFUnitStaticWrapper.getTestSuite();
    }

    protected Model validate(final RDFUnitConfiguration configuration, final TestSource testSource, final TestSuite testSuite) throws TestCaseExecutionException {
        return RDFUnitStaticWrapper.validate(configuration.getTestCaseExecutionType(), testSource, testSuite);
    }

    protected void printHelpMessage(HttpServletResponse httpServletResponse) throws IOException {
        String helpMessage =
                "\n -t\ttype: One of 'text|uri'" +
                        "\n -s\tsource: Depending on -t it can be either a uri or text" +
                        "\n -i\tInput format (in case of text type):'turtle|ntriples|rdfxml" + //|JSON-LD|RDF/JSON|TriG|NQuads'" +
                        "\n -o\tOutput format:'html(default)|turtle|jsonld|rdfjson|ntriples|rdfxml|rdfxml-abbrev" + //JSON-LD|RDF/JSON|TriG|NQuads'"
                        "";

    }
}


