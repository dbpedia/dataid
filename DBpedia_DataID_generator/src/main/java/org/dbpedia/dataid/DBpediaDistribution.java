package org.dbpedia.dataid;

public class DBpediaDistribution {
	
	private String turtleString = "";
	private String name = "";
	private String filename = "";
	private String label = "";
	private String uri = "";
	private String modified = "";
	
	public DBpediaDistribution() {
		
	}
	
	public String getTurtleString() {
		return turtleString;
	}
	public void setTurtleString(String turtleString) {
		this.turtleString = turtleString;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public String getUri() {
		return uri;
	}
	public void setUri(String uri) {
		this.uri = uri;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public String getModified() {
		return modified;
	}

	public void setModified(String modified) {
		this.modified = modified;
	}
	
	
}
