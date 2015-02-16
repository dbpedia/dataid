package aksw.dataid.datahub.jsonutils;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.core.JsonParseException;
import com.github.jsonldjava.utils.JsonUtils;

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
