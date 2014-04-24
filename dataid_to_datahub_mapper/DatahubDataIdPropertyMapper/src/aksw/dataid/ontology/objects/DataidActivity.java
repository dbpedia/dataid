package aksw.dataid.ontology.objects;

public class DataidActivity 
{
	private String dcTitle;
	private DataidAgent dcCreator;
	private DataidAgent dcContributer;
	private DataidEntity provUsed;
	
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
	public DataidAgent getDcContributer() {
		return dcContributer;
	}
	public void setDcContributer(DataidAgent dcContributer) {
		this.dcContributer = dcContributer;
	}
	public DataidEntity getProvUsed() {
		return provUsed;
	}
	public void setProvUsed(DataidEntity provUsed) {
		this.provUsed = provUsed;
	}
}
