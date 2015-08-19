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
import org.openrdf.rio.RDFParser;
import org.openrdf.rio.jsonld.JSONLDParser;
import org.openrdf.rio.nquads.NQuadsParser;
import org.openrdf.rio.rdfxml.RDFXMLParser;
import org.openrdf.rio.turtle.TurtleParser;
import org.semarglproject.rdf.NTriplesParser;

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
        return castJsonToObject(jsonString, clas, "");
    }
    public static <T> T castJsonToObject(String json, Class<T> clas, String subNode)
    {
        try {
            JsonNode node = mapper.readValue(json, JsonNode.class);
            JavaType mapping = mapper.getTypeFactory().constructType(clas);
            JsonNode graph = node.get(subNode);
            T config = mapper.readValue(graph.toString(), mapping);
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
        if(StaticJsonHelper.isJsonLdValid(input))
            return SerialiazationFormatFactory.createJsonLD();
        else if(StaticJsonHelper.isTurtleValid(input))
            return SerialiazationFormatFactory.createTurtle();
        else if(StaticJsonHelper.isNquadValid(input))
            return SerialiazationFormatFactory.createNQuads();
        else if(input.replace(" ", "").contains("rdf:resource=\"" + DataIdConfig.getDataIdUri() + "\""))
            return SerialiazationFormatFactory.createRDFXMLOut();
        return new SerializationFormat("NONE", null, null, null);
    }

    public static RDFParser getRdfParser(String input) throws DataIdInputException {
        SerializationFormat sf = getSerialization(input);
        if(sf.getName() == "TURTLE")
            return new TurtleParser();
        else if(sf.getName() == "N-QUADS")
            return new NQuadsParser();
        else if(sf.getName() == "JSON-LD")
            return new JSONLDParser();
        else if(sf.getName() == "RDF/XML")
            return new RDFXMLParser();
        return null;
    }
}
