package Model;

import java.io.File;
//import java.io.InputStream;
public class Constants {
	
	//public static final String ontologyAddress  =  "http://hanne.nlp2rdf.googlecode.com/hg/navigatorfiles/dbpedia/dbpedia_3.5.1.owl";
	//"http://downloads.dbpedia.org/3.6/dbpedia_3.6.owl";
	
	public static final int TYPE_INSTANCE       = 0;
	public static final int TYPE_CLASS          = 2;
	public static final int TYPE_VARIABLE       = 3;
	public static final int TYPE_LITERAL        = 4;
	public static final int TYPE_PROPERTY       = 1;
	
	public static final int TYPE_NE             = 1; // Named Entity
	public static final int TYPE_TE             = 2; // Term Expression
	public static final int TYPE_Single         = 3; // alone keyword
	public static final int TYPE_NP             = 4; // Noun Phrase
	
	public static final int LimitOfList         = 4;
	
	public static final  String[] StopWords = {"I" , "a", "about", "and", "an", "are", "as", "at", "be","been", "by", "me", "us", "your", "were", "also", "its",
			 "com", "for", "from", "how", "two", "in", "is", "it", "of", "on", "or", "that",
			 "the", "this", "that","often", "these","he","himself","most","those","I" , "did","many","there" ,"much", "does", "Mr.", "do","have", "has", "which", "to", "was", "were", "what", "when", "where", "who", "will", "with", "www","give" , "all","more","than" };
	
	
	public static final String DefaultGraphString ="http://dbpedia.org"; 
	
	public static final String urlsever ="http://live.dbpedia.org/sparql";//"http://139.18.2.96:8910/sparql";//"http://139.18.2.96:8910/sparql";// ;//;   //"http://live.dbpedia.org/sparql";// "http://139.18.2.96:8910/sparql";
	
	public static final String SPARQLEndPoint="http://live.dbpedia.org/sparql";//"http://139.18.2.96:8910/sparql"; //
	public static final String TaggerAddress= "left3words-wsj-0-18.tagger";
	 //F:\\Eclipse Project\\sina-vaad\\src\\Model\\taggers\\
	//public static final InputStream inputStream = WebAppFileReader.class.getClassLoader().getResourceAsStream("left3words-wsj-0-18.tagger");
	
	//public static final File ontologyAddress = new File("dbpedia_36.owl");
	public static final String ontologyAddress = "http://downloads.dbpedia.org/3.6/dbpedia_3.6.owl";
	
	
	
}
