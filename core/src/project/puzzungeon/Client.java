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
		this.clientUsername = "default";
	}
	
	public void connect() {
		
		try {
			System.out.println("Trying to connect to " + hostname + ":" + port);
			s = new Socket(hostname,port);
			System.out.println("connected to socket! Opening streams...");
			oos = new ObjectOutputStream(s.getOutputStream());
			ois = new ObjectInputStream(s.getInputStream());
			System.out.println("Connected to " + hostname + ":" + port);
			//only store the last 3 messages on the client side
			messageVec = new Vector<ChatMessage>(3);
			messageVec.add(new ChatMessage("Hello ", clientUsername+"!"));
			messageVec.add(new ChatMessage("", ""));
			messageVec.add(new ChatMessage("", ""));
				
		} catch (IOException ioe) {
			System.out.println("ioe: " + ioe.getMessage());
		}
		
		//use a thread to receive new message from the server
				new Thread(new Runnable(){
					
		            @Override
		            public void run() {
		            	
		            	try {
		            		while(true){
		            			ChatMessage newMessage = (ChatMessage)ois.readObject();
		                	
		            			messageVec.remove(0);
		            			messageVec.add(newMessage);
		            		}
		            	}catch(IOException ioe) {
		            		System.out.println("ioe: " + ioe.getMessage());
		            	}catch (ClassNotFoundException cnfe) {
		            		System.out.println("cnfe: " + cnfe.getMessage());
		            	}
		            }
		        }).start(); //start the thread;

		
	}
	public void sendMessage(ChatMessage cm) {
		//send message to serverthread
        try {
			oos.writeObject(cm);
			oos.flush();
		} catch (IOException ioe) {
			System.out.println("ioe: " + ioe.getMessage());
		}
		
	}
}
