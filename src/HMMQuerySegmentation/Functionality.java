package HMMQuerySegmentation;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.lang.*;


import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.sparql.engine.http.QueryEngineHTTP;


import uk.ac.shef.wit.simmetrics.*;
import uk.ac.shef.wit.simmetrics.similaritymetrics.AbstractStringMetric;
import uk.ac.shef.wit.simmetrics.similaritymetrics.CosineSimilarity;
import uk.ac.shef.wit.simmetrics.similaritymetrics.EuclideanDistance;
import uk.ac.shef.wit.simmetrics.similaritymetrics.JaccardSimilarity;
import uk.ac.shef.wit.simmetrics.similaritymetrics.Levenshtein;
import uk.ac.shef.wit.simmetrics.similaritymetrics.MatchingCoefficient;
import uk.ac.shef.wit.simmetrics.similaritymetrics.QGramsDistance;

/**
 * @author Saedeeh Shekarpour
 */
public class Functionality {
	
	private int maximumdegree = 0;
	private int minimumdegree = 1000;
	
	private int sumclass = 0;
	private int numclass = 0;
	
	private int sumproperty = 0;
	private int numproperty = 0;
	
	private int suminstance = 0;
	private int numinstance = 0;
	
	//public static final String SPARQL_ENDPOINT_URI = "http://hanne.aksw.org:8910/sparql";
	
	/**
	 * 
	 * @param term
	 * @param label
	 * @param minimumlength
	 * @param maximumlength
	 * @return
	 */
	public double SimilarityString(String term, String label,int minimumlength,int maximumlength) {
		
		/*if(term.length() < minimumlength  )
		{
			minimumlength=term.length();
		}
		return levenshtein(term,label);
		return (double)(1 - normalizeValue(minimumlength, maximumlength, label.length()));*/
		term=term.trim().toLowerCase();
		label=label.trim().toLowerCase();
	
	    AbstractStringMetric metric = new Levenshtein();
		//AbstractStringMetric metric = new QGramsDistance();
		//AbstractStringMetric metric = new MatchingCoefficient();
		//AbstractStringMetric metric = new uk.ac.shef.wit.simmetrics.similaritymetrics.();
		//AbstractStringMetric metric = new JaccardSimilarity();
		//AbstractStringMetric metric = new EuclideanDistance();
	    //AbstractStringMetric metric = new CosineSimilarity();
		//QGramsDistance.java
		//AbstractStringMetric metric = new uk.ac.shef.wit.simmetrics.similaritymetrics.
		//AbstractStringMetric metric = new EuclideanDistance();	
		float result = metric.getSimilarity(term, label); 
		return result;
	}
	
	/**
	 * @param minimumlength
	 * @param maximumlength
	 * @param value
	 * @return
	 */
	
	public double JaccardLevenstienSimilarity(ArrayList keyList, ArrayList label, String LabelOriginal)
	{
		AbstractStringMetric metric = new Levenshtein();
		double plus=0;
		int numStopWords = 0;
		
		String[] splitArray = LabelOriginal.split(" ");
		numStopWords 		= splitArray.length - label.size();
		
				
		int intersection = 0;
		int union 		 = keyList.size() + label.size();
		
		for (int i=0; i < keyList.size() ; i++ )
		{
			double max=0;
			for(int j=0; j < label.size(); j++)
			{
				double s = metric.getSimilarity(keyList.get(i).toString().toLowerCase(), label.get(j).toString().toLowerCase());
				if ( s > max)
				{
				max = s;		
				}
				
				
			}
			//System.out.println(" max similarity " + max);
			if(max >= 0.7 )
			{
				intersection ++;
				plus = plus + max;
			}
			
			
		}
		/*System.out.println(" LabelOriginal = " + LabelOriginal);
		System.out.println(" intersection = " + intersection);
		System.out.println(" union = " + union);
		System.out.println(" plus = " + plus);*/
		
		
		//double similarity = plus/label.size();
		double similarity = plus/((union - intersection )+(0.1 * numStopWords));
		//double similarity = plus/(union - intersection );
		return similarity;
		
	}
	
	// -----------------------------------------------
	
	public double normalizeValue(int minimumlength, int maximumlength, int value) {
		
		
		 if((value - minimumlength) == (maximumlength - minimumlength))
		{
			return 0;
		}
		 else if ( (maximumlength - minimumlength)!= 0 ){
				
				return ((double)(value - minimumlength)) / ((double)(maximumlength - minimumlength));
				}
		else 
		{
			return 0;
		}
	}
	
// ----------------------
	
	public double MinMaxnormalizationValue(int minimumlength, int maximumlength, int value) {
		
		double MinimumRange	=	0.1;
		double MaximumRange	=	0.5;
		 if((value - minimumlength) == (maximumlength - minimumlength))
		{
			return 0;
		}
		 else if ( (maximumlength - minimumlength)!= 0 ){
				
				return ((double)(value - minimumlength)) / ((double)(maximumlength - minimumlength)) * (MaximumRange - MinimumRange) + MinimumRange;
				}
		else 
		{
			return 0;
		}
	}
	
	
	/**
	 * 
	 * @param resourceList
	 */
	public void countingInAndOutDegree(List<ResourceInfo> resourceList) {

		ResourceInfo resource = null;
		String resourceUri = "";
		
		String urlsever = "jdbc:virtuoso://139.18.2.96:1130/";
		try {
			Class.forName("virtuoso.jdbc4.Driver");
			Connection  conn = DriverManager.getConnection(urlsever, "dba", "dba");
		} catch (Exception  e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		for (int i = 0; i < resourceList.size(); i++) {
			
			resource	= resourceList.get(i);
			resourceUri = "<" + resource.getUri() + ">";

			String querystring;

			if ( resource.getType() == Constants.TYPE_PROPERTY ) {
				
				String query = "SELECT Degree  FROM  DB.DBA.DBPedia_PredicateDegree "+
				" where Predicate like '"+resource.getUri()+"'";

				Statement st;
				try {
					Class.forName("virtuoso.jdbc4.Driver");
					Connection  conn = DriverManager.getConnection(urlsever, "dba", "dba");
				
					st = conn.createStatement();
					java.sql.ResultSet rs = st.executeQuery(query);
					while (rs.next())
						{
						resource.setConnectivityDegree(rs.getInt("Degree"));
		    	        }
					st.close();
					conn.close();
					
				} catch (Exception  e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}				
				
		   	}
			else {
				
				int indegree = 1;
				int outdegree = 1;
				
				String query ="SELECT OutDegree  FROM  DB.DBA.DBPedia_SubjectOutDegree "+
				" where Subject like '"+resource.getUri()+"'";
		
				Statement st;
				try {
					Class.forName("virtuoso.jdbc4.Driver");
					Connection  conn = DriverManager.getConnection(urlsever, "dba", "dba");
					st = conn.createStatement();
					java.sql.ResultSet rs = st.executeQuery(query);
				
					while (rs.next())
						{
						outdegree=rs.getInt("OutDegree");
						//indegree=rs.getInt("InDegree");
		    	        }
					st.close();
					conn.close();
				} catch (Exception  e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				//-------------------
				

				query = "SELECT InDegree  FROM  DB.DBA.DBPedia_ObjectInDegree "+
				" where Object like '"+resource.getUri()+"'";

				
				try {
					Class.forName("virtuoso.jdbc4.Driver");
					Connection  conn = DriverManager.getConnection(urlsever, "dba", "dba");
				
					st = conn.createStatement();
					java.sql.ResultSet rs = st.executeQuery(query);
					while (rs.next())
						{
						
						indegree=rs.getInt("InDegree");
						
						}
					st.close();
					conn.close();

				} catch (Exception  e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			//--------------------------
				
						
				resource.setConnectivityDegree(indegree + outdegree);
				
				if ( resource.getConnectivityDegree() > maximumdegree ) {
					
					maximumdegree = resource.getConnectivityDegree();
				}
				if ( resource.getConnectivityDegree() < minimumdegree ) {
					
					minimumdegree = resource.getConnectivityDegree();
				}

				if (resource.getType() == Constants.TYPE_CLASS) {
					
					numclass++;
					sumclass = sumclass + resource.getConnectivityDegree();
				}
				else {
				
					numinstance++;
					suminstance = suminstance + resource.getConnectivityDegree();
				}

			}
			resource.setLogConnectivityDegree(Math.log10((double) resource.getConnectivityDegree()));
		}
	}
}
