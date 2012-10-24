package Model;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.apibinding.*;
import org.semanticweb.owlapi.util.*;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.*;

import java.net.URI;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.HashSet;
import java.lang.reflect.InvocationTargetException;
import com.clarkparsia.pellet.owlapiv3.PelletReasoner;
import com.clarkparsia.pellet.owlapiv3.PelletReasonerFactory;
//import com.clarkparsia.pellet.utils.CollectionUtils;


public class test {
	 
	
  //#######################################
	   
    public Set objectPropertiesbetweenEntiies (OWLClass ClassSource, OWLClass ClassSink, OWLOntology DBpediaOWL )
    {
		
    	Set<OWLObjectProperty> PropertySet  =  new HashSet();
    	

    	PelletReasoner reasoner = PelletReasonerFactory.getInstance().createReasoner(DBpediaOWL);
    	reasoner.getSuperClasses(ClassSink, false);
    	// Set<OWLClass> NSSink=reasoner.getSuperClasses(ClassSink, false).getFlattened();
    	 
		return PropertySet;
	}
}
