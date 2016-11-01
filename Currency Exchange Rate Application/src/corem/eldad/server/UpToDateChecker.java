package corem.eldad.server;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.*;
import java.net.HttpURLConnection;
import java.net.URL;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class UpToDateChecker extends Thread{								//Makes sure the server XML is up to date
	InputStream is = null;
    HttpURLConnection con = null;
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    DocumentBuilder builder;
    TransformerFactory transformerFactory;
	Transformer transformer;
	URL url;
	private boolean notifier=true;
	
	public boolean isNotifier() {
		return notifier;
	}
	
	public void setNotifier(boolean notifier){
		this.notifier = notifier;
	}
	@Override
	public void run() {
		File file = new File("CurrencyData/server");
		file.mkdirs();	
		while(true){
			try {
				parse();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				}
		}	      
	}
	private void parse() throws IOException {
		CookieHandler.setDefault(new CookieManager(null, CookiePolicy.ACCEPT_ALL));
		while(true){
			try{
				url = new URL("http://www.boi.org.il/currency.xml");
				con = (HttpURLConnection)url.openConnection();
			    con.setRequestMethod("GET");
			    con.connect();
			    is = con.getInputStream();
			    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			    DocumentBuilder builder = factory.newDocumentBuilder();
			    Document doc = builder.parse(is);
				TransformerFactory transformerFactory = TransformerFactory.newInstance();
				Transformer transformer = transformerFactory.newTransformer();
				DOMSource source = new DOMSource(doc);
				StreamResult result = new StreamResult(new File("CurrencyData/server/currencies.xml"));
				transformer.transform(source, result);
				System.out.println("Local storage is up to date!");
			} catch (ParserConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SAXException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (TransformerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		    finally
		    {
		      if(is!=null)
		      {
		          try
		          {
		              is.close();
		          }
		          catch(IOException e)
		          {
		              e.printStackTrace();
		          }
		      }
		    }
			try {
				if(con!=null)
			          con.disconnect();
				notifier=true;
                Thread.sleep(1800000);
            } catch (InterruptedException e) {
                e.getStackTrace();
            }
		}
	}
}