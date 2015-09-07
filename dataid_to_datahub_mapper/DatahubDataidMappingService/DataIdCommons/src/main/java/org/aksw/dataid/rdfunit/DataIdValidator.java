package org.aksw.dataid.rdfunit;

import java.io.IOException;
import java.util.*;

import com.hp.hpl.jena.rdf.model.Model;
import org.aksw.rdfunit.RDFUnitConfiguration;
import org.aksw.rdfunit.enums.TestCaseExecutionType;
import org.aksw.rdfunit.exceptions.TestCaseExecutionException;
import org.aksw.rdfunit.exceptions.UndefinedSerializationException;
import org.aksw.rdfunit.io.reader.RDFModelReader;
import org.aksw.rdfunit.io.reader.RDFReaderException;
import org.aksw.rdfunit.io.reader.RDFReaderFactory;
import org.aksw.rdfunit.sources.SchemaSource;
import org.aksw.rdfunit.sources.TestSource;
import org.aksw.rdfunit.tests.TestCase;
import org.aksw.rdfunit.tests.TestSuite;
import org.aksw.rdfunit.validate.ParameterException;
import org.aksw.rdfunit.validate.wrappers.RDFUnitStaticWrapper;
import javax.servlet.http.HttpServletResponse;

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
        for(String prefix : ontologySources.keySet())
        {
            String dls = ontologySources.get(prefix).getValue();
            if(dls == null)
                dls = ontologySources.get(prefix).getKey();
            RDFUnitStaticWrapper.initWrapper(prefix, ontologySources.get(prefix).getKey(), dls);
            org.aksw.rdfunit.io.reader.RDFReader ontologyReader = new RDFModelReader(RDFReaderFactory.createDereferenceReader(dls).read());
            ontologySource.add(new SchemaSource( prefix, ontologySources.get(prefix).getKey(), ontologyReader));
        }

    }

    public RDFUnitConfiguration getConfiguration(String type, String source, String inputFormat, String outputFormat, TestCaseExecutionType testCaseType) throws ParameterException {

        boolean isText = type.equals("text");

        String datasetName = source;
        if (isText) {
            datasetName = "custom-text";
        }

        RDFUnitConfiguration configuration = new RDFUnitConfiguration(datasetName, "../data/");
        configuration.setTestCacheEnabled(false);
        configuration.setSchemata(ontologySource);

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
        TestSuite ts = RDFUnitStaticWrapper.getTestSuite();
        if(exceptions != null) {
            List<TestCase> removees = new ArrayList<>();
            Iterator<TestCase> tCases = ts.getTestCases().iterator();
            while (tCases.hasNext()) {
                TestCase tc = tCases.next();
                if(exceptions.keySet().contains(tc.getAutoGeneratorURI()))
                {
                    for(String e : exceptions.get(tc.getAutoGeneratorURI())) {
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


