package Model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import Model.GraphBuilding.ClassManagement;
import Model.GraphBuilding.Entity;
import Model.GraphBuilding.Graph;
import Model.GraphBuilding.PropertyManagement;
import Model.IRIMapping.ResourceInfo;
import Model.GraphBuilding.Template;

import java.util.Collections;
import java.util.Comparator;

public class Management {
	
	private List<Template> TemporaryTemplateList = new ArrayList();
	private List<Template> FinalTemplateList     = new ArrayList();
	private List<Template> ConnectingList=new ArrayList();
	
	public void initialization(List<ResourceInfo> IRIList ) throws OWLOntologyCreationException 
	{
      //List<InputIRI> IRIList=new ArrayList();
      ClassManagement CM=new ClassManagement();
      System.out.println(" hello world");
		 
		 Template a=new Template();
		 		 
		 for(ResourceInfo s:IRIList)
		 {
		 System.out.println("print iri...."+s.getUri());
		 }
		 
		 for(ResourceInfo s:IRIList)
		 {
			 // add instance
			  if( s.getType() == Constants.TYPE_INSTANCE )
			 {
				 Entity s1=new Entity();
				 s1.setIRI(s.getUri());
				 s1.settype(s.getType());
				 Set classiri=new HashSet();
				 classiri.addAll(s.getClassSet());
				 s1.setClassTypeIRI(classiri);
				 Graph g=new Graph(); 
				 g.addEntity(s1);
				 a.addGraph(g);
				 
			 }
			  
			 // add class
			 else if( s.getType() == Constants.TYPE_CLASS )
			 {
				 Entity c     =  new Entity();
				 c.setIRI(s.getUri());
				 c.settype(2);
				 Set classiri =  new HashSet();
				 classiri.add(s.getUri());
				 classiri.addAll(CM.getSubClasses(s.getUri()));
				
				 Graph g      =  new Graph();
				 Entity v     =  g.addVariableEntity(classiri, a.getNextIndexVariable());
				 g.addTriple(v, "a", c);
				 a.addGraph(g);
			 }
		 }
			 
		 TemporaryTemplateList.add(a);
		 List<Template> GeneratedTemplateList=new ArrayList();
		 
			 for(ResourceInfo s:IRIList)
			 {
				 String property="";
				 
				  if( s.getType() == Constants.TYPE_PROPERTY )
				 {
					   property=s.getUri();
					   
					   for (Template t:TemporaryTemplateList)
					  {
						  GeneratedTemplateList.addAll(this.addProperty(property, t));
					  }
					  
					  TemporaryTemplateList.clear();
					  TemporaryTemplateList.addAll(GeneratedTemplateList);
					  GeneratedTemplateList.clear();
					  					 
			 }
			
			 } // end of for 
			 
			  for(Template t:TemporaryTemplateList)
			 {
				 if(t.getGraphList().size()<=1)
				 {
					 FinalTemplateList.add(t);
					 
				 }
				 else
				 {
					 ConnectingList.add(t);
				 }
				 							
			 }
			 TemporaryTemplateList.clear();		 
			 TemporaryTemplateList.addAll(ConnectingList);
			 ConnectingList.clear();
			
			 
			
			
		}
	
	//-------------------------------
	
	public List<Template> addProperty(String property,Template t) throws OWLOntologyCreationException
	{
		
		 OWLOntologyManager manager=OWLManager.createOWLOntologyManager();
		 IRI iri=IRI.create(Constants.ontologyAddress);
		 OWLOntology DBpediaOWL;
		
			DBpediaOWL = manager.loadOntologyFromOntologyDocument(iri);
			OWLDataFactory dataFactory = manager.getOWLDataFactory();
			 IRI iri_property=IRI.create(property);
			 OWLObjectProperty OWLProperty=dataFactory.getOWLObjectProperty(iri_property);
			 PropertyManagement PM=new PropertyManagement();
			 List<Template> GeneratedTemplateList=new ArrayList();
			 // check whether a property is datatype or not
			 // if property is datatype, it adds a triple as e p ?l
			 
			 if(PM.isDatatypeProperty(property, DBpediaOWL))
			 {
				
				 GeneratedTemplateList.addAll(t.addDataTypeTriple(property));
				
			 }
			 
			// if property is datatype, it adds a triple as:
			// (e p ?v) or (?v p e) or (e1 p e2)
			 
			 else
			 {
				 GeneratedTemplateList.addAll(t.addObjectPropertyTriple(property));
			 }	
			
			 return GeneratedTemplateList;
			
		
		 

		 
		
	}
	//-------------------------------
	
	public void connectingFunction() throws OWLOntologyCreationException
	{
		
		 while(TemporaryTemplateList.size()>0)
		 {
			 // This index is the head of Queue
			 int index=0;
			 Template tem=TemporaryTemplateList.get(index);
			 if(tem.getGraphList().size()==1)
			 {
				 FinalTemplateList.add(tem); 
				 TemporaryTemplateList.remove(index);
			 }
			 else
			 {
			 List<Template> generatedTemplate=tem.connectGraph();
			 if(generatedTemplate!=null )
			 {
				 if( generatedTemplate.size()>0)
				 {
			      TemporaryTemplateList.addAll(generatedTemplate);
				 }
			 }
			
			 
			 TemporaryTemplateList.remove(index);
			 }// end of else
		 }
		 
		 Comparator comparator=new TripleNumberComparator();
		 Collections.sort(FinalTemplateList, comparator);
		 
		 for(Template t:FinalTemplateList)
		 {
			 System.out.println("############# printing real Final template ################## "); 
			 t.printTemplate();
			 							
		 }
		 
		 System.out.println(" Connecting step finished");
		 System.out.println(" size of FinalTemplateList = " + FinalTemplateList.size());
		 System.out.println(" size of TemporaryTemplateList = " + TemporaryTemplateList.size());
		 
	}
	
	//##################################################

	/*
	 * 
	 * get final final List
	 */
	public List<Template>  getFinalTemplateList()
	{
		return FinalTemplateList;
	}
	/*
	 * 
	 * get final template List
	 */
	public List<Template>  getTemporaryTemplateList()
	{
		return TemporaryTemplateList;
	}
	
}




/*
InputIRI i=new InputIRI();
i=new InputIRI();
i.IRI="http://dbpedia.org/resource/The_Beatles";
i.type=1;
i.Class.add("http://dbpedia.org/ontology/Band");
IRIList.add(i);

i=new InputIRI();
i.IRI="http://dbpedia.org/ontology/artist";
i.type=5;
IRIList.add(i);

InputIRI i=new InputIRI();
i.IRI="http://dbpedia.org/ontology/Person";
i.type=2;
IRIList.add(i);
	 
	 
i=new InputIRI();
i.IRI="http://dbpedia.org/ontology/London";
i.type=1;
i.Class.add("http://dbpedia.org/ontology/City");
IRIList.add(i);*/

/*i.IRI="http://dbpedia.org/ontology/capital";
i.type=5;
IRIList.add(i);*/
	 
  /*i=new InputIRI();
	 i.IRI="http://dbpedia.org/resource/California";
	 i.type=1;
	 IRIList.add(i);
	 
	 */

  /* InputIRI i=new InputIRI();
   i.IRI="http://dbpedia.org/ontology/SoccerPlayer";
	 i.type=2;
	 IRIList.add(i);
	 
	 i=new InputIRI();
   i.IRI="http://dbpedia.org/resource/Goalkeeper";
   i.Class.add("http://dbpedia.org/ontology/Person");
	 i.type=1;
	 IRIList.add(i);*/

   /*InputIRI i=new InputIRI();
   i.IRI="http://dbpedia.org/ontology/birthPlace";
	 i.type=5;
	 IRIList.add(i);
	 */
	/* i=new InputIRI();
   i.IRI="http://dbpedia.org/ontology/spouse";
	 i.type=5;
	 IRIList.add(i);
	 */
	/* 
	 i=new InputIRI();
	 i.IRI="http://dbpedia.org/resource/London";
	 i.type=1;
	 i.Class.add("http://dbpedia.org/ontology/City");
	 IRIList.add(i); 
	 
	 i=new InputIRI();
   i.IRI="http://dbpedia.org/ontology/birthDate";
	 i.type=5;
	 IRIList.add(i);*/
	 
	/* i=new InputIRI();
   i.IRI="http://dbpedia.org/ontology/capital";
	 i.type=5;
	 IRIList.add(i);*/
	 
	 /*i=new InputIRI();
   i.IRI="http://dbpedia.org/ontology/PopulatedPlace/populationDensity";
	 i.type=5;
	 IRIList.add(i);
	 
	 
	 i=new InputIRI();
   i.IRI="http://dbpedia.org/ontology/academyAward";
	 i.type=5;
	 IRIList.add(i);
	 
	 i=new InputIRI();
   i.IRI="http://dbpedia.org/ontology/starring";
	 i.type=5;
	 IRIList.add(i);
	 */
	 
	 /*
	 i=new InputIRI();
   i.IRI="http://dbpedia.org/ontology/deathPlace";
	 i.type=5;
	 IRIList.add(i);
	
	 i=new InputIRI();
   i.IRI="http://dbpedia.org/resource/Southampton";
	 i.type=1;
	 i.Class.add("http://dbpedia.org/ontology/City");
	 IRIList.add(i);*/
	 
	// i=new InputIRI();
//   i.IRI="http://dbpedia.org/ontology/Country";
 //  i.type=2;
   //i.Class.add("http://dbpedia.org/ontology/Country");
 //  IRIList.add(i);
   
	/*
   InputIRI i=new InputIRI();
	 i.IRI="http://dbpedia.org/resource/Netherlands";
	 i.type=1;
	 i.Class.add("http://dbpedia.org/ontology/PopulatedPlace");
	 IRIList.add(i);

	 i=new InputIRI();
	 i.IRI="http://dbpedia.org/ontology/subdivisions";
	 i.type=5;
	 IRIList.add(i);

	 i=new InputIRI();
   i.IRI="http://dbpedia.org/ontology/Country";
   i.type=2;
   i.Class.add("http://dbpedia.org/ontology/Country");
   IRIList.add(i);*/
	 
   // organization foundation
  /* InputIRI i=new InputIRI();
	 i.IRI="http://dbpedia.org/ontology/Organisation";
	 i.type=2;
	 IRIList.add(i);
	 */
	 
	/* i=new InputIRI();
	 i.IRI="http://dbpedia.org/ontology/address";
	 i.type=5;
	 IRIList.add(i);*/
	 
//	 i=new InputIRI();
//	 i.IRI="http://dbpedia.org/ontology/Software";
//	 i.type=2;
//	 IRIList.add(i);
	 
	 /*i=new InputIRI();
   i.IRI="http://dbpedia.org/ontology/foundationPlace";
	 i.type=5;
	 IRIList.add(i);
	 
	 i=new InputIRI();
	 i.IRI="http://dbpedia.org/ontology/developer";
	 i.type=5;
	 IRIList.add(i);
	 
	 i=new InputIRI();
	 i.IRI="http://dbpedia.org/resource/California";
	 i.Class.add("http://dbpedia.org/ontology/City");
	 i.type=1;
	 IRIList.add(i);
	 */