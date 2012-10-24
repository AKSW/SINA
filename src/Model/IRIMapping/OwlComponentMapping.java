package Model.IRIMapping;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.PrefixManager;
import org.semanticweb.owlapi.util.DefaultPrefixManager;
import org.semanticweb.owlapi.vocab.OWLRDFVocabulary;

import Model.Constants;
import Model.javatools.*;//.TermExtraction.PorterStemmer2;

public class OwlComponentMapping {
	
	private PorterStemmer2 stemmer		=	new PorterStemmer2();
	private Functionality f 			= 	new Functionality();
	
	public List<ResourceInfo> getMappingOwlClasses(String term) throws OWLOntologyCreationException{
	
		
		term  						=	stemmer.stemWord(term.trim());
		Set<OWLClass> ClassSet 		=	new HashSet();
		List<ResourceInfo> foundResources = new ArrayList<ResourceInfo>();
		OWLOntologyManager manager	=	OWLManager.createOWLOntologyManager();
		IRI iri=IRI.create(Constants.ontologyAddress);
	    OWLOntology DBpediaOWL 		=	manager.loadOntologyFromOntologyDocument(iri);
	
	    OWLDataFactory dataFactory 	= 	manager.getOWLDataFactory();
	    String base 				= 	"http://dbpedia.org/ontology/";
	    PrefixManager pm 			=	new DefaultPrefixManager(base);
	    int minimumlength			=	100;
		int maximumlength			=	0;
			
	   
	    OWLAnnotationProperty label = dataFactory.getOWLAnnotationProperty(OWLRDFVocabulary.RDFS_LABEL.getIRI());
	    
	    
        for (OWLClass cls : DBpediaOWL.getClassesInSignature()) {
	     
	                // Get the annotations on the class that use the label property
	     
	                   for (OWLAnnotation annotation : cls.getAnnotations(DBpediaOWL, label)) {
	                	   
	                    
	                	   if (annotation.getValue() instanceof OWLLiteral) {
	     
	                          OWLLiteral val = (OWLLiteral) annotation.getValue();
	                         if(val.hasLang("en")){
	                        	 
	                        	 String Label = val.getLiteral();
	                     			if (Label.contains("(") &&  Label.contains(")"))
	                     			{
	                     				int indexStart = Label.indexOf("(");
	                     				int indexEnd   = Label.indexOf(")");
	                     				StringBuffer s = new StringBuffer(Label);
	                     				s=s.delete(indexStart, indexEnd+1);
	                     				Label=s.toString();
			
	                     			}
	                        	 String StemmedLabel=stemmer.stemWord(Label.trim());
	                     
	                    //   if (Pattern.compile(Pattern.quote(term), Pattern.CASE_INSENSITIVE).matcher(val.getLiteral()).find()  ) {
	                        	 if (Pattern.compile(Pattern.quote(term), Pattern.CASE_INSENSITIVE).matcher(StemmedLabel).find()  ) {
	     	                    
	                        		ResourceInfo object = new ResourceInfo();
	                     			String URIName = cls.getIRI().toString();
	                     			object.setUri(URIName);
	                     			
	                     			
	                     			object.setLabel(Label.trim());
	                     			
	                     			
	                     			object.setType(Constants.TYPE_CLASS);
	                     			object.setStringSimilarityScore( f.SimilarityString(term , StemmedLabel, minimumlength, maximumlength) );
	                    			
	                     			foundResources.add(object);
	                     			
	                     		
	                           // System.out.println(object.getUri() + " -> " + val.getLiteral());
	     
	                          }
	                          
	                	   }
	     
                   }
	     
	                }
	     
            }
    
        List<ResourceInfo> finalfoundRessources = new ArrayList<ResourceInfo>();
        
       if (foundResources.size() !=0) 
       {
        
    	   
			for(ResourceInfo resourceInfo: foundResources) 
			{
				if(resourceInfo.getStringSimilarityScore() >= 0.8)
				{
					finalfoundRessources.add(resourceInfo);
				}
			
			} 
			Collections.sort(finalfoundRessources, new StringSimilarityComparator());
   			
       }
        
        
	  return finalfoundRessources;
	  
	}
	
	
	//####################################################################
      public List<ResourceInfo> getMappingOwlObjectProperties(String term) throws OWLOntologyCreationException{
	
    	term								 =	stemmer.stemWord(term.trim());
    	List<ResourceInfo> foundResources	 =	new ArrayList<ResourceInfo>();
        Set<OWLObjectProperty> PropertySet	 =	new HashSet();
		OWLOntologyManager manager=OWLManager.createOWLOntologyManager();
		IRI iri								 =	IRI.create(Constants.ontologyAddress);
	    OWLOntology DBpediaOWL				 = 	manager.loadOntologyFromOntologyDocument(iri);
	
	    OWLDataFactory dataFactory 			 =  manager.getOWLDataFactory();
	    String base = "http://dbpedia.org/ontology/";
	    PrefixManager pm 					 = new DefaultPrefixManager(base);
	    int minimumlength					 =	100;
		int maximumlength					 =	0;
	   
	    OWLAnnotationProperty label = dataFactory.getOWLAnnotationProperty(OWLRDFVocabulary.RDFS_LABEL.getIRI());
	    
	    
        for (OWLObjectProperty cls : DBpediaOWL.getObjectPropertiesInSignature()) {
	     
	                // Get the annotations on the class that use the label property
	     
	                   for (OWLAnnotation annotation : cls.getAnnotations(DBpediaOWL, label)) {
	     
	                    if (annotation.getValue() instanceof OWLLiteral) {
	     
	                          OWLLiteral val = (OWLLiteral) annotation.getValue();
	                          if(val.hasLang("en")){
	                        	  
	                        	  String Label = val.getLiteral();
	                     			if (Label.contains("(") &&  Label.contains(")"))
	                     			{
	                     				int indexStart = Label.indexOf("(");
	                     				int indexEnd   = Label.indexOf(")");
	                     				StringBuffer s = new StringBuffer(Label);
	                     				s=s.delete(indexStart, indexEnd+1);
	                     				Label=s.toString();
			
	                     			}
	                        	  
	                            String StemmedLabel=stemmer.stemWord(Label.trim());
	                           if ( Pattern.compile(Pattern.quote(term), Pattern.CASE_INSENSITIVE).matcher(StemmedLabel).find() )
	                    	   //val.getLiteral().contains(term) )
	                    	   {
	                        	   ResourceInfo object = new ResourceInfo();
	                     			String URIName = cls.getIRI().toString();
	                     			object.setUri(URIName);
	                     			object.setLabel(Label.trim());
	                     			object.setType(Constants.TYPE_PROPERTY);
	                     			object.setStringSimilarityScore( f.SimilarityString(term ,StemmedLabel , minimumlength, maximumlength) );
	                     			foundResources.add(object);
	                        	
	                        	  // PropertySet.add(cls);
	                           // System.out.println(cls + " -> " + Label + "language " + val.getLang() + "  label " + StemmedLabel + "  term  " + term);
	     
	                          }
	                          }
	     
                   }
	     
	                }
	     
            }
        
        
        
        List<ResourceInfo> finalfoundRessources = new ArrayList<ResourceInfo>();
        
        if (foundResources.size() !=0) 
        {
        		
     		for(ResourceInfo resourceInfo: foundResources) 
 			{
 				if(resourceInfo.getStringSimilarityScore() >= 0.7)
 				{
 					finalfoundRessources.add(resourceInfo);
 				}
 			
 			} 
     		Collections.sort(finalfoundRessources, new StringSimilarityComparator());
          	
        }
        
        
        return finalfoundRessources;
        
      }
	     
	      
	//#########################################################     
	        
        public List<ResourceInfo> getMappingOwlDataTypeProperties(String term) throws OWLOntologyCreationException{
        	
        	term								 =  stemmer.stemWord(term.trim());
        	List<ResourceInfo> foundResources	 =	new ArrayList<ResourceInfo>();
            Set<OWLDataProperty> PropertySet	 = new HashSet();
    		OWLOntologyManager manager=OWLManager.createOWLOntologyManager();
    		IRI iri=IRI.create(Constants.ontologyAddress);
    	    OWLOntology DBpediaOWL 				 = manager.loadOntologyFromOntologyDocument(iri);
    	    int minimumlength					 =	100;
    		int maximumlength					 =	0;
    	   
    	
    	    OWLDataFactory dataFactory = manager.getOWLDataFactory();
    	    String base = "http://dbpedia.org/ontology/";
    	    PrefixManager pm = new DefaultPrefixManager(base);
    	   
    	    OWLAnnotationProperty label = dataFactory.getOWLAnnotationProperty(OWLRDFVocabulary.RDFS_LABEL.getIRI());
    	    
    	    
            for (OWLDataProperty cls : DBpediaOWL.getDataPropertiesInSignature()) {
    	     
    	                // Get the annotations on the class that use the label property
    	     
    	                   for (OWLAnnotation annotation : cls.getAnnotations(DBpediaOWL, label)) {
    	     
    	                    if (annotation.getValue() instanceof OWLLiteral) {
    	     
    	                          OWLLiteral val = (OWLLiteral) annotation.getValue();
    	     
    	                          //val.getLiteral().  .contains(term)
    	                          if(val.hasLang("en")){
    	                        	  
    	                        		String Label = val.getLiteral();
    	                     			if (Label.contains("(") &&  Label.contains(")"))
    	                     			{
    	                     				int indexStart = Label.indexOf("(");
    	                     				int indexEnd   = Label.indexOf(")");
    	                     				StringBuffer s = new StringBuffer(Label);
    	                     				s=s.delete(indexStart, indexEnd+1);
    	                     				Label=s.toString();
    			
    	                     			}
    	                        	  String StemmedLabel=stemmer.stemWord(Label.trim());
    	                        	  if ( Pattern.compile(Pattern.quote(term), Pattern.CASE_INSENSITIVE).matcher(StemmedLabel).find() )
    	                       
    	                        	  {
    	                            ResourceInfo object = new ResourceInfo();
	                     			String URIName = cls.getIRI().toString();
	                     			object.setUri(URIName);
	                     			
	                     		
	                     			
	                     			object.setLabel(Label.trim());
	                     			
	                     			
	                     			
	                     			object.setType(Constants.TYPE_PROPERTY);
	                     			object.setStringSimilarityScore( f.SimilarityString(term , StemmedLabel, minimumlength, maximumlength) );
	                 			
	                     			foundResources.add(object);
	                        	
    	                    	   //PropertySet.add(cls);
    	                           // System.out.println(cls + " -> " + val.getLiteral());
	                     			// System.out.println(cls + " -> " + Label + "language " + val.getLang() + "  label " + StemmedLabel + "  term  " + term);
	                     		     
    	     
    	                          }
    	                          }
    	     
    	                    }
    	     
    	                }
    	     	
            }
            
            
            List<ResourceInfo> finalfoundResources = new ArrayList<ResourceInfo>();
            
            if (foundResources.size() !=0) 
            {
             
         		for(ResourceInfo resourceInfo: foundResources) 
     			{
       			// System.out.println(resourceInfo.getUri() + " ....... " + resourceInfo.getLabel() + "....score...." + resourceInfo.getStringSimilarityScore());	
         			if(resourceInfo.getStringSimilarityScore() >= 0.7)
     				{
     					finalfoundResources.add(resourceInfo);
     				}
     			
     			} 
         		
            }
            
            Collections.sort(finalfoundResources, new StringSimilarityComparator());
    		 
            
			return finalfoundResources;
            
            
      //#################################################################

	  
	}
	  
	  
	  
	  
	  
	  
	  
	  

}
