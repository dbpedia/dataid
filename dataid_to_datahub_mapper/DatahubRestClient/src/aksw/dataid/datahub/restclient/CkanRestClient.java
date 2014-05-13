package aksw.dataid.datahub.restclient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.type.CollectionType;
import org.codehaus.jackson.type.JavaType;

import aksw.dataid.datahub.jsonobjects.*;
import aksw.dataid.datahub.restclient.HttpMethod;


public class CkanRestClient 
{
	private String connectionUrl;
    private final ObjectMapper serializer = new ObjectMapper();
    private final ObjectMapper deserializer = new ObjectMapper();
    private static int normalTimeout = 10000;
    private String apiKey;
    private Map<String, String> actionMap = new HashMap<String, String>();

    public CkanRestClient(String  baseUrl, String apiKey, Map<String, String> actionMap) 
    {
    	this(baseUrl, apiKey, actionMap, normalTimeout);
    }

	public CkanRestClient(String  baseUrl, String apiKey, Map<String, String> actionMap, int timeout) 
    {
    	this.apiKey = apiKey;
    	normalTimeout = timeout;
        connectionUrl = baseUrl + (!(baseUrl.endsWith("/")) ? "/" : "");
        this.actionMap = actionMap;
        serializer.configure(org.codehaus.jackson.map.SerializationConfig.Feature.WRITE_DATES_AS_TIMESTAMPS, false);
        serializer.setDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS"));
    }
    
    public Dataset GetDataset(String nameId)
    {
    	String subPath = actionMap.get("GetDataset") + "?id=" + nameId;
    	HttpURLConnection conn = getHttpConnection(subPath, HttpMethod.Get, normalTimeout);
    	String json = handleHttpResponse(conn);
    	DatahubResponse<Dataset> returnObject = null;
		try {
			JavaType type = deserializer.getTypeFactory().constructParametricType(DatahubResponse.class, Dataset.class);
			returnObject = deserializer.readValue(json, type);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return returnObject.getResult();
    }
    
    public boolean CreateDataset(String DataHubId, Dataset set)
    {
    	set.setName(DataHubId);
    	set.PrepareForParsing();
    	JsonNode json = serializer.convertValue(set, JsonNode.class);
    	String path = actionMap.get("CreateDataset");  
    	return postJson(path, json.toString());
    }
    
    public boolean UpdateDataset(String DataHubId, Dataset set)
    {
    	set.setName(DataHubId);
    	set.PrepareForParsing();
    	JsonNode json = serializer.convertValue(set, JsonNode.class);
    	String path = actionMap.get("UpdateDataset");  
    	return postJson(path, json.toString());
    }
    
    public String GetOrganizationId(String orgName)
    {
    	String subPath = actionMap.get("GetOrganizationId") + "?id=" + orgName;
    	HttpURLConnection conn = getHttpConnection(subPath, HttpMethod.Get, normalTimeout);
    	String json = handleHttpResponse(conn); 
    	DatahubResponse<JsonNode> returnObject = null;
		try {
			JavaType type = serializer.getTypeFactory().constructParametricType(DatahubResponse.class, JsonNode.class);
			returnObject = serializer.readValue(json, type);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		JsonNode org = returnObject.getResult().get("id");
		return org.toString().replace("\"", "");
    }
    
    public List<DatasetRelationship> GetRelationships(String nameId)
    {
    	String subPath = actionMap.get("GetRelationships") + "?id=" + nameId;
    	String json = handleHttpResponse(getHttpConnection(subPath, HttpMethod.Get, normalTimeout));
    	DatahubResponse<List<DatasetRelationship>> returnObject = null;
		try {
			CollectionType collType = serializer.getTypeFactory().constructCollectionType(List.class, DatasetRelationship.class);
			JavaType type = serializer.getTypeFactory().constructParametricType(DatahubResponse.class, collType);
			returnObject = serializer.readValue(json, type);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return returnObject.getResult();
    }
    
    public boolean CreateDatasetRelationship(DatasetRelationship orgRel)
    {
    	String json = getJsonTree(orgRel).toString();

    	String path = actionMap.get("CreateDatasetRelationship");    	
    	return postJson(path, json);
    }
    
    public boolean UpdateDatasetRelationship(DatasetRelationship orgRel, DatasetRelationship newRel)
    {
    	String dynamicJson = "";
		try {
			dynamicJson = getDynamicJsonObjDifference(orgRel, newRel);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
    	
    	String path = actionMap.get("UpdateDatasetRelationship");
    	return postJson(path, dynamicJson);
    }

	private <T> String getDynamicJsonObjDifference(T orgObj, T newObj) throws Exception 
	{
		if(orgObj == null || newObj == null)
			throw new Exception("one object is null");
		if(orgObj.getClass() != newObj.getClass())
			throw new Exception("objects are not of the same type");
		String dynamicJson = "{";
    	JsonNode jsonOrgRel = getJsonTree(orgObj);
    	JsonNode jsonNewRel = getJsonTree(newObj);
    	
    	Iterator<String> nodes = jsonOrgRel.getFieldNames();
    	
    	for(String st; nodes.hasNext();)
    	{
    		st = nodes.next();
    		if(!jsonOrgRel.get(st).equals(jsonNewRel.get(st))) //not!!
    		{
    			dynamicJson += jsonNewRel.get(st).toString() + ", ";
    		}
    	}
    	
    	dynamicJson = dynamicJson.substring(0, dynamicJson.length()-2) + "}";
    	return dynamicJson;
	}
	
	private boolean postJson(String path, String jsonData)
	{
		HttpURLConnection conn = this.getHttpConnection(path, HttpMethod.Post, normalTimeout);
		int status = 0;
		try {
			OutputStream os = conn.getOutputStream();
			byte[] output = jsonData.getBytes();
			os.write(output);
			os.close();
			status = conn.getResponseCode();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			conn.disconnect();
		}
		conn.disconnect();
		if(status == 200 || status == 201)
			return true;
		else
			return false;
	}
    
	private String handleHttpResponse(HttpURLConnection conn)
	{
		int status =0;
		try {
			status = conn.getResponseCode();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			return null;		}
		switch (status) {
		case 200:
		case 201:
			String json = null;
			try {
				json = ReadJsonResponse(conn);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        conn.disconnect();
			return json;
		default:
	        conn.disconnect();
			return null;
			//TODO do not return null!
		}
	}

	private String ReadJsonResponse(HttpURLConnection conn) throws IOException 
	{
		StringBuilder sb;
		BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		sb = new StringBuilder();
		String line;
		while ((line = br.readLine()) != null) 
		{
			line = line.replace("\\/Date(", "").replace(")\\/", ""); //handle Date(472382436328) to 472382436328
			sb.append(line + "\n");
		}
		br.close();
		return sb.toString();
	}
	
	private HttpURLConnection getHttpPostConnection(String path)
	{
		return getHttpConnection(path, HttpMethod.Post, normalTimeout);
	}
	
	private HttpURLConnection getHttpGetConnection(String path)
	{
		return getHttpConnection(path, HttpMethod.Get, normalTimeout);
	}
	
    private HttpURLConnection getHttpConnection(String path, HttpMethod requestMethod, int timeout)
    {
    	HttpURLConnection conn = null;
        try {
			URL uri = new URL(connectionUrl + path);
			conn = (HttpURLConnection) uri.openConnection();
			conn.setUseCaches(false);
			conn.setConnectTimeout(timeout);
			conn.setReadTimeout(timeout);
			conn.setRequestProperty("Content-Type", "application/json");			
			conn.setRequestProperty("Accept", "application/json");
			conn.setRequestProperty("Authorization", apiKey);
			conn.setRequestMethod(requestMethod.toString().toUpperCase());
			if(requestMethod == HttpMethod.Post)
			{
				conn.setDoInput (true);
				conn.setDoOutput (true);
			}
			conn.connect();
		} //TODO do not return null!
        catch (MalformedURLException e) {
			return null;
		}
		catch (IOException e) {
			return null;
		}
      //TODO do not return null!
	    return conn;
    }
    
    private JsonNode getJsonTree(Object obj)
    {
    	try {
    		String json = serializer.writeValueAsString(obj);
    		JsonNode node = serializer.readTree(json);
			return node;
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

	public static int getNormalTimeout() {
		return normalTimeout;
	}

	public static void setNormalTimeout(int normalTimeout) {
		CkanRestClient.normalTimeout = normalTimeout;
	}

	public String getApiKey() {
		return apiKey;
	}

	public void setApiKey(String apiKey) {
		this.apiKey = apiKey;
	}
}
