package Model.GraphBuilding;
import java.util.HashSet;
import java.util.Set;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLObjectPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.NodeSet;




import com.clarkparsia.pellet.owlapiv3.PelletReasoner;
import com.clarkparsia.pellet.owlapiv3.PelletReasonerFactory;

public class PropertyManagement  {
	
	
	public Set objectPropertieswihSpecificDomain( OWLClass ClassSource , OWLOntology DBpediaOWL ){
		
		Set<OWLObjectProperty> PropertySet = new  HashSet();
				
		for ( OWLObjectPropertyExpression  prop : DBpediaOWL.getObjectPropertiesInSignature() ) {
		   for( OWLObjectPropertyDomainAxiom  axiom : DBpediaOWL.getObjectPropertyDomainAxioms(prop) )
    	    {
    
    	        if ( axiom.getDomain().containsConjunct(ClassSource) )
    	 	     {
    		      PropertySet.addAll( axiom.getObjectPropertiesInSignature());
    	 	      }
        	 }
    		
    	}
		
		return PropertySet;
	}
	
	//#####################################
	
   public Set objectPropertieswihSpecificRange ( OWLClass ClassSource , OWLOntology DBpediaOWL ){
		
		Set<OWLObjectProperty> PropertySet = new HashSet();
		for (OWLObjectPropertyExpression prop : DBpediaOWL.getObjectPropertiesInSignature()) {
		 for ( OWLObjectPropertyRangeAxiom axiom : DBpediaOWL.getObjectPropertyRangeAxioms(prop) )
    	 {
        	 if (axiom.getRange().containsConjunct(ClassSource))
    	 	{
    	    	PropertySet.addAll( axiom.getObjectPropertiesInSignature());
    	 	}
    		
    	 }
    		
      }
		
		return PropertySet;
	}
  
  //#######################################
   
    public Set objectPropertiesbetweenEntiies (OWLClass ClassSource, OWLClass ClassSink, OWLOntology DBpediaOWL ){
		
		 Set<OWLObjectProperty> PropertySet  =  new HashSet();
		 PelletReasoner reasoner = PelletReasonerFactory.getInstance().createReasoner(DBpediaOWL);
		 
		 NodeSet<OWLClass> NSSource=reasoner.getSuperClasses(ClassSource, false);
		 Set<OWLClass> AncestorSource=NSSource.getFlattened(); 
		 AncestorSource.add(ClassSource);
		 NodeSet<OWLClass> NSSink=reasoner.getSuperClasses(ClassSink, false);
		 Set<OWLClass> AncestorSink=NSSink.getFlattened();
		 AncestorSink.add(ClassSink);
		 
				
		for (OWLObjectPropertyExpression prop : DBpediaOWL.getObjectPropertiesInSignature()) 
		{
			
			 Set<OWLClass> DomainSet = new HashSet();
			 Set<OWLClass> RangeSet  = new HashSet();
			
			 for( OWLObjectPropertyDomainAxiom DomainAxiom : DBpediaOWL.getObjectPropertyDomainAxioms(prop) )
			 {
				 DomainSet.addAll(DomainAxiom.getDomain().getClassesInSignature());
			 }
			 
			 for( OWLObjectPropertyRangeAxiom RangeAxiom  : DBpediaOWL.getObjectPropertyRangeAxioms(prop) )
			 {
				 RangeSet.addAll(RangeAxiom.getRange().getClassesInSignature());
			 } 
			 
			 DomainSet.retainAll(AncestorSource);
			 RangeSet.retainAll(AncestorSink)   ;
			 
				 if( DomainSet.size()>0  &&  RangeSet.size()>0 )
				{
					PropertySet.add(prop.asOWLObjectProperty());	
				}
		
  	    } 
		
		
		return PropertySet;
	}
    //#######################################
    public Set getobjectPropertiesbetweenEntiies(Set<String> SourceClassesSet,Set<String> SinkClassesSet,OWLOntology DBpediaOWL){
  		
  		Set<OWLObjectProperty> PropertySet =  new HashSet();
  		OWLOntologyManager manager         =  OWLManager.createOWLOntologyManager();
  		OWLDataFactory dataFactory         =  manager.getOWLDataFactory();		
  		PelletReasoner reasoner            =  PelletReasonerFactory.getInstance().createReasoner(DBpediaOWL);
  		Set<OWLClass> AncestorSource       =  new HashSet();
  		NodeSet<OWLClass> NSSource;
  		 
  		 
  		/* for(String s:SourceClassesSet)
  		 {
  			 IRI iri              =  IRI.create(s);
  			 OWLClass ClassObject =  dataFactory.getOWLClass(iri);
  			 NSSource             =  reasoner.getSuperClasses(ClassObject, false);
  			 AncestorSource.addAll(NSSource.getFlattened());
  			 AncestorSource.add(ClassObject);
  		 }*/
  		 for(String s:SourceClassesSet)
  		 {
  			 IRI iri              =  IRI.create(s);
  		     OWLClass ClassObject =  dataFactory.getOWLClass(iri);
  			 AncestorSource.add(ClassObject);
  		 }
  		
  		 NodeSet<OWLClass> NSSink;
  		 Set<OWLClass>  AncestorSink  = new HashSet();
  		
  		/* for(String s:SinkClassesSet)
  		 {
  			 IRI iri              =  IRI.create(s);
  			 OWLClass ClassObject =  dataFactory.getOWLClass(iri);
  			 NSSink               =  reasoner.getSuperClasses(ClassObject, false);
  			 AncestorSink.addAll(NSSink.getFlattened());
  			 AncestorSink.add(ClassObject);
  		 }*/
  		 
  		 for(String s:SinkClassesSet)
  		 {
  			 IRI iri              =  IRI.create(s);
  			 OWLClass ClassObject =  dataFactory.getOWLClass(iri);
  			 AncestorSink.add(ClassObject);
  		 }
  		
  		 DomainRangeInference DRI=new DomainRangeInference();
  		 
  		for ( OWLObjectPropertyExpression prop : DBpediaOWL.getObjectPropertiesInSignature() ) 
  		{
  			
  			 Set<OWLClass> DomainSet                     = new HashSet();
  			 Set<OWLClass> RangeSet                      = new HashSet();
  			 Set<OWLClassExpression> domainExpressionSet = new HashSet();
  			 Set<OWLClassExpression> domainUnionSet      = prop.getDomains(DBpediaOWL);
  			
  			 /*for ( OWLClassExpression i : domainUnionSet )
  			 {
  				 domainExpressionSet.addAll(i.asDisjunctSet());
  			 }*/
  			
  			 for(OWLObjectPropertyDomainAxiom DomainAxiom : DBpediaOWL.getObjectPropertyDomainAxioms(prop))
  			 {
  				 
  				 DomainSet.addAll(DomainAxiom.getDomain().getClassesInSignature());
  				 				 
  			 }
  			 
  			
  			 
  			 for(OWLObjectPropertyRangeAxiom RangeAxiom : DBpediaOWL.getObjectPropertyRangeAxioms(prop))
  			 {
  				 RangeSet.addAll(RangeAxiom.getRange().getClassesInSignature());
  			 }
  			 
  			 
  			 
  			 Set<OWLClass> DomainChecker                     = new HashSet();
  			 Set<OWLClass> RangeChecker                      = new HashSet();
  			 DomainChecker.addAll(DomainSet);
  			 RangeChecker.addAll(RangeSet);
  			 
  			 
  			 DomainSet.retainAll(AncestorSource);
  			 RangeSet.retainAll(AncestorSink)   ; 
  			 
  				 if( DomainSet.size()>0 && RangeSet.size()>0)
  				{
  					PropertySet.add(prop.asOWLObjectProperty());
  								
  				}
  				 
  				 else if(DomainSet.size()>0 && RangeChecker.size()==0)
  				 {
  					 
  					 if(RangeChecker.size() == 0)
  					 {
  						 Set<String> DS                    = new HashSet();
  					 	 DS.addAll(DRI.getRangeInferenceOverKB(prop.asOWLObjectProperty().getIRI().toString()));
  					 	 for(String s:DS)
  					 	 {
  					 		 IRI iri              =  IRI.create(s);
  							 OWLClass ClassObject =  dataFactory.getOWLClass(iri); 
  							 RangeSet.add(ClassObject);
  					 	 }
  					 	 
  					 	RangeSet.retainAll(AncestorSink)   ;
  					 	
  					 	 if( RangeSet.size()>0)
  							{
  								PropertySet.add(prop.asOWLObjectProperty());
  											
  							}
  							
  					 }
  					
  					 
  				 }
  				 
  				 else if(RangeSet.size()>0 && DomainChecker.size()==0)
  				 {
  					 
  					 if(DomainSet.size() == 0)
  					 {
  						 Set<String> DS                    = new HashSet();
  					 	 DS.addAll(DRI.getDomainInferenceOverKB(prop.asOWLObjectProperty().getIRI().toString()));
  					 	 for(String s:DS)
  					 	 {
  					 		 IRI iri              =  IRI.create(s);
  							 OWLClass ClassObject =  dataFactory.getOWLClass(iri); 
  							 DomainSet.add(ClassObject);
  					 	 }
  					 
  					 	 
  					 	DomainSet.retainAll(AncestorSource);
  					 	
  					 	 if( DomainSet.size()>0 && RangeSet.size()>0)
  							{
  								PropertySet.add(prop.asOWLObjectProperty());
  											
  							}
  							
  					 }
  					
  					 
  				 }
  						 
  				 
  		
    	    }
  		
  		return PropertySet;
  	}
    
    //#####################################
	// ----    getDomainSubClasses
	//#####################################
  
  public Set getDomainSubClasses(OWLObjectProperty property,OWLOntology DBpediaOWL)
  {
		
	 
	  
		 PelletReasoner reasoner                     =  PelletReasonerFactory.getInstance().createReasoner(DBpediaOWL);
		 OWLOntologyManager manager                  =  OWLManager.createOWLOntologyManager();
		 OWLDataFactory dataFactory                  =  manager.getOWLDataFactory();
		 NodeSet<OWLClass> SubClassSet;
		 Set<OWLClassExpression> domainUnionSet      = property.getDomains(DBpediaOWL);
		 Set<OWLClass> domainSet                     = new HashSet();
		 Set<OWLClassExpression> domainExpressionSet = new HashSet();
		 Set<OWLClass> TempdomainExpressionSet       = new HashSet();
		
	    // convert union set to a set of disjoint sets
		 
		/* if(property.getIRI().toString().equals("http://xmlns.com/foaf/0.1/homepage"))
			{
			 domainSet.addAll(dataFactory.get)
			 
			}*/
		 
		 //&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& pieces hidden
		 
		/* 
		 for ( OWLClassExpression i : domainUnionSet )
		 {
			 domainExpressionSet.addAll(i.asDisjunctSet());
		 }
		 
		 for ( OWLClassExpression i : domainExpressionSet )
		 {
			
			 SubClassSet  =  reasoner.getSubClasses(i, false);
			 TempdomainExpressionSet.addAll(SubClassSet.getFlattened());
		 }
		 
		 // false returns all levels of subclasses
		 // true returns just first level of classes
		 // convert OWLClassExpression to OwlClass
		 
		 for ( OWLClassExpression i : domainExpressionSet )
		 {
			 domainSet.add(i.asOWLClass());
		 }
		
		domainSet.addAll(TempdomainExpressionSet);
		domainSet.remove(dataFactory.getOWLNothing());
		
		*/
		//&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& end  pieces hidden
		/*if ( domainSet.size() == 0)
		 {
		   domainSet.addAll(DBpediaOWL.getClassesInSignature()); 
		 }
		 */
		
		if ( domainSet.size() == 0)
		 {
			DomainRangeInference dri=new DomainRangeInference();
			HashSet<String>  d= dri.getDomainInferenceOverKB(property.getIRI().toString());
			
			for(String i:d)
			{
				IRI iri=IRI.create(i);
				OWLClass c = dataFactory.getOWLClass(iri);
				domainSet.add(c);
				
			} 
		 }	
		return domainSet;
		
	}
  
 
  
//#####################################
// ----    getDomainDataTypeProperty
//#####################################
  
  public Set getDomainDataTypeProperty(OWLDataProperty property,OWLOntology DBpediaOWL)
  
    {
		 PelletReasoner reasoner                     = PelletReasonerFactory.getInstance().createReasoner(DBpediaOWL);
		 Set<OWLClassExpression> domainUnionSet      = property.getDomains(DBpediaOWL);
		 OWLOntologyManager manager                  = OWLManager.createOWLOntologyManager();
		 OWLDataFactory dataFactory                  = manager.getOWLDataFactory();
		 Set<OWLClass> domainSet                     = new HashSet();
		 Set<OWLClassExpression> domainExpressionSet = new HashSet();
		 Set<OWLClass> TempdomainExpressionSet       = new HashSet();
		 NodeSet<OWLClass> SubClassSet;
		
		 // convert union set to a set of disjoint sets
		 
		//&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& begin pieces hidden
		 /*for(OWLClassExpression i:domainUnionSet)
		 {
			 domainExpressionSet.addAll(i.asDisjunctSet());
		 }
		 	 
		 
		 for ( OWLClassExpression i : domainExpressionSet )
		 {
			
			 SubClassSet  =  reasoner.getSubClasses(i, false);
			 TempdomainExpressionSet.addAll(SubClassSet.getFlattened());
		 }
		
		 for ( OWLClassExpression i : domainExpressionSet )
		 {
			 domainSet.add(i.asOWLClass());
		 }
		
		domainSet.addAll(TempdomainExpressionSet);
		domainSet.remove(dataFactory.getOWLNothing());*/
		
		//&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& end pieces hidden
		
		/*if ( domainSet.size() == 0)
		 {
		   domainSet.addAll(DBpediaOWL.getClassesInSignature()); 
		 }*/
		
		if ( domainSet.size() == 0)
		 {
			DomainRangeInference dri=new DomainRangeInference();
			
			HashSet<String>  d= dri.getDomainInferenceOverKB(property.getIRI().toString());
			
			for(String i:d)
			{
				IRI iri=IRI.create(i);
				OWLClass c = dataFactory.getOWLClass(iri);
				domainSet.add(c);
				
			}
		   
		 }
		
		
		
		
		
		
		return domainSet;
		
	}
  
//#####################################
// ----    getRangeSubClasses
//#####################################
  
  public Set getRangeSubClasses(OWLObjectProperty property,OWLOntology DBpediaOWL)
  {
		 PelletReasoner reasoner                     =  PelletReasonerFactory.getInstance().createReasoner(DBpediaOWL);
		 OWLOntologyManager manager                  =  OWLManager.createOWLOntologyManager();
		 OWLDataFactory dataFactory                  =  manager.getOWLDataFactory();
	     Set<OWLClassExpression> RangeUnionSet       =  property.getRanges(DBpediaOWL);
		 Set<OWLClass> RangeSet                     =  new HashSet();
		 Set<OWLClassExpression> RangeExpressionSet =  new HashSet();
		 Set<OWLClass> TempRangeExpressionSet       =  new HashSet();
		 NodeSet<OWLClass> SubClassSet;
		
		//&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& begin pieces hidden
		 
		/* // convert union set to a set of disjoint sets
		 for ( OWLClassExpression i : RangeUnionSet )
		 {
			 RangeExpressionSet.addAll(i.asDisjunctSet());
		 }
				 	 
		 for ( OWLClassExpression i : RangeExpressionSet )
		 {
			 SubClassSet  = reasoner.getSubClasses(i, false);
			 TempRangeExpressionSet.addAll(SubClassSet.getFlattened());
		 }
		 
		 // convert OWLClassExpression to OwlClass
		 for ( OWLClassExpression i : RangeExpressionSet )
		 {
			 RangeSet.add(i.asOWLClass());
		 }
		 RangeSet.addAll(TempRangeExpressionSet);
		 RangeSet.remove(dataFactory.getOWLNothing());
		 
		 if ( RangeSet.size() == 0)
		 {
			 RangeSet.addAll(DBpediaOWL.getClassesInSignature()); 
		 }*/
		 
		//&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& end pieces hidden
		 
		 if ( RangeSet.size() == 0)
		 {
			DomainRangeInference dri=new DomainRangeInference();
			HashSet<String>  d= dri.getRangeInferenceOverKB(property.getIRI().toString());
			
			for(String i:d)
			{
				IRI iri=IRI.create(i);
				OWLClass c = dataFactory.getOWLClass(iri);
				RangeSet.add(c);
				
			}
		   
		 }
		 
		 
	
		return RangeSet;
		
	}
 /* public Set getClassDomain(OWLObjectProperty property,OWLOntology DBpediaOWL)
    {
	 
	 Set<OWLClassExpression> domainUnionSet      =  property.getDomains(DBpediaOWL);
	 Set<OWLClass> domainSet                     =  new HashSet();
	 Set<OWLClassExpression> domainExpressionSet =  new HashSet();
	
	 // convert union set to a set of disjoint sets
	 for( OWLClassExpression i:domainUnionSet )
	 {
		 domainExpressionSet.addAll(i.asDisjunctSet());
	 }
	
	 // convert OWLClassExpression to OwlClass
	 for(OWLClassExpression i:domainExpressionSet)
	 {
		 domainSet.add(i.asOWLClass());
	 }
	
	return domainSet;
	  
  }*/
  //----------------------
  
  /*public Set getClassRange(OWLObjectProperty property, OWLOntology DBpediaOWL)
  {
	
	     Set<OWLClassExpression>  RangeUnionSet     =  property.getRanges(DBpediaOWL);
		 Set<OWLClass> RangeSet                     =  new HashSet();
		 Set<OWLClassExpression> RangeExpressionSet =  new HashSet();
		
		 // convert union set to a set of disjoint sets
		 for( OWLClassExpression i:RangeUnionSet )
		 {
			 RangeExpressionSet.addAll(i.asDisjunctSet());
		 }
		
		 // convert OWLClassExpression to OwlClass
		 for( OWLClassExpression i:RangeExpressionSet )
		 {
			 RangeSet.add(i.asOWLClass());
		 }
	  
	 return RangeSet;
	  
  }*/
//###############################################
  //check weather an iri is a datatype property or not
//###############################################
  public boolean isDatatypeProperty(String s,OWLOntology DBpediaOWL)
    {
	  IRI iri_property=IRI.create(s);
	  if(DBpediaOWL.containsDataPropertyInSignature(iri_property))
		{
			//System.out.println("yes");
			return true;
		}
		else
		{
			//System.out.println("no");
			return false;
		}
  }
//###############################################
  //check weather an iri is a object property or not
//###############################################	
 
  public boolean isObjectProperty(String s,OWLOntology DBpediaOWL)
  {
	  IRI iri_property = IRI.create(s);
	  if ( DBpediaOWL.containsObjectPropertyInSignature(iri_property) )
		{
			return true;
		}
	 else
		{
			return false;
		}
  
  }
  
  //-----------
}
