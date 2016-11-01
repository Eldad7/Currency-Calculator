package corem.eldad.client;

import java.io.IOException;
import java.net.UnknownHostException;

import javax.xml.parsers.ParserConfigurationException;

public interface Model {
	
	public void refresh();	
	public Currency search(String s, String s2);
	public void LoadFromLocalXML();
	abstract void RemoteServerConnect();
	public void create(String newCoin[]) throws ParserConfigurationException;
	public void parseLocalCoins();
	public void reconnect() throws UnknownHostException, IOException;
	public void makeLocalXML();
	abstract void updateHistory();
	abstract void buildHistory();
}