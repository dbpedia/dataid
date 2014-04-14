package chl.datahub.ckan.restclient;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.configuration.PropertiesConfigurationLayout;

public class ConfigFileManager {

    private String fileName = null;
    private PropertiesConfiguration config;

    public ConfigFileManager() 
    {
    	this.config = new PropertiesConfiguration();
    }
    
    public void SaveProperties() throws ConfigurationException 
    {
    	if(fileName != null)
    	{
    		Path path = Paths.get(fileName);
    		if(Files.isWritable(path))
    		{
        		config.save(fileName);
    		}
    		throw new ConfigurationException("File is not writable.");
    	}
    	throw new ConfigurationException("No File loaded.");
    }
    
    public void LoadProperties(String filePath) throws ConfigurationException 
    {
    	Path path = Paths.get(filePath);
    	if(Files.exists(path) && !Files.isDirectory(path))
    	{
        	this.fileName = filePath;
    		if(Files.isReadable(path))
    		{
    			config.load(filePath);
    			return;
    		}
    	}
    	else if(!Files.exists(path) && Files.exists(path.getParent()) && Files.isDirectory(path.getParent()))
    	{
    		this.fileName = filePath;
    		config.load();
    		return;
    	}
    	throw new ConfigurationException("File could not be loaded.");
    }

}
