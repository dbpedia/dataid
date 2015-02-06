package aksw.dataid.datahub.jsonobjects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DatahubResponse <T>
{
    public boolean success;
    public T result;	
    public DatahubError error;
    
	public boolean isSuccess() {
		return success;
	}
	public void setSuccess(boolean success) {
		this.success = success;
	}
	public T getResult() {
		return result;
	}
	public void setResult(T result) {
		this.result = result;
	}
	public DatahubError getError() {
		return error;
	}
	public void setError(DatahubError error) {
		this.error = error;
	}
}
