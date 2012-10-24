package Model.IRIMapping;

import java.util.HashSet;
import java.util.Set;

public class InputIRI {
	
	public String IRI;
	public int type;
	public Set<String> Class=new HashSet();
	
	
	// type=1, instance
	// type=2 class
	// type=3 variable
	// type=4 literal
	//type=5 property

}
