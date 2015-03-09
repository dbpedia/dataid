package aksw.dataid.datahub.jsonutils;

import java.io.*;

import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.core.JsonParseException;
import com.github.jsonldjava.core.JsonLdError;
import com.github.jsonldjava.core.JsonLdProcessor;
import com.github.jsonldjava.core.RDFDataset;
import com.github.jsonldjava.impl.NQuadRDFParser;
import com.github.jsonldjava.impl.TurtleRDFParser;
import com.github.jsonldjava.utils.JsonUtils;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFParseException;
import org.openrdf.rio.turtle.TurtleParser;

public class StaticJsonHelper 
{
	public static final ObjectMapper mapper = new ObjectMapper();

	public static String GetStringFromInputStream(InputStream is) 
	{
		BufferedReader br = null;
		StringBuilder sb = new StringBuilder();
 
		String line;
		try {
 
			br = new BufferedReader(new InputStreamReader(is));
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}
 
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return sb.toString();
	}
	
	public static boolean isJsonLdValid(String content)
	{
        try {
            JsonUtils.fromString(content);
        } catch (IOException e) {
            return false;
        }
        return true;
	}

    public static boolean isTurtleValid(String content)
    {
        try {
            new TurtleParser().parse(new StringReader(content), "http://someuri.org");
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static boolean isNquadValid(String content)
    {
        try {
            new NQuadRDFParser().parse(content);
        } catch (JsonLdError jsonLdError) {
            return false;
        }
        return true;
    }

    public static boolean isRdfXmlValid(String content)
    {
        try {
            new NQuadRDFParser().parse(content);
        } catch (JsonLdError jsonLdError) {
            return false;
        }
        return true;
    }

	public static JsonNode getJsonContent(String path) {
		JsonFileManager mainFileManager = new JsonFileManager();
		try {
			mainFileManager.LoadJsonFile(path);
		} catch (FileNotFoundException e) {
			System.err.println("File '" + path + "' could not be found. Try editing the MainConfig.json file.");
		}
		return mainFileManager.getFileContent();
	}

    public static String getPrettyContent(String path)
    {
        return getPrettyContent(getJsonContent(path));
    }

    public static String getPrettyContent(JsonNode node)
    {
        try {
            return JsonUtils.toPrettyString(JsonUtils.fromString(node.toString()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
	
	public static void writeJsonContent(String path, String content) throws IOException {
		JsonFileManager mainFileManager = new JsonFileManager();
		mainFileManager.LoadJsonFile(path);
		mainFileManager.SaveToJsonFile(path, StaticJsonHelper.getPrettyContent(StaticJsonHelper.convertStringToJsonNode(content)));
	}
    
	public static JsonNode convertStringToJsonNode(String test) {
		try {
			return StaticJsonHelper.mapper.readValue(test, JsonNode.class);
		} catch (JsonParseException e) {
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
}
