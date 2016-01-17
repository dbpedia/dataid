package org.aksw.dataid.jsonutils;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.jsonldjava.core.JsonLdError;
import com.github.jsonldjava.impl.NQuadRDFParser;
import com.github.jsonldjava.utils.JsonUtils;
import org.aksw.dataid.config.DataIdConfig;
import org.aksw.dataid.errors.DataIdInputException;
import org.aksw.rdfunit.io.format.SerialiazationFormatFactory;
import org.aksw.rdfunit.io.format.SerializationFormat;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFParseException;
import org.openrdf.rio.RDFParser;
import org.openrdf.rio.jsonld.JSONLDParser;
import org.openrdf.rio.nquads.NQuadsParser;
import org.openrdf.rio.rdfxml.RDFXMLParser;
import org.openrdf.rio.turtle.TurtleParser;
import com.fasterxml.jackson.core.type.TypeReference;

import java.io.*;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URL;

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
	
	public static boolean isJsonLdValid(String content) throws RDFParseException, IOException, RDFHandlerException {
            new JSONLDParser().parse(new StringReader(content), "");
        return true;
	}

    public static boolean isTurtleValid(String content) throws RDFParseException, IOException, RDFHandlerException {
        new TurtleParser().parse(new StringReader(content), "http://someuri.org");
        return true;
    }

    public static boolean isNquadValid(String content) throws RDFParseException, IOException, RDFHandlerException {
        new NQuadsParser().parse(new StringReader(content), "");
        return true;
    }

    public static boolean isRdfXmlValid(String content) throws RDFParseException, IOException, RDFHandlerException {
            new RDFXMLParser().parse(new StringReader(content), "");
        return true;
    }

	public static JsonNode getJsonContent(String path) {
		JsonFileManager mainFileManager = new JsonFileManager();
		try {
			mainFileManager.LoadJsonFile(path);
		} catch (FileNotFoundException e) {
			System.err.println("File '" + path + "' could not be found. Try editing the MainConfig.json file.");
		} catch (RDFHandlerException e) {
            e.printStackTrace();
        } catch (RDFParseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
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

    public static String getPrettyContent(Object obj)
    {
        try {
            String zw = JsonUtils.toPrettyString(obj);
            if(!zw.startsWith("{"))
                zw = "{\n" + zw + "\n}";
            return zw;
        } catch (IOException e) {
            e.printStackTrace();
            return e.getMessage();
        }
    }
	
	public static void writeJsonContent(String path, String content) throws IOException, RDFParseException, RDFHandlerException {
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

    @SuppressWarnings("unchecked")
    public static <E> E dynamicGet(Object object, String fieldName)
    {
        Class<?> clazz = object.getClass();
        while (clazz != null) {
            try {
                Field field = clazz.getDeclaredField(fieldName);
                field.setAccessible(true);
                return (E) field.get(object);
            } catch (NoSuchFieldException e) {
                clazz = clazz.getSuperclass();
            } catch (Exception e) {
                throw new IllegalStateException(e);
            }
        }
        return null;
    }

/*    public static void writeToFile(String content, String filePath) {
        try {

            File file = new File(filePath);
            if (!file.exists()) {
                file.createNewFile();
            }

            FileWriter fw = new FileWriter(file.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(content);
            bw.close();

            System.out.println("Done");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String readFile(String filePath) {

        File file = new File(filePath);
        StringBuilder sb = new StringBuilder();
        try (FileInputStream fis = new FileInputStream(file)) {

            BufferedReader br = new BufferedReader(new InputStreamReader(fis));
            String line;
            while ((line = br.readLine()) != null)
            {
                sb.append(line + "\n");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }*/

    public static String readUrl(String url)
    {
        StringBuilder sb = new StringBuilder();
        InputStream in = null;
        try {
            URL u = new URL(url);
            in = u.openStream();
        } catch (MalformedURLException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        if (in != null)
        {
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String line;
            try {
                while ((line = br.readLine()) != null) {
                    sb.append(line + "\n");
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return sb.toString();
        }
        return null;
    }

    public static <T> T castJsonToObject(String jsonString, Class<T> clas)
    {
        return castJsonToObject(jsonString, clas, null);
    }
    public static <T> T castJsonToObject(JsonNode node, TypeReference<T> clas)
    {
        JavaType mapping = mapper.getTypeFactory().constructType(clas);
        return castJsonToObject(node, mapping, null);
    }
    public static <T> T castJsonToObject(String json, Class<T> clas, String subNode)
    {
        JsonNode node = null;
        try {
            node = mapper.readValue(json, JsonNode.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return castJsonToObject(node, clas, subNode);
    }
    public static <T> T castJsonToObject(JsonNode node, TypeReference<T> clas, String subNode)
    {
        JavaType mapping = mapper.getTypeFactory().constructType(clas);
        return castJsonToObject(node, mapping, subNode);
    }

    public static <T> T castJsonToObject(JsonNode node, Class<T> clas, String subNode)
    {
        JavaType mapping = mapper.getTypeFactory().constructType(clas);
        return castJsonToObject(node, mapping, subNode);
    }
    public static <T> T castJsonToObject(JsonNode node, JavaType clas, String subNode)
    {
        try {
            if(subNode != null)
                node = node.get(subNode);
            T config = mapper.readValue(node.traverse(), clas);
            return config;
        } catch (JsonParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (JsonMappingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    public static SerializationFormat getSerialization(String input) throws DataIdInputException {
        input = input.trim();
        StringBuilder errorBuilder = new StringBuilder();
        try {
            if(StaticJsonHelper.isJsonLdValid(input))
                return SerialiazationFormatFactory.createJsonLD();
        } catch (Exception e) {
            errorBuilder.append("JSONLD: " + e.getMessage() + "\n");
        }
        try {
            if(StaticJsonHelper.isTurtleValid(input))
                return SerialiazationFormatFactory.createTurtle();
        } catch (Exception e) {
            errorBuilder.append("TURTLE: " + e.getMessage() + "\n");
        }
        try {
            if(StaticJsonHelper.isNquadValid(input))
                return SerialiazationFormatFactory.createNQads();
        } catch (Exception e) {
            errorBuilder.append("NQUADS: " + e.getMessage() + "\n");
        }
        try {
            if(StaticJsonHelper.isRdfXmlValid(input))
                return SerialiazationFormatFactory.createRDFXMLOut();
        } catch (Exception e) {
            errorBuilder.append("RDFXML: " + e.getMessage() + "\n");
        }
        throw new DataIdInputException("Serialization format could not be determined. Please check the following parser errors:\n" + errorBuilder.toString());
    }
}
