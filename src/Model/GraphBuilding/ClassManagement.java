package Model.GraphBuilding;

import java.util.HashSet;
import java.util.Set;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.NodeSet;

import Model.Constants;

import com.clarkparsia.pellet.owlapiv3.PelletReasoner;
import com.clarkparsia.pellet.owlapiv3.PelletReasonerFactory;

	public class ClassManagement {
	
	//###########################################################
	// this function return all subclasses of the input class
	//###########################################################
	
		public Set getSubClasses(String ClassTarget) throws OWLOntologyCreationException{
	
			OWLOntologyManager manager	=	OWLManager.createOWLOntologyManager();
			IRI iri						=	IRI.create(Constants.ontologyAddress);
			OWLOntology DBpediaOWL		=	manager.loadOntologyFromOntologyDocument(iri);
			OWLDataFactory dataFactory 	= 	manager.getOWLDataFactory();
  	
  	  
			IRI iriclass				=	IRI.create(ClassTarget);
			OWLClass OWLClassTarget		=	dataFactory.getOWLClass(iriclass);
      
			PelletReasoner reasoner 	= 	PelletReasonerFactory.getInstance().createReasoner(DBpediaOWL);
			NodeSet<OWLClass> NS		=	reasoner.getSubClasses(OWLClassTarget, false);
	        // false returns all subclasses in any level while true returns just sublasses in the first level
	  
			Set<OWLClass> DescendentSet	=	NS.getFlattened();
			DescendentSet.add(OWLClassTarget);
			// convert to String
			DescendentSet.remove(dataFactory.getOWLNothing());
			Set<String> DescentSetString=	new HashSet();
			for(OWLClass n:DescendentSet)
			{
				DescentSetString.add(n.getIRI().toString());
			}
		
			return DescentSetString;
		
	
	}
  
  //@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
	public Set getSuperClasses(String ClassTarget) throws OWLOntologyCreationException{
			
			OWLOntologyManager manager	=	OWLManager.createOWLOntologyManager();
			IRI iri						=	IRI.create(Constants.ontologyAddress);
			OWLOntology DBpediaOWL		=	manager.loadOntologyFromOntologyDocument(iri);
			OWLDataFactory dataFactory 	= 	manager.getOWLDataFactory();
  	
  	  
			IRI iriclass				=	IRI.create(ClassTarget);
			OWLClass OWLClassTarget		=	dataFactory.getOWLClass(iriclass);
      
			PelletReasoner reasoner 	= 	PelletReasonerFactory.getInstance().createReasoner(DBpediaOWL);
			NodeSet<OWLClass> NS		=	reasoner.getSuperClasses(OWLClassTarget, false);
	        // false returns all subclasses in any level while true returns just sublasses in the first level
	  
			Set<OWLClass> DescendentSet	=	NS.getFlattened();
			DescendentSet.add(OWLClassTarget);
			// convert to String
			DescendentSet.remove(dataFactory.getOWLNothing());
			Set<String> DescentSetString=	new HashSet();
			for(OWLClass n:DescendentSet)
			{
				DescentSetString.add(n.getIRI().toString());
			}
			DescentSetString.remove(dataFactory.getOWLThing().getIRI().toString());
			return DescentSetString;
	//@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@	
	
	}
   }
