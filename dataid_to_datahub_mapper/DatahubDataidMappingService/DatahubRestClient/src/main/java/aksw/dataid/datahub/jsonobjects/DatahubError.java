package aksw.dataid.datahub.jsonobjects;

public class DatahubError extends Exception implements ValidCkanResponse
{
	private String message;
	private String type;

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
        this.type = cause.getClass().getName();
    }

    public DatahubError(Throwable cause) {
        super(cause);
        this.type = cause.getClass().getName();
        this.message = cause.getMessage();
    }

    protected DatahubError(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.message = message;
        this.type = cause.getClass().getName();
    }

    public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
}