package aksw.dataid.solr;

/**
 * Basic Exception for SOLR DataId related issues
 * 
 * @author mullekay
 *
 */
public class DataIdSolrException extends Exception {

	/**
	 * generated serial ID
	 */
	private static final long serialVersionUID = 1448001951030948224L;
	
	public DataIdSolrException(final String message) {
		super(message);
	}
	
	public DataIdSolrException(final String message, final Throwable cause) {
		super(message, cause);
	}
}
