package Model.TermExtraction;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.LinkedList;
import java.util.Scanner;




public class OpenCalaisConnection {
	

	public LinkedList<NEKeywordStructure>  PostMethodOpenCalais(String query) throws MalformedURLException, IOException {
		
		// connecting to API
		
		String content						= 	query;
		String licenseID					=	"r9xe7mg47yvv65c6dkh7e3pz";
		System.out.println("\n\n****** content sent to Open Calais:\n\n"+ content + "\n\n");
		String paramsXML 					=	"<c:params xmlns:c=\"http://s.opencalais.com/1/pred/\" xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\"><c:processingDirectives c:contentType=\"TEXT/RAW\" c:outputFormat=\"Text/Simple\"></c:processingDirectives><c:userDirectives c:allowDistribution=\"true\" c:allowSearch=\"true\" c:externalID=\"17cabs901\" c:submitter=\"ABC\"></c:userDirectives><c:externalMetadata></c:externalMetadata></c:params>";
		StringBuilder sb					=	new StringBuilder(content.length() + 512);
		sb.append("licenseID=").append(licenseID);
		sb.append("&content=").append(content);
		sb.append("&paramsXML=").append(paramsXML);
	    String payload						=	sb.toString();
		URLConnection connection			=	new URL("http://api.opencalais.com/enlighten/calais.asmx/Enlighten").openConnection();
		connection.addRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		connection.addRequestProperty("Content-Length", String.valueOf(payload.length()));
		connection.setDoOutput(true);
		OutputStream out					=	connection.getOutputStream();
		OutputStreamWriter writer			=	new OutputStreamWriter(out);
		writer.write(payload);
		writer.flush();
		Scanner result						=	new Scanner(connection.getInputStream()).useDelimiter("\n");
		
		// --- read the output ---
		int counter							=	0;
		LinkedList<NEKeywordStructure> NESet= new LinkedList();
		while (result.hasNextLine()){
		counter ++;
		String a= result.nextLine();
		if( counter == 1 )
		{
			if(result.hasNextLine())
			   a = result.nextLine();
		}
		if(result.hasNext()){
			
			int indexq           			 = a.indexOf(":");
			int indexc           			 = a.indexOf(",");
			NEKeywordStructure n 			 = new NEKeywordStructure();
			
			n.setNE(a.substring(0, indexq).trim());
			n.setKeyword(a.substring(indexq+1, indexc).trim());
			NESet.add(n);
			System.out.println("output of calais---->" + n.getNE() + "....." + n.getKeyword());
		
		 }
		}
		
		return NESet;
	 }

}
