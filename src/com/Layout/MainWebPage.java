package com.Layout;

import java.io.Serializable;
import java.util.LinkedList;

import Model.javatools.parsers.PlingStemmer;

import com.vaadin.data.Container;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.terminal.ExternalResource;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Button.ClickEvent;

public class MainWebPage implements ClickListener {
	
	
	public Window mainWindow = new Window("Sina - Semantic Search Engine");
	
	private Panel panelQuery=new Panel();
	private Panel panelresult=new Panel();
	private Button searchButton = new Button("Search");
	private final TextField SearchTextField = new TextField("");
	//private Table table = new Table("ISO-3166 Country Codes and flags");
	
	Label labelBlog = new Label("<h3> For more information about this project, visit:  <a href=\"http://aksw.org/Projects/lodquery\">our page</a></h3>");
	Label labelBrowser = new Label("<h3> Please just use google chrome </h3>");
	  
	
	Label ExampleLable1 = new Label("<b>Example 1:</b>  <i> soccer clubs in Spain</i>");
	Label ExampleLable2 = new Label("<b>Example 2:</b>  <i>date of  Battle of Gettysburg</i>");
	Label ExampleLable3 = new Label("<b>Example 3:</b>  <i>In which films directed by Garry Marshall was Julia Roberts starring</i>");
	
	public void showMainWebPage()
	{
		// logo image
		Embedded e = new Embedded("",
                new ThemeResource("SinaLogo2.jpg"));
		e.setHeight("160px");
		e.setWidth("480px");
		
		SearchTextField.setWidth("450px");
		labelBlog.setContentMode(Label.CONTENT_XHTML);
		labelBrowser.setContentMode(Label.CONTENT_XHTML);
		ExampleLable1.setContentMode(Label.CONTENT_XHTML);
		ExampleLable2.setContentMode(Label.CONTENT_XHTML);
		ExampleLable3.setContentMode(Label.CONTENT_XHTML);
		
		
		searchButton.addListener(this);
		//panelQuery.addComponent(labelBlog);
		panelQuery.addComponent(labelBrowser);
		panelQuery.addComponent(e);
		panelQuery.addComponent(SearchTextField);
		panelQuery.addComponent(searchButton);
		panelQuery.addComponent(ExampleLable1);
		panelQuery.addComponent(ExampleLable2);
		panelQuery.addComponent(ExampleLable3);
		
		mainWindow.addComponent(panelQuery);
		mainWindow.addComponent(panelresult);
		
		 // let's adjust the panels default layout (a VerticalLayout)
        VerticalLayout layout = (VerticalLayout) panelQuery.getContent();
        layout.setMargin(true); // we want a margin
        layout.setSpacing(true); // and spacing between components
        layout.setComponentAlignment(e, Alignment.TOP_CENTER);
        layout.setComponentAlignment(SearchTextField, Alignment.TOP_CENTER);
        layout.setComponentAlignment(searchButton, Alignment.TOP_CENTER);
        layout.setComponentAlignment(ExampleLable1, Alignment.BOTTOM_CENTER);
        layout.setComponentAlignment(ExampleLable2, Alignment.BOTTOM_CENTER);
        layout.setComponentAlignment(ExampleLable3, Alignment.BOTTOM_CENTER);
       
		// setMainWindow(mainWindow);
		
	}
	
	private  void searchButtonClicked() {
		
		
		//	System.out.println("  plimmer stemmer ..... " + new PlingStemmer().stem("people"));
		
		
		 panelresult.removeAllComponents();
		 VerticalLayout layoutresult = (VerticalLayout) panelresult.getContent();
		 layoutresult.setMargin(true); // we want a margin
		 layoutresult.setSpacing(true); // and spacing between components
		 
		 Label labeltitle = new Label("<h2> Results of the query:  "+ (String) SearchTextField.getValue()+"</h2>");
		 labeltitle.setContentMode(Label.CONTENT_XHTML);
		 panelresult.addComponent(labeltitle);
		 layoutresult.setComponentAlignment(labeltitle, Alignment.TOP_CENTER);
		 
		 ConnectionClass c=new ConnectionClass();
		 c.setSearchQuery((String) SearchTextField.getValue());
		 System.out.println((String) SearchTextField.getValue());
		 c.runquery();
		 
		 RunSparqlQueries runObject = new RunSparqlQueries();
		 for(int  i=0 ; i<c.getquery_list().size() ; i++)
		 {
			 String s=c.getquery_list().get(i);
			 Label a=new Label("<h4 style=\"background-color:#6698FF;\">"+"Query Pattern:  " +s);
			 a.setContentMode(Label.CONTENT_XHTML);
			 panelresult.addComponent(a);
			 
			 
			 String s2=c.getSPARQLquery_list().get(i);
			 Link l = new Link("Show the query in DBpedia SPARQL endpoint",
		                new ExternalResource("http://dbpedia.org/sparql?default-graph-uri=http%3A%2F%2Fdbpedia.org&qtxt="+s2));
			 panelresult.addComponent(l);
			 //panelresult.addComponent(new Label(s2));
			 
			 runObject.runSparqlQuery(s2);
			 Table t = new Table("Sample of the results");
			 t.setContainerDataSource(runObject.getResultContainer());
			 t.setHeight("100px");
			 panelresult.addComponent(t);
			 layoutresult.setComponentAlignment(t, Alignment.TOP_CENTER);
			 
		 }	
	
		 mainWindow.addComponent(panelresult);
	
	}
	
	public void buttonClick(ClickEvent event) {
		if(event.getComponent()==searchButton)
		{
			searchButtonClicked(); 
		}
		
		
    }
	
	
	public class QueryBean implements Serializable {
	   
		public QueryBean( String s) {
			// TODO Auto-generated constructor stub
		//	index=i;
			query=s;
		}
		
	    String query;   

	}
	
	
	//#######################################################
	
	  private static IndexedContainer createDummyData(LinkedList <String> pattern, LinkedList <String> sparql) {
//LinkedList <String> pattern, LinkedList <String> sparql
	        String[] fnames = { "Peter", "Alice", "Joshua", "Mike", "Olivia",
	                "Nina", "Alex", "Rita", "Dan", "Umberto", "Henrik", "Rene",
	                "Lisa", "Marge" };
	        String[] lnames = { "Smith", "Gordon", "Simpson", "Brown", "Clavel",
	                "Simons", "Verne", "Scott", "Allison", "Gates", "Rowling",
	                "Barks", "Ross", "Schneider", "Tate" };

	        IndexedContainer ic = new IndexedContainer();
// private static
	        String[] fields = { "Pattern", "SPARQL" };
	        
	        for (String p : fields) {
	            ic.addContainerProperty(p, String.class, "");
	        }

	        // Create dummy data by randomly combining first and last names
	        for (int i = 0; i < pattern.size(); i++) {
	            Object id = ic.addItem();
	            ic.getContainerProperty(id, "Pattern").setValue(
	            		pattern.get(i));
	            ic.getContainerProperty(id, "SPARQL").setValue(
	            		sparql.get(i));
	        }

	        return ic;
	    }

 //#############################################################
	

}
