package com.Layout;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.LinkedList;

import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import Model.StartThread;;

public class ConnectionClass {

	private String message;
	private int numberOfEntriesInList;
	private String searchQuery;
	private LinkedList <String> query_list   =  new LinkedList<String>();
	private LinkedList <String> SPARQLquery_list   =  new LinkedList<String>();
	
	public void runquery(){
		
		
		StartThread m=new StartThread();
	
			try {
				m.mainTask(searchQuery);
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (OWLOntologyCreationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		
		query_list.addAll(m.Query_List);
		SPARQLquery_list .addAll(m.SPARQLQuery_List);
		
		
	}
	
	public void setSearchQuery(String s){
	
		searchQuery=s;
		
	}
	
	public LinkedList <String> getquery_list(){
		
		return query_list;
		
	}
	
   public LinkedList <String> getSPARQLquery_list(){
		
		return SPARQLquery_list;
		
	}
	
}
