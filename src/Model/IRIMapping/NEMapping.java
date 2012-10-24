package Model.IRIMapping;

import java.util.ArrayList;
import java.util.LinkedList;

import Model.Constants;

public class NEMapping {
	
	LinkedList<String> CategoryList = new LinkedList();
	LinkedList<String> dbpediaIRIList = new LinkedList();
	ArrayList<Integer> TypeList    = new ArrayList();

	public LinkedList<String> getCategoryList()
	
	{
		return CategoryList;
	}
	
	//##############################################################
   public LinkedList<String> getdbpediaIRIList()
	
	{
		return dbpediaIRIList;
	}
 //##############################################################
   public ArrayList<Integer> getTypeList()
	
	{
		return TypeList;
	}


	
	// ##############################################################
	// This functions makes a map fron calais categories to DBpedia classses and properties
	//###############################################################
	
	public void initializeMappingSet() {
		
		/*Anniversary, City, Company, Continent, Country, Currency, EmailAddress,
		EntertainmentAwardEvent, Facility, FaxNumber, Holiday, IndustryTerm, MarketIndex,
		MedicalCondition, MedicalTreatment, Movie, MusicAlbum, MusicGroup, NaturalFeature,
		OperatingSystem, Organization, Person, PhoneNumber, PoliticalEvent, Position, Product,
		ProgrammingLanguage, ProvinceOrState, PublishedMedium, RadioProgram, RadioStation, 
		Region, SportsEvent, SportsGame, SportsLeague, Technology, TVShow, TVStation, URL
		
		*
		*
		*
		*
		Anniversary
		EmailAddress
		FaxNumber
		PhoneNumber
		Holiday
		Product
		MarketIndex
		Technology
		
		
		ProgrammingLanguage
		
		Facility mapped to two classes
		IndustryTerm mapped to a property
		
		*
		*/
		
		//1
		/*CategoryList.addLast("Anniversary");
		dbpediaIRIList.addLast("");
		TypeList.add(Constants.TYPE_CLASS);*/
		
		//2
		CategoryList.addLast("City");
		dbpediaIRIList.addLast("http://dbpedia.org/ontology/PopulatedPlace");
		TypeList.add(Constants.TYPE_CLASS);
		
		//3
		CategoryList.addLast("Company");
		dbpediaIRIList.addLast("http://dbpedia.org/ontology/Company");
		TypeList.add(Constants.TYPE_CLASS);
		
		//4
		CategoryList.addLast("Continent");
		dbpediaIRIList.addLast("http://dbpedia.org/ontology/Continent");
		TypeList.add(Constants.TYPE_CLASS);
		
		//5
		CategoryList.addLast("Country");
		dbpediaIRIList.addLast("http://dbpedia.org/ontology/Country");
		TypeList.add(Constants.TYPE_CLASS);
		
		//6
		CategoryList.addLast("Currency");
		dbpediaIRIList.addLast("http://dbpedia.org/ontology/Currency");
		TypeList.add(Constants.TYPE_CLASS);
		
		//7
		CategoryList.addLast("EntertainmentAwardEvent");
		dbpediaIRIList.addLast("http://dbpedia.org/ontology/Award");
		TypeList.add(Constants.TYPE_CLASS);
		
		//8
		CategoryList.addLast("MedicalCondition");
		dbpediaIRIList.addLast("http://dbpedia.org/ontology/Disease");
		TypeList.add(Constants.TYPE_CLASS);
		
		//9
		CategoryList.addLast("MedicalTreatment");
		dbpediaIRIList.addLast("http://dbpedia.org/ontology/Drug");
		TypeList.add(Constants.TYPE_CLASS);
		
		//10
		CategoryList.addLast("Movie");
		dbpediaIRIList.addLast("http://dbpedia.org/ontology/Film");
		TypeList.add(Constants.TYPE_CLASS);
		
		//11
		CategoryList.addLast("MusicAlbum");
		dbpediaIRIList.addLast("http://dbpedia.org/ontology/MusicalWork");
		TypeList.add(Constants.TYPE_CLASS);
		
		//12
		CategoryList.addLast("MusicGroup");
		dbpediaIRIList.addLast("http://dbpedia.org/ontology/Band");
		TypeList.add(Constants.TYPE_CLASS);
		
		//13
		CategoryList.addLast("NaturalFeature");
		dbpediaIRIList.addLast("http://dbpedia.org/ontology/Place");
		TypeList.add(Constants.TYPE_CLASS);
		
		//14
		CategoryList.addLast("OperatingSystem");
		dbpediaIRIList.addLast("http://dbpedia.org/ontology/Software");
		TypeList.add(Constants.TYPE_CLASS);
		
		//15
		CategoryList.addLast("Organization");
		dbpediaIRIList.addLast("http://dbpedia.org/ontology/Organisation");
		TypeList.add(Constants.TYPE_CLASS);
		
		//16
		CategoryList.addLast("Person");
		dbpediaIRIList.addLast("http://dbpedia.org/ontology/Person");
		TypeList.add(Constants.TYPE_CLASS);
		
		//17
		CategoryList.addLast("ProvinceOrState");
		dbpediaIRIList.addLast("http://dbpedia.org/ontology/PopulatedPlace");
		TypeList.add(Constants.TYPE_CLASS);
		
		//18
		CategoryList.addLast("PublishedMedium");
		dbpediaIRIList.addLast("http://dbpedia.org/ontology/Work");
		TypeList.add(Constants.TYPE_CLASS);
		
		//19
		CategoryList.addLast("RadioProgram");
		dbpediaIRIList.addLast("http://dbpedia.org/ontology/Work");
		TypeList.add(Constants.TYPE_CLASS);
		
		//20
		CategoryList.addLast("RadioStation");
		dbpediaIRIList.addLast("http://dbpedia.org/ontology/Organisation");
		TypeList.add(Constants.TYPE_CLASS);
		
		//21
		CategoryList.addLast("Region");
		dbpediaIRIList.addLast("http://dbpedia.org/ontology/Place");
		TypeList.add(Constants.TYPE_CLASS);
		
		//22
		CategoryList.addLast("SportsEvent");
		dbpediaIRIList.addLast("http://dbpedia.org/ontology/SportsEvent");
		TypeList.add(Constants.TYPE_CLASS);
		
		//23
		CategoryList.addLast("SportsGame");
		dbpediaIRIList.addLast("http://dbpedia.org/ontology/Activity");
		TypeList.add(Constants.TYPE_CLASS);
		
		//24
		CategoryList.addLast("SportsLeague");
		dbpediaIRIList.addLast("http://dbpedia.org/ontology/Organisation");
		TypeList.add(Constants.TYPE_CLASS);
		
		//25
		/*CategoryList.addLast("Technology");
		dbpediaIRIList.addLast("");
		TypeList.add(Constants.TYPE_CLASS);*/
		
		//26
		CategoryList.addLast("TVShow");
		dbpediaIRIList.addLast("http://dbpedia.org/ontology/Work");
		TypeList.add(Constants.TYPE_CLASS);
		
		//27
		CategoryList.addLast("TVStation");
		dbpediaIRIList.addLast("http://dbpedia.org/ontology/Organisation");
		TypeList.add(Constants.TYPE_CLASS);
		
		//28
		CategoryList.addLast("URL");
		dbpediaIRIList.addLast("http://dbpedia.org/ontology/Website");
		TypeList.add(Constants.TYPE_CLASS);
		
		//29
		CategoryList.addLast("PoliticalEvent");
		dbpediaIRIList.addLast("http://dbpedia.org/ontology/Event");
		TypeList.add(Constants.TYPE_CLASS);
		
		//30
		CategoryList.addLast("Position");
		dbpediaIRIList.addLast("http://dbpedia.org/ontology/Person");
		TypeList.add(Constants.TYPE_CLASS);
		
		//31
		CategoryList.addLast("Facility");
		dbpediaIRIList.addLast("http://dbpedia.org/ontology/Organisation");
		TypeList.add(Constants.TYPE_CLASS);
		
		//32
		CategoryList.addLast("Facility");
		dbpediaIRIList.addLast("http://dbpedia.org/ontology/Place");
		TypeList.add(Constants.TYPE_CLASS);
		
		
		//32
		CategoryList.addLast("IndustryTerm");
		dbpediaIRIList.addLast("http://dbpedia.org/ontology/industry");
		TypeList.add(Constants.TYPE_PROPERTY);
		
		
		
	}

	public LinkedList<String> getNESet() {
		// TODO Auto-generated method stub
		return CategoryList;
	}

	public LinkedList<String> getUriSet() {
		// TODO Auto-generated method stub
		return dbpediaIRIList;
	}

	

}
