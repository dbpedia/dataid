package aksw.dataid.datahub.jsonutils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.configuration.ConfigurationException;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;

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
    
    public void SaveProperties() throws ConfigurationException 
    {
    	if(fileName != null)
    	{
    		Path path = Paths.get(fileName);
    		if(Files.isWritable(path))
    		{
        		//config.save(fileName);
    		}
    		throw new ConfigurationException("File is not writable.");
    	}
    	throw new ConfigurationException("No File loaded.");
    }
    public JsonNode LoadJsonFile(String filePath) throws FileNotFoundException 
    {
    	return LoadJsonFile(filePath, false);
    }
    public JsonNode LoadJsonFile(String filePath, boolean classpath) throws FileNotFoundException 
    {
    	Path path = Paths.get(filePath);
    	if(classpath)
    	{
			InputStream inputStream = getClass().getResourceAsStream(filePath);
			String test = StaticHelper.GetStringFromInputStream(inputStream);
			if(StaticHelper.isJsonValid(test))
				fileContent= convertStringToJsonNode(test);
			return fileContent;
    	}
    	else if(Files.exists(path) && !Files.isDirectory(path))
    	{
        	this.fileName = filePath;
        	
        	if(Files.isReadable(path))
    		{
    			try {
					InputStream inputStream = new FileInputStream(filePath);
					String test = StaticHelper.GetStringFromInputStream(inputStream);
					if(StaticHelper.isJsonValid(test))
						fileContent = convertStringToJsonNode(test);
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
    		this.fileContent = StaticHelper.mapper.createObjectNode();
    		return fileContent;
    	}
    	return null;
    }
    
	private JsonNode convertStringToJsonNode(String test) {
		try {
			return StaticHelper.mapper.readValue(test, JsonNode.class);
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
