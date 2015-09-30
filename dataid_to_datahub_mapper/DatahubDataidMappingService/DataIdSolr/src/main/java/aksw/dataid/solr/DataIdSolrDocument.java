package aksw.dataid.solr;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.log4j.Logger;
import org.apache.solr.common.SolrInputDocument;

/**
 * This class can be used to create a JSON string which can
 * be stored in SOLR
 * 
 * @author mullekay
 *
 */
public class DataIdSolrDocument {
	
	/** logger class */
	Logger logger = Logger.getLogger(DataIdSolrDocument.class);
	
	/** buffer which stores JSON string */
	protected StringBuffer buffer = new StringBuffer();
	
	/** children of this instance */
	protected List<DataIdSolrDocument> children = new ArrayList<>();
	
	/** specifies the date output format which is expected by SOLR */
	protected final static SimpleDateFormat solrDateFormat =
			new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
	
	/** specifies whether the final json string has been build already */
	protected boolean finishedString = false;
	
	/** specifies whether a previous entry already exists */
	protected boolean hasPreviousEntry = false;
	
	/** id which is represented by this document */
	protected final String id;
	
	/** known SOLR field names */
	protected final Collection<String> knownSolrFieldNames;
	
	/** map of field name and field values */
	protected Map<String, List<String>> inputMap = new LinkedHashMap<>();
	
	/** actual document which will be send to SOLR */
	protected SolrInputDocument solrDocument = new SolrInputDocument();
	
	/**
	 * Constructor
	 * 
	 * @param fieldName 			- field name of ID for this instance/document
	 * @param id					- actual ID for this instance/document
	 * @param knownSolrFieldNames	- known SOLR field names
	 * @param namespacePrefixes		- namespace prefixes of document
	 * @throws DataIdSolrException 
	 */
	public DataIdSolrDocument(final String fieldName, final String id,
							  final Collection<String> knownSolrFieldNames,
							  final Collection<String> namespacePrefixes)
							throws DataIdSolrException {
		this.id = id;
		this.knownSolrFieldNames = knownSolrFieldNames;
		
		// save in document
		this.addFieldData(fieldName, id);
		
		if (null != namespacePrefixes) {
			for (String namespacePrefix : namespacePrefixes) {
				this.addFieldData("namespacePrefixes", namespacePrefix);
			}
		}
	}
	
	
	/**
	 * Constructor
	 * 
	 * @param fieldName - field name of ID for this instance/document
	 * @param id		- actual ID for this instance/document
	 * @throws DataIdSolrException 
	 */
	public DataIdSolrDocument(final String fieldName, final String id,
							  final Collection<String> namespacePrefixes)
							throws DataIdSolrException {
		this(fieldName, id, (Collection<String>) null, namespacePrefixes);
	}
	
	/**
	 * 
	 * @return ID of this instance/document
	 */
	public String getId() {
		return this.id;
	}
	
	/**
	 * This method can be used to add data for each field
	 * 
	 * @param fieldName	- field name
	 * @param value		- value of the field
	 * @throws DataIdSolrException 
	 */
	public void addFieldData(final String fieldName, final String value) throws DataIdSolrException {
		if (null == fieldName || null == value) {
			return;
		}
		
		// check whether the field name is known
		if (null != knownSolrFieldNames &&
			false == knownSolrFieldNames.contains(fieldName)) {
			
			// return if this field name is not known
			if (logger.isDebugEnabled()) {
				logger.debug("Got unknown field name: " + fieldName);
			}			
			return;
		}
		
		List<String> values = this.inputMap.get(fieldName);
		if (null == values) {
			values = new ArrayList<>();			
			this.inputMap.put(fieldName, values);
		}
		
		// only add, if it is already known
		if (false == values.contains(value)) {
			values.add(value);
			this.solrDocument.addField(fieldName, this.cleanValue(value));
		}
		
		if (this.finishedString) {
			this.hasPreviousEntry = false;
			this.finishedString = false;
			this.buffer.delete(0, buffer.length());
		}
	}
	
	/**
	 * This method allows the addition of child documents
	 * 
	 * @param children	- list of children
	 */
	public void addChildDocuments(List<DataIdSolrDocument> children) {
		if (null == children || children.isEmpty()) {
			return;
		}
		
		for (DataIdSolrDocument child : children) {
			if (this.children.contains(child)) {
				continue;
			}
			
			// add new child
			this.children.add(child);
			this.solrDocument.addChildDocument(child.getSolrDoc());
		}
		
		if (this.finishedString) {
			this.hasPreviousEntry = false;
			this.finishedString = false;
			this.buffer.delete(0, buffer.length());
		}
	}
	
	/**
	 * This method can be used to convert the XML dates into SOLR dates
	 * 
	 * @param xmlDateStr	- XML date string	
	 * @return SOLR compatible date string
	 * @throws DataIdSolrException
	 */
	protected String convertXml2SolrDate(final String xmlDateStr) throws DataIdSolrException {
		if (null == xmlDateStr) {
			return null;
		}
		
		String outputString = null;		
		try {
			DatatypeFactory factory = DatatypeFactory.newInstance();
	
			// Parses a string in ISO 8601 format to an XMLGregorianCalendar
			XMLGregorianCalendar xmlCal = factory.newXMLGregorianCalendar(xmlDateStr);
			outputString = solrDateFormat.format(xmlCal.toGregorianCalendar().getTime());
		} catch (Exception e) {
			throw new DataIdSolrException("Problems when converting date", e);
		}
		
		if (null == outputString) {
			throw new DataIdSolrException("Invalid date: " + xmlDateStr);
		} else {
			return outputString;
		}
	}
	
	/**
	 * This method can be used to clean the input value string
	 * from date/integer/etc. additions.
	 * 
	 * @param value	- original field value
	 * @return cleaned field value
	 * @throws DataIdSolrException 
	 */
	protected String cleanValue(final String value) throws DataIdSolrException {
		if (null == value) {
			return null;
		}
		
		final String cleanedValue;
		// handle dates
		int index = -1;
		if ((index = value.indexOf("^^http://www.w3.org/2001/XMLSchema#date")) >= 0) {
			cleanedValue = this.convertXml2SolrDate(value.substring(0, index));
		} else {
			// deal with everything else
			index = value.indexOf("^^");
			if (0 <= index) {
				cleanedValue = value.trim().substring(0, index);
			} else {
				cleanedValue = value;
			}
		}
		
		return cleanedValue;
	}
	
	/**
	 * Here field data can be added for fields which only have one value
	 * 
	 * @param fieldName	- field name
	 * @param value		- value of the field
	 * @throws DataIdSolrException 
	 */
	protected void addSolrJsonFieldData(final String fieldName, final String value) throws DataIdSolrException {
		if (null == fieldName || null == value) {
			return;
		}
		
		if (this.hasPreviousEntry) {
			this.hasPreviousEntry = true;
			this.buffer.append(", ");
		}
		
		this.buffer.append("\"");
		this.buffer.append(fieldName);
		this.buffer.append("\":\"");		
		
		String cleanedValue = cleanValue(value);
		this.buffer.append(cleanedValue);		
		this.buffer.append("\"");	
		
		this.hasPreviousEntry = true;
	}
	
	/**
	 * This method can be used to ad multiple values to a field
	 * 
	 * @param fieldName		- field names
	 * @param values		- list of field values for the field
	 * @throws DataIdSolrException 
	 */
	protected void addSolrJsonMultiValueFieldData(final String fieldName,
											    final List<String> values) throws DataIdSolrException {
		if (null == fieldName || null == values || 0 == values.size()) {
			return;
		}
		
		if (this.hasPreviousEntry) {
			this.hasPreviousEntry = true;
			this.buffer.append(", ");
		}
		
		this.buffer.append("\"");
		this.buffer.append(fieldName);
		this.buffer.append("\":[");
		
		this.buffer.append("\"");
		
		
		String cleanedValue = cleanValue(values.get(0));
		this.buffer.append(cleanedValue);		
		this.buffer.append("\"");
		
		for (int i = 1; i < values.size(); ++i) {
			this.buffer.append(", ");
			this.buffer.append("\"");			
			
			cleanedValue = cleanValue(values.get(i));
			this.buffer.append(cleanedValue);
			
			this.buffer.append("\"");
		}
		this.buffer.append("]");
		
		this.hasPreviousEntry = true;
	}
	
	
	/**
	 * This method can be used to add SOLR JSON
	 * children to the JSON output string.
	 */
	protected void addJsonSolrChildren() {
		if (false == this.children.isEmpty()) {
			 
			if (this.hasPreviousEntry) {
				this.hasPreviousEntry = true;
				this.buffer.append(", ");
			}
			
			this.buffer.append("_childDocuments_:[");
			
			this.buffer.append(this.children.get(0).toJsonString());
			
			for (int i = 1; i < children.size(); ++i) {
				this.buffer.append(", ");
				this.buffer.append(children.get(i).toJsonString());
			}
			
			this.buffer.append("]");
		}
	}
	
	/**
	 * This method can be used to create a JSON string out of the input
	 * 
	 * @return JSON string for this object
	 */
	protected String toJsonString() {
		try {
			// make sure the JSON string is finished
			if (false == this.finishedString) {
				this.finishedString = true;
				
				this.buffer.append("{");
				
				for (String fieldName : this.inputMap.keySet()) {
					List<String> values = this.inputMap.get(fieldName);
					
					if (1 == values.size()) {
						this.addSolrJsonFieldData(fieldName, values.get(0));
					} else {
						this.addSolrJsonMultiValueFieldData(fieldName, values);
					}
				}
				
				this.addJsonSolrChildren();
				
				this.buffer.append("}");
			}
			
			return buffer.toString();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * 
	 * @return Solr document which is associated with this instance
	 */
	SolrInputDocument getSolrDoc() {
		return this.solrDocument;
	}
	
	@Override
	public String toString() {
		return this.toJsonString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DataIdSolrDocument other = (DataIdSolrDocument) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
}
