package com.Layout;

import com.vaadin.Application;
import com.vaadin.ui.*;
import com.vaadin.ui.Window;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Button.ClickEvent;
import java.io.*;

public class Sina_vaad extends Application  {
	
	
	private Button search = new Button("Search");
	private Window mainWindow2 = new Window("second page");
	private Window mainWindow = new Window("Sina_vaad");
	private Panel panelresult=new Panel();
	
	public void init() {
		
		/*Label label = new Label("Hello Vaadin user");
		mainWindow.addComponent(label);
		 
		 mainWindow.addComponent(search);
		 search.addListener(this);*/
		System.out.println("absolute address:" +  this.getContext().getBaseDirectory().getAbsolutePath());
		MainWebPage layOutObject=new MainWebPage();
		layOutObject.showMainWebPage();
		 setMainWindow(layOutObject.mainWindow);
	 
		 
	}

	

	
	

	
}
