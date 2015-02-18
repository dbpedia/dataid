package aksw.dataid.datahub.restclient;

import aksw.dataid.datahub.jsonobjects.DatahubError;

public class DatahubException extends Exception
{
    public DatahubException(String msg)
    {
        super(msg);
    }
	public DatahubException(DatahubError error)
	{
		super(error.getMessage());
	}
}
