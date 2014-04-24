package aksw.dataid.ontology.objects;

public class DataidEntity 
{
	private String dcTitle;
	private DataidAgent dcCreator;
	private DataidAgent dcPublisher;
	private DataidAgent dcContributer;
	
	public String getDcTitle() {
		return dcTitle;
	}
	public void setDcTitle(String dcTitle) {
		this.dcTitle = dcTitle;
	}
	public DataidAgent getDcCreator() {
		return dcCreator;
	}
	public void setDcCreator(DataidAgent dcCreator) {
		this.dcCreator = dcCreator;
	}
	public DataidAgent getDcPublisher() {
		return dcPublisher;
	}
	public void setDcPublisher(DataidAgent dcPublisher) {
		this.dcPublisher = dcPublisher;
	}
	public DataidAgent getDcContributer() {
		return dcContributer;
	}
	public void setDcContributer(DataidAgent dcContributer) {
		this.dcContributer = dcContributer;
	}
	
}
