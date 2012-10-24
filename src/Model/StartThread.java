package Model;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import javax.xml.parsers.ParserConfigurationException;

import org.aksw.commons.collections.CartesianProductIterator;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import HMMQuerySegmentation.HiddenMarkovModel;
import Model.GraphBuilding.Template;
import Model.IRIMapping.OwlComponentMapping;
import Model.IRIMapping.ResourceInfo;
import Model.IRIMapping.UriRetrieval;
import Model.TermExtraction.NEKeywordStructure;

//import Model.OldCode.PorterStemmer2;
//import Model.OldCode.ResourceInfo;
//import Model.OldCode.StringSimilarityComparator;
//import Model.OldCode.Template;
//import Model.OldCode.UriRetrieval;
//import SINA.sina.QueryProcessing;


/*import SINA.sina.MainManagement;*/

public class StartThread {

	
	public List<String> KeywordList      = new ArrayList<String>();
	public List<ResourceInfo> IRI_List   = new ArrayList();
	
	private List<Template>  TemporaryTemplateList = new ArrayList();
	private List<Template>  FinalTemplateList     = new ArrayList();
	private List<Template>  ConnectingList        = new ArrayList();
	
	public List<String>     Query_List               = new ArrayList();
	public List<String>     SPARQLQuery_List         = new ArrayList();
	
	public static List<List<ResourceInfo>> Comprehensive_List   = new ArrayList();
	


    public void mainTask(String searchQuery) throws MalformedURLException, OWLOntologyCreationException, IOException  
    {
    	
    	 System.out.println(" searchQuery = " +searchQuery); 
    	 QueryProcessing q=new QueryProcessing();
		 String query=searchQuery;
		// q.QueryPreProcessing(query);
		
		 HiddenMarkovModel hmm = new HiddenMarkovModel();
		 hmm.initialization();
		 hmm.StartMarkovModel(searchQuery);
		 
		 System.out.println(" searchQuery = " +searchQuery); 
		 System.out.println("first best path" + hmm.getFirstbestpath());
		 System.out.println("second best path" + hmm.getSecondbestpath());
		 
		 String FirstPath 				=   hmm.getFirstbestpath();
		 String SecondPath				=   hmm.getSecondbestpath();
		 
		 Query_List.clear();
	     SPARQLQuery_List.clear();
	     TemporaryTemplateList.clear();
	     FinalTemplateList.clear();
	     ConnectingList.clear();
	     IRI_List.clear();
	     KeywordList.clear();
	     Comprehensive_List.clear();
	     
		 statisticallyConstructingQuery(FirstPath);
		 statisticallyConstructingQuery(SecondPath);
		 System.out.println(" searchQuery = " +searchQuery); 
		
		 
		 /*QueryConstructionManagement QueryCM		=	new QueryConstructionManagement();
		List<Template>  TemporaryTemplateList = new ArrayList();
	    List<Template>  FinalTemplateList     = new ArrayList();
	    List<Template>  ConnectingList        = new ArrayList();
		
	    List<ResourceInfo> IRI_List   	= 	new ArrayList();
		Iterator<ResourceInfo> it 		=   new CartesianProductIterator(Comprehensive_List);
	    */
		 
		 
		// LinguisticTermExtraction(query);
		 
		 
		/*System.out.println("........... printing iterator results............. "  );
		while (it.hasNext())
		{
			System.out.println("\n.... new iterator results ......\n");
			IRI_List=(List<ResourceInfo>) it.next();
			
			for (ResourceInfo r: IRI_List)
			{
				System.out.println(" resource .... : " +r.getUri() + "  type  " +  r.getType());
				
				System.out.println(".... class set .... " );	
				for(String c: r.getClassSet())
				{
					System.out.println(c);	
				}
			}
			
			
			long Endmappingtime=System.currentTimeMillis();
		    long startgeneratingtime=System.currentTimeMillis();
	    	try {
	    		QueryCM.initialization(IRI_List);
	    		QueryCM.connectingFunction();
				FinalTemplateList = QueryCM.getFinalTemplateList();
			} catch (OWLOntologyCreationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			long endgeneratingtime=System.currentTimeMillis();
			
			long Mappingtime=Endmappingtime - startmappingtime;
			long Generatingtime=endgeneratingtime - startgeneratingtime;
			long totaltime=endgeneratingtime - startmappingtime;
			
			
			System.out.println("mapping time : " + Mappingtime );
		   	System.out.println("generating time:"+Generatingtime);
		   	System.out.println("total time ="+totaltime);
			
		   	Query_List.clear();
		   	SPARQLQuery_List.clear();
			for (Template t : FinalTemplateList)
			{
				System.out.println("template: " + t.getSPARQL_query());
				System.out.println("template: " + t.getQueryRepresentation());
				//Query_List.add(t.getSPARQL_query());
				SPARQLQuery_List.add(t.getSPARQL_query());
				Query_List.add(t.getQueryRepresentation());
			}
			
			for (String s:SPARQLQuery_List)
			{
				System.out.println("sparql printing list....." +s);
			}
			
		}*/
		
//*****************************************************************
    
    	
    	
    }
    
    private void statisticallyConstructingQuery(String path)
    {
    	 QueryConstructionManagement QueryCM		=	new QueryConstructionManagement();
		 List<Template>  TemporaryTemplateList = new ArrayList();
		 List<Template>  FinalTemplateList     = new ArrayList();
		 List<Template>  ConnectingList        = new ArrayList();
		 UriRetrieval    UR					   = new UriRetrieval();	
		
    	List<ResourceInfo> IRI_List   	= 	new ArrayList();
		
		 String[] FirstPathArray = path.split(",");
		 for ( String s: FirstPathArray )
		 {
			 s=s.trim();
			    ResourceInfo object=new ResourceInfo();
				object.setUri(s);
				int index = s.lastIndexOf("/");
				String a= s.substring(index+1, index+2);
				char[] b= a.toCharArray();
				char d=b[0];
				
				if(s.startsWith("http://dbpedia.org/resource"))
				{
					object.setType(Constants.TYPE_INSTANCE);	
				}
				else if(s.startsWith("http://dbpedia.org/property"))
				{
					object.setType(Constants.TYPE_PROPERTY);	
				}
				else if(s.startsWith("http://dbpedia.org/ontology/") && Character.isUpperCase(d))
				{
					object.setType(Constants.TYPE_CLASS);
					
				}
				else
				{
				   object.setType(Constants.TYPE_PROPERTY);
					
				}
				
			IRI_List.add(object);	
			System.out.println(" uri of path " + object.getUri() +"type " + object.getType());
		 }
		 
		 
		 for(ResourceInfo r:IRI_List)
		 {
			if (r.getType() == Constants.TYPE_INSTANCE) 
			{
				r.setClassSet(UR.getClassofInstance(r.getUri()));
			}
		 }
		 
		 try {
			    System.out.println(" hello saeedeh goli 1 ");
	    		QueryCM.initialization(IRI_List);
	    		System.out.println(" hello saeedeh goli 2 ");
	    		QueryCM.connectingFunction();
	    		System.out.println(" hello saeedeh goli 3 ");
				FinalTemplateList = QueryCM.getFinalTemplateList();
				System.out.println(" hello saeedeh goli 4 ");
				
			} catch (OWLOntologyCreationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//Query_List.clear();
		   	//SPARQLQuery_List.clear();
			for (Template t : FinalTemplateList)
			{
				System.out.println("template: " + t.getSPARQL_query());
				System.out.println("template: " + t.getQueryRepresentation());
				//Query_List.add(t.getSPARQL_query());
				SPARQLQuery_List.add(t.getSPARQL_query());
				Query_List.add(t.getQueryRepresentation());
			}
			
			for (String s:SPARQLQuery_List)
			{
				System.out.println("sparql printing list....." +s);
			}
			
			
    }
    
    
    private void LinguisticTermExtraction(String query) throws MalformedURLException, IOException, OWLOntologyCreationException
    {
    	QueryProcessing q=new QueryProcessing();
    	UriRetrieval UR=new UriRetrieval();
		q.QueryPreProcessing(query);
		 
    	
    	
    	
    	q.NEChecker(query);
		 try {
			q.YahooTermExpressionChecker(query);
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
	
		try {
			q.NounPhraseChecker(query);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
   	
		// printing stuff....................
		System.out.println(" printing stuff....................");
		for( NEKeywordStructure  s:q.getNEList() )
		{
			System.out.println("..111.. Named Entity List ..." + s.getKeyword());
		}
		for(String  s:q.getYahooTermExpression())
		{
			System.out.println("..222.. Terms List..." + s );
		}
		
		for(String  s:q.getNounPhraseList())
		{
			
			System.out.println(".. 333.. noun phrases..." +  s);
		}
		
		for(String  s:q.getKeywordList())
		{
			int index= q.getKeywordList().indexOf(s);
			if(q.getTypeList().get(index) == Constants.TYPE_Single){
			System.out.println("..444.. single keywords..." + s + ".. type.." + q.getTypeList().get(q.getKeywordList().indexOf(s)));}
		}
		
//############## retrieval process ###################
		 
		
		
		
		for ( NEKeywordStructure  s:q.getNEList() )
		{
		List<ResourceInfo> foundResources = new ArrayList<ResourceInfo>();
		if(s.getNE().equals("Position"))
		{
			q.getYahooTermExpression().addLast(s.getKeyword());
		}
	    
		
		else
		{
			
			if(s.getNE().equals("IndustryTerm"))
			{
				
				ResourceInfo object=new ResourceInfo();
				object.setUri(s.geturi());
				object.setType(Constants.TYPE_PROPERTY);
				List<ResourceInfo> f = new ArrayList<ResourceInfo>();
				f.add(object);
				Comprehensive_List.add(f);
				
			}
			
			
		foundResources.addAll(UR.getIRIsForNE(s));
		if(foundResources.size() != 0)
		{
		Comprehensive_List.add(foundResources);
		}
		else
		{
			q.getYahooTermExpression().addLast(s.getKeyword());
		}
		System.out.println("................  mapped IRIs for " + s.getKeyword() +"..........");
		for(ResourceInfo r : foundResources )
		{
			
			 System.out.println(r.getUri() + " ....... " + r.getLabel() + "....score...." + r.getStringSimilarityScore());	
		}
		
		}
		}
		// mapping to ontology
		OwlComponentMapping owlm=new OwlComponentMapping();
		
//@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
   	
//retrival for yahoo terms
		
		
		for(String  s:q.getYahooTermExpression())
		{
			List<ResourceInfo> total_List   = new ArrayList();
			
			if(s.toLowerCase().equals("homepage"))
			{
				ResourceInfo object=new ResourceInfo();
				object.setType(Constants.TYPE_PROPERTY);
				object.setUri("http://xmlns.com/foaf/0.1/homepage");
				total_List.add(object);
				Comprehensive_List.add(total_List);
				break;
			}
			
			System.out.println(" result of mapping to ontology for " +s);
			List<ResourceInfo> resourceClassSet                     = owlm.getMappingOwlClasses(s.trim());
			List<ResourceInfo> ObjectPropertySet   = owlm.getMappingOwlObjectProperties(s.trim());
			List<ResourceInfo> DataPropertySet       = owlm.getMappingOwlDataTypeProperties(s.trim());
			
			if (resourceClassSet.size() == 0  &&  ObjectPropertySet.size() == 0 && DataPropertySet.size() == 0)
			{
				
				System.out.println("................  mapped IRIs for " + s +"..........");
				List<ResourceInfo> foundResources = UR.getIRIsTerms(s.trim());
				
				for(ResourceInfo r : foundResources )
				{					
					 System.out.println(r.getUri() + " ....... " + r.getLabel() + "....score...." + r.getStringSimilarityScore());	
				}
				
				total_List.addAll(foundResources);
				
			}
			else
			{
				
				total_List.addAll(resourceClassSet);
				total_List.addAll(ObjectPropertySet);
				total_List.addAll(DataPropertySet);
				
				for( ResourceInfo r : resourceClassSet )
				{					
					 System.out.println(r.getUri() + " ....... " + r.getLabel() + "....score...." + r.getStringSimilarityScore());	
				}
				
				for(ResourceInfo r : ObjectPropertySet )
				{					
					 System.out.println(r.getUri() + " ....... " + r.getLabel() + "....score...." + r.getStringSimilarityScore());	
				}
				for(ResourceInfo r : DataPropertySet )
				{					
					 System.out.println(r.getUri() + " ....... " + r.getLabel() + "....score...." + r.getStringSimilarityScore());	
				}
			}
			if(total_List.size() != 0)
			{
				Comprehensive_List.add(total_List);
			}
		
			
		}
		//@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
		for(String  s:q.getNounPhraseList())
		{
			
			
			System.out.println(" result of mapping to ontology for " +s);
			List<ResourceInfo> resourceClassSet                     = owlm.getMappingOwlClasses(s.trim());
			List<ResourceInfo> ObjectPropertySet   = owlm.getMappingOwlObjectProperties(s.trim());
			List<ResourceInfo> DataPropertySet       = owlm.getMappingOwlDataTypeProperties(s.trim());
			List<ResourceInfo> total_List   = new ArrayList();
			if (resourceClassSet.size() == 0  &&  ObjectPropertySet.size() == 0 && DataPropertySet.size() == 0)
			{
				System.out.println("................  mapped IRIs for " + s +"..........");
				List<ResourceInfo> foundRessources = UR.getIRIsTerms(s.trim());
				
				for(ResourceInfo r : foundRessources )
				{					
					 System.out.println(r.getUri() + " ....... " + r.getLabel() + "....score...." + r.getStringSimilarityScore());	
				}
				total_List.addAll(foundRessources);
			}
			
			else
			{
				total_List.addAll(resourceClassSet);
				total_List.addAll(ObjectPropertySet);
				total_List.addAll(DataPropertySet);
				
				for(ResourceInfo r : resourceClassSet )
				{					
					 System.out.println(r.getUri() + " ....... " + r.getLabel() + "....score...." + r.getStringSimilarityScore());	
				}
				
				for(ResourceInfo r : ObjectPropertySet )
				{					
					 System.out.println(r.getUri() + " ....... " + r.getLabel() + "....score...." + r.getStringSimilarityScore());	
				}
				
				for(ResourceInfo r : DataPropertySet )
				{					
					 System.out.println(r.getUri() + " ....... " + r.getLabel() + "....score...." + r.getStringSimilarityScore());	
				}
			}
			
			if(total_List.size() != 0)
			{
				Comprehensive_List.add(total_List);
			}
		
			
		}
		//@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
		
		for(String  s:q.getKeywordList())
		{
			List<ResourceInfo> total_List   = new ArrayList();
			if(s.toLowerCase().equals("homepage"))
			{
				ResourceInfo object=new ResourceInfo();
				object.setType(Constants.TYPE_PROPERTY);
				object.setUri("http://xmlns.com/foaf/0.1/homepage");
				total_List.add(object);
				Comprehensive_List.add(total_List);
				break;
			}
			
			int index= q.getKeywordList().indexOf(s);
			if(q.getTypeList().get(index) == Constants.TYPE_Single)
			{
			System.out.println(" result of mapping to ontology for " +s);
			List<ResourceInfo> resourceClassSet                     = owlm.getMappingOwlClasses(s.trim());
			List<ResourceInfo> ObjectPropertySet   = owlm.getMappingOwlObjectProperties(s.trim());
			List<ResourceInfo> DataPropertySet       = owlm.getMappingOwlDataTypeProperties(s.trim());
			
			if (resourceClassSet.size() == 0  &&  ObjectPropertySet.size() == 0 && DataPropertySet.size() == 0)
			{
				System.out.println("................  mapped IRIs for " + s +"..........");
				List<ResourceInfo> foundRessources = UR.getIRIsTerms(s.trim());
				
				for(ResourceInfo r : foundRessources )
				{					
					 System.out.println(r.getUri() + " ....... " + r.getLabel() + "....score...." + r.getStringSimilarityScore());	
				}
				total_List.addAll(foundRessources);
			}
			
			else
			{
				
				total_List.addAll(resourceClassSet);
				total_List.addAll(ObjectPropertySet);
				total_List.addAll(DataPropertySet);
				
				for(ResourceInfo r : resourceClassSet )
				{					
					 System.out.println(r.getUri() + " ....... " + r.getLabel() + "....score...." + r.getStringSimilarityScore());	
				}
				
				for(ResourceInfo r : ObjectPropertySet )
				{					
					 System.out.println(r.getUri() + " ....... " + r.getLabel() + "....score...." + r.getStringSimilarityScore());	
				}
				
				for(ResourceInfo r : DataPropertySet )
				{					
					 System.out.println(r.getUri() + " ....... " + r.getLabel() + "....score...." + r.getStringSimilarityScore());	
				}
			}
			if(total_List.size() != 0)
			{
				Comprehensive_List.add(total_List);
			}
		
			
			}
		
			
		}
		
		for (List<ResourceInfo> list :Comprehensive_List)
		{
			System.out.println("\n printing list .... : \n" );
			
			for (ResourceInfo r: list)
			{
				System.out.println(" resource .... : " +r.getUri() );
			}
		}
		
		
		QueryConstructionManagement QueryCM		=	new QueryConstructionManagement();
		List<Template>  TemporaryTemplateList = new ArrayList();
	    List<Template>  FinalTemplateList     = new ArrayList();
	    List<Template>  ConnectingList        = new ArrayList();
		
	    List<ResourceInfo> IRI_List   	= 	new ArrayList();
		Iterator<ResourceInfo> it 		=   new CartesianProductIterator(Comprehensive_List);
	    
		System.out.println("........... printing iterator results............. "  );
		while (it.hasNext())
		{
			System.out.println("\n.... new iterator results ......\n");
			IRI_List=(List<ResourceInfo>) it.next();
			
			for (ResourceInfo r: IRI_List)
			{
				System.out.println(" resource .... : " +r.getUri() + "  type  " +  r.getType());
				
				System.out.println(".... class set .... " );	
				for(String c: r.getClassSet())
				{
					System.out.println(c);	
				}
			}
			
			
			long Endmappingtime=System.currentTimeMillis();
		    long startgeneratingtime=System.currentTimeMillis();
	    	try {
	    		QueryCM.initialization(IRI_List);
	    		QueryCM.connectingFunction();
				FinalTemplateList = QueryCM.getFinalTemplateList();
			} catch (OWLOntologyCreationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			long endgeneratingtime=System.currentTimeMillis();
			
		//	long Mappingtime=Endmappingtime - startmappingtime;
		//	long Generatingtime=endgeneratingtime - startgeneratingtime;
		//	long totaltime=endgeneratingtime - startmappingtime;
			
			
			/*System.out.println("mapping time : " + Mappingtime );
		   	System.out.println("generating time:"+Generatingtime);
		   	System.out.println("total time ="+totaltime);
			*/
		   	Query_List.clear();
		   	SPARQLQuery_List.clear();
			for (Template t : FinalTemplateList)
			{
				System.out.println("template: " + t.getSPARQL_query());
				System.out.println("template: " + t.getQueryRepresentation());
				//Query_List.add(t.getSPARQL_query());
				SPARQLQuery_List.add(t.getSPARQL_query());
				Query_List.add(t.getQueryRepresentation());
			}
			
			for (String s:SPARQLQuery_List)
			{
				System.out.println("sparql printing list....." +s);
			}
			
		}
	
		System.out.println(" successfully  finished ");
	
    	
    	
    	
    	
    	
    } // end of term extraction function



}
