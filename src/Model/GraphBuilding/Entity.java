package Model.GraphBuilding;

import java.util.HashSet;
import java.util.Set;

public class Entity {
	
	private String IRI;
	private int type;
	private boolean traverseFlag = false;
	
	
	// type=1, instance
	// type=2 class
	// type=3 variable
	// type=4 literal
	private Set<String> ClassTypeIRI=new HashSet();

		
	
	
	//############################################
	// --- This function copy the input entity ---
	//############################################
	
	public void Copy(Entity e) {
		this.type=e.gettype();
		this.IRI=e.getIRI();
		this.ClassTypeIRI=e.getClassTypeIRI();
	}
	
	//############################################
	
	public String getIRI() {
		return IRI;
	}
	//--------------------------------------------
	public int gettype() {
		return type;
	}
	//--------------------------------------------
	public Set<String> getClassTypeIRI() {
		return ClassTypeIRI;
	}
	//--------------------------------------------	
	public void setClassTypeIRI(String i) {
		ClassTypeIRI.add(i);
	}
	//--------------------------------------------
	public void setClassTypeIRI(Set i) {
		//for(String a:i)
		ClassTypeIRI=i;
	}
	//--------------------------------------------
	public boolean gettraverseFlag() {
		return traverseFlag;
	}
	//--------------------------------------------
	public void settraverseFlag(boolean i) {
		traverseFlag=i;
	}
	//--------------------------------------------
	public void setIRI(String i) {
		IRI=i;
	}
	//--------------------------------------------
	public void settype(int i) {
		type=i;
	}
	//--------------------------------------------
}