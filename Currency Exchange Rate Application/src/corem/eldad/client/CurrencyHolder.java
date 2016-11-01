package corem.eldad.client;

import java.io.*;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.text.*;
import java.util.*;
import javax.swing.*;
import javax.xml.parsers.*;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
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

public class CurrencyHolder extends Thread implements Model{
	private Vector<Currency> vector;
	private Socket clientSocket;
	private Date date;
	private boolean local=false;
	private File serverFile;
	private ObjectInputStream ois;
	private boolean notifier=false;
	
	public Socket getClientSocket() {
		return clientSocket;
	}
	
	public Vector<Currency> getVector() {
		return vector;
	}

	public Date getDate() {
		return date;
	}

	public boolean isLocal() {
		return local;
	}

	@SuppressWarnings("unchecked")
	public CurrencyHolder(){
		vector = new Vector<Currency>();
		date = new Date();
		InputStream is = null;
		try {
			clientSocket = new Socket("localhost", 8080);
			is = clientSocket.getInputStream();
			ois = new ObjectInputStream(is);
			date = ((Date) ois.readObject());
			vector = ((Vector<Currency>) ois.readObject());
			System.out.println("Connection Successful");
			makeLocalXML();
		    File f = new File("CurrencyData/local/coins.xml");
		    if(f.isFile()) 
		    	parseLocalCoins();
		    System.out.println("Starting");
		    synchronized(this){
		    	this.start();
		    }
		} catch (IOException | ClassNotFoundException e) {
			// TODO Auto-generated catch block
			local=true;
			System.out.println("Can't connect to server, loading from local");
			LoadFromLocalXML();
		}
		File f = new File("CurrencyData/local/history/state.txt");
		if (f.exists()){
				updateHistory();
		}
		else{
				buildHistory();
		}
	}
	
	public void refresh(){
		LoadFromLocalXML();
	}
	
	public Currency search(String s, String s2){
		for (int i=0; i<vector.size(); i++){
			if (s.equals("Country")){
				if (vector.get(i).getCountry().equals(s2)){
					return vector.get(i);
				}
			}
			else{
				if (vector.get(i).getCurrencyCode().equals(s2)){
					return vector.get(i);
				}
			}
		}
		return null;
	}
	
	public void LoadFromLocalXML(){								//If we can't connect to server
		vector = new Vector<Currency>();
		try{
			date = new Date();
			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	        DocumentBuilder builder = factory.newDocumentBuilder();
			builder = factory.newDocumentBuilder();
			Document doc = builder.parse(new File("CurrencyData/local/currencies.xml"));
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
	        	vector.add(new Currency(list.item(j).getFirstChild().getNodeValue(),
	        			list1.item(j).getFirstChild().getNodeValue(), list2.item(j).getFirstChild().getNodeValue(),
	        			list3.item(j).getFirstChild().getNodeValue(), list4.item(j).getFirstChild().getNodeValue(),
	        			list5.item(j).getFirstChild().getNodeValue()));
	        	vector.get(j).buildHistory("CurrencyData/local/history/");
	        }
		}
	    catch(IOException e)
	    {
	    	JInternalFrame frame = new JInternalFrame("Error");
    		frame.setVisible(true);
			JOptionPane.showMessageDialog(frame, "Local storage not found. Trying remote server");
	        RemoteServerConnect();
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
	    	File f = new File("CurrencyData/local/coins.xml");
		    if(f.isFile()) 
		    	parseLocalCoins();
	    }
	}
	
	public void RemoteServerConnect() {							//If we can't connect to server and there is no local storage
		InputStream is = null;
	    HttpURLConnection con = null;
		URL url;
		File file = new File("CurrencyData/local");
		file.mkdirs();	
		CookieHandler.setDefault(new CookieManager(null, CookiePolicy.ACCEPT_ALL));
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
			StreamResult result = new StreamResult(new File("CurrencyData/local/currencies.xml"));
			transformer.transform(source, result);
			System.out.println("Local storage created successfuly");			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerConfigurationException e) {
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
		if(con!=null)
        {
          con.disconnect();
        }
		LoadFromLocalXML();
	}
	
	public void create(String newCoin[]) throws ParserConfigurationException {	//Creating local storage
		// TODO Auto-generated method stub
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
		Document doc = docBuilder.newDocument();
		File file = new File("CurrencyData/local");
		file.mkdirs();
	    File f = new File("CurrencyData/local/coins.xml");
	    if(f.isFile()) {
	    	System.out.println("File exists!");
	    	try {
				doc = docBuilder.parse(new File("CurrencyData/local/coins.xml"));
		        Element rootElement = doc.getDocumentElement();
		        Element currency = doc.createElement("Currency");
				rootElement.appendChild(currency);
	
				Element name = doc.createElement("Name");
				name.appendChild(doc.createTextNode(newCoin[0]));
				currency.appendChild(name);
	
				Element unit = doc.createElement("Unit");
				unit.appendChild(doc.createTextNode(newCoin[1]));
				currency.appendChild(unit);
				
				Element code = doc.createElement("CurrencyCode");
				code.appendChild(doc.createTextNode(newCoin[2]));
				currency.appendChild(code);
				
				Element country = doc.createElement("Country");
				country.appendChild(doc.createTextNode(newCoin[3]));
				currency.appendChild(country);
				
				Element rate = doc.createElement("Rate");
				rate.appendChild(doc.createTextNode(newCoin[4]));
				currency.appendChild(rate);
	    	}
		    catch (SAXException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    }
	    else{
	    	System.out.println("File doesn't exist!");
			Element rootElement = doc.createElement("CURRENCIES");
			doc.appendChild(rootElement);
			Element currency = doc.createElement("Currency");
			rootElement.appendChild(currency);

			Element name = doc.createElement("Name");
			name.appendChild(doc.createTextNode(newCoin[0]));
			currency.appendChild(name);

			Element unit = doc.createElement("Unit");
			unit.appendChild(doc.createTextNode(newCoin[1]));
			currency.appendChild(unit);
			
			Element code = doc.createElement("CurrencyCode");
			code.appendChild(doc.createTextNode(newCoin[2]));
			currency.appendChild(code);
			
			Element country = doc.createElement("Country");
			country.appendChild(doc.createTextNode(newCoin[3]));
			currency.appendChild(country);
			
			Element rate = doc.createElement("Rate");
			rate.appendChild(doc.createTextNode(newCoin[4]));
			currency.appendChild(rate);
	    }
			// write the content into xml file
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer;
			try {
				transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(new File("CurrencyData/local/coins.xml"));
			transformer.transform(source, result);
			System.out.println("File saved!");
			}
			catch (TransformerConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (TransformerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		System.out.println("Coin added");
	}

	public boolean connect(Socket clientSocket) {
		// TODO Auto-generated method stub
		return false;
	}

	public void parseLocalCoins() {
		try{
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	        DocumentBuilder builder;
			builder = factory.newDocumentBuilder();
			builder = factory.newDocumentBuilder();
			Document doc = builder.parse(new File("CurrencyData/local/coins.xml"));
	        NodeList list = doc.getElementsByTagName("Name");
	        NodeList list1 = doc.getElementsByTagName("Unit");
	        NodeList list2 = doc.getElementsByTagName("CurrencyCode");
	        NodeList list3 = doc.getElementsByTagName("Country");
	        NodeList list4 = doc.getElementsByTagName("Rate");
	        int length = list.getLength();
	        for(int j=0; j<length; j++)
	        {
	        	vector.add(new Currency(list.item(j).getFirstChild().getNodeValue(),
	        			list1.item(j).getFirstChild().getNodeValue(), list2.item(j).getFirstChild().getNodeValue(),
	        			list3.item(j).getFirstChild().getNodeValue(), list4.item(j).getFirstChild().getNodeValue(),
	        			String.valueOf(0)));
	        	vector.lastElement().setLocal(true);
	        }
		}
		catch (ParserConfigurationException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void run() {												//Making sure local storage is up to date
		
		// TODO Auto-generated method stub
		while (true){
			makeLocalXML();
			if (notifier){
				refresh();
				JInternalFrame frame = new JInternalFrame("Info");
				frame.setVisible(true);
				JOptionPane.showMessageDialog(frame, "Your database has been updated!");
				notifier=false;
			}	
			try {
				Thread.sleep(900000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public void reconnect() throws UnknownHostException, IOException{
		clientSocket = new Socket("localhost", 8080);
		refresh();
		local = false;
	}
	
	public void makeLocalXML() {
		System.out.println("Creating local XML\n");
		File file = new File("CurrencyData/local");
		file.mkdirs();		
		try {
			serverFile = (File) ois.readObject();
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
			Document doc = docBuilder.parse(serverFile);
			File f = new File("CurrencyData/local/currencies.xml");
			if (f.isFile()){
				Document doc1 = docBuilder.parse(new File("CurrencyData/local/currencies.xml"));
				String old = doc1.getElementsByTagName("LAST_UPDATE").item(0).getFirstChild().getNodeValue();
				String New = doc.getElementsByTagName("LAST_UPDATE").item(0).getFirstChild().getNodeValue();
				if (!(old.equals(New))){
					// write the content into xml file
					TransformerFactory transformerFactory = TransformerFactory.newInstance();
					Transformer transformer = transformerFactory.newTransformer();
					DOMSource source = new DOMSource(doc);
					StreamResult result = new StreamResult(new File("CurrencyData/local/currencies.xml"));
					transformer.transform(source, result);
					notifier=true;
				}
				else{
					System.out.println("Up to date");
				}
			}
			else{
				TransformerFactory transformerFactory = TransformerFactory.newInstance();
				Transformer transformer = transformerFactory.newTransformer();
				DOMSource source = new DOMSource(doc);
				StreamResult result = new StreamResult(new File("CurrencyData/local/currencies.xml"));
				transformer.transform(source, result);
				System.out.println("Local data created");			
			}
		  } catch (ParserConfigurationException pce) {
			pce.printStackTrace();
		  } catch (TransformerException tfe) {
			tfe.printStackTrace();
		  } catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("Disconnected");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	
	public void updateHistory() {
		
		System.out.println("Checking for updates...\n");
		for (int i=0; i<vector.size(); i++){
		try {
			String filepath = "CurrencyData/local/history/"+vector.get(i).getCurrencyCode()+".xml";
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
	        	   node.setTextContent(String.valueOf(vector.get(i).getRate()));
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

		   } catch (ParserConfigurationException pce) {
			pce.printStackTrace();
		   } catch (TransformerException tfe) {
			tfe.printStackTrace();
		   } catch (IOException ioe) {
			ioe.printStackTrace();
		   } catch (SAXException sae) {
			sae.printStackTrace();
		   }
			System.out.println("Updating process finished successfully");
		}
	}

	public void buildHistory(){
		System.out.println("Creating local history\n");
		File file = new File("CurrencyData/local/history");
		file.mkdirs();	
		String s = String.valueOf(date);
		for (int i=0; i<vector.size(); i++){
			try {
				DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
				DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
				
				// root elements
				Document doc = docBuilder.newDocument();
				Element rootElement = doc.createElement(vector.get(i).getCurrencyCode());
				doc.appendChild(rootElement);
				
				Element date = doc.createElement("Date");
				date.appendChild(doc.createTextNode(s));
				rootElement.appendChild(date);
				
				// staff elements
				Element history = doc.createElement("History");
				rootElement.appendChild(history);
	
				// firstname elements
				Element seven = doc.createElement("Seven_days_ago");
				seven.appendChild(doc.createTextNode(String.valueOf(vector.get(i).getRate())));
				history.appendChild(seven);
	
				// lastname elements
				Element six = doc.createElement("Six_days_ago");
				six.appendChild(doc.createTextNode(String.valueOf(vector.get(i).getRate())));
				history.appendChild(six);
	
				// firstname elements
				Element five = doc.createElement("Five_days_ago");
				five.appendChild(doc.createTextNode(String.valueOf(vector.get(i).getRate())));
				history.appendChild(five);
	
				// lastname elements
				Element four = doc.createElement("Four_days_ago");
				four.appendChild(doc.createTextNode(String.valueOf(vector.get(i).getRate())));
				history.appendChild(four);
				// firstname elements
				Element three = doc.createElement("Three_days_ago");
				three.appendChild(doc.createTextNode(String.valueOf(vector.get(i).getRate())));
				history.appendChild(three);
	
				// lastname elements
				Element two = doc.createElement("Two_days_ago");
				two.appendChild(doc.createTextNode(String.valueOf(vector.get(i).getRate())));
				history.appendChild(two);
				// lastname elements
				Element one = doc.createElement("Yesterday");
				one.appendChild(doc.createTextNode(String.valueOf(vector.get(i).getRate())));
				history.appendChild(one);
				
				Element zero = doc.createElement("Today");
				zero.appendChild(doc.createTextNode(String.valueOf(vector.get(i).getRate())));
				history.appendChild(zero);
	
				// write the content into xml file
				TransformerFactory transformerFactory = TransformerFactory.newInstance();
				Transformer transformer = transformerFactory.newTransformer();
				DOMSource source = new DOMSource(doc);
				StreamResult result = new StreamResult(new File("CurrencyData/local/history/"+vector.get(i).getCurrencyCode()+".xml"));
	
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
	public interface MathOperation {
		float execute(float a, int aUnit, float num, float b, int bUnit);
	}
	
	public float calculate(String _from, String _to, MathOperation op, Vector<Currency> vector, String _num) {
		float a=0, b=0, num;
		int aUnit=0, bUnit=0;
		for (int i=0; i<vector.size(); i++){
			if (vector.get(i).getCurrencyCode().equals(_from)){
				a = vector.get(i).getRate();
				aUnit = vector.get(i).getUnit();
			}
			if (vector.get(i).getCurrencyCode().equals(_to)){
				b = vector.get(i).getRate();
				bUnit = vector.get(i).getUnit();
			}
		}
		num = Float.valueOf(_num);
		return op.execute(a, aUnit, num ,b, bUnit);
	}
}