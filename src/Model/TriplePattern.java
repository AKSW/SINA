package Model;

import java.util.HashSet;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLObjectProperty;

import Model.GraphBuilding.*;;

public class TriplePattern {
	
	private Entity Subject;
	private String Pridicate;
	private Entity Object;
	private Set<OWLObjectProperty> PropertySet =  new HashSet();
	private boolean HasSetOfProperties         =  false;
	
	
	//########################
	public void copy(TriplePattern t,Entity s,Entity o) {
		
		this.Pridicate  = t.getPridicate();
		this.Subject    = s;
		this.Object     = o;
		
	}
	//########################
	public void setPropertySet(Set<OWLObjectProperty> s) {
		
		PropertySet.addAll(s);
		
	}
	//########################
	public Set<OWLObjectProperty> getPropertySet() {
		
		
		return PropertySet;
		
	}
	//########################
   public void setHasSetOfProperties(boolean s) {
		
	   HasSetOfProperties=s;
	}
   //########################
   
   public boolean getHasSetOfProperties() {
		
	   return HasSetOfProperties;
		
	}
	//########################
	
    public void setSubject(Entity s) {
		Subject=s;
	}
	//########################
	public void setPridicate(String p) {
		Pridicate=p;
	}
	//########################
	public void setObject(Entity o) {
		Object=o;
	}
	//########################
	
	public Entity getSubject( ) {
		return Subject;
	}
	//########################
	public String getPridicate() {
		return Pridicate;
	}
	//########################
	public Entity getObject() {
		return Object;
	}
	//########################
	

}
