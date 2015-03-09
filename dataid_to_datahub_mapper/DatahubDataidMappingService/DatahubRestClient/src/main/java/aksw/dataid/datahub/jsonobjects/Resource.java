package aksw.dataid.datahub.jsonobjects;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonIgnore;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Resource  implements DataHubListObject
{
    private String id;
    private String resource_group_id;
    private String name;
    private String description;
    private Date created;
    private String url;
    private Integer size;
    private Integer position;
    private Integer triples;
    private String resource_type;
    private Date last_modified;
    private String hash;
    private String format;
    private String mimetype;
    private String mimetype_inner;
    private String state;


	public Resource() 
	{
		size = 0;
		state = "active";
	}
	
	/**
	 * method necessary since a non-simple get method produces a NullPointerException in jackson.json.ObjectMapper!?
	 */
	public void PrepareForParsing()
	{
    	if (format != null) {
			//datahub displays different formats of a resource via the || operators
			if (format.startsWith("["))
				format = format.substring(1, format.length() - 2);
			format = format.replace(";", ",").replace(" ", "");
			format = format.replace(",", "||");
		}
    	if (mimetype != null) {
			//datahub displays different mimetypes of a resource via the || operators
			if (mimetype.startsWith("["))
				mimetype = mimetype.substring(1, mimetype.length() - 2);
			mimetype = mimetype.replace(";", ",").replace(" ", "");
			mimetype = mimetype.replace(",", "||");
		}
	}
	
    public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

    public String getResource_group_id() {
        return resource_group_id;
    }

    public void setResource_group_id(String resource_group_id) {
        this.resource_group_id = resource_group_id;
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

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Date getCreated() {
        return created;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public int getSize() {
        return size;
    }

    public int getPosition() {
        return position;
    }

    public void setResource_type(String resource_type) {
        this.resource_type = resource_type;
    }

    public String getResource_type() {
        return resource_type;
    }

    public void setLast_modified(Date last_modified) {
        this.last_modified = last_modified;
    }

    public Date getLast_modified() {
        return last_modified;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getHash() {
        return hash;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getFormat() {
        return format;
    }

    public void setMimetype(String mimetype) {
        this.mimetype = mimetype;
    }

    public String getMimetype() {
        return mimetype;
    }

    public void setMimetype_inner(String mimetype_inner) {
        this.mimetype_inner = mimetype_inner;
    }

    public String getMimetype_inner() {
        return mimetype_inner;
    }
    @JsonIgnore
	public Integer getTriples() {
		return triples;
	}

    @JsonIgnore
	public void setTriples(Integer triples) {
		this.triples = triples;
	}

	public void setSize(Integer size) {
		this.size = size;
	}

	public void setPosition(Integer position) {
		this.position = position;
	}
}
