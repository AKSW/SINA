package HMMQuerySegmentation;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javatools.datatypes.Immutable.Set;


public class HiddenMarkovModel {
	
	
 private static Hashtable< ResourceInfo, List<Integer>> StatewithBorders	  		 		= 
    	new Hashtable<ResourceInfo, List<Integer>>();

 private static List<ResourceInfo> StateSpace 											    =  	new ArrayList();

 private static Hashtable<ArrayList, List<ResourceInfo>> ObservedMap					    = 
    	new Hashtable<ArrayList, List<ResourceInfo>>();
 private static List<ArrayList> Observation_list 										    = new ArrayList();

 private static Hashtable<ResourceInfo, Hashtable<ResourceInfo, Float>> Distance_Matrix 	= 
    new Hashtable<ResourceInfo, Hashtable<ResourceInfo, Float>>();
  
 private static Hashtable<ResourceInfo, Hashtable<ResourceInfo, Double>> Trans_Matrix	    = new  Hashtable<ResourceInfo, Hashtable<ResourceInfo, Double>>();
 private static Hashtable<ResourceInfo, Hashtable<Integer, Double>> Emission_Matrix 	    = new  Hashtable<ResourceInfo, Hashtable<Integer, Double>>();
 private static Hashtable<ResourceInfo, Double> Start_Probability_Matrix 					= new  Hashtable<ResourceInfo, Double>();
 private static List<ResourceInfo> BestPath 												= new ArrayList(); 	
 private static Hashtable<ResourceInfo,  List<Object[]>> TraceTree						    = new Hashtable<ResourceInfo,  List<Object[]>>();
 private static double 									 beta								=	0;	
 private static LinkedList<Object[]> 					listSortedPaths					    = new LinkedList<Object[]>();	
 
 
 public void initialization()
 {
	 StatewithBorders.clear();	
	 StateSpace.clear();
	 ObservedMap.clear();
	 Observation_list.clear();
	 Distance_Matrix.clear();
	 Trans_Matrix.clear();
	 Emission_Matrix.clear();
	 Start_Probability_Matrix.clear();
	 BestPath.clear();
	 TraceTree.clear();
	 listSortedPaths.clear();
 }
 
public void test()
{
	// List<ResourceInfo> StateSpace = new ArrayList();
	  
	  ResourceInfo e1				= new ResourceInfo();
	  ResourceInfo e2				= new ResourceInfo();
	  ResourceInfo e3				= new ResourceInfo();
	  ResourceInfo e4				= new ResourceInfo();
	  
	  e1.setLabel("e1");
	  e2.setLabel("e2");
	  e3.setLabel("e3");
	  e4.setLabel("e4");
	  
	  StateSpace.add(e1);
	  StateSpace.add(e2);
	  StateSpace.add(e3);
	  StateSpace.add(e4);
	  
	 // Hashtable<ResourceInfo, Hashtable<ResourceInfo, Float>> Distance_Matrix = new Hashtable<ResourceInfo, Hashtable<ResourceInfo, Float>> ();
	  
	  Hashtable<ResourceInfo, Float> row =new Hashtable<ResourceInfo, Float>();
	  row.put(e2, (float) 1.0);
	  row.put(e4, (float) 2.0);
	  Distance_Matrix.put(e1, row);
	  
	  row =new Hashtable<ResourceInfo, Float>();
	  row.put(e4, (float) 3.0);
	  Distance_Matrix.put(e3, row);
	  
	  
	  MarkovParameters mp = new MarkovParameters();
	  mp.ApplyingHITS(StateSpace, Distance_Matrix);
	  mp.buildTransitionMatrixbyWeightedHITS(StateSpace, Distance_Matrix);
}
public void StartMarkovModel(String query)
{
	 ArrayList keywordQuery = new ArrayList();
		
	// keywordQuery.add("germany");
	// keywordQuery.add("capital");
	 
	 Functionality f= new Functionality();
		 
	//	  String query		 =" revenue of IBM ";
		 //Is Egypt largest city also its capital?
		  //" film directed Garry Marhsall starring Julia Roberts ";
		  PreProcessing PP 	 = new PreProcessing();
		  keywordQuery		 = PP.removeStopWordswithlem(query);
		/*  keywordQuery.add("Egypt");
		  keywordQuery.add("largest");
		  keywordQuery.add("city");
		  keywordQuery.add("capital");*/
		// ArrayList l = PP.removeStopWordswithlem(" Who created Wikipedia ");
		
			
		  for(int j=0; j < keywordQuery.size() ; j++)
		  {
			  System.out.println("keyword :   " + keywordQuery.get(j));
		  }
		  
		  ObservationState ObservationObject = new ObservationState();
		  ObservationObject.buildModerateObservationState(keywordQuery);
		 // ObservationObject.buildGreedyObservationState(keywordQuery);
		  
		  ObservationObject.buildStateSpace(keywordQuery);
		  ObservationObject.buildDistanceMatrix();
		  
		  StateSpace 			=  ObservationObject.getStateSpace();
		  StatewithBorders 	    =  ObservationObject.getStatewithBorders();
		  Distance_Matrix 	    =  ObservationObject.getDistance_Matrix();
		  
		  
		  MarkovParameters MM   = new MarkovParameters();
		  MM.ApplyingHITS(StateSpace, Distance_Matrix);
		  MM.buildTransitionMatrixbyWeightedHITS(StateSpace, Distance_Matrix);
		  Trans_Matrix 			= MM.getTrans_Matrix();
	
		  buildEmission_Matrix(keywordQuery);
		  buildStart_Probability_BasedonHITS();
		  
		  System.out.println(" Emission_Matrix " + Emission_Matrix);
		  System.out.println(" Start_Probability_Matrix " + Start_Probability_Matrix);
		  Object[] ret = forward_viterbi( keywordQuery);
		 
	        
	   
		 BestPath.clear();
		 BestPath = ((List) ret[3]);
	     System.out.println("total probability: " +((Double) ret[0]).doubleValue());             
	     System.out.println("path of maximum: " +(String) ret[1]);
	     System.out.println("maximum probability: " +((Double) ret[2]).doubleValue());
	     System.out.println("Best Path Entites: " +BestPath);
	     System.out.println("  **********************  TraceTree ******************");
	     
	     for(ResourceInfo s:StateSpace)
	     {
	    	 if(TraceTree.containsKey(s))
	    	 {
	    		 System.out.println("$$$$$$$$$$$$$$$$" + s.getUri() + "$$$$$$$$$$$$$$$$$$$$$$$");
	    		 List<Object[]> y = TraceTree.get(s);
	    		 
	    		 System.out.println(" TraceTree SIZE "+ y.size());
	    		 for (int i=0; i< y.size(); i++)
	    		 {
	    			
	    		  String path_state 	= "";
                  double probability_state 	= 1; 	  
               		  
               	  Object[] Traceobjs  = y.get(i);
               	  probability_state	 = ((Double) Traceobjs[0]).doubleValue();
               	  path_state 	     = (String) Traceobjs[1];
               	//  List pathEntityList_source_state 		 = ((List) Traceobjs[2]);
               	  
               	System.out.println(" probability_state " + probability_state);
               	System.out.println(" path_state " + path_state);
               //	System.out.println(" pathEntityList " + pathEntityList_source_state); 
	    			 
	    			 
	    		 }
	    	 }
	     }
	     
	     sortTraceTree();
	     
	     
	    /* List<ResourceInfo> TempStateSpace =  	new ArrayList();
	     Hashtable<ResourceInfo, Hashtable<ResourceInfo, Double>> Temp_Trans_Matrix = new  Hashtable<ResourceInfo, Hashtable<ResourceInfo, Double>>();
	     Hashtable<ResourceInfo, Hashtable<Integer, Double>> Temp_Emission_Matrix = new  Hashtable<ResourceInfo, Hashtable<Integer, Double>>();
	     Hashtable<ResourceInfo, Double> Temp_Start_Probability_Matrix = new  Hashtable<ResourceInfo, Double>();
	     List<ResourceInfo> BestSecondPath = new ArrayList(); 		
	    	
	     
	     for(ResourceInfo s: BestPath)
	     {
	    	
	    	 
	    	 Collections.copy(TempStateSpace,StateSpace);
	    	 TempStateSpace.remove(s);
	    	 MM.ApplyingHITS(TempStateSpace, Distance_Matrix);
			 MM.buildTransitionMatrixbyWeightedHITS(TempStateSpace, Distance_Matrix);
			 // MM.buildTransitionMatrix(StateSpace, Distance_Matrix);
			 Temp_Trans_Matrix 			= MM.getTrans_Matrix();
			 //buildEmission_Matrix(keywordQuery);
			 // buildStart_Probability_Matrix();
			  buildStart_Probability_BasedonHITS();
			  
			  System.out.println(" Emission_Matrix " + Emission_Matrix);
			  System.out.println(" Start_Probability_Matrix " + Start_Probability_Matrix);
		
			  Object[] ret = forward_viterbi( keywordQuery);
			 
		        
		      //  for (String ob:observations){
		    // System.out.println(" observation " + ob ); } 
		        
		    //    for(String s: states){
		   //  System.out.println(" states " + s ); }  
			  
			 BestPath = ((List) ret[3]);
		     System.out.println("total probability: " +((Double) ret[0]).doubleValue());             
		     System.out.println("path of maximum: " +(String) ret[1]);
		     System.out.println("maximum probability: " +((Double) ret[2]).doubleValue());
		     System.out.println("Best Path Entites: " +BestPath);
		     System.out.println("  **********************  Best Second Path******************");
		     
	    	 
	    	 
	    	 
	    	 
	     }*/
	    
	     //  Second Best Path
	     
	     /*Hashtable<ResourceInfo, Double> change = new Hashtable<ResourceInfo, Double>();
	     change.put(BestPath.get(1), (Double)0.0);
	     Trans_Matrix.put(BestPath.get(0),change);
	     
	     BestPath.clear();
	     
	     
	     ret = forward_viterbi( keywordQuery);
		 
	        
	      //  for (String ob:observations){
	    // System.out.println(" observation " + ob ); } 
	        
	    //    for(String s: states){
	   //  System.out.println(" states " + s ); }  
		  
		 BestPath = ((List) ret[3]);
	     System.out.println("total probability: " +((Double) ret[0]).doubleValue());             
	     System.out.println("path of maximum: " +(String) ret[1]);
	     System.out.println("maximum probability: " +((Double) ret[2]).doubleValue());
	     System.out.println("Best Path Entites: " +BestPath);*/
	
}
  //############################################################

public void sortTraceTree()
{
	
	
	
	 for(ResourceInfo s:StateSpace)
	    {
	   	 if(TraceTree.containsKey(s))
	   	 {
	   		
	   		 List<Object[]> y = TraceTree.get(s);
	   		 
	   		 //System.out.println(" TraceTree SIZE "+ y.size());
	   		
	   		 for (int i=0; i< y.size(); i++)
	   		 {
	   			listSortedPaths.add(y.get(i));
	   		 }
	   	 }
	    }
	    

	Collections.sort(listSortedPaths, new Comparator<Object[]>() {
        public int compare(Object[] o1, Object[] o2) {
        	double p1 = ((Double) o1[0]).doubleValue();
        	double p2 = ((Double) o2[0]).doubleValue();
        	
        	if( p1 > p2 )
        	{ return 1;}
        	else if( p1 < p2 ) 
        	{return -1;}
        	else
        	{return 0;}
        }
	
	});
	
	for( int i=0; i<listSortedPaths.size(); i++)
	{
		Object[] y = listSortedPaths.get(i);
		double p = ((Double) y[0]).doubleValue();
		String path_state 	     = (String) y[1];
		
		System.out.print(p + ": "); 
	    System.out.println(path_state); 
	}
     
	Object[] y = listSortedPaths.getLast();
	double p = ((Double) y[0]).doubleValue();
	String path_state 	     = (String) y[1];
	
	// System.out.println("first path == p=" + p + " path_state=" + path_state);  
    
	// System.out.println("function best path" + getSecondbestpath());
}

//#################################################################
public String getFirstbestpath()
{
	Object[] y = listSortedPaths.getLast();
	double p = ((Double) y[0]).doubleValue();
	String path_state 	     = (String) y[1];
	//System.out.println("first path == p=" + p + " path_state=" + path_state);
	
	return path_state;
}

//#################################################################

public String getSecondbestpath()
{
	int index  = listSortedPaths.size()-2;
	Object[] y = listSortedPaths.get(index);
	double p = ((Double) y[0]).doubleValue();
	String path_state 	     = (String) y[1];
	//System.out.println("first path == p=" + p + " path_state=" + path_state);
	
	return path_state;
}



//####################################################################

public LinkedList<Object[]> getSortedList()
{
	return listSortedPaths;
}



//#################################################################
  
  public static Object[] forward_viterbi(ArrayList keywordQuery)
{
	 
	  ArrayList obs 														= keywordQuery;
	  List<ResourceInfo> states 											= StateSpace;
	  Hashtable<ResourceInfo, Double> start_p 								= Start_Probability_Matrix;
	  Hashtable<ResourceInfo, Hashtable<ResourceInfo, Double>> trans_p  	= Trans_Matrix;
	  Hashtable<ResourceInfo, Hashtable<Integer, Double>> emit_p 			= Emission_Matrix;
	  Hashtable<ResourceInfo, Object[]> T								    = new Hashtable<ResourceInfo, Object[]>();
	  
	 
	  // System.out.println(" Trans_Matrix " + Trans_Matrix);
	// Hashtable<ResourceInfo, List<Object[]>> TraceTree = new Hashtable<ResourceInfo, List<Object[]>>();
      
	  //List<Object[]> TraceList;
	  for (ResourceInfo state : states)
	  {
		  List pathEntities = new ArrayList();
		  pathEntities.add(state);
		  Object[] Obj = new Object[]{start_p.get(state), state.getUri(), start_p.get(state), pathEntities};
          T.put(state, Obj);
          
         // Hashtable<ResourceInfo, List<Object[]>> TraceTree = new Hashtable<ResourceInfo, List<Object[]>>();
          Object[] Obj2 = new Object[]{start_p.get(state), state.getUri(), pathEntities};
          
          List<Object[]> TraceList = new ArrayList();
          TraceList.add(Obj2);
          TraceTree.put(state, TraceList);
	  
	  }
	  
	  
	     System.out.println("**********************  Initialization Step ******************");
	     
	     for(ResourceInfo s:states)
	     {
	    	 if(TraceTree.containsKey(s))
	    	 {
	    		 
	    		 System.out.println("$$$$$$$$$$$$$$$$" + s.getUri() + "$$$$$$$$$$$$$$$$$$$$$$$");
	    		 //List<ObjecTraceList = new ArrayList();
	    		 List<Object[]> t = TraceTree.get(s);
	    		 System.out.println(" TraceList size " + t.size());
	    		 for (int i=0; i< t.size(); i++)
	    		 {
	    		  System.out.println("hello saeedeh  Initialization Step ");
	    		  String path_state 	= "";
                  double probability_state 	= 1; 	  
            		  
            	  Object[] Traceobjs  = t.get(i);
            	  probability_state	 = ((Double) Traceobjs[0]).doubleValue();
            	  path_state 	     = (String) Traceobjs[1];
            	 // List pathEntityList_source_state 		 = ((List) Traceobjs[2]);
            	  
            	System.out.println(" probability_state " + probability_state);
            	System.out.println(" path_state " + path_state);
            	 
	    			 
	    			 
	    		 }
	    	 }
	    	 
	    	 else 
	    	 {
	    		 System.out.println(" does not contain ");
	    	 }
	     }
		 
 
	for ( int i=0; i< obs.size()-1 ; i++ )	 
  {
          Hashtable<ResourceInfo, Object[]> U = new Hashtable<ResourceInfo, Object[]>();
          Hashtable<ResourceInfo,  List<Object[]>> TemporaryTraceTree	 = new Hashtable<ResourceInfo,  List<Object[]>>();
         // TemporaryTraceTree = TraceTree;
          
          double valmax_QueryCleaning 	= 0;
          
          System.out.println(" iii:  " + i);
          for (ResourceInfo next_state : states)
          {
                  double total		= 0;
                  String argmax 	= "";
                  double valmax 	= 0;
                  List   BPath        = new ArrayList();
                                    
                  double prob 		= 1;
                  String v_path 	= "";
                  double v_prob 	= 1; 
                  
                  List<Object[]> TracePaths_next_state = new ArrayList();
                   
                  System.out.println(".... trace 2...");
                  System.out.println(".... next_state ..." + next_state.getUri());
                  
                  for (ResourceInfo source_state : states)
                  {
                          Object[] objs  = T.get(source_state);
                          prob			 = ((Double) objs[0]).doubleValue();
                          v_path 	     = (String) objs[1];
                          v_prob		 = ((Double) objs[2]).doubleValue();
                          List path		 = ((List) objs[3]);
                                                    
                          int Start		 = StatewithBorders.get(source_state).get(0);
                          int End		 = StatewithBorders.get(source_state).get(1);
                          double trans	 = 0;
                          
                          
                          System.out.println(".... source_state ..." + source_state.getUri());
                          
                          
                          
                        if ( i == End )
                          { 
                        	
                            
                        // System.out.println(" emit_p " + emit_p.get(source_state).get(i) );
                        	  
                        	if(trans_p.containsKey(source_state)){
                        	  if(trans_p.get(source_state).get(next_state) != null)
                        	  {
                        		  
                        		  trans = trans_p.get(source_state).get(next_state);
                         //  System.out.println(" trans_p " + (trans_p.get(source_state)).get(next_state));
                        		
                        	  }}
                         // double p = emit_p.get(source_state).get(i) *
                                           // trans_p.get(source_state).get(next_state);
                        	
                        	 
                         double p = emit_p.get(source_state).get(i) * trans;
                                            
                          prob 		*= p;
                          v_prob 	*= p;
                          total	    += prob;
                          if (v_prob > valmax)
                          {
                        	      argmax	 = v_path + " , " + next_state.getUri();
                                  valmax	 = v_prob;
                                  BPath.clear();
                                  BPath.addAll(path);
                                  BPath.add(next_state);
                          }
                          
                        
                          if( p != 0 && TraceTree.containsKey(source_state))
                          {
                        	  List<Object[]> TracePaths_source_state = TraceTree.get(source_state);
                              
                        	  
                        	  for (int j=0; j < TracePaths_source_state.size() ; j++ )
                        	  {
                        	  //double probalityTotal 		= 1;
                              String path_source_state 	= "";
                              double probability_source_state 	= 1; 	   
                        	  Object[] Traceobjs  = TracePaths_source_state.get(j);
                        	  probability_source_state	 = ((Double) Traceobjs[0]).doubleValue();
                        	  path_source_state 	     = (String) Traceobjs[1];
                        	  List pathEntityList_source_state 		 = ((List) Traceobjs[2]);
                        	  
                        	  
                             // prob			 = ((Double) Traceobjs[0]).doubleValue();
                             /* v_path 	     = (String) Traceobjs[1];
                              v_prob		 = ((Double) Traceobjs[2]).doubleValue();
                              List path		 = ((List) Traceobjs[3]);
                        	 */
                        	  probability_source_state *= p;  
                        	 path_source_state = path_source_state + " , " + next_state.getUri();
                        	 pathEntityList_source_state.add(next_state);
                        	//  pathEntityList_source_state.clear();
                        	//  pathEntityList_source_state.addAll(BPath);
                        	  Object[] Obj = new Object[]{probability_source_state, path_source_state, pathEntityList_source_state};
                              if(p!=0){
                        	  TracePaths_next_state.add(Obj);}
                             // System.out.println("...... object .... " + path_source_state);
                             // System.out.println("...... next_state ..... " + next_state.getUri());
                             // System.out.println("...... TracePaths_next_state.size .... " + TracePaths_next_state.size());
                              
                              
                              
                        	  } 
                        	 
                          }
                          
                         
                          
                         }
                        
                          else if( Start <= i &&  i < End )
                          {
                        	  
                        	  if( next_state.equals( source_state))
                        	  {
                        		  
                        	      if (v_prob > valmax)
                                  {
                                          argmax = v_path;// + " , " + next_state.getUri();
                                          valmax = v_prob;
                                          BPath.clear();
                                          BPath.addAll(path);
                                          
                                  } 
                        	      
                        	      if(TraceTree.containsKey(next_state))
                        	      {
                        	      TracePaths_next_state.addAll( TraceTree.get(next_state));
                        	    
                        	      }
                        	      
                        	  }
                        	  else
                        	  {
                        		  
                        		 /* double p   = 0;
                        		  prob 		*= p;
                                  v_prob 	*= p;
                                  total	    += prob;
                                  if (v_prob > valmax)
                                  {
                                          argmax = v_path ;//+ " , " + next_state.getUri();
                                          valmax = v_prob;
                                          BPath.clear();
                                          BPath.addAll(path);
                                          //BPath.add(next_state);
                                  } */
                                  
                                 
                        	  }
                        	  
                        	  System.out.println(".... trace  4 ..." );
                          }
                          else if( i > End  ||  i < Start )
                          {
                        	  System.out.println(".... trace  5 ..." );
                        	  double p   = 0;
                    		  prob 		*= p;
                              v_prob 	*= p;
                              total	    += prob;
                              if (v_prob > valmax)
                              {
                                    //  argmax = v_path + " , " + next_state.getUri();
                                      valmax = v_prob;
                                      BPath.clear();
                                      BPath.addAll(path);
                                    //  BPath.add(next_state);
                              } 
                              
                              System.out.println(".... trace  6 ..." );
                    	}
                          
                     
                  }
                  
                  if(TracePaths_next_state.size() != 0)
                  {  
                  TemporaryTraceTree.put(next_state, TracePaths_next_state);
                  }
                  System.out.println(" khaterkhah next_state " + next_state.getUri());
                  System.out.println(" khaterkhah " + BPath);
                  U.put(next_state , new Object[] {total, argmax, valmax,BPath});
                 // BPath.clear();
         
                 /* if( valmax > valmax_QueryCleaning)
                  {
                  valmax_QueryCleaning =valmax;
                  
                  }*/
          }
          
        //  System.out.println(".... valmax_QueryCleaning ..." +valmax_QueryCleaning);
       //   System.out.println(".... vkeyword at position..." +obs.get(i));
          
       //  if (  valmax_QueryCleaning  !=  0 )
      //   {
       //   System.out.println(".... dirty query...");
       //   System.out.println("1.... T size..." + T.size());
          
          T = U;
          System.out.println("......before change TraceTree .... " + TraceTree.size());
          TraceTree = TemporaryTraceTree;
          
        // }
        
         
          System.out.println("...... Final Check .... " );
          System.out.println("...... TraceTree .... " + TraceTree.size());
          
          
  }
	
	System.out.println("....gogoli. hello saeedeh  end main step....");
	System.out.println("  ********************** End Loop Step ******************");
    
    for(ResourceInfo s:states)
    {
   	 if(TraceTree.containsKey(s))
   	 {
   		 
   		 System.out.println("$$$$$$$$$$$$$$$$" + s.getUri() + "$$$$$$$$$$$$$$$$$$$$$$$");
   		 //List<ObjecTraceList = new ArrayList();
   		 List<Object[]> t = TraceTree.get(s);
   		 System.out.println(" TraceList size " + t.size());
   		 for (int i=0; i< t.size(); i++)
   		 {
   			 
   		 System.out.println("hello saeedeh last step");
   		 String path_state 	= "";
         double probability_state 	= 1; 	  
       		  
       	  Object[] Traceobjs  = t.get(i);
       	  probability_state	 = ((Double) Traceobjs[0]).doubleValue();
       	  path_state 	     = (String) Traceobjs[1];
       	 // List pathEntityList_source_state 		 = ((List) Traceobjs[2]);
       	  
       	System.out.println(" probability_state " + probability_state);
       	System.out.println(" path_state " + path_state);
       	 
   			 
   			 
   		 }
   	 }
   	 
   	 else 
   	 {
   		 System.out.println(" does not contain ");
   	 }
    }	


System.out.println("......End Step .... " );	
int i = obs.size()-1;	
Hashtable<ResourceInfo, Object[]> U = new Hashtable<ResourceInfo, Object[]>();
double total		= 0;
String argmax 		= "";
double valmax 		= 0;

double prob 		= 1;
String v_path 		= "";
double v_prob 		= 1; 
List BPath = new ArrayList();

Hashtable<ResourceInfo,  List<Object[]>> TemporaryTraceTree	 = new Hashtable<ResourceInfo,  List<Object[]>>();


	for (ResourceInfo source_state : states)
	{
		List<Object[]> TracePaths_source_state = new ArrayList();
		List<Object[]> TracePaths_next_state = new ArrayList();
		
		Object[] objs 	    = T.get(source_state);
        prob			    = ((Double) objs[0]).doubleValue();
        v_path 	   		    = (String) objs[1];
        v_prob			    = ((Double) objs[2]).doubleValue();
        List path 			= ((List) objs[3]);
                                          
        int Start		    = StatewithBorders.get(source_state).get(0);
        int End			    = StatewithBorders.get(source_state).get(1);
        
        System.out.println("special source_state" + source_state.getUri() ); 
        System.out.println("special path" + BPath ); 
        if ( End == i )
         {	 
           double p = emit_p.get(source_state).get(i);
                             
           prob *= p;
           v_prob *= p;
           total += prob;
          
           if (v_prob > valmax)
           {
             argmax = v_path; //;+ "," + source_state.getUri();
             valmax = v_prob;
             BPath.clear();
             BPath.addAll(path);
            
           }
           
           if( p != 0)
           {
         	  System.out.println("...... adding step .... ");
        	   
        	  if(TraceTree.containsKey(source_state))
        	  {
         	  TracePaths_source_state = TraceTree.get(source_state);
              
         	  for (int j=0; j < TracePaths_source_state.size() ; j++ )
         	  {
         	  //double probalityTotal 		= 1;
         	
              String path_source_state 	= "";
              double probability_source_state 	= 1; 	  
         		  
         	  Object[] Traceobjs  = TracePaths_source_state.get(j);
         	  probability_source_state	 = ((Double) Traceobjs[0]).doubleValue();
         	  path_source_state 	     = (String) Traceobjs[1];
         	  List pathEntityList_source_state 		 = ((List) Traceobjs[2]);
         	  
         	 
         	  probability_source_state *= p;  
         	  Object[] Obj = new Object[]{probability_source_state, path_source_state, pathEntityList_source_state};
              TracePaths_next_state.add(Obj);
              
         	  
         	  } 
        	  }
        	  else
        	  {
        		  System.out.println("......source node does not exist .... " );   
        	  }
        	  
        	  
        	  
        	  if(TracePaths_next_state.size() != 0)
              {  
           	   TemporaryTraceTree.put(source_state, TracePaths_next_state);
           	   
           	   System.out.println("goli adds: " + source_state.getLabel());
            	System.out.println("Start position " + Start);
            	System.out.println("End position " + End);
                
              }
        	  
        	  TracePaths_source_state.clear();
         	
           }
          
           System.out.println(" goli path " + BPath);
           U.put(source_state , new Object[] {total, argmax, valmax, BPath}); 
           
                               
         }
       
       
        else if(i != End )
         {	             	 
                double p	  = 0 ;                  
                prob		 *= p;
                v_prob		 *= p;
                total 		 += prob;
                if (v_prob > valmax)
                {
                        argmax = v_path ;//+ "," + source_state.getUri();
                        valmax = v_prob;
                        BPath.clear();
                        BPath.addAll(path);
                        
                }
                TracePaths_next_state.clear();          
       }
        
        
      
       
      //System.out.println("saeedeh pay attention Best Path Entites: " + BPath);
	}
T = U;  
TraceTree.clear();
TraceTree = TemporaryTraceTree;
System.out.println(" size of TraceTree " +TraceTree.size());

System.out.println("saeedeh pay attention Best Path Entites: " +BestPath);
/*System.out.println(" very crucial point ");
for(ResourceInfo s:states)
{
	 if(TraceTree.containsKey(s))
	 {
		 
		 System.out.println("$$$$$$$$$$$$$$$$" + s.getUri() + "$$$$$$$$$$$$$$$$$$$$$$$");
		 //List<ObjecTraceList = new ArrayList();
		 List<Object[]> t = TraceTree.get(s);
		 System.out.println(" TraceList size " + t.size());
		 for (int i1=0; i1< t.size(); i1++)
		 {
			 
		 System.out.println("hello saeedeh last step");
		 String path_state 	= "";
     double probability_state 	= 1; 	  
   		  
   	  Object[] Traceobjs  = t.get(i1);
   	  probability_state	 = ((Double) Traceobjs[0]).doubleValue();
   	  path_state 	     = (String) Traceobjs[1];
   	 // List pathEntityList_source_state 		 = ((List) Traceobjs[2]);
   	  
   	System.out.println(" probability_state " + probability_state);
   	System.out.println(" path_state " + path_state);
   	 
			 
			 
		 }
	 }
	 
	 else 
	 {
		 System.out.println(" does not contain ");
	 }
}
System.out.println(" end crucial point ");*/
 /*  
  double total = 0;
  String argmax = "";
  double valmax = 0;
  double prob;
  String v_path;
  double v_prob;
  List path;
  
  */

  for (ResourceInfo state : states)
  {
	  
	  if(T.containsKey(state))
	  {
          Object[] objs = T.get(state);
          prob = ((Double) objs[0]).doubleValue();
          v_path = (String) objs[1];
          v_prob = ((Double) objs[2]).doubleValue();
          List path = ((List) objs[3]);
          total += prob;
          if (v_prob > valmax)
          {
                  argmax = v_path;
                  valmax = v_prob;
                  BPath.clear();
                  BPath.addAll(path);
                  
          }
	  }   
  }
  
  
  System.out.println(" %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% " );
  System.out.println(" T matrix " );
  /*for (ResourceInfo state : states)
  {
	  System.out.println(" ******************************************** " );
	  System.out.println(" Entity " + state.getUri()); 
	  Object[] objs = T.get(state);
      prob 			= ((Double) objs[0]).doubleValue();
      v_path 		= (String) objs[1];
      v_prob	    = ((Double) objs[2]).doubleValue();
      List path 			= ((List) objs[3]);
      System.out.println(" total  " + prob); 
      System.out.println(" v_path " + v_path); 
      System.out.println(" prob_max " + v_prob); 
      System.out.println(" path entities " + path); 
	   
	  
  }*/
  System.out.println(" ******************************************** " );
  return new Object[]{total, argmax, valmax, BPath};   

	
}
  
  private void buildEmission_Matrix(ArrayList keywordQuery)
  {
	  
	  
	  for(int j=0; j<StateSpace.size();j++)
	  {
		  Hashtable<Integer, Double> rowEmission = new Hashtable<Integer, Double>();
		  ResourceInfo Entity = StateSpace.get(j);
	  
		  for ( int i=0; i< keywordQuery.size() ;i++)
		  {
			  
		   
		    double Similarity=0;
		    
		    int Start 			= (StatewithBorders.get(Entity)).get(0);
		    int End				= (StatewithBorders.get(Entity)).get(1);
		    if ( Start <= i && i <= End )
		    {
		    	Similarity = Entity.getStringSimilarityScore();	
		    }
		    rowEmission.put(i, Similarity);
		  
	  }
		  
		  Emission_Matrix.put(Entity, rowEmission);
		  
  }
 }
  private   void buildStart_Probability_Matrix(){
	  
	  
	  double count = 0;
	  
	  // the following lines count the number of entities that starts with the first keywords
	  for(int j=0; j<StateSpace.size();j++)
	  {
		
	  ResourceInfo Entity = StateSpace.get(j);
	  int Start 			= (StatewithBorders.get(Entity)).get(0);
	  if( Start == 0 )
	  {
		  count = count+1; 
	  }  
	  }
	  
	  System.out.println(" count " + count);
	  
	  // the following lines put the probability of the 
	  
	  for(int j=0; j<StateSpace.size();j++)
	  { 
		  
	  ResourceInfo Entity = StateSpace.get(j);
	  int Start 			= (StatewithBorders.get(Entity)).get(0);
	  double prob = 0;	  
	  if( Start == 0 )
	  {
		  prob = 1/count; 
		 
	  }
	  
	  
	  Start_Probability_Matrix.put(Entity, prob);
	  
	  }
	  
	 
	  
  }
  
 private   void buildStart_Probability_BasedonHITS(){
	  
	  
	  double count = 0;
	  double Sigma = 0;
	  
	  
	  // the following lines count the number of entities that starts with the first keywords
	  for(int j=0; j<StateSpace.size();j++)
	  {
		
	  ResourceInfo Entity = StateSpace.get(j);
	  int Start 			= (StatewithBorders.get(Entity)).get(0);
	  if( Start == 0 )
	  {
		  count ++; 
		  Sigma = Sigma + StateSpace.get(j).getauthorithy() + StateSpace.get(j).gethub();
	  }  
	  }
	  
	 // System.out.println(" count " + count);
	  
	  // the following lines put the probability of the 
	  
	  for(int j=0; j<StateSpace.size();j++)
	  { 
	  ResourceInfo Entity = StateSpace.get(j);
	  int Start 			= (StatewithBorders.get(Entity)).get(0);
	  double prob = 0;	  
	  if( Start == 0 )
	  {
		 
		  //prob = 1/count; 
		  prob = (( Entity.getauthorithy() + StateSpace.get(j).gethub() )/Sigma) * (1-beta);
		 
	  }
	  
	  
	  Start_Probability_Matrix.put(Entity, prob);
	  }
	  
	 
	  
  }
	
	

}





//TreeMap tm = new TreeMap();  

/* for(ResourceInfo s:StateSpace)
{
	 if(TraceTree.containsKey(s))
	 {
		
		 List<Object[]> y = TraceTree.get(s);
		 
		 //System.out.println(" TraceTree SIZE "+ y.size());
		 for (int i=0; i< y.size(); i++)
		 {
			
		  String path_state 	= "";
         double probability_state 	= 1; 	  
      		  
      	  Object[] Traceobjs  = y.get(i);
      	  probability_state	 = ((Double) Traceobjs[0]).doubleValue();
      	  path_state 	     = (String) Traceobjs[1];
      	 // List pathEntityList_source_state 		 = ((List) Traceobjs[2]);
      	  
      	tm.put(probability_state, path_state); 
      	  
      	 
			 
			 
		 }
	 }
}

// Get a set of the entries 
java.util.Set set = tm.entrySet(); 
// Get an iterator 
Iterator i = set.iterator(); 
// Display elements 
while(i.hasNext()) { 
Map.Entry me = (Map.Entry)i.next(); 
System.out.print(me.getKey() + ": "); 
System.out.println(me.getValue()); 
}
// System.out.println("$$$$$$$$$$$$$$$$" + s.getUri() + "$$$$$$$$$$$$$$$$$$$$$$$");

*/

