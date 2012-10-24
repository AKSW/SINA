package HMMQuerySegmentation;
import java.util.*;

public class MarkovParameters {
	
	private Hashtable<ResourceInfo, Hashtable<ResourceInfo, Double>> Trans_Matrix = new  Hashtable<ResourceInfo, Hashtable<ResourceInfo, Double>>();
	//private Hashtable<ResourceInfo, Hashtable<ResourceInfo, Float>> Distance_Matrix = 
      //  new Hashtable<ResourceInfo, Hashtable<ResourceInfo, Float>>();
	
	
	//###################################################################
	
	public  Hashtable<ResourceInfo, Hashtable<ResourceInfo, Double>> getTrans_Matrix()
	{
		return Trans_Matrix;
	}
	
	//###################################################################
	
	public Hashtable<ResourceInfo, Hashtable<ResourceInfo, Double>> buildTransitionMatrix(List<ResourceInfo> states, Hashtable<ResourceInfo, Hashtable<ResourceInfo, Float>> DistanceMatrix)
	{
		int DisSize = DistanceMatrix.size();
		List rowElement_list = new ArrayList();
		List Sorting_list = new ArrayList();
		List rowRank_list = new ArrayList();
		
		Set<Float> set=new HashSet<Float>();
		
		System.out.println(" DistanceMatrix: " + DistanceMatrix);
		// calculating the transition probability using distance for state by state
		
		for (ResourceInfo s:states)
		{
			
			System.out.println(" State: " + s );
			Hashtable<ResourceInfo, Float> row_Distance = DistanceMatrix.get(s);
			System.out.println( "  hello world 3 " + s.getUri());
			for (ResourceInfo c : states)
			{
				if (row_Distance.containsKey(c))
				{
				Float d = row_Distance.get(c);
				if( d != Constants.MaximumDistance )
				{
				rowElement_list.add(d);
				set.add(d);
				}
				}
				
			}
			
			Sorting_list.addAll(set);
			Collections.sort(Sorting_list);
			
			// finding the rank of each element in a row
		
		    for(int h=0; h < rowElement_list.size();h++)
		    {
		    	int index 		= 	Sorting_list.indexOf(rowElement_list.get(h));
		    	int rank		=	index +1 ;
				rowRank_list.add(h,rank);
		    }
			
			
			//System.out.println(" row_Distance: " + row_Distance);
			System.out.println(" rowElement_list: " + rowElement_list);
			System.out.println(" set: " + set);
			System.out.println(" Sorting_list: " + Sorting_list);
			System.out.println(" rowRank_list: " + rowRank_list);
			
			
			Double Sigma = 0.0;
			for (int j=0; j<rowRank_list.size(); j++)
			{
				Sigma= Sigma + (1/(Double.parseDouble(Integer.toString((Integer) rowRank_list.get(j)))));
			}
			System.out.println(" Sigma: " + Sigma);
			System.out.println( "  hello world 7 ");			
			Hashtable<ResourceInfo, Double> tran_row = new Hashtable<ResourceInfo, Double>();
				for (ResourceInfo c:states)
				{
					if(row_Distance.containsKey(c))
					{
					Float distance = row_Distance.get(c);
					if( distance != Constants.MaximumDistance)
					{
						int index = rowElement_list.indexOf(distance);
						int rank  = (Integer) rowRank_list.get(index);
						Double tran_prob = 1/(rank * Sigma);
						tran_row.put(c, tran_prob );
					}
					
					else
					{
						tran_row.put(c, (double) 0 );
					}
					
					
					}
				}
				if(!tran_row.isEmpty())
				{
				Trans_Matrix.put(s, tran_row);
				}
				System.out.println( "tran_row " + tran_row);
			
			//row_Distance.clear();
			rowElement_list.clear();
			Sorting_list.clear();
			rowRank_list.clear();
			set.clear();
		}
		System.out.println(" DistanceMatrix: " + DistanceMatrix);
		System.out.println(" Trans_Matrix: " + Trans_Matrix);
		
		return Trans_Matrix;
	}
	
	//########################################
	//#########
	//########################################
	
	public void ApplyingHITS(List<ResourceInfo> StateSpace, Hashtable<ResourceInfo, Hashtable<ResourceInfo, Float>> Distance_Matrix)
	{
		int k = 5; // k is the number of iterations
		double closeness;
		for (int step= 1; step <= k; step++ )
		{
			System.out.println(" step = " + step);
			double  norm = 0;
			for (ResourceInfo p: StateSpace) // update all authority values first
			{
				   p.setauthorithy(0);
			       
			       for (ResourceInfo s: StateSpace)  // p.incomingNeighbors is the set of pages that link to p
			       { 
			    	  if(Distance_Matrix.containsKey(s)) 
			    	  { 
			    	   Hashtable<ResourceInfo, Float> DistanceRow = Distance_Matrix.get(s);
			    	   
			    	   if (DistanceRow != null)
			    	   {
			    	   if (DistanceRow.get(p) != null) 
			    	   {
			    		  
			    		   if(DistanceRow.get(p) != Constants.MaximumDistance )
			    		   {
			    		   closeness =6 - DistanceRow.get(p);
			    	       p.setauthorithy(p.getauthorithy() + s.gethub()*closeness );
			    		   }
			    	    
			    	   }
			    	   
			    	   }
			    	  }
			       }
			       
			       norm +=  Math.pow(p.getauthorithy(),2); // calculate the sum of the squared auth values to normalise
			    	  
			}
			
			norm = Math.sqrt(norm);
			for (ResourceInfo p: StateSpace) // update the auth scores 
			{
				
				p.setauthorithy(p.getauthorithy() / norm);
				//System.out.println(" state == " + p.getLabel() + " hub = " + p.gethub() + " authorithy = " + p.getauthorithy());
				
			}
			
		    norm = 0;
		    
		    for (ResourceInfo p: StateSpace) // update all hub values first
			{
		    	
			       p.sethub(0);
			       if(Distance_Matrix.containsKey(p)) 
			    	  { 
			    	  
			       Hashtable<ResourceInfo, Float> DistanceRow = Distance_Matrix.get(p);
			      
			       for (ResourceInfo s: StateSpace)  // p.outgoingNeighbors is the set of pages that p links to
			       {
			       if (DistanceRow != null)
			       {	
			    	   if (DistanceRow.containsKey(s)) 
			    	   {
			    		   
			    		   if(DistanceRow.get(s) != Constants.MaximumDistance )
			    		   {
			    			   
			    			   closeness = 6 - DistanceRow.get(s);
			    			   p.sethub(p.gethub() + s.getauthorithy() * closeness);
			    		   }
			    	    
			    	   }
			       }
			       }
			}
			       
			       norm +=  Math.pow(p.gethub(),2); // calculate the sum of the squared auth values to normalise
			    	  
			}
			
			norm = Math.sqrt(norm);
			
			for (ResourceInfo p: StateSpace) // update the auth scores 
			{
			//	System.out.println(" state == " + p.getLabel() + " hub = " + p.gethub() + " authorithy = " + p.getauthorithy());
			//    System.out.println(" norm " + norm);
				p.sethub(p.gethub() / norm);
			//	System.out.println(" state == " + p.getLabel() + " hub = " + p.gethub() + " authorithy = " + p.getauthorithy());
			    
			}
		    
			for (ResourceInfo p: StateSpace) // update the auth scores 
			{
				System.out.println(" state == " + p.getLabel() + " hub = " + p.gethub() + " authorithy = " + p.getauthorithy());
				
			}
		
	}
	
		
	

}
	
	
//###################################################################
	
	public Hashtable<ResourceInfo, Hashtable<ResourceInfo, Double>> buildTransitionMatrixbyWeightedHITS(List<ResourceInfo> states, Hashtable<ResourceInfo, Hashtable<ResourceInfo, Float>> DistanceMatrix)
	{
		int DisSize = DistanceMatrix.size();
		Set<Float> set=new HashSet<Float>();
		
		System.out.println(" DistanceMatrix: " + DistanceMatrix);
		// calculating the transition probability using distance for state by state
		
		for ( ResourceInfo s : states )
		{
			
			System.out.println(" State: " + s.getLabel() );
			if ( DistanceMatrix.containsKey(s) )
			{	
			Hashtable<ResourceInfo, Float> row_Distance = DistanceMatrix.get(s);
			double leftProbability = s.gethub(); // this is the probability of not going to Unknown Entity 
			
			System.out.println(" leftProbability: " + leftProbability );
			
			double sigma		   = 0;			
			for (ResourceInfo c : states)
			{
				if (row_Distance.containsKey(c))
				{
					
				if( row_Distance.get(c) != Constants.MaximumDistance )
				{
					sigma +=c.getauthorithy();
				}
				}
				
			}
			
			
			
			Hashtable<ResourceInfo, Double> tran_row = new Hashtable<ResourceInfo, Double>();
			for (ResourceInfo c : states)
			{
				if(row_Distance.containsKey(c))
				{
				Float distance = row_Distance.get(c);
				if( distance != Constants.MaximumDistance)
				{
					double tran_prob = ((c.getauthorithy())/ sigma) * leftProbability;
					tran_row.put(c, tran_prob );
				}
				
				else
				{
					tran_row.put(c, (double) 0 );
				}
				
				
				}
			}
			
		
				if(!tran_row.isEmpty())
				{
				Trans_Matrix.put(s, tran_row);
				}
				System.out.println( "tran_row " + tran_row);
			
				
				System.out.println(" Trans_row: " + tran_row);
			}	
		}
		System.out.println(" DistanceMatrix: " + DistanceMatrix);
		System.out.println(" Trans_Matrix: " + Trans_Matrix);
		
		return Trans_Matrix;
	}
	
	//########################################
	
	
	
}







/*

for (String s:states)
{
	
	System.out.println(" State: " + s );
	Hashtable<String, Float> row_Distance = DistanceMatrix.get(s);
	
	for (String c:states)
	{
		Float d = row_Distance.get(c);
		if( d != -1 )
		{
		rowElement_list.add(d);
		set.add(d);
		}
		
	}
	
	
	Sorting_list.addAll(set);
	Collections.sort(Sorting_list);
	
	
	
	// finding the rank of each element in a row

    for(int h=0; h < rowElement_list.size();h++)
    {
    	int index 		= 	Sorting_list.indexOf(rowElement_list.get(h));
    	int rank		=	index +1 ;
		rowRank_list.add(h,rank);
    }
	
	
	System.out.println(" row_Distance: " + row_Distance);
	System.out.println(" rowElement_list: " + rowElement_list);
	System.out.println(" set: " + set);
	System.out.println(" Sorting_list: " + Sorting_list);
	System.out.println(" rowRank_list: " + rowRank_list);
	
	
	Double Sigma = 0.0;
	for (int j=0; j<rowRank_list.size(); j++)
	{
		Sigma= Sigma + (1/(Double.parseDouble(Integer.toString((Integer) rowRank_list.get(j)))));
	}
	System.out.println(" Sigma: " + Sigma);
				
	Hashtable<String, Double> tran_row = new Hashtable<String, Double>();
		for (String c:states)
		{
			Float distance = row_Distance.get(c);
			if( distance != -1)
			{
				int index = rowElement_list.indexOf(distance);
				int rank  = (Integer) rowRank_list.get(index);
				Double tran_prob = 1/(rank * Sigma);
				tran_row.put(c, tran_prob );
			}
			else
			{
				tran_row.put(c, (double) -1.0 );
			}
			
		}
		Trans_Matrix.put(s, tran_row);
	
	
	//row_Distance.clear();
	rowElement_list.clear();
	Sorting_list.clear();
	rowRank_list.clear();
	set.clear();

}
System.out.println(" DistanceMatrix: " + DistanceMatrix);
System.out.println(" Trans_Matrix: " + Trans_Matrix);

return Trans_Matrix;*/
