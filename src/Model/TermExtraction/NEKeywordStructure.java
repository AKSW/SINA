package Model.TermExtraction;

public class NEKeywordStructure {

	
	private String NE;
	private String Keyword;
	private String uri;
	private int Type;
	
	public String getKeyword() {
		// TODO Auto-generated method stub
		return Keyword;
	}

	public void setNE(String s) {
		NE=s;
		
	}
	
	public void setType(Integer integer) {
		Type=integer;
		
	}

	public void setUri(String s) {
		uri=s;
		
	}
	
	public void setKeyword(String s) {
		Keyword=s;
		
	}

	public String getNE() {
		
		return NE;
	}
	
	
	public String geturi() {
		return uri;
		
	}

	public int getType(String s) {
		return Type;
		
	}
	
	

}
