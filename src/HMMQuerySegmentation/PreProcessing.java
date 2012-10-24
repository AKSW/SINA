package HMMQuerySegmentation;
import java.io.*;
import java.util.*;

import edu.stanford.nlp.ling.CoreAnnotations.LemmaAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;

import javatools.parsers.PlingStemmer;




public class PreProcessing {
	
	
	 protected StanfordCoreNLP pipeline;

	 
	 public void StanfordLemmatizer() {
	        // Create StanfordCoreNLP object properties, with POS tagging
	        // (required for lemmatization), and lemmatization
	        Properties props;
	        props = new Properties();
	        props.put("annotators", "tokenize, ssplit, pos, lemma");

	        // StanfordCoreNLP loads a lot of models, so you probably
	        // only want to do this once per execution
	        this.pipeline = new StanfordCoreNLP(props);
	    }
	 
	 
	   private List<String> lemmatize(String documentText)
	    {
	        List<String> lemmas = new LinkedList<String>();

	        // create an empty Annotation just with the given text
	        Annotation document = new Annotation(documentText);

	        // run all Annotators on this text
	        this.pipeline.annotate(document);

	        // Iterate over all of the sentences found
	        List<CoreMap> sentences = document.get(SentencesAnnotation.class);
	        for(CoreMap sentence: sentences) {
	            // Iterate over all tokens in a sentence
	            for (CoreLabel token: sentence.get(TokensAnnotation.class)) {
	                // Retrieve and add the lemma for each word into the
	                // list of lemmas
	            	String t = token.get(LemmaAnnotation.class);
	                lemmas.add(t);
	            }
	        }

	        return lemmas;
	    }
	
	 
	 
	
	public ArrayList removeStopWordswithlem(String inputString)
	{
		//System.out.println(" hi saeedeh 1");
		
		inputString = inputString.replace(".", "");
		inputString = inputString.replace("?", "");
		inputString = inputString.replace(",", "");
		
		StanfordLemmatizer();
		FileReader fr;
		ArrayList<String> StopWords =new ArrayList<String>();
		//String[] splitArray = inputString.split(" ");
		ArrayList<String> OutputList =new ArrayList<String>();
		//PorterStemmer2 PorterStemmer = new PorterStemmer2();
		List<String> splitArray = lemmatize(inputString);
		//System.out.println(" attention  " +  PorterStemmer.stemWord("longest"));
		//System.out.println(" hi saeedeh 2");
		try {
			
			InputStream in = this.getClass().getClassLoader().getResourceAsStream("Stopwords635.txt");
			
			//fr = new FileReader ("Stopwords635.txt");
			BufferedReader br = new BufferedReader(new InputStreamReader(in)); 
			String s; 
			
			while((s = br.readLine()) != null) { 
			StopWords.add(s);
						
			}
			
			for(String str:splitArray)
			 {
				 str=str.trim();
				 if ( ! StopWords.contains(str.toLowerCase()) )
				  {
					//str = lemmatize(str);
					 //str = PlingStemmer.stem(str);
					 //str = PorterStemmer.stemWord(str);
					// str = str.replaceAll("\\W", "");
					 str = str.replaceAll("[^a-zA-Z]", " ");
					  if ( str.trim().length()>= 1 ){
						 // System.out.println(" attention  " +str);
						  OutputList.add(str.trim() ); 
						  }
				  }
			
			 }	
		//	System.out.println(" size of stop words is= " + StopWords.size());	
		//	System.out.println(" OutputList= ");
			
		/*for (int j=0; j< OutputList.size() ; j++)
			{
				System.out.print(OutputList.get(j) + " ");
		}*/
			
			in.close(); 
			
			
		} 
		
		catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// String[] StopWords = StopWordsFile.
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return OutputList;
	}
	
	public ArrayList removeStopWordswithStemming(String inputString)
	{
		//System.out.println(" hi saeedeh 1");
		
		StanfordLemmatizer();
		FileReader fr;
		ArrayList StopWords =new ArrayList();
		String[] splitArray = inputString.split(" ");
		ArrayList OutputList =new ArrayList();
		//PorterStemmer2 PorterStemmer = new PorterStemmer2();
		//List<String> splitArray = lemmatize(inputString);
		
		//System.out.println(" hi saeedeh 2");
		try {
			
			//fr = new FileReader ("E:\\HMM\\src\\Stopwords635.txt");
            InputStream in = this.getClass().getClassLoader().getResourceAsStream("Stopwords635.txt");
			
			//fr = new FileReader ("Stopwords635.txt");
			BufferedReader br = new BufferedReader(new InputStreamReader(in)); 
			
			//BufferedReader br = new BufferedReader(fr); 
			
			String s; 
			while((s = br.readLine()) != null) { 
			StopWords.add(s);
						
			}
			
			for(String str:splitArray)
			 {
				 str=str.trim();
				 if ( ! StopWords.contains(str.toLowerCase()) )
				  {
					
					 str = PlingStemmer.stem(str);
					 //str = PorterStemmer.stemWord(str);
					  if ( str.length()>= 1 ){
						  OutputList.add(str.trim() ); 
						  }
				  }
			
			 }	
		//	System.out.println(" size of stop words is= " + StopWords.size());	
		//	System.out.println(" OutputList= ");
			
		/*for (int j=0; j< OutputList.size() ; j++)
			{
				System.out.print(OutputList.get(j) + " ");
		}*/
			
			//fr.close(); 
			in.close();
			
		} 
		
		catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// String[] StopWords = StopWordsFile.
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return OutputList;
	}
	
	

}
