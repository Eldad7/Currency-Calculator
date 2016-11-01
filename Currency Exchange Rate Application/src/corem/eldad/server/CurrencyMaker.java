package corem.eldad.server;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.io.*;
import java.util.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import corem.eldad.client.Currency;

public class CurrencyMaker {					//Creating all information regarding the currencies
	private DateFormat dateFormat;
	private Date date;
	private InputStream is = null;
    private Vector<Currency> objects = new Vector<Currency>();
    
	public Vector<Currency> getCurr(){
		return objects;
	}
	
	public void makeCurrency() {
		dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		date = new Date();
		try{
			date = new Date();
			dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	        DocumentBuilder builder = factory.newDocumentBuilder();
			builder = factory.newDocumentBuilder();
			Document doc = builder.parse(new File("CurrencyData/server/currencies.xml"));
	        String s = new String(doc.getElementsByTagName("LAST_UPDATE").item(0).getFirstChild().getNodeValue());
	        date = dateFormat.parse(s);
	        NodeList list = doc.getElementsByTagName("NAME");
	        NodeList list1 = doc.getElementsByTagName("UNIT");
	        NodeList list2 = doc.getElementsByTagName("CURRENCYCODE");
	        NodeList list3 = doc.getElementsByTagName("COUNTRY");
	        NodeList list4 = doc.getElementsByTagName("RATE");
	        NodeList list5 = doc.getElementsByTagName("CHANGE");
	        int length = list.getLength();
	        for(int j=0; j<length; j++)
	        {
	        	objects.add(new Currency(list.item(j).getFirstChild().getNodeValue(),
	        			list1.item(j).getFirstChild().getNodeValue(), list2.item(j).getFirstChild().getNodeValue(),
	        			list3.item(j).getFirstChild().getNodeValue(), list4.item(j).getFirstChild().getNodeValue(),
	        			list5.item(j).getFirstChild().getNodeValue()));
	        	objects.get(j).buildHistory("CurrencyData/server/history/");
	        }
		}
	    catch(IOException e)
	    {
	        e.printStackTrace();
	    }
	    catch(ParserConfigurationException e)
	    {
	        e.printStackTrace();
	    }
	    catch(SAXException e)
	    {
	        e.printStackTrace();
	    } catch (DOMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
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
	      File f = new File("CurrencyData/server/history");
	      if (f.exists()){
				updateHistory();
			}
			else{
				buildHistory();
			}
	    }
	}
	public Date getDate() {
		return date;
	}
	
private void updateHistory() {
		
		System.out.println("Checking for updates...\n");
		for (int i=0; i<objects.size(); i++){
		try {
			String filepath = "CurrencyData/server/history/"+objects.get(i).getCurrencyCode()+".xml";
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
			Document doc = docBuilder.parse(filepath);
			Node date = doc.getElementsByTagName("Date").item(0);
			String s = String.valueOf(date);
			if (date.getTextContent().equals(s)){
				System.out.println("No update needed\n");
				break;
			}
			date.setTextContent(s);
			Node history = doc.getElementsByTagName("History").item(0);
			NodeList list = history.getChildNodes();
			for (int j = 0; j < list.getLength(); j++) {
	           Node node = list.item(j);
			   // get the salary element, and update the value
	           if ("Today".equals(node.getNodeName())){
	        	   node.setTextContent(String.valueOf(objects.get(i).getRate()));
	           }
	           else{
	        	   node.setTextContent(list.item(j+1).getTextContent());
	           
	           }
			}
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(new File(filepath));
			transformer.transform(source, result);

			System.out.println("Updating process finished successfully");

		   } catch (ParserConfigurationException pce) {
			pce.printStackTrace();
		   } catch (TransformerException tfe) {
			tfe.printStackTrace();
		   } catch (IOException ioe) {
			ioe.printStackTrace();
		   } catch (SAXException sae) {
			sae.printStackTrace();
		   }
		}
	}

	private void buildHistory(){
		System.out.println("Creating local history\n");
		File file = new File("CurrencyData/server/history/");
		file.mkdirs();	
		String s = String.valueOf(date);
		for (int i=0; i<objects.size(); i++){
			try {
				DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
				DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
				
				// root elements
				Document doc = docBuilder.newDocument();
				Element rootElement = doc.createElement(objects.get(i).getCurrencyCode());
				doc.appendChild(rootElement);
				
				Element date = doc.createElement("Date");
				date.appendChild(doc.createTextNode(s));
				rootElement.appendChild(date);
				
				// staff elements
				Element history = doc.createElement("History");
				rootElement.appendChild(history);
	
				// firstname elements
				Element seven = doc.createElement("Seven_days_ago");
				seven.appendChild(doc.createTextNode(String.valueOf(objects.get(i).getRate())));
				history.appendChild(seven);
	
				// lastname elements
				Element six = doc.createElement("Six_days_ago");
				six.appendChild(doc.createTextNode(String.valueOf(objects.get(i).getRate())));
				history.appendChild(six);
	
				// firstname elements
				Element five = doc.createElement("Five_days_ago");
				five.appendChild(doc.createTextNode(String.valueOf(objects.get(i).getRate())));
				history.appendChild(five);
	
				// lastname elements
				Element four = doc.createElement("Four_days_ago");
				four.appendChild(doc.createTextNode(String.valueOf(objects.get(i).getRate())));
				history.appendChild(four);
				// firstname elements
				Element three = doc.createElement("Three_days_ago");
				three.appendChild(doc.createTextNode(String.valueOf(objects.get(i).getRate())));
				history.appendChild(three);
	
				// lastname elements
				Element two = doc.createElement("Two_days_ago");
				two.appendChild(doc.createTextNode(String.valueOf(objects.get(i).getRate())));
				history.appendChild(two);
				// lastname elements
				Element one = doc.createElement("Yesterday");
				one.appendChild(doc.createTextNode(String.valueOf(objects.get(i).getRate())));
				history.appendChild(one);
				
				Element zero = doc.createElement("Today");
				zero.appendChild(doc.createTextNode(String.valueOf(objects.get(i).getRate())));
				history.appendChild(zero);
	
				// write the content into xml file
				TransformerFactory transformerFactory = TransformerFactory.newInstance();
				Transformer transformer = transformerFactory.newTransformer();
				DOMSource source = new DOMSource(doc);
				StreamResult result = new StreamResult(new File("CurrencyData/server/history/"+objects.get(i).getCurrencyCode()+".xml"));
	
				transformer.transform(source, result);
	
				System.out.println("File saved!");
	
			  } catch (ParserConfigurationException pce) {
				pce.printStackTrace();
			  } catch (TransformerException tfe) {
				tfe.printStackTrace();
			  }
		}
		try {
			PrintWriter writer = new PrintWriter("CurrencyData/server/history/state.txt", "UTF-8");
			writer.println("active");
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("History created successfully");
	}
}

