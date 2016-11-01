package corem.eldad.client;

import java.io.*;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class Currency implements Serializable{
	
	/**
	 * 
	 */
	
	private static final long serialVersionUID = 1L;
	private String name;
	private int unit;
	private String currencyCode;
	private String country;
	private float rate;
	private float change;
	private boolean local=false;
	
	public boolean isLocal() {
		return local;
	}

	public void setLocal(boolean local) {
		this.local = local;
	}

	private ArrayList<Float> history;
	
	public Currency(String _name, String _unit, String _currencyCode, String _country, String _rate, String _change){
		name = new String(_name);
		unit = Integer.parseInt(_unit);
		currencyCode = new String(_currencyCode);
		country = new String(_country);
		rate = Float.parseFloat(_rate);
		change = Float.parseFloat(_change);
		history = new ArrayList<Float>();
	}

	public void buildHistory(String path) {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder;
		try {
			builder = factory.newDocumentBuilder();
			Document doc = builder.parse(new File(path+currencyCode+".xml"));
			NodeList list = doc.getElementsByTagName("History").item(0).getChildNodes();
			for (int i=0; i<list.getLength(); i++)
				history.add(Float.parseFloat(list.item(i).getTextContent()));
		} catch (ParserConfigurationException e) {
		// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			return;
			// TODO Auto-generated catch block
		}
		
	}

	public Currency(Currency curr){
		// TODO Auto-generated constructor stub
		this.name = curr.getName();
		this.unit = curr.getUnit();
		this.currencyCode = curr.getCurrencyCode();
		this.country = curr.getCountry();
		this.rate = curr.getRate();
		this.change = curr.getChange();
	}

	public String getName() {
		return name;
	}

	public int getUnit() {
		return unit;
	}

	public String getCurrencyCode() {
		return currencyCode;
	}

	public String getCountry() {
		return country;
	}

	public float getRate() {
		return rate;
	}

	public float getChange() {
		return change;
	}
	
	public ArrayList<Float> getHistory(){
		return history;
	}
}
