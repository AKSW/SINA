package Model.GraphBuilding;

import java.util.HashSet;

import org.semanticweb.owlapi.model.OWLOntology;

import Model.Constants;

import com.clarkparsia.pellet.owlapiv3.PelletReasoner;
import com.clarkparsia.pellet.owlapiv3.PelletReasonerFactory;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.sparql.engine.http.QueryEngineHTTP;

public class DomainRangeInference {
	
	//###########################################################
	// --  This function finds domain of a property by leveraging inference over the knowledge base
	//###########################################################
	
	public HashSet<String> getDomainInferenceOverKB(String property)
	{
		
		HashSet<String> DomainSet	=	new HashSet();
				
		if(property.equals("http://xmlns.com/foaf/0.1/homepage"))
		{
			DomainSet.add("http://www.w3.org/2002/07/owl#Thing");
			DomainSet.add("owl:Thing");
			return DomainSet;
		}
		
		String querystring=" SELECT distinct ?class WHERE { ?s  <"+ property + "> ?o. " +
							 "?s a ?class. FILTER regex(?class, \"^http://dbpedia.org/ontology/\")   }  ";
		//FILTER regex(?class, \"^http://dbpedia.org/ontology/\") 
		Query query=QueryFactory.create(querystring);
		//QueryEngineHTTP qexec = new QueryEngineHTTP("http://db0.aksw.org:8895/sparql", query);	
		QueryEngineHTTP qexec = new QueryEngineHTTP(Constants.SPARQLEndPoint, query);	
		
					
		try{
			ResultSet results 	= 	qexec.execSelect();
			while(results.hasNext()){
    	
			QuerySolution  solution=results.nextSolution();
			String ClassName	=	solution.get("class").toString();
			DomainSet.add(ClassName);
    	        	       	
			}//end of second while
 
		}
		catch(Exception e) {
		
		System.out.println("finish by exeption" +e.getMessage());
	
		}
		finally{//qexec.close();
		}
		
		return DomainSet;
		
	}
	
	//###########################################################
	// --  This function finds range of a property by leveraging inference over the knowledge base
	//###########################################################
		
	public HashSet<String> getRangeInferenceOverKB(String property)
	{
		HashSet<String> RangeSet	=	new HashSet();
		if(property.equals("http://xmlns.com/foaf/0.1/homepage"))
		{
			//RangeSet.add("http://www.w3.org/2002/07/owl#Thing");
			RangeSet.add("owl:Thing");
			return RangeSet;
		}
		String querystring=" SELECT distinct ?class WHERE { ?s  <"+ property + "> ?o. " +
							 "?o a ?class.  FILTER regex(?class, \"^http://dbpedia.org/ontology/\")   }  ";
		//FILTER regex(?class, \"^http://dbpedia.org/ontology/\")
		Query query=QueryFactory.create(querystring);
		//QueryEngineHTTP qexec = new QueryEngineHTTP("http://db0.aksw.org:8895/sparql", query);	
		QueryEngineHTTP qexec = new QueryEngineHTTP(Constants.SPARQLEndPoint, query);	
					
		try{
			ResultSet results = qexec.execSelect();
			
			while(results.hasNext()){
    		QuerySolution  solution=results.nextSolution();
    		String ClassName=solution.get("class").toString();
    		RangeSet.add(ClassName);}//end of second while
 
			}
		catch(Exception e) {
			System.out.println("finish by exeption" +e.getMessage());
			}
		finally{
			qexec.close();
		}
		
		return RangeSet;
		
	}
//@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
}
