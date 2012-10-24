package HMMQuerySegmentation;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.sparql.engine.http.QueryEngineHTTP;


public class EntityRetrieval {
	
	
	///################################################
	//###########  retrieval of Entities ##############
	//#################################################
	private double thresholdtheta 		=  0.7;
	
	public List<ResourceInfo> getIRIsForPatterns(String Pattern, ArrayList Segment) {
		
		List<ResourceInfo> foundRessources = new ArrayList<ResourceInfo>();
		
		 String querystring;
		 
		// querystring = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>  SELECT distinct * WHERE " +
		//  "{  ?iri rdfs:label ?label.   ?label  bif:contains  \"" + queryLabel  + "\"  " +  " FILTER( langMatches(lang(?label), \"en\")). "+
		//  " OPTIONAL {?iri  <http://dbpedia.org/ontology/wikiPageRedirects> ?mainpage .} }";
		String option=" OPTIONAL {?iri  <http://dbpedia.org/ontology/wikiPageRedirects> ?mainpage . ?mainpage rdfs:label ?mLabel. FILTER( langMatches(lang(?mLabel), \"en\")).} ";
		 querystring = " PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> " +
			" SELECT distinct * WHERE { ?iri rdfs:label ?label.   ?label  bif:contains  \"" + Pattern  + "\"  " + 
					" FILTER( langMatches(lang(?label), \"en\")). "+ option +  "   }  "; //limit 10 " FILTER regex(str(?iri), \"http://dbpedia.org/resource/\")
		
		System.out.println("-------retrieved query is:"+querystring);
		//" where Label like '**"+originalKeyword+"'" ;//+" and length(Label)<= 4*length('"+originalKeyword+ "')"  ;
		String urlsever = Constants.urlsever;;
		//Query query=QueryFactory.create(querystring);
		QueryEngineHTTP qexec = new QueryEngineHTTP(urlsever, querystring);	
//		QueryExecution qexec = QueryExecutionFactory.sparqlService(urlsever,query);
		qexec.addDefaultGraph(Constants.DefaultGraphString);
		Set<String> setiri=new TreeSet<String>();
						
		try{
	    ResultSet results = qexec.execSelect();
	    while(results.hasNext()){
	    	
	    	QuerySolution  solution=results.nextSolution();
	    	
	    	
	    	ResourceInfo object = new ResourceInfo();
			String URIName = null, Label = null, mLabel=null;
			if(solution.get("mainpage") != null)
			{
			URIName = solution.get("mainpage").toString();
			Label = solution.get("mLabel").toString();
			}
			else
			{
				URIName = solution.get("iri").toString();
				Label = solution.get("label").toString();	
			}
			
			if(URIName.startsWith("http://dbpedia.org/property/"))
			{
				object.setType(Constants.TYPE_PROPERTY);
			}
			else if(URIName.startsWith("http://dbpedia.org/resource/"))
			{
				
				object.setType(Constants.TYPE_INSTANCE);
			}
			
			
			/*if (solution.get("mainpage") != null)
			{
				
				if (!solution.get("mainpage").toString().isEmpty())
			{
					URIName = solution.get("mainpage").toString();
					mLabel = solution.get("mLabel").toString();
			}
			}*/
			
			object.setUri(URIName);
			if(mLabel == null)
			{
			Label = solution.get("label").toString();
			}
			else
			{
			Label = mLabel;
			}
			
		
			if (Label.endsWith("@en")) {
			Label = Label.replace("@en", "");}
			object.setLabel(Label);
			
			if (Label.endsWith("\"")) {
				Label = Label.replace("\"", "");}
				object.setLabel(Label);
				
				if (Label.startsWith("\"")) {
					Label = Label.replace("\"", "");}
				
					object.setLabel(Label.trim());
					
					//foundRessources.add(object);
			if(! URIName.startsWith("http://dbpedia.org/ontology/") && !setiri.contains(URIName))
			{
			foundRessources.add(object);
			setiri.add(URIName);
					
			}
	            	       	
	    }//end of second while
	 
		}
		catch(Exception e) {
			
			System.out.println("finish by exeption" +e.getMessage());
			
		}
		finally{
		}
		
		
		Functionality f = new Functionality();
		PreProcessing p = new PreProcessing();
		
		ArrayList LabelList;
		
		for(ResourceInfo resourceInfo: foundRessources) 
		{
			if(resourceInfo.getUri().startsWith("http://dbpedia.org/resource")){
			LabelList = p.removeStopWordswithStemming(resourceInfo.getLabel());
			resourceInfo.setStringSimilarityScore(f.JaccardLevenstienSimilarity(Segment, LabelList , resourceInfo.getLabel()));
			}
			else if(resourceInfo.getUri().startsWith("http://dbpedia.org/property"))
			{
				LabelList = p.removeStopWordswithlem(resourceInfo.getLabel());
				resourceInfo.setStringSimilarityScore(f.JaccardLevenstienSimilarity(Segment, LabelList , resourceInfo.getLabel()));
					
			}
			//resourceInfo.setStringSimilarityScore( f.SimilarityString(Term.trim() , resourceInfo.getLabel().trim(), minimumlength, maximumlength) );
			//System.out.println("  Term  " + Term + "  Label "+ resourceInfo.getLabel() + " score " + resourceInfo.getStringSimilarityScore());
			
		} 
		List<ResourceInfo> finalfoundResources = new ArrayList<ResourceInfo>();
		for(ResourceInfo resourceInfo: foundRessources) 
		{
			if(resourceInfo.getStringSimilarityScore() >=  thresholdtheta )
			{
				finalfoundResources.add(resourceInfo);
			}
		
		}
		// sort the final list
		Collections.sort(finalfoundResources, new StringSimilarityComparator());
		// cut the final list if it is so big
		if (finalfoundResources.size() > Constants.LimitOfList)
		{
			finalfoundResources=finalfoundResources.subList(0, Constants.LimitOfList);
		}
		// set the associating class of final resources
		 /*for(ResourceInfo resourceInfo: finalfoundResources) 
			{
			 if(resourceInfo.getType() == Constants.TYPE_INSTANCE)
			   resourceInfo.setClassSet(getClassofInstance(resourceInfo.getUri()));
			} */ 
		
		
		 return finalfoundResources;
		//return foundRessources;
	}

	

	///################################################ ##############
	//###########  computing lenght of path between 2 entities ##############
	//################################################# ##############
	
	public int getDistance(ResourceInfo e1, ResourceInfo e2)
	{
		
		 String  query1		= "";
		 String  query2		= "";
		 String uery3		= "";
		
		int Firstdistance 	= Constants.MaximumDistance;
		int SecodDistance	= Constants.MaximumDistance;
		
		if (e1.getType() == Constants.TYPE_CLASS && e2.getType()== Constants.TYPE_CLASS)
		{
			// this query compute lenght = 6
			query1 = " ask WHERE {  {?v1 a <"+ e1.getUri() +  ">.  ?v2 a <"+ e2.getUri() + ">. ?v1 ?p1 ?v2.} union {?v1 a <"+
			e2.getUri()+  ">. ?v2 a <"+ e1.getUri() + ">. ?v2 ?p1 ?v1. } }  "; 
			
			query2 = " ask WHERE { ?v1 a <"+ e1.getUri() +  ">.  ?v2 a <"+ e2.getUri() + ">. { ?v1 ?p1  ?x. ?x ?p2 ?v2.} union {"+
			" ?v2 ?p2  ?x. ?x ?p1 ?v1.} union { ?v1 ?p1 ?x. ?v2 ?p2 ?x. } "+
			" union {?x ?p1 ?v1. ?x ?p2 ?v2 .} } "; 
			
			
			Firstdistance   = 2;
			SecodDistance	= 4;
		}
		
		else if (e1.getType() == Constants.TYPE_INSTANCE && e2.getType()== Constants.TYPE_INSTANCE)
		{
			// this query compute lenght = 2
			query1 = " ask WHERE {  {<"+ e1.getUri() +  "> ?p1 <"+ e2.getUri() + ">.} union {<"+
			e2.getUri()+  "> ?p2 <"+ e1.getUri() + ">. } }  "; 
			
			// this query compute lenght = 4
			query2 = " ask WHERE {  {<"+ e1.getUri() +  ">  ?p1  ?x. ?x ?p2 <"+ e2.getUri() + ">.} union {<"+
			e2.getUri()+  "> ?p2  ?x. ?x ?p1 <"+ e1.getUri() + ">.} union {<"+ e1.getUri()+"> ?p1 ?x. <"+ e2.getUri()+"> ?p2 ?x. } "+
			" union {?x ?p1 <"+ e1.getUri()+">. ?x ?p2 <"+ e2.getUri()+"> .} } "; 
			
			Firstdistance  = 2;
			SecodDistance  = 4;
			
		}
		
		else if (e1.getType() == Constants.TYPE_PROPERTY && e2.getType()== Constants.TYPE_PROPERTY)
		{
			Firstdistance  = 2;
			
			// this query compute lenght = 2
			query1 = " ask WHERE {  {?x <"+ e1.getUri() +  ">  ?y. ?y  <"+ e2.getUri() + "> ?z.} union {?z <"+
			e2.getUri()+  "> ?y. ?y <"+ e1.getUri() + "> ?x.} union {?x <"+ e1.getUri()+">  ?y. ?z <"+ e2.getUri()+"> ?y. } "+
			" union {?y <"+ e1.getUri()+"> ?x. ?y <"+ e2.getUri()+"> ?z. } } "; 
			
			/*// this query compute lenght = 3
			query2 = " ask WHERE {  {?x <"+ e1.getUri() +  ">  ?y. ?y ?p ?s. ?s  <"+ e2.getUri() + "> ?z.} union {?z <"+
			e2.getUri()+  "> ?y. ?y <"+ e1.getUri() + "> ?x. union {?x <"+ e1.getUri()+">  ?y. ?z <"+ e2.getUri()+"> ?y. } "+
			" union {?y <"+ e1.getUri()+"> ?x. ?y <"+ e2.getUri()+"> ?z. } ";*/ 
			
		}
		
		else if (e1.getType() == Constants.TYPE_CLASS && e2.getType()== Constants.TYPE_PROPERTY)
		{
			Firstdistance  = 1;
			SecodDistance  = 3;
			
			// this query compute lenght = 3
			query1 = " ask WHERE {  {?v a <"+ e1.getUri() +  ">. ?v  <"+ e2.getUri() + "> ?x.} union { ?x <"+
			e2.getUri()+  "> ?v. ?v a <"+ e1.getUri() + ">.} } "; 
			
			// this query compute lenght = 5
			query2 = " ask WHERE {  {?v a <"+ e1.getUri() +  ">. ?v ?p1 ?x. ?x  <"+ e2.getUri() + "> ?y.} union {?y <"+
			e2.getUri()+  ">  ?x. ?x ?p1 ?v. ?v a <"+ e1.getUri() + ">.} union {?v a <"+ e1.getUri()+">. ?v ?p1 ?x. ?y <"+ e2.getUri()+"> ?x. } "+
			" union {?x ?p1 ?v. ?v a<"+ e1.getUri()+">. ?x <"+ e2.getUri()+"> ?y.} } ";
			
		}
		
		else if (e1.getType() == Constants.TYPE_PROPERTY && e2.getType()== Constants.TYPE_CLASS)
		{
			Firstdistance  = 1;
			SecodDistance  = 3;
			
			// this query compute lenght = 3
			query1 = " ask WHERE {  {?v a <"+ e2.getUri() +  ">. ?v  <"+ e1.getUri() + "> ?x.} union { ?x <"+
			e1.getUri()+  "> ?v. ?v a <"+ e2.getUri() + ">.} } "; 
			
			// this query compute lenght = 5
			query2 = " ask WHERE {  {?v a <"+ e2.getUri() +  ">. ?v ?p1 ?x. ?x  <"+ e1.getUri() + "> ?y.} union {?y <"+
			e1.getUri()+  ">  ?x. ?x ?p1 ?v. ?v a <"+ e2.getUri() + ">. } union {?v a <"+ e2.getUri()+">. ?v ?p1 ?x. ?y <"+ e1.getUri()+"> ?x. } "+
			" union {?x ?p1 ?v. ?v a<"+ e2.getUri()+">. ?x <"+ e1.getUri()+"> ?y.} } ";
			
			
		}
		
		else if (e1.getType() == Constants.TYPE_INSTANCE && e2.getType()== Constants.TYPE_CLASS)
		{
			Firstdistance  = 2;
			SecodDistance  = 4;
			
			// this query compute lenght = 4
			query1 = " ask WHERE {  {?v a <"+ e2.getUri() +  ">. ?v ?p1 <"+ e1.getUri() + ">.} union { <"+
			e1.getUri()+  "> ?p1 ?v. ?v a <"+ e2.getUri() + ">.}  }"; 
			
			// this query compute lenght = 6
			query2 = " ask WHERE {  {?v a <"+ e2.getUri() +  ">. ?v ?p1 ?x. ?x ?p2 <"+ e1.getUri() + ">.} union {<"+
			e1.getUri()+  "> ?p2  ?x. ?x ?p1 ?v. ?v a <"+ e2.getUri() + ">.} union {?v a <"+ e2.getUri()+">. ?v ?p1 ?x. <"+ e1.getUri()+"> ?p2 ?x. } "+
			" union {?x ?p1 ?v. ?v a<"+ e2.getUri()+">. ?x ?p2 <"+ e1.getUri()+"> . } } ";
			
		}
		
		else if (e1.getType() == Constants.TYPE_CLASS && e2.getType()== Constants.TYPE_INSTANCE)
		{
			Firstdistance  = 2;
			SecodDistance  = 4;
			
			// this query compute lenght = 4
			query1 = " ask WHERE {  {?v a <"+ e1.getUri() +  ">. ?v ?p1 <"+ e2.getUri() + ">.} union { <"+
			e2.getUri()+  "> ?p1 ?v. ?v a <"+ e1.getUri() + ">.} } "; 
			
			// this query compute lenght = 6
			query2 = " ask WHERE {  {?v a <"+ e1.getUri() +  ">. ?v ?p1 ?x. ?x ?p2 <"+ e2.getUri() + ">.} union {<"+
			e2.getUri()+  "> ?p2  ?x. ?x ?p1 ?v. ?v a <"+ e1.getUri() + ">.} union {?v a <"+ e1.getUri()+">. ?v ?p1 ?x. <"+ e2.getUri()+"> ?p2 ?x. } "+
			" union {?x ?p1 ?v. ?v a<"+ e1.getUri()+">. ?x ?p2 <"+ e2.getUri()+"> . } } "; 
		}
		
		else if (e1.getType() == Constants.TYPE_INSTANCE && e2.getType()== Constants.TYPE_PROPERTY)
		{
			Firstdistance  = 1;
			SecodDistance  = 3;
			// this query compute lenght = 3
			query1 = " ask WHERE {  { <"+ e1.getUri() +  ">  <"+ e2.getUri() + "> ?x.} union { ?x <"+
			e2.getUri()+  ">  <"+ e1.getUri() + ">.} } "; 
			
			// this query compute lenght = 5
			query2 = " ask WHERE {  { <"+ e1.getUri() +  ">  ?p1 ?x. ?x  <"+ e2.getUri() + "> ?y.} union {?y <"+
			e2.getUri()+  ">  ?x. ?x ?p1  <"+ e1.getUri() + ">.} union { <"+ e1.getUri()+"> ?p1 ?x. ?y <"+ e2.getUri()+"> ?x. } "+
			" union {?x ?p1 <"+ e1.getUri()+">. ?x <"+ e2.getUri()+"> ?y.} } ";
			
		}
		
		else if (e1.getType() == Constants.TYPE_PROPERTY && e2.getType()== Constants.TYPE_INSTANCE)
		{
			Firstdistance  = 1;
			SecodDistance  = 3;
			// this query compute lenght = 3
			query1 = " ask WHERE {  { <"+ e2.getUri() +  ">  <"+ e1.getUri() + "> ?x.} union { ?x <"+
			e1.getUri()+  ">  <"+ e2.getUri() + ">.} } "; 
			
			// this query compute lenght = 5
			query2 = " ask WHERE {  { <"+ e2.getUri() +  "> ?p1 ?x. ?x  <"+ e1.getUri() + "> ?y.} union {?y <"+
			e1.getUri()+  ">  ?x. ?x ?p1  <"+ e2.getUri() + ">.} union { <"+ e2.getUri()+"> ?p1 ?x. ?y <"+ e1.getUri()+"> ?x. } "+
			" union {?x ?p1 <"+ e2.getUri()+">. ?x <"+ e1.getUri()+"> ?y.} } ";
			
		}
		
		
		
		//System.out.println("-------retrieved query is:"+query1);
		//System.out.println("-------retrieved query is:"+query2);
		String urlsever = Constants.urlsever;;
		//Query query=QueryFactory.create(querystring);
		QueryEngineHTTP qexec = new QueryEngineHTTP(urlsever, query1);	
//		QueryExecution qexec = QueryExecutionFactory.sparqlService(urlsever,query);
		qexec.addDefaultGraph(Constants.DefaultGraphString);
		int Distance = Constants.MaximumDistance;		
		boolean result = false;
		try{
			result = qexec.execAsk() ;
			
			if ( result )
			{
				Distance = Firstdistance;
			//	System.out.println("in first neighborhood found");
			}
			else if (!(e1.getType() == Constants.TYPE_PROPERTY && e2.getType()== Constants.TYPE_PROPERTY))
			{
				qexec	 = new QueryEngineHTTP(urlsever, query2);	
				qexec.addDefaultGraph(Constants.DefaultGraphString);
				result	 = qexec.execAsk() ;
				if(result)
				{
				Distance = SecodDistance; 
			//	System.out.println("in second neighborhood found");
				}
				else
				{
				//	System.out.println("nothing found");
				}
				
				
				
			}
			
			
			
	  		}
		catch(Exception e) {
			
		System.out.println("finish by exeption" +e.getMessage());
			}
	finally{
		}
	

		return Distance;
		
	}
	
	
	//####################################################################################
	
public boolean checkExistenceOfPatterns(String Pattern) {
		
		List<ResourceInfo> foundRessources = new ArrayList<ResourceInfo>();
		
		String querystring;
		querystring = " PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> " +
			" ask WHERE { ?iri rdfs:label ?label.   ?label  bif:contains  \"" + Pattern  + "\"  " + 
					" FILTER( langMatches(lang(?label), \"en\")).  } ";
		
		System.out.println("-------retrieved query is:"+querystring);
		String urlsever = Constants.urlsever;;
		//Query query=QueryFactory.create(querystring);
		QueryEngineHTTP qexec = new QueryEngineHTTP(urlsever, querystring);	
//		QueryExecution qexec = QueryExecutionFactory.sparqlService(urlsever,query);
		qexec.addDefaultGraph(Constants.DefaultGraphString);
				
		boolean result = false;
		try{
			result = qexec.execAsk() ;
	  		}
		catch(Exception e) {
			
		System.out.println("finish by exeption" +e.getMessage());
			}
		finally{
		}
		return result;
}
	

}
