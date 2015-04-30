package org.dbpedia.dataid;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DBpediaVersion {
	
	private String language;
	private String version;
	private Date modified;
	private List<DBpediaDistribution> distributions;
	
	public DBpediaVersion() {
		distributions = new ArrayList<DBpediaDistribution>();
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public List<DBpediaDistribution> getDistributions() {
		return distributions;
	}

	public void setDistributions(List<DBpediaDistribution> distributions) {
		this.distributions = distributions;
	}
	
	public void addDistribution(DBpediaDistribution distribution) {
		this.distributions.add(distribution);
	}

	public Date getModified() {
		return modified;
	}

	public void setModified(Date modified) {
		this.modified = modified;
	}
	
}
