package HMMQuerySegmentation;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.semanticweb.owlapi.model.OWLOntologyCreationException;


public class ObservationState {

	
	private Hashtable< ResourceInfo, List<Integer>> StatewithBorders = 
        	new Hashtable<ResourceInfo, List<Integer>>();
	
	private List<ResourceInfo> StateSpace =  	new ArrayList();
	
	private Hashtable<ArrayList, List<ResourceInfo>> ObservedMap = 
        	new Hashtable<ArrayList, List<ResourceInfo>>();
	
	private List<ArrayList> Observation_list = new ArrayList();
	
	private Hashtable<ResourceInfo, Hashtable<ResourceInfo, Float>> Distance_Matrix = 
        new Hashtable<ResourceInfo, Hashtable<ResourceInfo, Float>>();
		
	
	
	//############################################################
	
	
	public List<ResourceInfo> getStateSpace()
	{
		return StateSpace;
	}
	
//############################################################
	
	
	public Hashtable< ResourceInfo, List<Integer>> getStatewithBorders()
	{
		return StatewithBorders;
	}
	//############################################################
	
	public Hashtable<ResourceInfo, Hashtable<ResourceInfo, Float>> getDistance_Matrix()
	{
		return Distance_Matrix;
	}
	
	
	//############################################################
	//############# This function generate stateSpace along with 
	// the starting and end point of keywords of observable segments    
	//############################################################
	
	public void buildStateSpace(List keywordList){
		
		System.out.println( " hello  to state space ");
		
		for (int i=0 ; i< Observation_list.size(); i++)
		{
			
			List Segment =Observation_list.get(i);
			List<ResourceInfo> r = ObservedMap.get(Segment);
			int Start, End;
			
			Start = keywordList.indexOf(Segment.get(0));
			End = keywordList.indexOf(Segment.get(Segment.size()-1));
			
			// for example for gettysburge battle instead of " battle of gettysburge" 
			if( Start > End)
			{
				int temp = Start;
				Start    = End;
				End		= temp;
			}
			
			List borderPoints = new ArrayList();
			borderPoints.add(Start);
			borderPoints.add(End);
			System.out.println(" segment " + Segment );
			for (int j=0 ; j<r.size() ; j++)
			{
				System.out.println(" resource " + r.get(j).getUri() +"Start= " + Start + "End" + End);
				StatewithBorders.put(r.get(j), borderPoints);
				StateSpace.add(r.get(j));
			}
			
		
		}
		
		/*for (int i=0 ; i< StateSpace.size(); i++)
		{
		System.out.println(" positionha state: " + i +" " + StateSpace.get(i)+ "Start " + StatewithBorders.get(StateSpace.get(i)).get(0) + "End " + StatewithBorders.get(StateSpace.get(i)).get(1));
		}*/
		
	}
	
	//############################################################
	//############# This function generate DistanceMatrix    
	//############################################################
	public void buildDistanceMatrix(){
		
		List<ResourceInfo> NextResources = new ArrayList();
	    EntityRetrieval er =new EntityRetrieval();
		 
		for(int i=0; i<StateSpace.size();i++)
		{
			int End=(Integer) StatewithBorders.get(StateSpace.get(i)).get(1);
			
			for(int j=0; j<StateSpace.size();j++)
			{
				int NStart = (Integer) StatewithBorders.get(StateSpace.get(j)).get(0);
				if(NStart == End+1)
				{
					NextResources.add(StateSpace.get(j));	
				}
				
			}
			
			
			System.out.println(" neighbor entities to "	+ StateSpace.get(i).getUri());
			Hashtable<ResourceInfo, Float> Distance_row = new Hashtable<ResourceInfo, Float>();
			for(int j=0; j<NextResources.size();j++)
			{
				
				int distance=er.getDistance(StateSpace.get(i), NextResources.get(j));
				Distance_row.put(NextResources.get(j),  (float) distance);
				System.out.println( NextResources.get(j).getUri() + "   Distance  " + distance);
				
			}
			
			 Distance_Matrix.put(StateSpace.get(i), Distance_row);
			
			NextResources.clear();
			
		}
		
		System.out.println( "distance matrix");
		System.out.println( Distance_Matrix);
		
	}
	
private void ResourceRetrivalforPattern(String pattern,ArrayList keywordQuery,int start,int end){
	
	//System.out.println(" noooo exist  " +  PatternAsk  + "  ");
	//System.out.println(" pattern  " +  pattern  + "  ");
	
	OwlComponentMapping owlR = new OwlComponentMapping();
	List<ResourceInfo> foundRessources = new ArrayList<ResourceInfo>();
	EntityRetrieval ER 				 = new EntityRetrieval();
	
	ArrayList Segment = new ArrayList();
	
	for (int j= start; j< end; j++)
		{
		Segment.add(keywordQuery.get(j));
		}
	List<ResourceInfo> Resources = null;
	System.out.println(" Segment  " +  Segment  + "  ");
	
	/*try {
	
	Resources = owlR.getMappingOwlClasses(Segment);
	Resources.addAll(owlR.getMappingOwlObjectProperties(Segment));
	Resources.addAll(owlR.getMappingOwlDataTypeProperties(Segment));
	
	
		} catch (OWLOntologyCreationException e) {
	// TODO Auto-generated catch block
	e.printStackTrace();
		}
		if(Resources.size() == 0)
		{
			Resources.addAll( ER.getIRIsForPatterns(pattern ,Segment) );
		}
		System.out.println("resource size " + Resources.size() );

		if( Resources.size() !=0 ){
			ObservedMap.put(Segment, Resources);
			Observation_list.add(Segment);
		}
*/	
}
	//##########################################################################
	//#############     ##############
	//##########################################################################
	
	public void buildGreedyObservationState(ArrayList keywordQuery)
	
	{
		int QuerySize	  				 = keywordQuery.size();
		int Start		 				 = 0;
		EntityRetrieval ER 				 = new EntityRetrieval();
		OwlComponentMapping owlR = new OwlComponentMapping();
		List<ResourceInfo> foundRessources = new ArrayList<ResourceInfo>();
		
		Start			    =	0;
		int Startpointer	=	0;
		int Endpointer		=	0;
		String lastPattern ="";
		while ( Start !=  QuerySize )
		{
			for(int i = Start; i <  QuerySize ; i++)
			{
				
				Startpointer =i;
				String PatternAsk = "";
				String PatternSelect = "";
				
				
				for (int j = Start ; j <= i ; j++)
				{
					if( j != Start)
					{
						PatternAsk = PatternAsk +"  and  "+ keywordQuery.get(j);
						PatternSelect = PatternSelect +"  and  "+ keywordQuery.get(j);
						
					}
					
					else if( j == Start )
					{
						PatternAsk = PatternAsk +"  "+ keywordQuery.get(j);
						PatternSelect = PatternSelect +"  "+ keywordQuery.get(j);
					}
				}
				
				//System.out.println("from " + Start + " to " + i + PatternAsk);
				List<ResourceInfo> Resources = null;
				
				if( ER.checkExistenceOfPatterns(PatternAsk) )
				{
					//System.out.println("  exist  " +  PatternAsk  + "  ");
					lastPattern = PatternAsk;
					if( i ==  QuerySize-1 )
					{
						
						ResourceRetrivalforPattern(PatternAsk, keywordQuery,Start,i+1);
						Start = QuerySize;
						
					}
										
				}
				else
				{
					if( i ==  QuerySize-1 )
					{
						lastPattern = PatternAsk; 	
					}
					
					ResourceRetrivalforPattern(lastPattern , keywordQuery, Start,i);
						
					Start = i;
					i = QuerySize;
				
					
				}// end of else
								
					
		}
				
				
			
			//Start ++;
			
		}
		
		/*while ( Start !=  QuerySize )
		{
		for(int i = Start; i <  QuerySize ; i++)
		{
			pointer = i;
			String PatternAsk = "";
			String PatternSelect = "";
			
			for (int j = Start ; j <= i ; j++)
			{
				if( j < i &&  j != Start)
				{
					PatternAsk = PatternAsk +"  and  "+ keywordQuery.get(j);
					PatternSelect = PatternSelect +"  and  "+ keywordQuery.get(j);
					
				}
				
				else if( j == Start )
				{
					PatternAsk = PatternAsk +"  "+ keywordQuery.get(j);
					
					if( j < i ){
						PatternSelect = PatternSelect +"  "+ keywordQuery.get(j);
						}
					else
					{
						if( i+1 < QuerySize )
						{
						PatternSelect = PatternSelect +"  "+ keywordQuery.get(j)  +"  and not "+ keywordQuery.get(j+1);
						}
						else
						{
						PatternSelect = PatternSelect +"  "+ keywordQuery.get(j);	
						//}
					}
				}
				else if( j== i )
				{
					PatternAsk = PatternAsk +"  and  "+ keywordQuery.get(j);
					if( i+1 < QuerySize )
					{
					PatternSelect = PatternSelect +"  and  "+ keywordQuery.get(j)  +"  and not "+ keywordQuery.get(j+1);
					}
					else
					{
						PatternSelect = PatternSelect +"  and  "+ keywordQuery.get(j);	
					//}
					
				}
				
				if( Start == QuerySize - 1)
				{
					if (j-1 >= 0){
					PatternAsk = PatternAsk +" and not "+ keywordQuery.get(j-1);	
					PatternSelect = PatternSelect +" and not  "+ keywordQuery.get(j-1);}
				}
			}
			
			
			System.out.println("Ask:   " +  PatternAsk  + "  ");
			System.out.println("Select:   " +  PatternSelect  + "  ");
			
			List<ResourceInfo> Resources = null;
			//Resources = ER.getIRIsForPatterns(PatternAsk);
			
		//	if (Resources.size() == 0) // is the pattern valid 
			
			
			if(!ER.checkExistenceOfPatterns(PatternAsk))
			{
				System.out.println(" noooo exist  " +  PatternAsk  + "  ");
				i=QuerySize;
			
			}
			else
			{
				System.out.println("  exists  " +  PatternAsk  + "  ");	
		    ArrayList Segment = new ArrayList();
			for (int j=Start; j<= i; j++)
				{
				Segment.add(keywordQuery.get(i));
				}	
			
			try {
				
				Resources = owlR.getMappingOwlClasses(Segment);
				Resources.addAll(owlR.getMappingOwlObjectProperties(Segment));
				Resources.addAll(owlR.getMappingOwlDataTypeProperties(Segment));
				
				
			} catch (OWLOntologyCreationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//if(Resources.size() == 0)
			//{
			Resources.addAll( ER.getIRIsForPatterns(PatternSelect ,Segment) );
			//}
			System.out.println("resource size " + Resources.size() );
			
			if( Resources.size() !=0 ){
			ObservedMap.put(Segment, Resources);
			Observation_list.add(Segment);
			}
			}
		
		}
		Start = pointer;
		}
		
		System.out.println(" list of segments  ");
		
		for (int i=0 ; i< Observation_list.size(); i++)
		{
			
			System.out.println(Observation_list.get(i));
			List<ResourceInfo> r = ObservedMap.get(Observation_list.get(i));
			System.out.println(" size of retrieved iris =   " +r.size());
			for (int j=0 ; j<r.size() ; j++)
			{
			System.out.println(r.get(j).getUri() + "     " + r.get(j).getLabel() + "  " + r.get(j).getStringSimilarityScore());
			}
			
			
		}
		*/
		
	}
		

	//################################
	//#############     ##############
	//################################
	public void buildModerateObservationState(ArrayList keywordQuery)
	{
		//List<ArrayList> Observation_list = new ArrayList();
		int QuerySize	  				 = keywordQuery.size();
		int Start		 				 = 0;
		EntityRetrieval ER 				 = new EntityRetrieval();
		OwlComponentMapping owlR = new OwlComponentMapping();
		List<ResourceInfo> foundRessources = new ArrayList<ResourceInfo>();
	//	Hashtable<ArrayList, List<ResourceInfo>> ObservedPointers = 
     //   new Hashtable<ArrayList, List<ResourceInfo>>();
		
		
		while ( Start !=  QuerySize )
		{
		for(int i = Start; i< QuerySize; i++)
		{
			String PatternAsk = "";
			String PatternSelect = "";
			
			for (int j=Start; j<= i; j++)
			{
				if( j < i &&  j != Start){
					PatternAsk = PatternAsk +"  and  "+ keywordQuery.get(j);
					PatternSelect = PatternSelect +"  and  "+ keywordQuery.get(j);
					}
				else if( j == Start )
				{
					PatternAsk = PatternAsk +"  "+ keywordQuery.get(j);
					if(j<i){
						PatternSelect = PatternSelect +"  "+ keywordQuery.get(j);}
					else
					{
						if( i+1 < QuerySize )
						{
						PatternSelect = PatternSelect +"  "+ keywordQuery.get(j)  +"  and not "+ keywordQuery.get(j+1);
						}
						else
						{
						PatternSelect = PatternSelect +"  "+ keywordQuery.get(j);	
						}
					}
				}
				else if( j== i )
				{
					PatternAsk = PatternAsk +"  and  "+ keywordQuery.get(j);
					if( i+1 < QuerySize )
					{
					PatternSelect = PatternSelect +"  and  "+ keywordQuery.get(j)  +"  and not "+ keywordQuery.get(j+1);
					}
					else
					{
						PatternSelect = PatternSelect +"  and  "+ keywordQuery.get(j);	
					}
					
				}
				
				if( Start == QuerySize-1)
				{
					if (j-1 >= 0){
					PatternAsk = PatternAsk +" and not "+ keywordQuery.get(j-1);	
					PatternSelect = PatternSelect +" and not  "+ keywordQuery.get(j-1);}
				}
			}
			
			
			System.out.println("Ask:   " +  PatternAsk  + "  ");
			System.out.println("Select:   " +  PatternSelect  + "  ");
			
			List<ResourceInfo> Resources = null;
			//Resources = ER.getIRIsForPatterns(PatternAsk);
			
		//	if (Resources.size() == 0) // is the pattern valid 
			
			
			if(!ER.checkExistenceOfPatterns(PatternAsk))
			{
				System.out.println(" noooo exist  " +  PatternAsk  + "  ");
				i=QuerySize;
			
			}
			else
			{
				
		    ArrayList Segment = new ArrayList();
			for (int j=Start; j<= i; j++)
				{
				Segment.add(keywordQuery.get(j));
				}	
			
			try {
				
				Resources = owlR.getMappingOwlClasses(Segment);
				Resources.addAll(owlR.getMappingOwlObjectProperties(Segment));
				Resources.addAll(owlR.getMappingOwlDataTypeProperties(Segment));
				
				
			} catch (OWLOntologyCreationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//if(Resources.size() == 0)
			//{
			Resources.addAll( ER.getIRIsForPatterns(PatternSelect ,Segment) );
			//}
			System.out.println("resource size " + Resources.size() );
			
			if( Resources.size() !=0 ){
			ObservedMap.put(Segment, Resources);
			Observation_list.add(Segment);
			
			
			//System.out.println("Resources" + Resources);
			
			
			}
			}
			//Resources.clear();
		}
		Start++;
		}
		
		/*System.out.println("look at observed map)");
		for (int i=0; i<= Observation_list.size(); i++)
		{
			System.out.println("Segment" + Segment);
			ObservedMap.get(Observation_list.get(i));
		}
		*/
		System.out.println(" list of segments  ");
		
		for (int i=0 ; i< Observation_list.size(); i++)
		{
			
			System.out.println(Observation_list.get(i));
			List<ResourceInfo> r = ObservedMap.get(Observation_list.get(i));
			System.out.println(" size of retrieved iris =   " +r.size());
			for (int j=0 ; j<r.size() ; j++)
			{
			System.out.println(r.get(j).getUri() + "     " + r.get(j).getLabel() + "  " + r.get(j).getStringSimilarityScore());
			}
			
			
		}
		
		
	}
	
	
}


// ==========================================

/*PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> 
	 SELECT distinct * WHERE { ?iri rdfs:label ?label.   
				 FILTER( langMatches(lang(?label), "en")). 
	FILTER bif:contains (?label,   "marshall and garry and not films").}*/
