package aksw.dataid.datahub.jsonobjects;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Dataset 
{
	   	private String id;
	    private String name;
	    private String title;
	    private String revision_id;
	    private String maintainer;
	    private String maintainer_email;
	    private String license_id;
	    private String license;
	    private String license_title;
	    private String license_url;
	    private Date metadata_created;
	    private Date metadata_modified;
	    private String author;
	    private String author_email;
	    private String download_url;
	    private String state;
	    private String version;
	    private String type;
	    private String notes;
	    private boolean isopen;
	    private List<Tag> tags;
	    private List<Resource> resources;
	    private String url;
	    private List<DatasetExtras> extras;
		private List<DatasetRelationship> relationships_as_object;
	    private List<DatasetRelationship> relationships_as_subject;
	    private boolean isPrivate;
	    private String owner_org;
	    private Integer num_resources;
	    private Integer num_tags;


		public Dataset(){
			//commons
			this.isopen = true;
			this.isPrivate = false;
			this.state = "active";
			
			extras = new ArrayList<DatasetExtras>();
			tags = new ArrayList<Tag>();
			resources = new ArrayList<Resource>();
			relationships_as_object = new ArrayList<DatasetRelationship>();
			relationships_as_subject = new ArrayList<DatasetRelationship>();
		}


	    public List<DatasetRelationship> getRelationships_as_object() {
			return relationships_as_object;
		}

		public void setRelationships_as_object(
				List<DatasetRelationship> relationships_as_object) {
			this.relationships_as_object = relationships_as_object;
		}

		public List<DatasetRelationship> getRelationships_as_subject() {
			return relationships_as_subject;
		}

		public void setRelationships_as_subject(
				List<DatasetRelationship> relationships_as_subject) {
			this.relationships_as_subject = relationships_as_subject;
		}

		public boolean isPrivate() {
			return isPrivate;
		}

		public void setPrivate(boolean isPrivate) {
			this.isPrivate = isPrivate;
		}

		public String getOwner_org() {
			return owner_org;
		}

		public void setOwner_org(String owner_org) {
			this.owner_org = owner_org;
		}

		public Integer getNum_resources() {
			return num_resources;
		}

		public void setNum_resources(Integer num_resources) {
			this.num_resources = num_resources;
		}

		public Integer getNum_tags() {
			return num_tags;
		}

		public void setNum_tags(Integer num_tags) {
			this.num_tags = num_tags;
		}
		
	    public List<DatasetExtras> getExtras() {
			return extras;
		}

		public void setExtras(List<DatasetExtras> extras) {
			this.extras = extras;
		}
		
	    public void setId(String id) {
	        this.id = id;
	    }
	    public String getId() {
	        return id;
	    }

	    public void setName(String name) {
	        this.name = name;
	    }

	    public String getName() {
	        return name;
	    }

	    public void setTitle(String title) {
	        this.title = title;
	    }

	    public String getTitle() {
	        return title;
	    }

	    public void setRevision_id(String revision_id) {
	        this.revision_id = revision_id;
	    }

	    public String getRevision_id() {
	        return revision_id;
	    }

	    public void setMaintainer(String maintainer) {
	        this.maintainer = maintainer;
	    }

	    public String getMaintainer() {
	        return maintainer;
	    }

	    public void setMaintainer_email(String maintainer_email) {
	        this.maintainer_email = maintainer_email;
	    }

	    public String getMaintainer_email() {
	        return maintainer_email;
	    }

	    public void setLicense_id(String license_id) {
	        this.license_id = license_id;
	    }

	    public String getLicense_id() {
	        return license_id;
	    }

	    public void setLicense(String license) {
	        this.license = license;
	    }

	    public String getLicense() {
	        return license;
	    }

	    public void setLicense_title(String license_title) {
	        this.license_title = license_title;
	    }

	    public String getLicense_title() {
	        return license_title;
	    }

	    public void setLicense_url(String license_url) {
	        this.license_url = license_url;
	    }

	    public String getLicense_url() {
	        return license_url;
	    }

	    public void setMetadata_created(Date metadata_created) {
	        this.metadata_created = metadata_created;
	    }

	    public Date getMetadata_created() {
	        return metadata_created;
	    }

	    public void setMetadata_modified(Date metadata_modified) {
	        this.metadata_modified = metadata_modified;
	    }

	    public Date getMetadata_modified() {
	        return metadata_modified;
	    }

	    public void setAuthor(String author) {
	        this.author = author;
	    }

	    public String getAuthor() {
	        return author;
	    }

	    public void setAuthor_email(String author_email) {
	        this.author_email = author_email;
	    }

	    public String getAuthor_email() {
	        return author_email;
	    }

	    public void setDownload_url(String download_url) {
	        this.download_url = download_url;
	    }

	    public String getDownload_url() {
	        return download_url;
	    }

	    public void setState(String state) {
	        this.state = state;
	    }

	    public String getState() {
	        return state;
	    }

	    public void setVersion(String version) {
	        this.version = version;
	    }

	    public String getVersion() {
	        return version;
	    }

	    public void setType(String type) {
	        this.type = type;
	    }

	    public String getType() {
	        return type;
	    }

	    public void setNotes(String notes) {
	        this.notes = notes;
	    }

	    public String getNotes() {
	        return notes;
	    }

	    public void setIsopen(boolean isopen) {
	        this.isopen = isopen;
	    }

	    public boolean isIsopen() {
	        return isopen;
	    }

	    public void setTags(List<Tag> tags) {
	        this.tags = tags;
	    }

	    public List<Tag> getTags() {
	        return tags;
	    }

	    public void setUrl(String url) {
	        this.url = url;
	    }

	    public String getUrl() {
	        return url;
	    }

	    public List<Resource> getResources() {
	        return resources;
	    }

	    public void setResources( List<Resource> resources ) {
	        this.resources = resources;
	    }

	    public String toString() {
	        return "<Dataset:" + this.getName() + " ," + this.getTitle() + "," + this.getAuthor() + ", " + this.getUrl() + ">";
	    }
}
