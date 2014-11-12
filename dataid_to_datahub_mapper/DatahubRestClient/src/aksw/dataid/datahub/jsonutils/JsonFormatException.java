package aksw.dataid.datahub.jsonutils;

import java.io.IOException;

public class JsonFormatException extends IOException
{
	  public JsonFormatException() { super(); }
	  public JsonFormatException(String message) { super(message); }
	  public JsonFormatException(String message, Throwable cause) { super(message, cause); }
	  public JsonFormatException(Throwable cause) { super(cause); }
}