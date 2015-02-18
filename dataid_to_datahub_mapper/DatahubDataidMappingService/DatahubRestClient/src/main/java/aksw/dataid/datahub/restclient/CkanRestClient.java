package aksw.dataid.datahub.restclient;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.databind.*;

import aksw.dataid.datahub.jsonobjects.*;


public class CkanRestClient 
{
	private String connectionUrl;
    private final ObjectMapper serializer = new ObjectMapper();
    private final ObjectMapper deserializer = new ObjectMapper();
    private static int normalTimeout = 30000;
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
        serializer.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        serializer.setDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS"));
    }
    
    public Dataset GetDataset(String nameId) throws IOException, DatahubException
    {
    	String subPath = actionMap.get("GetDataset") + "?id=" + nameId;
    	HttpURLConnection conn = getHttpConnection(subPath, HttpMethod.Get, normalTimeout);
    	String json = handleHttpResponse(conn);
    	if (json != null) {
			DatahubResponse<Dataset> returnObject = null;
			JavaType type = deserializer.getTypeFactory().constructParametricType(DatahubResponse.class, Dataset.class);
			returnObject = getDatahubResponse(json, type, Dataset.class);
			return returnObject.getResult();
		}
    	return null;
    }

	@SuppressWarnings("unchecked")
	private <T> DatahubResponse<T> getDatahubResponse(String json, JavaType type, Class<T> exp) throws IOException, DatahubException 
	{
		DatahubResponse<T> response;
		response = (DatahubResponse<T>) deserializer.readValue(json, type);
		if(response.getError() != null)
			throw new DatahubException(response.getError());
		return response;
	}
    
    public ValidCkanResponse CreateDataset(Dataset set) throws IOException, DatahubException {
    	set.setName(set.getName().replace(" ", ""));
    	set.PrepareForParsing();
    	JsonNode json = serializer.convertValue(set, JsonNode.class);
    	String path = actionMap.get("CreateDataset");  
    	return postJson(path, json.toString());
    }
    
    public ValidCkanResponse UpdateDataset(Dataset set) throws IOException, DatahubException {
    	set.setName(set.getName().replace(" ", ""));
    	set.PrepareForParsing();
    	JsonNode json = serializer.convertValue(set, JsonNode.class);
    	String path = actionMap.get("UpdateDataset");  

    	return postJson(path, json.toString());
    }
    
    public String GetOrganizationId(String orgName) throws IOException, DatahubException
    {
    	String subPath = actionMap.get("GetOrganizationId") + "?id=" + orgName;
    	HttpURLConnection conn = getHttpConnection(subPath, HttpMethod.Get, normalTimeout);
    	String json = handleHttpResponse(conn); 
    	DatahubResponse<JsonNode> returnObject = null;
		JavaType type = serializer.getTypeFactory().constructParametricType(DatahubResponse.class, JsonNode.class);
		returnObject = getDatahubResponse(json, type, JsonNode.class);

		JsonNode org = returnObject.getResult().get("id");
		return org.toString().replace("\"", "");
    }
    
    @SuppressWarnings("unchecked")
	public List<DatasetRelationship> GetRelationships(String nameId) throws IOException, DatahubException
    {
    	String subPath = actionMap.get("GetRelationships") + "?id=" + nameId;
    	String json = handleHttpResponse(getHttpConnection(subPath, HttpMethod.Get, normalTimeout));
    	DatahubResponse<Object> returnObject = null;
		CollectionType collType = serializer.getTypeFactory().constructCollectionType(List.class, DatasetRelationship.class);
		JavaType type = serializer.getTypeFactory().constructParametricType(DatahubResponse.class, collType);
		returnObject = getDatahubResponse(json, type, Object.class);

    	return (List<DatasetRelationship>)returnObject.getResult();
    }
    
//    public boolean CreateDatasetRelationship(DatasetRelationship orgRel) throws HttpResponseException, IOException
//    {
//    	String json = getJsonTree(orgRel).toString();
//
//    	String path = actionMap.get("CreateDatasetRelationship");    	
//    	return postJson(path, json);
//    }
//    
//    public boolean UpdateDatasetRelationship(DatasetRelationship orgRel, DatasetRelationship newRel) throws HttpResponseException, IOException
//    {
//    	String dynamicJson = "";
//		try {
//			dynamicJson = getDynamicJsonObjDifference(orgRel, newRel);
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//			return false;
//		}
//    	
//    	String path = actionMap.get("UpdateDatasetRelationship");
//    	return postJson(path, dynamicJson);
//    }
	
	private ValidCkanResponse postJson(String path, String jsonData) throws IOException, DatahubException {
		HttpURLConnection conn = this.getHttpConnection(path, HttpMethod.Post, normalTimeout);
		OutputStream os = conn.getOutputStream();
		byte[] output = jsonData.getBytes();
		os.write(output);
		os.close();
		DatahubResponse<Dataset> returnObject = null;
		JavaType type = deserializer.getTypeFactory().constructParametricType(DatahubResponse.class, Dataset.class);

        String res = handleHttpResponse(conn);
        returnObject = getDatahubResponse(res, type, Dataset.class);

		if(returnObject.getResult() != null)
			return returnObject.getResult();
		else
			return returnObject.getError();
	}
    
	private String handleHttpResponse(HttpURLConnection conn) throws DatahubException {
		int status =0;
		try {
			status = conn.getResponseCode();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
        }
		String json = null;

			try {
                if (status < 400)
				    json = ReadResponseStream(conn.getInputStream());
                else
                    throw new DatahubException(ReadResponseStream(conn.getErrorStream()));
			} catch (IOException e) {
				e.printStackTrace();
                return null;
			}

		conn.disconnect();
		return json;
	}

	private String ReadResponseStream(InputStream stream) throws IOException
	{
		StringBuilder sb;
		BufferedReader br = new BufferedReader(new InputStreamReader(stream));
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
	
	private HttpURLConnection getHttpPostConnection(String path) throws IOException
	{
		return getHttpConnection(path, HttpMethod.Post, normalTimeout);
	}
	
	private HttpURLConnection getHttpGetConnection(String path) throws IOException
	{
		return getHttpConnection(path, HttpMethod.Get, normalTimeout);
	}
	
    private HttpURLConnection getHttpConnection(String path, HttpMethod requestMethod, int timeout) throws IOException
    {
    	HttpURLConnection conn = null;
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
	    return conn;
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
