package Model.GraphBuilding;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import Model.Constants;

public class Template {
	
	private List<Graph> GraphList  = new ArrayList();
	private int LastIndexVariable  = -1;
	private int LastIndexProperty  = -1;
	private int LastIndexLiteral   = -1;
//###############################################
// access methods
//###############################################		
	
   public int getNextIndexVariable() {
		
	   LastIndexVariable++;
	   return LastIndexVariable;
	}
   
 //#####################################
	
   public int getLastIndexVariable() {
		
	   return LastIndexVariable;
	}
   
  //#####################################
	
   public String getSPARQL_query() {
		
	   String query = "select * where {  ";
	   
	   for ( Graph g:this.getGraphList() ){
			
		   for(TriplePattern t:g.getTripleList())
		   { 
			   String subject   = t.getSubject().getIRI();
			   if( t.getSubject().gettype() == Constants.TYPE_INSTANCE )
			   {
				   subject="<"+subject+">";  
			   }
			   String object    = t.getObject().getIRI();
			   if ( (t.getObject().gettype() == Constants.TYPE_INSTANCE) || (t.getObject().gettype() == Constants.TYPE_CLASS) )
			   {
				   object="<"+object+">";  
			   }
			   
			   String predicate  = t.getPredicate();
			   if(! t.getHasSetOfProperties())
			   {
				   
				   if(!(predicate.equals("a")) &&  !predicate.startsWith("?") &&  !predicate.startsWith("[")){
				   predicate="<"+predicate+">"; } 
				   if(predicate.startsWith("["))
				   {
					   predicate= predicate.replace("[", " ");
					   predicate= predicate.replace("]", " ");
				   }
				   query = query + "   " + subject + "   "  + predicate + "   " + object + ". ";
			   }
			   			   
			   else {
				   
				   
				    String unionPredicate = "";
				  
				   
				   Iterator it = t.getPropertySet().iterator();
				   
				/*   if (t.getPropertySet().size() == 1)
				   {
					   OWLObjectProperty p= (OWLObjectProperty)it.next();
					   predicate=  subject +"  <" + p.getIRI().toString() + ">  " + object + ". ";
					   query = query + "   " + subject + "   "  + predicate + "   " + object + ". ";
				   }
				   else{
				   */
				   
				   while(it.hasNext()){
				   String w; 
				   OWLObjectProperty p= (OWLObjectProperty)it.next();
				   w=  subject +"  <" + p.getIRI().toString() + ">  " + object + ". ";
				   
				   if (it.hasNext())
				   {
					  w=" { " + w + " } UNION "; 
				   }
				   else
				   {
					  w=" { " + w + " }  "; 
				   }
				   unionPredicate = unionPredicate + w;
				   
				   }// end of while
				   
				    ;
				   query = query + " " + unionPredicate;
				//   }// end of else
			   }
			   
			   
			  
			        }
		 }
	   query = query +" }";
	   
	   return query;
	}

 //#####################################
	
   public String getQueryRepresentation() {
		
	   String query = " ";
	   
	   for ( Graph g:this.getGraphList() ){
			
		   for(TriplePattern t:g.getTripleList())
		   { 
			   String subject   = t.getSubject().getIRI();
			   if( t.getSubject().gettype() == Constants.TYPE_INSTANCE )
			   {
				  int index = subject.lastIndexOf("/"); 
				  if(index >= 0)
				   {
				  subject=subject.substring(index);
				  subject=subject.replaceAll("/", "");
			   }
			   }
			  
			   String object = t.getObject().getIRI()  ;
			  
			   if(t.getObject().gettype() != Constants.TYPE_VARIABLE)
			   {
				   object    = t.getObject().getIRI();
				   int index = object.lastIndexOf("/"); 
				   if(index >= 0)
				   {
				   object=object.substring(index);
				   object.replaceAll("/","");
				   }
			   }
			   
			  // String predicate  = t.getPredicate();
			
			 /*  if(! t.getHasSetOfProperties())
			   {
				  predicate  = t.getPredicate();
				  if()
				  int index = predicate.lastIndexOf("/"); 
				  predicate=predicate.substring(index);
				  predicate.replace("/", "");				     
			   }
			   else
			   {*/
			   String predicate="[ ";
			   if( t.getHasSetOfProperties()){
				   for (OWLObjectProperty p:t.getPropertySet())
				   {
					
					String s=p.getIRI().toString();
					int index = s.lastIndexOf("/"); 
					s=s.substring(index);
					s=s.replace("/", "");
				   predicate= predicate+ " --- " + s;  
				   }
			  
				   predicate= predicate+"]";
		   }
			   else
			   {
				   if(!t.getPropertySet().isEmpty()){
				   for (OWLObjectProperty p:t.getPropertySet())
				   {
					
					String s=p.getIRI().toString();
					int index = s.lastIndexOf("/"); 
					s=s.substring(index);
					s=s.replace("/", " ");
				    predicate= predicate+ " --- " + s;  
				   }
				    
			   }
				   else{
					 String s=t.getPredicate();
					 int index = s.lastIndexOf("/"); 
					 if(index>0){
					 s=s.substring(index);
					 s=s.replace("/", " ");}
				    predicate= s;
				   }
				    
			   }
			   query = query + "   " + subject + "      "  + predicate + "     " + object + "  . ";
			        }
		 }
	   query = query +" ";
	   
	   return query;
	}

   
  //#####################################
   
   public int getNextIndexProperty() {
		
	   LastIndexProperty++;
	   return LastIndexProperty;
	}
  //#####################################
   
   public int getLastIndexProperty() {
		
	   return LastIndexProperty;
	}
  //#####################################
   
   public int getNextIndexLiteral() {
		
	   LastIndexLiteral++;
	   return LastIndexLiteral;
	}
   
 //#####################################
   
   public int getLastIndexLiteral() {
		
	   	   return LastIndexLiteral;
	}
   
  //#####################################
   
   public void setNextIndexLiteral(int i) {
		
	   LastIndexLiteral=i;
	  
	}
   
  //#####################################
   
   public void setLastIndexVariable(int i) {
		
	   LastIndexVariable=i;
	  
	}
   
   //#####################################
    
   public void setLastIndexProperty(int i) {
		
	   LastIndexProperty=i;
	}
   
   //#####################################
   
	public void setGraphList(List<Graph> i) {
		
		Collections.copy(GraphList,i); 
	}
   //#####################################
	public void addGraph(Graph i) {
		
		GraphList.add(i); 
	}
	
   //#####################################
	
	public List<Graph> getGraphList() {
		
		return GraphList;
	}
	
//###############################################
//functionality methods
//###############################################		
	
	//#####################################
	// ----    Copy template
	//#####################################
	public Template copyTemplate()
	{
				
		Template b  = new Template();
		b.setLastIndexProperty(this.getLastIndexProperty());
		b.setLastIndexVariable(this.getLastIndexVariable());
		b.setNextIndexLiteral(this.getLastIndexLiteral());
		
				 
		 for ( Graph g:this.getGraphList() )
		 {
			 Graph gh = new Graph();
			 
			 for( Entity e:g.getEntityList() ){
			    
				 Entity a = new Entity();
				 a.Copy(e);				 
				 gh.addEntity(a);
				 
			 }
				
			 for( TriplePattern t:g.getTripleList() ){
				 
				TriplePattern a  =  new TriplePattern();
				a.copy( t, t.getSubject(), t.getObject() );
				a.setHasSetOfProperties(t.getHasSetOfProperties());
				a.setPropertySet(t.getPropertySet());
				gh.addTriple( a );
							    
			 }
			
		 b.addGraph(gh);
		
		}
		 
	return b; 
	}
	
	//#####################################
	// ----    print template
	//#####################################
	
	public void printTemplate()
  	{
		
		for ( Graph g:this.getGraphList() ){
		
		   for(Entity e:g.getEntityList()){
		   System.out.println("entity:   "+e.getIRI());
		   System.out.println("entity class type:   "+e.getClassTypeIRI());
		   }
		 
		   for(TriplePattern t:g.getTripleList())
		   { 
	       System.out.println("triple:   " + t.getSubject().getIRI() + "  " + t.getPredicate() + "  " + t.getObject().getIRI());
	       if(t.getHasSetOfProperties())
	       { 
	        System.out.println(" PropertySet..... " + t.getPropertySet());
	       }
		   }
		 }
  	}

	
	// merge two unconnected graph by adding a new triple pattern
	public void mergGraph(int indexfirstGraph,int indexSecondGraph, Entity e1, Entity e2,String property, Set<OWLObjectProperty> PropertySet)
	{
		// add entities
		for(Entity e:this.getGraphList().get(indexSecondGraph).getEntityList())
		{
		this.GraphList.get(indexfirstGraph).addEntity(e);
		}
		
		// add triples
		for(TriplePattern t:this.getGraphList().get(indexSecondGraph).getTripleList())
		{
			this.GraphList.get(indexfirstGraph).addTriple(t);	
		}
		
		TriplePattern t1  =  new TriplePattern();
		t1.setSubject(e1);
		t1.setPredicate(property); //TODO: management of added property 
		t1.setObject(e2);
		if ( PropertySet.size() > 1 )
		{
			t1.setPropertySet(PropertySet);
			t1.setHasSetOfProperties(true);
		}
		
		
		this.GraphList.get(indexfirstGraph).addTriple(t1);
		this.GraphList.remove(indexSecondGraph);
		
		this.printTemplate();
			
	}
	
//#####################################
// ----    connectGraph
//#####################################
// this function has the main functionality to construct an integrated template
	
	public List<Template> connectGraph() throws OWLOntologyCreationException
	{
	  if ( GraphList.size() == 1 )
	  {
		return null;		
	  }
	
	PropertyManagement PM             =  new PropertyManagement();
	List<Template> GeneratedTemplates =  new ArrayList();;
	int index                         =  0;
	Graph BG                          =  GraphList.get(index); // base graph
	index++;
	boolean FoundConnection           =  false;
	OWLOntologyManager manager        =  OWLManager.createOWLOntologyManager();
	IRI iri                           =  IRI.create(Constants.ontologyAddress);
	OWLOntology DBpediaOWL            =  manager.loadOntologyFromOntologyDocument(iri);
	OWLDataFactory dataFactory        =  manager.getOWLDataFactory();
	
	while (!FoundConnection && index<GraphList.size())
	{
			Graph g2=GraphList.get(index);
			
			for ( Entity e1 : BG.getEntityList() )
			{
			//	if(!e1.gettraverseFlag())
			//	{
				for ( Entity e2 : g2.getEntityList() )
				{
					Set<String> SourceClassSet  =  new HashSet();
					Set<String> SinkClassSet    =  new HashSet();
					
					if ( e1.gettype() == Constants.TYPE_CLASS )
					{
						SourceClassSet.add(e1.getIRI());
					}
					else if ( e1.gettype() == Constants.TYPE_INSTANCE  ||  e1.gettype() == Constants.TYPE_VARIABLE )
					{
						SourceClassSet.addAll(e1.getClassTypeIRI());
					}
					
					if ( e2.gettype() == Constants.TYPE_CLASS )
					{ 
						SinkClassSet.add(e2.getIRI());
					 
					}
					else if ( e2.gettype() == Constants.TYPE_INSTANCE  ||  e2.gettype() == Constants.TYPE_VARIABLE )
					{
						SinkClassSet.addAll(e2.getClassTypeIRI());
					}
					
					//todo: baraye anvadae kelas ha iri bayad sakhte shav
					
					// Connection from e1 to e2
					Set PropertySet  =  PM.getobjectPropertiesbetweenEntiies(SourceClassSet, SinkClassSet, DBpediaOWL);	
					if ( PropertySet.size() > 0 ) 
					{
					    String Property = "";	
						if ( PropertySet.size() == 1 )
						{
							Property = PropertySet.toString();
						}
						else
						{
							Property = "?p";
							// todo: a method for creating new property variable
						}
					   Template a           = this.copyTemplate();
					   int indexfirstGraph  = this.getGraphList().indexOf(BG);
					   int indexSecondGraph = this.getGraphList().indexOf(g2);
					   
					  /* for ( Entity e : a.getGraphList().get(indexfirstGraph).getEntityList() )
						{
						   e.settraverseFlag(true);
						}*/
						
					   Set<OWLObjectProperty> ps=new HashSet();
					   ps.addAll(PropertySet);
					   a.mergGraph(indexfirstGraph, indexSecondGraph,e1,e2,Property, ps );
					   GeneratedTemplates.add(a);
					   FoundConnection      = true;
					} 
					// Connection from e2 to e1
					PropertySet.clear();
					PropertySet            = PM.getobjectPropertiesbetweenEntiies(SinkClassSet, SourceClassSet, DBpediaOWL);	
					if ( PropertySet.size() > 0 ) 
					{
			            String Property = "";	
						if ( PropertySet.size() == 1 )
						{
							Property = PropertySet.toString();
						}
						else
						{
							Property = "?p";
						// todo: a method for creating new property variable
						}
						
					int indexfirstGraph   =  this.getGraphList().indexOf(BG);
					int indexSecondGraph  =  this.getGraphList().indexOf(g2);
					Template a            =  this.copyTemplate();
					/*for ( Entity e : a.getGraphList().get(indexfirstGraph).getEntityList() )
					{
					   e.settraverseFlag(true);
					}*/
					Set<OWLObjectProperty> ps=new HashSet();
					ps.addAll(PropertySet);
					a.mergGraph(indexSecondGraph,indexfirstGraph,e2,e1,Property, ps);
					GeneratedTemplates.add(a);
					FoundConnection       =  true;
					
					}
									
				}
		//	}
				
			}
			index++;
			
	} // end of while
	
	
	return GeneratedTemplates;
		
	}
	
	//#####################################
	// ----    addDataTypeTriple
	//#####################################
	
	
	public List<Template> addDataTypeTriple(String property) throws OWLOntologyCreationException
	{
		 OWLOntologyManager manager         =  OWLManager.createOWLOntologyManager();
		 IRI iri                            =  IRI.create(Constants.ontologyAddress);
		 OWLOntology DBpediaOWL             =  manager.loadOntologyFromOntologyDocument(iri);
		 OWLDataFactory dataFactory         =  manager.getOWLDataFactory();
		 IRI iri_property                   =  IRI.create(property);
		 OWLDataProperty OWLProperty        =  dataFactory.getOWLDataProperty(iri_property);
		 PropertyManagement PM              =  new PropertyManagement();
		 Set<OWLClass> Domain               =  PM.getDomainDataTypeProperty(OWLProperty, DBpediaOWL);
		 List<Template> GeneratedTemplates  =  new ArrayList();
		 Set<String> DomainP                =  new HashSet();
		 		
		 for(OWLClass n:Domain)
		 {
			 DomainP.add(n.getIRI().toString());
		 }
		
		 
		 // check whether a property is datatype or not
		 // if property is datatype, it adds a triple as e p ?l
		 if(PM.isDatatypeProperty(property, DBpediaOWL))
		 {
			
			 for ( Graph gSource : this.getGraphList() )
			 {
				 for ( Entity  esource : gSource.getEntityList() )
				 {
					 Set Including=new HashSet();
					 Including.addAll(esource.getClassTypeIRI());
					 Including.retainAll(DomainP);
					 
					 if ( Including.size() > 0 )
					 {
						 Template b  =  this.copyTemplate();
						 int index   =  this.getGraphList().indexOf(gSource);
						 Entity v    =   b.getGraphList().get(index).addLiteralEntity( b.getNextIndexLiteral());
						 b.getGraphList().get(index).addTriple(esource,property,v);
						 GeneratedTemplates.add(b);
					 }
				 }
			 }
		 }
		 		
		   return GeneratedTemplates;
		   
	     }

//#####################################
// ----    addObjectPropertyTriple
//#####################################
	
	public List<Template> addObjectPropertyTriple(String property) throws OWLOntologyCreationException
	{
		
		OWLOntologyManager manager         =  OWLManager.createOWLOntologyManager();
		 IRI iri                           =  IRI.create(Constants.ontologyAddress);
		 OWLOntology DBpediaOWL            =  manager.loadOntologyFromOntologyDocument(iri);
		 OWLDataFactory dataFactory        =  manager.getOWLDataFactory();
		 IRI iri_property                  =  IRI.create(property);
		 OWLObjectProperty OWLProperty     =  dataFactory.getOWLObjectProperty(iri_property);
		 PropertyManagement PM             =  new PropertyManagement();
		 Set<OWLClass> Domain              =  PM.getDomainSubClasses(OWLProperty, DBpediaOWL);
		 Set<OWLClass> Range               =  PM.getRangeSubClasses(OWLProperty, DBpediaOWL);
		 List<Template> GeneratedTemplates =  new ArrayList();
		
		 System.out.println( "Property" + property );
		 System.out.println( "Domain="  + Domain );
		 System.out.println( "Range="   + Range );
		 
		 Set<String> DomainP = new HashSet();
		 Set<String> RangeP  = new HashSet();
		 ClassManagement CM=new ClassManagement();
		 Set<String> ComprehensiveDomainP = new HashSet();
		 Set<String> ComprehensiveRangeP  = new HashSet();
		 
		 for(OWLClass n:Domain)
		 {
			 DomainP.add(n.getIRI().toString());
		 }
		 for(String c:DomainP)
		 {
			 ComprehensiveDomainP.add(c);
			 ComprehensiveDomainP.addAll(CM.getSuperClasses(c));		 
		 }
		 
		 
		 for(OWLClass n:Range)
		 {
			 RangeP.add(n.getIRI().toString());
		 }
		 for(String c:RangeP)
		 {
			 ComprehensiveRangeP.add(c);
			 ComprehensiveRangeP.addAll(CM.getSuperClasses(c));		 
		 }
		 
		 
		 System.out.println( "DomainP=" + DomainP);
		 System.out.println( "RangeP="  + RangeP);
		 
		 boolean foundAnchorPoint = false;
		 
		 for ( Graph gSource : this.getGraphList() )
		 {
			 for ( Entity  esource : gSource.getEntityList() )
			 {
				 System.out.println("entity...."+esource.getIRI());
				 System.out.println("entity Type...."+esource.getClassTypeIRI());
				
				boolean DomainIncluded    =  false; 
				boolean foundSinkEntity   =  false;
				boolean RangeIncluded     =  false;
				boolean foundSourceEntity =  false;
				
				 // add triple with the template: e p ?v
				 Set Including            =  new HashSet();
				 Including.addAll(esource.getClassTypeIRI());
				 Including.retainAll(DomainP);
				
				 System.out.println("Including  Domain="+Including);
				 System.out.println("entity Type...."+esource.getClassTypeIRI());			 
				 
				 Set IncludingRange       =  new HashSet();
				 IncludingRange.addAll(esource.getClassTypeIRI());
				 IncludingRange.retainAll(RangeP);
				 System.out.println("Including  Range="+IncludingRange);
				 
				 if ( Including.size() > 0 )
				 {
					 
					 
					 DomainIncluded       =  true;
					 foundAnchorPoint     =  true;
					 // check whether a sink entity for this source can be found
					 boolean FoundEntityInNeghbours =  false;
					 
					 for ( Graph  gSink : this.getGraphList() )
					 {
						 
						 if ( ! gSink.equals(gSource) )
						 {
						 	 for ( Entity esink:gSink.getEntityList() )
							 {	
						 		 if ( ! esink.equals(esource)  &&  (esource.gettype() == Constants.TYPE_INSTANCE || esource.gettype()== Constants.TYPE_VARIABLE )  && ( esink.gettype() == Constants.TYPE_INSTANCE || esink.gettype()==Constants.TYPE_VARIABLE))
						 		 {
						 		 Set includingSink  =  new HashSet();
								 includingSink.addAll(esink.getClassTypeIRI());
								 includingSink.retainAll(RangeP);
								 
								 // add triple eSource property eSink
								 if( includingSink.size() > 0 )
								 {
									 foundSinkEntity         =  true;
									 FoundEntityInNeghbours  =  true;
									 Template b              =  this.copyTemplate();
									 int indexgraphSource    =  this.getGraphList().indexOf(gSource);
									 int indexgraphSink      =  this.getGraphList().indexOf(gSink);
									 int indexentitysource   =  this.getGraphList().get(indexgraphSource).getEntityList().indexOf(esource);
									 int indexentitySink     =  this.getGraphList().get(indexgraphSink).getEntityList().indexOf(esink);
									 Set<OWLObjectProperty> PropertySet =  new HashSet();
									 if(!gSource.equals(gSink))
									 {
										 b.mergGraph(indexgraphSource, indexgraphSink, esource, esink, property, PropertySet );	 
									 }
									 else
									 {
										 b.getGraphList().get(indexgraphSource).addTriple(this.getGraphList().get(indexgraphSource).getEntityList().get(indexentitysource), property,
										 this.getGraphList().get(indexgraphSink).getEntityList().get(indexentitySink));
										 	
									 }
									 
									 GeneratedTemplates.add(b);
									    
									 
									//}
					//-------------------------------------------------
									 
								 }
						 		 
						 		 }
							 }
					 }
					 }// end of for
					 
					 // check that is there any sink entity in the same graph 
					 if( ! FoundEntityInNeghbours)
					 {
						 
						 for(Entity esink:gSource.getEntityList())
						 {	
							 
					 		// if(!esink.equals(esource) && !(esource.gettype()==1 && esink.gettype()==1))
							 if (  !esink.equals(esource)  &&  (esource.gettype() == Constants.TYPE_INSTANCE || esource.gettype()== Constants.TYPE_VARIABLE )  && ( esink.gettype() == Constants.TYPE_INSTANCE || esink.gettype()==Constants.TYPE_VARIABLE))
							 {
					 		 Set includingSink   =  new HashSet();
							 includingSink.addAll(esink.getClassTypeIRI());
							 includingSink.retainAll(RangeP);
							 
							 // add triple eSource property eSink
							 if( includingSink.size()>0 )
							 {
								 foundSinkEntity       =  true;
								 Template b            =  this.copyTemplate();
								 int indexgraphSource  =  this.getGraphList().indexOf(gSource);
								 int indexgraphSink    =  this.getGraphList().indexOf(gSource);
								 int indexentitysource =  this.getGraphList().get(indexgraphSource).getEntityList().indexOf(esource);
								 int indexentitySink   =  this.getGraphList().get(indexgraphSink).getEntityList().indexOf(esink);
																
								 b.getGraphList().get(indexgraphSource).addTriple(this.getGraphList().get(indexgraphSource).getEntityList().get(indexentitysource), property,
								 this.getGraphList().get(indexgraphSink).getEntityList().get(indexentitySink));
									 							 
								 GeneratedTemplates.add(b);
								    
							
							 }
					 		 
					 		 }
						 } 
					 }
					 /// --------------------
					 
					 if(!foundSinkEntity)
					 {
						 
						 // if there was not any sink, it tries to define a new variable as 
						 // its sink as eSource property ?v
						 Template b            =  new Template();
						 b                     =  this.copyTemplate();
						 int indexgraphSource  =  this.getGraphList().indexOf(gSource);
						 int indexEntitySource =  this.getGraphList().get(indexgraphSource).getEntityList().indexOf(esource);
						 b.getGraphList().get(indexgraphSource).getEntityList().get(indexEntitySource).setClassTypeIRI(Including);
						 Entity v              =  b.getGraphList().get(indexgraphSource).addVariableEntity(ComprehensiveRangeP, b.getNextIndexVariable());
						 b.getGraphList().get(indexgraphSource).addTriple(b.getGraphList().get(indexgraphSource).getEntityList().get(indexEntitySource), property, v);
						 GeneratedTemplates.add(b);
						 
						 
					 }
				 }// end of if source including	 		 
					
					 if(IncludingRange.size()>0)
					 {
						 RangeIncluded    = true;
						 foundAnchorPoint = true;
						 
						// System.out.println("chech whether esource is in range: including"+IncludingRange);
					    // check out whether there is a source for the corresppoinding entity as source
						
						 
						 
						 for(Graph gSink:this.getGraphList())
						 {
							 	 for(Entity esink:gSink.getEntityList())
								 {	
							 		 if ( ! esink.equals(esource) )
							 		 {
							 		Set SourceSet=new HashSet();
							 		SourceSet.addAll(esink.getClassTypeIRI());
							 		SourceSet.retainAll(DomainP);
									 
									 // add triple eSource property eSink
									 if ( SourceSet.size() > 0 )
									 {
										 foundSourceEntity=true;
									 }
							 		 }
								 }
						 }
						 
						 // add variable as ?v property eSink
						 if(!foundSourceEntity)
						 {
							 
							 Template b             =  this.copyTemplate();
							 int indexgraphSource   =  this.getGraphList().indexOf(gSource);
							 int indexEntitySource  =  this.getGraphList().get(indexgraphSource).getEntityList().indexOf(esource);
														
							 b.getGraphList().get(indexgraphSource).getEntityList().get(indexEntitySource).setClassTypeIRI(IncludingRange);
							 Entity v=b.getGraphList().get(indexgraphSource).addVariableEntity(ComprehensiveDomainP, b.getNextIndexVariable());
							 b.getGraphList().get(indexgraphSource).addTriple(v, property, b.getGraphList().get(indexgraphSource).getEntityList().get(indexEntitySource));
							 GeneratedTemplates.add(b);
							 
						 }
						 
						
						 
						 
					 } // end of if range
						 		 
					 
						 		 
							 } // end of for eSource
						 	 
					 } // end of for gSource
					 
					// add triple if foundAnchorPoint=false
		 if(!foundAnchorPoint)
		 {
			 
			 Template b  =  this.copyTemplate();
			 Graph g     =  new Graph(); 
			 Entity v1   =  g.addVariableEntity(ComprehensiveDomainP, b.getNextIndexVariable());
			 Entity v3   =  g.addVariableEntity(ComprehensiveRangeP, b.getNextIndexVariable());
			 g.addTriple(v1, property, v3);
			 b.addGraph(g);
			 GeneratedTemplates.add(b); 
			 
		 }
				 
			
		
		 return GeneratedTemplates;
	}
	
}
