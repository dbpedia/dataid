package org.aksw.dataid.datahub.jsonobjects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.aksw.dataid.errors.DataIdServiceException;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DatahubError extends DataIdServiceException implements ValidCkanResponse
{
	private String message;
	private String __type;

    public DatahubError() {
        super();
    }

    public DatahubError(String message) {
        super();
        this.message = message;
    }

    public DatahubError(String message, Throwable cause) {
        super(message, cause);
        this.message = message;
        this.__type = cause.getClass().getName();
    }

    public DatahubError(Throwable cause) {
        super(cause);
        this.__type = cause.getClass().getName();
        this.message = cause.getMessage();
    }

    @Override
    public String getMessage() {return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
    public void setName(String[] message) {
        this.message = message[0];
    }
    @JsonProperty("__type")
	public String getType() {
		return __type;
	}
    @JsonProperty("__type")
	public void setType(String type) {
		this.__type = type;
	}
}