package Model;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import javax.xml.parsers.ParserConfigurationException;

import Model.IRIMapping.NEMapping;
import Model.TermExtraction.NEKeywordStructure;
import Model.TermExtraction.OpenCalaisConnection;
import Model.TermExtraction.PorterStemmer2;
import Model.TermExtraction.YahooApiTermExtractions;
import Model.javatools.parsers.PlingStemmer;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;




public class QueryProcessing {
	
private 	LinkedList<String> KeywordList          = new LinkedList();
private 	LinkedList<NEKeywordStructure> NEList	= new LinkedList();
private 	LinkedList<String> YahooTermList        = new LinkedList();
private 	LinkedList<String> NounPhraseList       = new LinkedList();
private 	ArrayList<Integer> TypeList             = new ArrayList();
private 	PlingStemmer PlingStemmer=new PlingStemmer();

	
//###############################################################################
// This function tokenize input query and removes stop words
//###############################################################################
	
	public void QueryPreProcessing(String query) {
		
		PorterStemmer2 stemmer=new PorterStemmer2();
		
		
		 
		 List<String> StopWords = Arrays.asList(Constants.StopWords);   
		 String[] splitArray = query.split(" ");
		 for(String str:splitArray)
		 {
			 str=str.trim();
			 if ( ! StopWords.contains(str.toLowerCase()) )
			  {
				  str = PlingStemmer.stem(str);
				  if ( str.length()>= 1 ){
					  KeywordList.add(str.trim() ); 
					  TypeList.add(Constants.TYPE_Single);
				  }
			  }
		
		 }	
		
		
		
	}
	
	//###############################################################################
	// This function identifies Named Entities and assign the related type to the related keyword
	//###############################################################################

	public void NEChecker(String query) throws MalformedURLException, IOException {

		// recognize Named Entities by calling open calais api
		 OpenCalaisConnection occ=new OpenCalaisConnection();
		 
		 LinkedList<NEKeywordStructure> TempNEList	= new LinkedList();
		 TempNEList.addAll( occ.PostMethodOpenCalais(query));
		 List<String> StopWords = Arrays.asList(Constants.StopWords);  
		//NEList	
		// mapp dbpedia iris to the detected identified iris
		 NEMapping nem=new NEMapping();
		 nem.initializeMappingSet();
		 
		 for (NEKeywordStructure nek: TempNEList)
		 {
		 if( nem.getNESet().contains(nek.getNE()))
		{
			int indexNE=nem.getNESet().indexOf(nek.getNE());
			nek.setUri(nem.getUriSet().get(indexNE));
			nek.setType(nem.getTypeList().get(indexNE));
			boolean found=false;
			for (NEKeywordStructure n: NEList)
			{
				if(n.getKeyword().equals(nek.getKeyword())){
					found=true;}
				
			}
			if(!found){
			NEList.add(nek);}
		}
		 }
		 
		 
		// update type of keywords which have been found as the named entities
		 
		
		 for(String k:KeywordList) 
		 {
			 int index= KeywordList.indexOf(k);
			 String KT =k.toLowerCase();
			 for(int i=0 ; i< NEList.size() ; i++) 
			 {
			 String[] splitArray = NEList.get(i).getKeyword().split(" ");
			 String label="";
			  
			 for(String str:splitArray)
			 {
				 str=str.trim();
				 
				 if ( ! StopWords.contains(str.toLowerCase()) )
				  {				  
					  if ( str.length()>= 1 ){
						  label = label + " " + str;	  }
				  }
			
				 
				 str = PlingStemmer.stem(str.toLowerCase());
					  if ( str.equals(KT) ){
						  TypeList.set(index,Constants.TYPE_NE);
					  }
				  }
			 
			 NEList.get(i).setKeyword(label);
			
			 }	
			 
		 }
		 
		
	}

//###############################################################################

	public void setKeywordList(Object queryPreProcessing) {
		// TODO Auto-generated method stub
		
	}
//###########################################################################

	public void NounPhraseChecker(String query) throws IOException, ClassNotFoundException 
		{
		
		Set NounSet         = new HashSet();
		NounSet.add("NN");
		NounSet.add("NNS");
		NounSet.add("NNP");
		NounSet.add("NP");
		NounSet.add("NPS");
		
		Set AdjectiveSet    = new HashSet();
		AdjectiveSet.add("JJ");
		AdjectiveSet.add("JJR");
		AdjectiveSet.add("JJS");
		
		LinkedList<String> TemporaryNounPhraseList       = new LinkedList();
		
		MaxentTagger tagger = new MaxentTagger(Constants.TaggerAddress);
		String tagged = tagger.tagString(query);
		String[] taggedArray = tagged.split(" ");
		
		// first recognize all noun phrases as a combination of Adjective + noun or noun + noun
		 for(int i=0; i<taggedArray.length ; i++ )
		 {
			 String NounPhrase="";
			 String[] Tokentag=taggedArray[i].split("/");
			 if(NounSet.contains(Tokentag[1]) || AdjectiveSet.contains(Tokentag[1]))
			 {
				 NounPhrase=Tokentag[0];
				 if( i+1 < taggedArray.length )
				 {
				 Tokentag=taggedArray[i+1].split("/");
				 if(NounSet.contains(Tokentag[1]) )
				 {
					 NounPhrase = NounPhrase +" " + Tokentag[0];
					 TemporaryNounPhraseList.add(NounPhrase);
				 }
			 }
			 }
			 
			 
			
			 
		 }
		 
		// drop all noun phrases which have a keyword in common with named entities or yahoo terms
		 ArrayList<Integer> IncludedList    = new ArrayList();
		 for(int i=0 ; i< TemporaryNounPhraseList.size() ; i++) 
		 {
			 IncludedList.add(0);
		 }
		 
		 for(String k:KeywordList) 
		 {
			 int index= KeywordList.indexOf(k);
			 String KT =k.toLowerCase();
			 for(int i=0 ; i< TemporaryNounPhraseList.size() ; i++) 
			 {
			 String[] splitArray = TemporaryNounPhraseList.get(i).split(" ");
			 for(String str:splitArray)
			 {
				 str=str.trim();
				 str = PlingStemmer.stem(str.toLowerCase());
					  if ( str.equals(KT) ){
						  if(TypeList.get(index) == Constants.TYPE_NE   || TypeList.get(index) == Constants.TYPE_TE)
						  {
							  IncludedList.set(i, 1);
							  
						  }
						   
					  }
				  }
			
			 }	
			 
		 }
		 
		 
		 for(String  s:TemporaryNounPhraseList)
			{
			 System.out.println(" ^^^^^^^^^^temporary noun phrase ^^^^^^^^^^^^^^^"); 
				System.out.println(s);
			}
		 
		 for(int i=0 ; i< TemporaryNounPhraseList.size() ; i++) 
		 {
			 if(IncludedList.get(i) == 0 )
			 {
				 NounPhraseList.add(PlingStemmer.stem(TemporaryNounPhraseList.get(i))) ;
			 }
			 
		 }
		 
		 
		// update type of keywords which occur in noun phrases 
		 
		 for(String k:KeywordList) 
		 {
			 int index= KeywordList.indexOf(k);
			 String KT=k.toLowerCase();
			 for(int i=0 ; i< NounPhraseList.size() ; i++) 
			 {
			 String[] splitArray = NounPhraseList.get(i).split(" ");
			 for(String str:splitArray)
			 {
				 str=str.trim();
				 str = PlingStemmer.stem(str.toLowerCase());
					  if ( str.equals(KT) ){
						  if(TypeList.get(index) == Constants.TYPE_Single )
						  {
							  TypeList.set(index,Constants.TYPE_NP);
							  
						  }
						 
					  }
				  }
			
			 }	
			 
		 }
		
		 for(String  s:getNounPhraseList())
			{
				
				System.out.println(".. 333.. noun phrases..." +  s);
			} 
		
		
		
		}
		
	
	
	
//###########################################################################	
	public void YahooTermExpressionChecker(String query) throws MalformedURLException, IOException, ParserConfigurationException {
		
		
		    YahooApiTermExtractions y=new YahooApiTermExtractions();
		
		     LinkedList<String> CopyYahooTermList = y.PostMethodYahooApi(query);
			
			
			 ArrayList<Integer> IncludedList    = new ArrayList();
			 for(int i=0 ; i< CopyYahooTermList.size() ; i++) 
			 {
				 IncludedList.add(0);
			 }
			 
			 // drops terms which have a keyword in common with named entities
			 for(String k:KeywordList) 
			 {
				 int index= KeywordList.indexOf(k);
				 String KT =k.toLowerCase();
				 for(int i=0 ; i< CopyYahooTermList.size() ; i++) 
				 {
				 String[] splitArray = CopyYahooTermList.get(i).split(" ");
				 for(String str:splitArray)
				 {
					 str=str.trim();
					 str = PlingStemmer.stem(str.toLowerCase());
						  if ( str.equals(KT) ){
							  if(TypeList.get(index) == Constants.TYPE_NE )
							  {
								 // YahooTermList.remove(i);
								  IncludedList.set(i, 1);
								  
							  }
							  
							  
						  }
					  }
				
				 }	
				 
			 }
			 
			 for(String  s:CopyYahooTermList)
				{
				 System.out.println(" ^^^^^^^^^^CopyYahooTermList ^^^^^^^^^^^^^^^"); 
					System.out.println(s);
				}
			 
			 
			 for(int i=0 ; i< CopyYahooTermList.size() ; i++) 
			 {
				 if(IncludedList.get(i) == 0 )
				 {
					 YahooTermList.add(PlingStemmer.stem(CopyYahooTermList.get(i))) ;
				 }
				 
			 }
			 
			 
			// update type of keywords in terms 
			 for(String k:KeywordList) 
			 {
				 
				 int index= KeywordList.indexOf(k);
				 String KT=k.toLowerCase();
				 for(int i=0 ; i< YahooTermList.size() ; i++) 
				 {
				 String[] splitArray = YahooTermList.get(i).split(" ");
				 for(String str:splitArray)
				 {
					 str=str.trim();
					 str = PlingStemmer.stem(str.toLowerCase());
						  if ( str.equals(KT) ){
							  if(TypeList.get(index) == Constants.TYPE_Single )
							  {
								  TypeList.set(index,Constants.TYPE_TE);
								  
							  }
							 
						  }
					  }
				
				 }	
				 
			 }
			 
			
		
	}
//###########################################################
	public LinkedList<String> getKeywordList() {
		return KeywordList;
	}

	public LinkedList<NEKeywordStructure> getNEList() {
		return NEList;
	}

	public LinkedList<String> getYahooTermExpression() {
		return YahooTermList;
	}
	
	public LinkedList<String> getNounPhraseList() {
		
		return NounPhraseList;
	}

	public void setKeywordList(LinkedList<String> s) {
		KeywordList=s;
		
	}
	public ArrayList<Integer> getTypeList() {
		return TypeList;
	}
	
	
	

}
