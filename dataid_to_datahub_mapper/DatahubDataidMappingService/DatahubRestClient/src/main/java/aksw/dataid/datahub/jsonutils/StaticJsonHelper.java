package aksw.dataid.datahub.jsonutils;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Date;

import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.core.JsonParseException;
import org.json.JSONException;
import org.json.JSONObject;

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
	
	public static boolean isJsonValid(String test)
	{
        try {
            new JSONObject(test);
        } catch (JSONException e) {
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
        //catch (JSONException e) {
		//	System.err.println("File not in valid Json-format.");
		//}
		return mainFileManager.getFileContent();
	}
	
	public static void writeJsonContent(String path, String content)
	{
		JsonFileManager mainFileManager = new JsonFileManager();
		try {
			mainFileManager.LoadJsonFile(path);
		} catch (FileNotFoundException e) {
			System.err.println("File '" + path + "' could not be found. Try editing the MainConfig.json file.");
		} 
		String newPath = path.substring(0, path.lastIndexOf('.')) + new Date().toGMTString().replace(" ", "").replace(":", "") + ".json";
		mainFileManager.SaveToJsonFile(newPath, mainFileManager.getFileContent().toString());
		mainFileManager.SaveToJsonFile(path, content);
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
