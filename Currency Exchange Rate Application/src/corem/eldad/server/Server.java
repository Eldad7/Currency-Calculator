/** By Eldad Corem 201263860 **/

package corem.eldad.server;

import java.net.*;
import java.text.ParseException;
import java.io.*;

public class Server implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@SuppressWarnings("resource")

	public synchronized static void main(String[] args) throws IOException, ParseException, InterruptedException {
		ServerSocket serverSocket = null;
		serverSocket = new ServerSocket(8080);
		Socket clientSocket = null;
		UpToDateChecker checker = new UpToDateChecker();
		checker.start();
        while(true){
		    try {
		        clientSocket = serverSocket.accept();
		    }
		    catch (IOException e) {
		        System.err.println("Accept failed.");
		        e.printStackTrace();
		        System.exit(1);
		    }
		    System.out.println("Client connected");
		    ServerThread st = new ServerThread(clientSocket, checker);
		    synchronized(st){
		    	st.start();
		    }
        }
	}
}
