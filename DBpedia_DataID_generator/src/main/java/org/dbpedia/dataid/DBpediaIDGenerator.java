package org.dbpedia.dataid;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class DBpediaIDGenerator {

	private String outputfolder = "";
	private String downloadURI = "";
	private String provenance = "";
	private Map<String, String> languages = new HashMap<String, String>();
	private List<String> versionList = new ArrayList<String>();
	
	public DBpediaIDGenerator(String folder, String uri, String provenanceFile, String languageFile, String versions) throws IOException {
		this.outputfolder = folder;
		this.downloadURI = uri;
		this.provenance = readFileContent(provenanceFile);
		this.languages = readMap(languageFile);
		this.versionList = readList(versions);
	}
	
	private void writeDataID(DBpediaVersion version, File outputfolder) {
		File dataId = new File(outputfolder.getAbsolutePath()+"/dataid.ttl");
		String dataIdUri = this.downloadURI+version.getVersion()+"/"+version.getLanguage()+"/dataid.ttl";
		String datasetUri = dataIdUri + "#dataset";
		
		//use full language name if in language file, otherwise use language code
		String language = this.languages.containsKey(version.getLanguage()) ? this.languages.get(version.getLanguage()) : version.getLanguage();
		
		String datasetDescTurtle = "<"+dataIdUri+">\n" +
				" a void:DatasetDescription;\n" +
				" foaf:primaryTopic <"+ datasetUri +"> .\n\n";
		printTurtle(datasetDescTurtle,dataId);

		String datasetTurtle = "<"+datasetUri+">\n" +
				" a dataid:Dataset ;\n" +
				" dc:title \"DBpedia "+version.getVersion()+" "+language+" dumps dataset\"^^xsd:string ;\n" +
				" rdfs:label \"DBpedia_"+version.getVersion()+"_"+version.getLanguage()+"\"^^xsd:string ;\n" +
				" dcat:landingPage <http://dbpedia.org/> ;\n" +
				" dataid:associatedAgent <http://wiki.dbpedia.org/Association> ;\n" +
				" dc:license <http://purl.oclc.org/NET/rdflicense/cc-by-sa3.0de> ;\n" +
				" dc:modified \""+new SimpleDateFormat("MM-dd-yyyy").format(version.getModified())+"\"^^xsd:date ;\n" +
				" dc:description \"\"\"DBpedia is a crowd-sourced community effort to extract structured information from Wikipedia " +
				"and make this information available on the Web. DBpedia allows you to ask sophisticated queries against Wikipedia, " +
				"and to link the different data sets on the Web to Wikipedia data. We hope that this work will make it easier " +
				"for the huge amount of information in Wikipedia to be used in some new interesting ways. " +
				"Furthermore, it might inspire new mechanisms for navigating, linking, and improving the encyclopedia itself.\"\"\"^^xsd:string ;\n" +
				" void:subset "+generateSubsetUris(version, dataIdUri)+" .\n\n";
		printTurtle(datasetTurtle,dataId);		
		printTurtle(generateSubsetDesc(version, dataIdUri),dataId);
		printTurtle(provenance,dataId);
	}
	
	private String generateSubsetUris(DBpediaVersion version, String dataIdUri) {
		String turtle = "";
		Set<String> dists = new HashSet<String>();
		
		for(DBpediaDistribution dist : version.getDistributions()) {
			String distributionDatasetUri = dataIdUri+"#"+dist.getLabel();
			if(!dists.contains(distributionDatasetUri)) {
				turtle += " <"+distributionDatasetUri +">,";
				dists.add(distributionDatasetUri);
			}
		}
		return turtle.substring(0,turtle.length()-1);
	}
	
	private String generateSubsetDesc(DBpediaVersion version, String dataIdUri) {
		String turtle = "";
		Set<String> dists = new HashSet<String>();
		
		for(DBpediaDistribution dist : version.getDistributions()) {
			String distributionDatasetUri = dataIdUri+"#"+dist.getLabel();
			if(!dists.contains(distributionDatasetUri)) {
				turtle += "<"+distributionDatasetUri +">\n" +
						" a dataid:Dataset ;\n" +
						" dc:title \"DBpedia "+version.getVersion()+ " " +dist.getName()+" dump dataset\"^^xsd:string ;\n" +
						" rdfs:label \""+dist.getLabel()+"_"+version.getVersion()+"\"^^xsd:string ;\n" +
						" dcat:landingPage <http://dbpedia.org/> ;\n" +
						" dataid:associatedAgent <http://wiki.dbpedia.org/Association> ;\n" +
						" dc:license <http://purl.org/NET/rdflicense/cc-by-sa3.0de> ;\n" +
						" dc:modified \""+dist.getModified()+"\"^^xsd:date ;\n" +
						" dcat:distribution";
				//add all distributions of dataset
				for(DBpediaDistribution compdist : version.getDistributions()) {
					if(compdist.getFilename().startsWith(dist.getLabel())) {
						turtle += " <"+compdist.getUri()+">,";
					}
				}
				//cut off trailing comma, add .
				turtle = turtle.substring(0,turtle.length()-1)+" .\n\n";
				
				dists.add(distributionDatasetUri);
			}
			turtle += dist.getTurtleString();
		}
		return turtle;
	}
	
	private void printTurtle(String turtle, File idFile) {
		BufferedWriter writer;
		try {
			if(!idFile.exists()) {
				writer = new BufferedWriter(new FileWriter(idFile));
				String prefix = "@prefix dc: <http://purl.org/dc/terms/> .\n" +
						"@prefix dcat: <http://www.w3.org/ns/dcat#> .\n" +
						"@prefix void: <http://rdfs.org/ns/void#> .\n" +
						"@prefix dataid: <http://schema.dbpedia.org/dataid#> .\n" +
						"@prefix prov: <http://www.w3.org/ns/prov#> .\n" +
						"@prefix xsd: <http://www.w3.org/2001/XMLSchema#> .\n" +
						"@prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> .\n" +
						"@prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#> .\n" +
						"@prefix foaf: <http://xmlns.com/foaf/0.1/> .\n" +
						"@prefix owl: <http://www.w3.org/2002/07/owl#> .\n\n";
				
				writer.append(prefix);
				writer.flush();
				writer.close();
			}
			writer = new BufferedWriter(new FileWriter(idFile, true));
			writer.append(turtle);
			writer.flush();
		} catch(IOException ioe) {
			System.out.println("Output file could not be written");
			return;
		}
	}
	
	private String readFileContent(String filename) {
		String fileContent = "";
		try {
			FileInputStream fileIn =  new FileInputStream(filename);
			String line = "";
			BufferedReader reader = new BufferedReader(new InputStreamReader(fileIn));
			while ((line = reader.readLine()) != null) {  
				fileContent+=line+"\n";
			}
			reader.close();
		} catch (FileNotFoundException fnf) {
			System.err.println("File not found "+filename);
		} catch (IOException ioe) {
			System.err.println("Could not read file "+filename);
		}
		return fileContent;
	}
	
	public List<DBpediaVersion> createDataID() {
		List<DBpediaVersion> versions = new ArrayList<DBpediaVersion>();
		
	//for all versions
		for(String versionName : this.versionList) {
			try {
				Document doc = Jsoup.connect(downloadURI+versionName)
						.userAgent("DataID generator crawler").timeout(60000).get();

				Elements versionLinks = doc.select("a[href]");
				Iterator<Element> it = versionLinks.iterator();
					
				//for all links found in version folder, except 1 and 2, those stink anyway
				if(!versionName.equals("1.0")&&!versionName.equals("2.0")) {
	//single version can be switched here	
//				if(versionName.equals("2014")) {
					versions.addAll(crawlAndWriteLanguages(it,versionName));
				} 
			} catch(IOException ioe) {
				System.err.println("Could not connect to "+downloadURI+versionName);
			}	
		}
		return versions;
	}
	
	private List<DBpediaVersion> crawlAndWriteLanguages(Iterator<Element> languageLinks, String versionName) throws IOException {
		
		List<DBpediaVersion> languageVersions = new ArrayList<DBpediaVersion>();
		while(languageLinks.hasNext()) {
			
			Element vLink = languageLinks.next();
			if(!vLink.attr("href").equals("../")) {
			//single language can be switched here
//			if(vLink.attr("href").equals("de/")) {
				File languageFolder = new File(outputfolder+"/"+versionName+"/"+vLink.attr("href").replace("/", ""));
				//don't recrawl crawled languages
				if(!languageFolder.exists()) {
					languageFolder.mkdirs();
				} else {
					continue;
				}
				DBpediaVersion version = this.crawlVersion(downloadURI+versionName+"/"+vLink.attr("href"), vLink, versionName);
				if(version!=null) {
					languageVersions.add(version);
					writeDataID(version,languageFolder);
				}
			} 
		}
		return languageVersions;
	}
	
	
	
	private DBpediaVersion crawlVersion(String uri, Element languageEl, String versionNumber) throws IOException {
		DBpediaVersion version = new DBpediaVersion();
		version.setLanguage(languageEl.attr("href").replace("/", ""));

		System.out.println("crawling "+versionNumber+" "+version.getLanguage());
		version.setVersion(versionNumber);
		
		if(version.getLanguage().contains(".")) {
			System.out.println("version contains dot: "+uri);
			return null;
		}
		Document doc = Jsoup.connect(uri)
				.userAgent("DataID generator crawler").timeout(60000).get();
		Elements links = doc.select("a[href]");
		Iterator<Element> it = links.iterator();
		List<DBpediaDistribution> distributions = new ArrayList<DBpediaDistribution>();

		while(it.hasNext()) {
			Element link = it.next();
			if(!link.attr("href").equals("../")) {
//				System.out.println("text: "+link.attr("href"));
				String fileUri = uri+link.attr("href");
				
				DBpediaDistribution dist = null;
				dist = parseDistribution(fileUri, link.attr("href"), version);		
//				
				if(dist != null)
					distributions.add(dist);
			}
		}
		
		if(distributions.size()==0)
			return null;
		
		version.setDistributions(distributions);
//		System.out.println("returning "+version.getDistributions().size()+" distributions for "+version.getLanguage());
		return version;
	}
	
	private DBpediaDistribution parseDistribution(String uri, String filename, DBpediaVersion version) throws IOException {
		
		String lang = version.getLanguage();
		DBpediaDistribution dist = new DBpediaDistribution();	
		URL url = new URL(uri);
		URLConnection connection = url.openConnection();
		
		Date modifiedDate = new Date(connection.getLastModified());
		if(version.getModified()==null) {
			version.setModified(modifiedDate);
		} else if(modifiedDate.after(version.getModified())) {
			version.setModified(modifiedDate);
		} 
		
		String dateString = new SimpleDateFormat("MM-dd-yyyy").format(modifiedDate);
		String size = connection.getHeaderField("Content-Length");
		String mimetype = connection.getHeaderField("Content-Type");
		
		if(!filename.contains(".")) {
			System.out.println("No file: "+filename+" "+lang+" "+uri);
			return null;
		}
		dist.setUri(uri);
		dist.setName(filename.substring(0,filename.indexOf(".")).replace("_", " "));
		dist.setFilename(filename);
		dist.setLabel(filename.substring(0,filename.indexOf(".")));
		dist.setModified(dateString);
		String turtle = "<"+uri+">\n" +
		"  a dcat:Distribution ;\n" +
    "  dc:title \""+filename.substring(0,filename.indexOf(".")).replace("_", " ")+"\" ;\n" + 
    "  dc:description \"DBpedia dumpfile: "+filename+"\" ;\n" +
    "  dc:modified \""+dateString+"\"^^xsd:date ;\n" +
		"  dcat:downloadURL <"+uri+"> ;\n";
		
		Integer byteSize = size != null ? Integer.parseInt(size) : 0 ;
		if(size==null) {
			System.out.println(uri+" does not respond");
		}
		turtle += "  dcat:byteSize \""+byteSize.intValue()+"\"^^xsd:decimal ;\n";
		String formatString = filename.substring(filename.indexOf(".")+1);
		if(formatString.contains(".")) {
			
			turtle += "  dcat:mediaType \""+mimetype+"\" ;\n"; 
			turtle += "  dc:format \""+formatString.substring(0,formatString.lastIndexOf("."))+"\" .\n\n";
		} else {
			turtle += "  dcat:mediaType \""+mimetype+"\" .\n\n"; 
		}
		dist.setTurtleString(turtle);
		return dist;
	}
	
	private Map<String, String> readMap(String filename) throws FileNotFoundException,UnsupportedCharsetException,IOException{
		
		Map<String, String> csvmap=new HashMap<String, String>();
		InputStream input=new FileInputStream(filename);
		Charset charset=Charset.forName("UTF-8");
		CsvReader reader=new CsvReader(input,"\t".charAt(0),charset);
		while(reader.readRecord()) {
			String[] s = reader.getValues();
			csvmap.put(s[0],s[1]);
		}
		return csvmap;
	}
	
	private List<String> readList(String filename) throws FileNotFoundException,UnsupportedCharsetException,IOException{
		
		List<String> csvlist = new ArrayList<String>();
		InputStream input=new FileInputStream(filename);
		Charset charset=Charset.forName("UTF-8");
		CsvReader reader=new CsvReader(input,"\t".charAt(0),charset);
		while(reader.readRecord()) {
			String[] s = reader.getValues();
			csvlist.add(s[0]);
		}
		return csvlist;
	}
	
	public static void main(String[] args) throws IOException {
		DBpediaIDGenerator generator = new DBpediaIDGenerator("out", 
				"http://downloads.dbpedia.org/",
				"src/main/resources/provenance", 
				"src/main/resources/languages",
				"src/main/resources/versions");
		generator.createDataID();
	}
}
