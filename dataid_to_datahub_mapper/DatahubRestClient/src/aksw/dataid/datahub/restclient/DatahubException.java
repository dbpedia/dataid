package aksw.dataid.datahub.restclient;

import aksw.dataid.datahub.jsonobjects.DatahubError;

public class DatahubException extends Exception
{
	public DatahubException(DatahubError error)
	{
		super(error.getMessage());
	}
}
