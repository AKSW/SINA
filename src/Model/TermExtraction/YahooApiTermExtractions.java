package Model.TermExtraction;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.LinkedList;
import java.util.Scanner;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class YahooApiTermExtractions {

	public LinkedList<String> PostMethodYahooApi(String query) throws MalformedURLException, IOException, ParserConfigurationException {
	
	// ---  connecting to Yahoo Term Extraction	
	String content					=	query;
	String licenseID				=	"pGu.NsvV34HAWI7v93WtnQlO0rhikmhvCbFMtykf3HhMvaSda6qBG9NLdkdgxSha";
	System.out.println("\n\n****** content sent to Yahoo api:\n\n"+ content + "\n\n");
	StringBuilder sb    			=  new StringBuilder(content.length() + 512);
	sb.append("appid=").append(licenseID);
	sb.append("&context=").append(content);
    String payload 					= 	sb.toString();
	URLConnection connection 		=   new URL("http://api.search.yahoo.com/ContentAnalysisService/V1/termExtraction").openConnection();
	connection.setDoOutput(true);
	OutputStream out				=  connection.getOutputStream();
	OutputStreamWriter writer	   	=  new OutputStreamWriter(out);
	writer.write(payload);
	writer.flush();
	// reading the output
	LinkedList<String> YahooTermList = new LinkedList();
	DocumentBuilderFactory dbf 		 = DocumentBuilderFactory.newInstance();
	DocumentBuilder db 				 = dbf.newDocumentBuilder();
	try {
		Document dom 				 = db.parse(connection.getInputStream());
		NodeList nodelist 			 = dom.getElementsByTagName("Result");		
		for(int i=0; i<nodelist.getLength();i++ )
		{
		YahooTermList.add(nodelist.item(i).getTextContent());
		}
		
	} catch (SAXException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	
	return YahooTermList;

	}





}
