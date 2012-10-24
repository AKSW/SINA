package Model.GraphBuilding;

import java.util.HashSet;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLObjectProperty;


public class TriplePattern {
	
	private Entity Subject;
	private String Pridicate;
	private Entity Object;
	private Set<OWLObjectProperty> PropertySet =  new HashSet();
	private boolean HasSetOfProperties         =  false;
	
	
	//########################
	public void copy(TriplePattern t,Entity s,Entity o) {
		
		this.Pridicate  = t.getPredicate();
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
	public void setPredicate(String p) {
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
	public String getPredicate() {
		return Pridicate;
	}
	//########################
	public Entity getObject() {
		return Object;
	}
	//########################
	

}
