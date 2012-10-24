package Model;

import java.util.*;

import Model.GraphBuilding.Template;
public class TripleNumberComparator  implements Comparator{

		public int compare(Object emp1, Object emp2){

		//parameter are of type Object, so we have to downcast it to Employee objects

		double similarityScore1=((Template)emp1).getGraphList().get(0).getTripleList().size();
		double similarityScore2=((Template)emp2).getGraphList().get(0).getTripleList().size();

		//uses compareTo method of String class to compare names of the employee

		if( similarityScore1 > similarityScore2 )

			return -1;

			else if( similarityScore1 < similarityScore2 )

			return 1;

			else

			return 0;


		}

		



}
