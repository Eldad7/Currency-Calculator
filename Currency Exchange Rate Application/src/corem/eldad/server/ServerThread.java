package corem.eldad.server;

import java.io.*;
import java.net.*;
import java.text.DateFormat;
import java.util.Date;
import java.util.Vector;

import corem.eldad.client.Currency;

public class ServerThread extends Thread implements Serializable { //In charge of users connecting to the server
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected Socket clientSocket;
	private UpToDateChecker checker;

    public ServerThread(Socket clientSocket, UpToDateChecker checker) {
        this.clientSocket = clientSocket;								//Receives the client from the server
        this.checker = checker;											//Receives the UpToDateChecker Thread from the server
    }
    
    public synchronized void run() {
    	CurrencyMaker curr = new CurrencyMaker();
		curr.makeCurrency();
		ObjectOutputStream oos = null;
        try{
        	File file = new File("CurrencyData/server/currencies.xml"); 
        	oos = new ObjectOutputStream(clientSocket.getOutputStream());
        	oos.writeObject(curr.getDate());							//Sends the date of the XML
        	oos.flush();
        	oos.writeObject(curr.getCurr());							//Sends the Vector of Currencies
        	oos.flush();
        	oos.writeObject(file);										//Sends the up to date XML
        	oos.flush();
        }
        catch (IOException e){
        	e.printStackTrace();
        }
        while(true){													//If there is a change in the uptodatechecker, this will send the up to date XML
        	try {
        		File file = new File("CurrencyData/server/currencies.xml");
	        	if(checker.isNotifier()){
		        	oos.flush();
		        	oos.writeObject(file);
		        	checker.setNotifier(false);
	        	}
	        }
		        catch(EOFException e){
		        	if (clientSocket.isConnected()){
		        		System.out.println("EOF Client disconnected");
		        		e.printStackTrace();
		        		break;
		        	}
		        	e.printStackTrace();
		        }
        		catch (IOException e) {
		        	if (clientSocket.isConnected()){
		        		System.out.println("Client disconnected");
		        		e.printStackTrace();
		        		break;
		        	}
		        }
        }
    }
}
