package project.puzzungeon;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Vector;

import project.server.ChatMessage;

public class Client {
	
	public Socket s;
	public ObjectInputStream ois;
	public ObjectOutputStream oos;
	public int port;
	public String hostname;
	public Vector<ChatMessage> messageVec;
	public String clientUsername;
	
	public Client(String hostname, int port) {
		this.hostname = hostname;
		this.port = port;
		clientUsername = "default username";
	}
	
	public void connect() {
		
		try {
			s = new Socket(hostname,port);
			oos = new ObjectOutputStream(s.getOutputStream());
			ois = new ObjectInputStream(s.getInputStream());
			System.out.println("Connected to " + hostname + ":" + port);
				
		} catch (IOException ioe) {
			System.out.println("ioe: " + ioe.getMessage());
		}
	}
	
	public void setClientUsername(String username) {
		this.clientUsername = username;
	}
}
