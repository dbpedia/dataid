package aksw.dataid.datahub.jsonutils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.configuration.ConfigurationException;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.json.JSONException;

public class JsonFileManager {

    private String fileName = null;
    private JsonNode fileContent = null;

	public JsonFileManager() 
    {
    }
	
    public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public JsonNode getFileContent() {
		return fileContent;
	}
	
    public void SaveToJsonFile(String filePath, String content) throws JSONException
    {
    	if(StaticJsonHelper.isJsonValid(content))
    	{
			FileOutputStream fop = null;
			File file;
	 
			try {
	 
				file = new File(filePath);
				if (!file.exists()) {
					file.createNewFile();
				}
				fop = new FileOutputStream(file);
	 
				byte[] contentInBytes = content.getBytes();
				fop.write(contentInBytes);
				fop.flush();
				fop.close(); 
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					if (fop != null) {
						fop.close();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
    	}
    }
    
    public JsonNode LoadJsonFile(String filePath) throws FileNotFoundException, JSONException 
    {
    	return LoadJsonFile(filePath, false);
    }
    public JsonNode LoadJsonFile(String filePath, boolean classpath) throws FileNotFoundException, JSONException 
    {
    	Path path = Paths.get(filePath);
    	if(classpath)
    	{
			InputStream inputStream = getClass().getResourceAsStream(filePath);
			String test = StaticJsonHelper.GetStringFromInputStream(inputStream);
			if(StaticJsonHelper.isJsonValid(test))
				fileContent= StaticJsonHelper.convertStringToJsonNode(test);
				
			return fileContent;
    	}
    	else if(Files.exists(path) && !Files.isDirectory(path))
    	{
        	this.fileName = filePath;
        	
        	if(Files.isReadable(path))
    		{
    			try {
					InputStream inputStream = new FileInputStream(filePath);
					String test = StaticJsonHelper.GetStringFromInputStream(inputStream);
					if(StaticJsonHelper.isJsonValid(test))
						fileContent = StaticJsonHelper.convertStringToJsonNode(test);
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
    			return fileContent;
    		}
    	}
    	else if(!Files.exists(path) && Files.exists(path.getParent()) && Files.isDirectory(path.getParent()))
    	{
    		this.fileName = filePath;
    		this.fileContent = StaticJsonHelper.mapper.createObjectNode();
    		return fileContent;
    	}
    	return null;
    }
	
	public void setFileContent(String fileContent) {
		this.fileContent = StaticJsonHelper.convertStringToJsonNode(fileContent);
	}

}
