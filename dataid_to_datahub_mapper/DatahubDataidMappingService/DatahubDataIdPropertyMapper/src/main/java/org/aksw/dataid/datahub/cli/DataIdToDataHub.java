package org.aksw.dataid.datahub.cli;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.aksw.dataid.config.DataIdConfig;
import org.aksw.dataid.datahub.jsonobjects.DatahubError;
import org.aksw.dataid.datahub.jsonobjects.Dataset;
import org.aksw.dataid.datahub.propertymapping.*;
import org.aksw.dataid.datahub.restclient.CkanRestClient;
import org.aksw.dataid.errors.DataHubMappingException;
import org.aksw.dataid.errors.DataIdInputException;
import org.aksw.dataid.jsonutils.StaticJsonHelper;
import org.apache.commons.cli.*;
import org.apache.http.client.HttpResponseException;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class DataIdToDataHub {

	private static String sourceUrl = null;
	private static PropertyMapper mapper;
	private static CkanRestClient client;
	private static CommandLine cli;

	public static void main(String[] args) 
	{
		CommandLine cli = getCli(args);
		String dataIdContent = getDataIdContent(cli);
		List<Dataset> sets = null;
		DataIdProcesser processor;
		try {
			processor = new DataIdProcesser(DataIdConfig.getMappingConfigPath());
			sets = processor.parseToDataHubDataset(dataIdContent);
		} catch (DataHubMappingException e) {
			e.printStackTrace();
        } catch (DataIdInputException e1) {
            e1.printStackTrace();
        }
        client = createCkanRestClient();

		for(Dataset set : sets)
		{
	    	try {		
			    if(cli.getOptionValue("create") != null)
			    {
                    try {
                        client.CreateDataset(set);
                    } catch (DatahubError e) {
                        e.printStackTrace();
                    }
                }
			    else if(cli.getOptionValue("update") != null)
			    {
                    try {
                        client.UpdateDataset(set);
                    } catch (DatahubError e) {
                        e.printStackTrace();
                    }
                }
			} catch (HttpResponseException e) {
		        System.err.println( "An exception occurred while accessing Datahub: \n" + e.getMessage() );
		        System.exit(0);
			} catch (IOException e) {
		        System.err.println( "An exception occurred: \n" + e.getMessage() );
		        System.exit(0);
			}
		}
	}

	private static CkanRestClient createCkanRestClient() {
		String dataHubUrl = DataIdConfig.get("datahubActionUri");
        String apiKey = DataIdConfig.get("datahubApiKey");
        int timeout = Integer.parseInt(DataIdConfig.get("ckanTimeOut"));
		Map<String, String> actions = new HashMap<String, String>();
		Iterator<Entry<String, JsonNode>> i = DataIdConfig.getActionMap();
		for(Entry<String, JsonNode> key; i.hasNext();)
		{
			key = i.next();
			actions.put(key.getKey(), key.getValue().asText());
		}
		
		CkanRestClient client = new CkanRestClient(dataHubUrl, apiKey, actions, timeout);
		return client;
	}


	private static String getDataIdContent(CommandLine cli) {
		String content = null;
	    if(cli.getOptionValue("file") != null)
	    {
            try {
                sourceUrl = cli.getOptionValue("file");
                InputStream inputStream = new FileInputStream(sourceUrl);
                return StaticJsonHelper.GetStringFromInputStream(inputStream);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            return null;
        }
	    else
	    {
		    sourceUrl = cli.getOptionValue("url");
		    return StaticJsonHelper.readUrl(sourceUrl);
	    }
	}

	private static CommandLine getCli(String[] args) {
		Options options = createInputOptions();
	    CommandLineParser parser = new BasicParser();
	    CommandLine cli = null;
	    try {
	        // parse the command line arguments
	        cli = parser.parse( options, args );
	    }
	    catch( ParseException exp ) {
	        // oops, something went wrong
	        System.err.println( "The entered arguments are not sound.  Reason: " + exp.getMessage() );
	        System.exit(0);
	    }
		return cli;
	}

	private static String getPrettyString(JsonNode dataHubObject) {
		try {
			return new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(dataHubObject);
		} catch (JsonGenerationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	private static Options createInputOptions()
	{
		Options options = new Options();
		
		options.addOption(OptionBuilder.withArgName( "api key" )
                .hasArg()
                .withDescription(  "override the datahub-api-key given in the MainConfig file" )
                .create( "datahubApiKey"));
		
		OptionGroup group1 = new OptionGroup();
		group1.addOption(OptionBuilder.withArgName( "file" )
                .hasArg()
                .withDescription(  "use given file as DataId-source (formats: ttl, n3, json)" )
                .create( "file"));
		group1.addOption(OptionBuilder.withArgName( "url" )
                .hasArg()
                .withDescription(  "use given url as DataId-source (formats: ttl, n3, json)" )
                .create( "url"));
		group1.isRequired();
		options.addOptionGroup(group1);

		
		//TODO get rid of dataset-names
		OptionGroup group2 = new OptionGroup();
		group2.addOption(OptionBuilder.withArgName( "dataset name" )
                .hasArg()
                .withDescription(  "create a new dataset with a given name" )
                .create( "create"));
		group2.addOption(OptionBuilder.withArgName( "dataset name" )
                .hasArg()
                .withDescription(  "update a given dataset" )
                .create( "update"));
		group2.isRequired();
		options.addOptionGroup(group2);
		
		return options;
	}
}
