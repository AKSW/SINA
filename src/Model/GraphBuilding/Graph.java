package Model.GraphBuilding;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import Model.Constants;

// A graph is a set of triples, in other word a set of entities interwined together by properties


public class Graph {
	
	private List<TriplePattern> TripleList	=	new ArrayList();
	private List<Entity> EntityList			=	new ArrayList();
	private List<Entity> LiteralVariable	=	new ArrayList();
	
//###############################################
// access methods
//###############################################	
	public List<Entity> getEntityList() {
		return EntityList;
	}
	
	public List<TriplePattern> getTripleList() {
		return TripleList;
	}

	public void setEntityList(List<Entity> i) {
		Collections.copy(EntityList,i);
	}
	
	public void setTripleList(List<TriplePattern> i) {
		Collections.copy(TripleList,i);
	}
	
//###############################################
// functionality methods
//###############################################
	
	public void addTriple(TriplePattern i) {
		TriplePattern j	=	new TriplePattern();
		j.setSubject(i.getSubject());
		j.setPredicate(i.getPredicate());
		j.setObject(i.getObject());
		j.setPropertySet(i.getPropertySet());
		j.setHasSetOfProperties(i.getHasSetOfProperties());
		TripleList.add(j);
	}
	
	public void addTriple(Entity subject,String predicat, Entity Object) {
		TriplePattern j	=	new TriplePattern();
		j.setSubject(subject);
		j.setPredicate(predicat);
		j.setObject(Object);
		TripleList.add(j);
	}
	
	public void addEntity(Entity i) {
		Entity j	=	new Entity();
		j.setIRI(i.getIRI());
		j.settype(i.gettype());
		j.setClassTypeIRI(i.getClassTypeIRI());
		EntityList.add(j);
	}
	
	public Entity addVariableEntity(Set ClassType, int index) {
		// System.out.println("enter add variable");
		Entity j	=	new Entity();
		j.setIRI("?v"+index);
		j.settype(Constants.TYPE_VARIABLE);
		j.setClassTypeIRI(ClassType);
		EntityList.add(j);
		return j;
	}

	public Entity addLiteralEntity(int index) {
		Entity j	=	new Entity();
		j.setIRI("?l"+index);
		j.settype(Constants.TYPE_LITERAL);// type of literal
		LiteralVariable.add(j);
		return j;
	}
	
	
}
