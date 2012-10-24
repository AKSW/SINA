package Model.IRIMapping;

//import ResourceInfo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import Model.Constants;
import Model.TermExtraction.NEKeywordStructure;
//import SINA.sina.TermExtraction.NEMapping;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.sparql.engine.http.QueryEngineHTTP;

/**
 * @author Saedeeh Shekarpour
 */
public class UriRetrieval {
	
	private String defaultGraphString = "http://dbpedia.org"; 
	
	/**
	 * 
	 * @param keyword
	 * @return
	 */
//############################################################################
	
	
public List<ResourceInfo> getIRIsForNE(NEKeywordStructure ne) {
		
		List<ResourceInfo> foundResources = new ArrayList<ResourceInfo>();
		String queryLabel="";
		String typecheking;
		NEMapping nem=new NEMapping();
		 nem.initializeMappingSet();
		 
		 String[] splitArray = ne.getKeyword().trim().split(" ");
		 for(int i=0; i< splitArray.length ; i++)
		 {
			 if( i == 0  ){
			 queryLabel = queryLabel  + splitArray[i];}
			 else
			 {
			 queryLabel = queryLabel + " and " + splitArray[i];
			 }
		 }
		
		 String querystring; 
		 
		 
		if (ne.getNE().equals("Facility"))
		{
			int firstindex=nem.getNESet().indexOf("Facility");
			int lastindex=nem.getNESet().lastIndexOf("Facility");
			
			typecheking="{?iri a <"+nem.getUriSet().get(firstindex)+">.} UNION {?iri a  <"+nem.getUriSet().get(lastindex) +">.}";
			
			
			querystring=" PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> " +
			" SELECT distinct * WHERE { ?iri rdfs:label ?label."+ typecheking + ".  ?label  bif:contains  \"" + queryLabel  + "\"  " + 
			" FILTER( langMatches(lang(?label), \"en\")). } ";
		}
		else if(ne.getNE().equals("IndustryTerm"))
		{
			typecheking="?s  <"+ne.geturi()+"> ?iri. ";
			
			
			 splitArray = ne.getKeyword().trim().split(" ");
			 queryLabel="";
			 for(int i=0; i< splitArray.length ; i++)
			 {
				 if( i == 0  ){
				 queryLabel = queryLabel  + splitArray[i];}
				 else
				 {
				 queryLabel = queryLabel + " or " + splitArray[i];
				 }
			 }
			
			 String option=" OPTIONAL {?iri  <http://dbpedia.org/ontology/wikiPageRedirects> ?mainpage.   ?mainpage rdfs:label ?mLabel. FILTER( langMatches(lang(?mLabel), \"en\")).} ";
			querystring=" PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> " +
			" SELECT distinct ?iri, ?label  WHERE { ?iri rdfs:label ?label.  "+ typecheking + "  ?label  bif:contains  \"" + queryLabel  + "\"  " + 
			" FILTER( langMatches(lang(?label), \"en\")). " + option + "}";			
		}
		
		else if(ne.getNE().equals("Position"))
		{
			ResourceInfo object = new ResourceInfo();
			String URIName = ne.geturi();
			object.setUri(URIName);
			
			String Label = "Person";
			object.setType(Constants.TYPE_CLASS);
			foundResources.add(object);
			
			return foundResources;
			
		}
		
		
		else{
			 String option=" OPTIONAL {?s  <http://dbpedia.org/ontology/wikiPageRedirects> ?mainpage. ?mainpage rdfs:label ?mLabel. FILTER( langMatches(lang(?mLabel), \"en\")).} ";
			 querystring = " PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> " +
			" SELECT distinct * WHERE { ?iri rdfs:label ?label. ?iri a <" + ne.geturi() + ">.  ?label  bif:contains  \"" + queryLabel  + "\"  " + 
					" FILTER( langMatches(lang(?label), \"en\")). " +option + " }";
	
			
		}
		
		
		
		//IndustryTerm
		
		
		System.out.println("-------retrieved query is:"+querystring);
		//" where Label like '**"+originalKeyword+"'" ;//+" and length(Label)<= 4*length('"+originalKeyword+ "')"  ;
		String urlsever = Constants.urlsever;
		//Query query=QueryFactory.create(querystring);
		
		//querystring = querystring + "  limit 1000 ";
		QueryEngineHTTP qexec = new QueryEngineHTTP(urlsever, querystring);	
	//	QueryExecution qexec = QueryExecutionFactory.sparqlService(urlsever,query);
		qexec.addDefaultGraph(Constants.DefaultGraphString);
		int minimumlength=100;
		int maximumlength=0;
						
		try{
        ResultSet results = qexec.execSelect();
        while(results.hasNext()){
        	
        	QuerySolution  solution=results.nextSolution();
        	
        	ResourceInfo object = new ResourceInfo();
			String URIName = null, Label = null , mLabel=null;
			URIName = solution.get("iri").toString();
			if(URIName.startsWith("http://dbpedia.org/property/"))
			{
				object.setType(Constants.TYPE_PROPERTY);
			}
			else if(URIName.startsWith("http://dbpedia.org/resource/"))
			{
				
				object.setType(Constants.TYPE_INSTANCE);
				HashSet c = new HashSet();
				c.add(ne.geturi());
				object.setClassSet(c);
			}
			
			
			if (solution.get("mainpage") != null)
			{
				
				if (!solution.get("mainpage").toString().isEmpty())
			{
					URIName = solution.get("mainpage").toString();
					mLabel = solution.get("mLabel").toString();
				
			}
			}
			
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
				Label = Label.replace("\"", " ");}
				object.setLabel(Label);
				
				if (Label.startsWith("\"")) {
					Label = Label.replace("\"", " ");}
					object.setLabel(Label);
			
			
			 HashSet<String> ClassSet =new HashSet();
			 
			 
			
			
			 if(! ne.getNE().equals("IndustryTerm"))
			{
			ClassSet.add(ne.geturi());
			}
			
			object.setClassSet(ClassSet);
			
			foundResources.add(object);
                	       	
        }//end of second while
     
		}
		catch(Exception e) {
			
			System.out.println("finish by exeption" +e.getMessage());
			
		}
		finally{
		}
		
		
		Functionality f = new Functionality();
		
		for(ResourceInfo resourceInfo: foundResources) 
		{
			resourceInfo.setStringSimilarityScore( f.SimilarityString(ne.getKeyword() , resourceInfo.getLabel(), minimumlength, maximumlength) );
			
			} 
		List<ResourceInfo> finalfoundRessources = new ArrayList<ResourceInfo>();
		for(ResourceInfo resourceInfo: foundResources) 
		{
			if(resourceInfo.getStringSimilarityScore() >= 0.6)
			{
				finalfoundRessources.add(resourceInfo);
				
			}
		
		} 
		
		// sort the final list 
		Collections.sort(finalfoundRessources, new StringSimilarityComparator());
		// select the top resources in the final list
		if (finalfoundRessources.size() > Constants.LimitOfList)
		{
			finalfoundRessources=finalfoundRessources.subList(0, Constants.LimitOfList);
		}
	   if(ne.getNE().equals("IndustryTerm"))
	   {
		   for(ResourceInfo resourceInfo: finalfoundRessources) 
			{
			   resourceInfo.setClassSet(getClassofInstance(resourceInfo.getUri()));
			}  
	   }
	   
	   if (ne.getNE().equals("Facility")){
			//	int firstindex=nem.getNESet().indexOf("Facility");
			//	int lastindex=nem.getNESet().lastIndexOf("Facility");
				
			//	ClassSet.add(nem.getUriSet().get(firstindex));
			//	ClassSet.add(nem.getUriSet().get(lastindex));
		   for(ResourceInfo resourceInfo: finalfoundRessources) 
			{
				resourceInfo.setClassSet(getClassofInstance(resourceInfo.getUri()));
				
			}	
			}
	  
		
		return finalfoundRessources;
	}
	
	
	
	
 //###########################################################################

public List<ResourceInfo> getIRIsTerms(String Term) {
	
	List<ResourceInfo> foundRessources = new ArrayList<ResourceInfo>();
	String queryLabel="";
	
	 
	 String[] splitArray = Term.trim().split(" ");
	 for(int i=0; i< splitArray.length ; i++)
	 {
		 if( i == 0  ){
		 queryLabel = queryLabel  + splitArray[i];}
		 else
		 {
		 queryLabel = queryLabel + " and " + splitArray[i];
		 }
	 }
	
	 String querystring;
	 
	// querystring = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>  SELECT distinct * WHERE " +
	//  "{  ?iri rdfs:label ?label.   ?label  bif:contains  \"" + queryLabel  + "\"  " +  " FILTER( langMatches(lang(?label), \"en\")). "+
	//  " OPTIONAL {?iri  <http://dbpedia.org/ontology/wikiPageRedirects> ?mainpage .} }";
	String option=" OPTIONAL {?iri  <http://dbpedia.org/ontology/wikiPageRedirects> ?mainpage . ?mainpage rdfs:label ?mLabel. FILTER( langMatches(lang(?mLabel), \"en\")).} ";
	 querystring = " PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> " +
		" SELECT distinct * WHERE { ?iri rdfs:label ?label.   ?label  bif:contains  \"" + queryLabel  + "\"  " + 
				" FILTER( langMatches(lang(?label), \"en\")). "+ option +  " } ";
	
	System.out.println("-------retrieved query is:"+querystring);
	//" where Label like '**"+originalKeyword+"'" ;//+" and length(Label)<= 4*length('"+originalKeyword+ "')"  ;
	String urlsever = Constants.urlsever;;
	//Query query=QueryFactory.create(querystring);
	QueryEngineHTTP qexec = new QueryEngineHTTP(urlsever, querystring);	
//	QueryExecution qexec = QueryExecutionFactory.sparqlService(urlsever,query);
	qexec.addDefaultGraph(Constants.DefaultGraphString);
	int minimumlength=100;
	int maximumlength=0;
					
	try{
    ResultSet results = qexec.execSelect();
    while(results.hasNext()){
    	
    	QuerySolution  solution=results.nextSolution();
    	
    	ResourceInfo object = new ResourceInfo();
		String URIName = null, Label = null, mLabel=null;
		URIName = solution.get("iri").toString();
		
		if(URIName.startsWith("http://dbpedia.org/property/"))
		{
			object.setType(Constants.TYPE_PROPERTY);
		}
		else if(URIName.startsWith("http://dbpedia.org/resource/"))
		{
			
			object.setType(Constants.TYPE_INSTANCE);
		}
		
		
		if (solution.get("mainpage") != null)
		{
			
			if (!solution.get("mainpage").toString().isEmpty())
		{
				URIName = solution.get("mainpage").toString();
				mLabel = solution.get("mLabel").toString();
		}
		}
		
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
				
		if(! URIName.startsWith("http://dbpedia.org/ontology/"))
				{
		foundRessources.add(object);}
            	       	
    }//end of second while
 
	}
	catch(Exception e) {
		
		System.out.println("finish by exeption" +e.getMessage());
		
	}
	finally{
	}
	
	
	Functionality f = new Functionality();
	
	for(ResourceInfo resourceInfo: foundRessources) 
	{
		
		resourceInfo.setStringSimilarityScore( f.SimilarityString(Term.trim() , resourceInfo.getLabel().trim(), minimumlength, maximumlength) );
	//System.out.println("  Term  " + Term + "  Label "+ resourceInfo.getLabel() + " score " + resourceInfo.getStringSimilarityScore());
		} 
	List<ResourceInfo> finalfoundResources = new ArrayList<ResourceInfo>();
	for(ResourceInfo resourceInfo: foundRessources) 
	{
		if(resourceInfo.getStringSimilarityScore() >= 0.4)
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
	 for(ResourceInfo resourceInfo: finalfoundResources) 
		{
		 if(resourceInfo.getType() == Constants.TYPE_INSTANCE)
		   resourceInfo.setClassSet(getClassofInstance(resourceInfo.getUri()));
		}  
	
	
	return finalfoundResources;
}

//####################################################################################
	
	public List<ResourceInfo> retrieveResourcesForKeywords(String keyword) {
		
		List<ResourceInfo> foundRessources = new ArrayList<ResourceInfo>();
		
		String originalKeyword = keyword;
		String urlsever = "jdbc:virtuoso://139.18.2.96:1130/";
		
		try {
			Class.forName("virtuoso.jdbc4.Driver");
			Connection  conn = DriverManager.getConnection(urlsever, "dba", "dba");
		} catch (Exception  e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			System.out.println("exeption 1 occured");
		}
		
		String query = "SELECT id_to_IRI(id) as id,Label,InDegree,OutDegree,Degree,Type  FROM  DB.DBA.DBPedia_Information2 "+
		" where Label like '**"+originalKeyword+"'" ;//+" and length(Label)<= 4*length('"+originalKeyword+ "')"  ;

		System.out.println("-------retrieved query is:"+query);
		
		Statement st;
		try {
			Class.forName("virtuoso.jdbc4.Driver");
			Connection  conn = DriverManager.getConnection(urlsever, "dba", "dba");
		
			st = conn.createStatement();
			java.sql.ResultSet rs = st.executeQuery(query);
			int counter = 1;
			int minimumlength=100;
			int maximumlength=0;
			
			while (rs.next())
				{	
				
				ResourceInfo object = new ResourceInfo();
				String URIName = null, Label = null;
				URIName = rs.getString("id");
				object.setUri(URIName);
				
				Label = rs.getString("Label");
				 if (Label.endsWith("@en")) {
				Label = Label.replace("@en", "");}
				 int type=rs.getInt("Type");
				 if(type==1)
					{
						if( rs.getObject("Degree")!=null)
						{
							object.setConnectivityDegree(rs.getInt("Degree"));
						}
						else
						{
							object.setConnectivityDegree(1);
						}
							
					}
					else if(type!=1)
					{
						if( rs.getObject("InDegree")!=null && rs.getObject("OutDegree")!=null)
						{
							object.setConnectivityDegree(rs.getInt("InDegree")+rs.getInt("OutDegree"));
						}
						else if ( rs.getObject("InDegree")!=null && rs.getObject("OutDegree")==null)
						{
							object.setConnectivityDegree(rs.getInt("InDegree"));
						}
						else if ( rs.getObject("InDegree")==null && rs.getObject("OutDegree")!=null)
						{
							object.setConnectivityDegree(rs.getInt("OutDegree"));
						}
						else if ( rs.getObject("InDegree")==null && rs.getObject("OutDegree")==null)
						{
							object.setConnectivityDegree(1);
						}
							
					}

				object.setLabel(Label.trim());
				object.setType(rs.getInt("Type"));
				if(minimumlength>Label.length())
					minimumlength=Label.length();
				
				if(maximumlength<Label.length())
					maximumlength=Label.length();
				
				object.setRowNumber(counter);
				foundRessources.add(object);
				counter++;					
    	        }
			st.close();
			conn.close();
			Functionality f = new Functionality();
			
			for(ResourceInfo resourceInfo: foundRessources) 
			{
				resourceInfo.setStringSimilarityScore( f.SimilarityString(originalKeyword, resourceInfo.getLabel(), minimumlength, maximumlength) );
			}
			Collections.sort(foundRessources, new StringSimilarityComparator());
		
		} 
		catch (Exception  e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("exeption occured");
		}
		
		return foundRessources;
	}
	
	//#########################################################
	
public HashSet<String> getClassofInstance(String instance) {
		
		List<ResourceInfo> foundRessources = new ArrayList<ResourceInfo>();
		HashSet ClassSet=new HashSet();
		
		String urlsever =Constants.urlsever;// "jdbc:virtuoso://139.18.2.96:1130/";
			
		String queryString = "SELECT ?class "+
		" where { <"+instance+"> a ?class. }"  ;

		Query query=QueryFactory.create(queryString);
		//QueryEngineHTTP qexec = new QueryEngineHTTP("http://db0.aksw.org:8895/sparql", query);	
		QueryEngineHTTP qexec = new QueryEngineHTTP(urlsever, query);	
		//QueryExecution qexec = QueryExecutionFactory.sparqlService("http://pubmed.bio2rdf.org/sparql",query);
		qexec.addDefaultGraph(Constants.DefaultGraphString);
		
	try{
    ResultSet results = qexec.execSelect();
    while(results.hasNext()){
    	
    	QuerySolution  solution=results.nextSolution();
    	String ClassName=solution.get("?class").toString();
    	if(ClassName.startsWith("http://dbpedia.org/ontology/"))
    	{
    	ClassSet.add(ClassName);}
    	        	       	
   }//end of second while
 
	}
	catch(Exception e) {
		
	}
	finally{//qexec.close();
	}
	
	if(ClassSet.size() == 0)
	{
		ClassSet.add("http://www.w3.org/2002/07/owl#Thing");
	}
	
		return ClassSet;
	}
}
