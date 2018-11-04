package project.puzzungeon;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Vector;

import project.server.ChatMessage;
import project.server.Password;
import project.server.Player;
import project.server.Username;

public class Client {
	
	public Socket s;
	public ObjectInputStream ois;
	public ObjectOutputStream oos;
	public int port;
	public String hostname;
	public Vector<ChatMessage> messageVec;
	public String clientUsername;
	
	//client's own player
	public Player localPlayer;
	
	//the other player. constantly updated by the server
	public Player otherPlayer;
	
	public Client(String hostname, int port) {
		this.hostname = hostname;
		this.port = port;
		this.clientUsername = "default";
	}
	
	
	//setting up connection between a client and the server
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
			messageVec.add(new ChatMessage("", ""));
			messageVec.add(new ChatMessage("", ""));
			messageVec.add(new ChatMessage("", ""));
			
			//send localPlayer to the server
			localPlayer = new Player(clientUsername);
			sendPlayer(localPlayer);
				
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
	//send message from a client(front-end) to a serverthread(back-end)
	public void sendMessage(ChatMessage cm) {
        try {
			oos.writeObject(cm);
			oos.flush();
		} catch (IOException ioe) {
			System.out.println("ioe: " + ioe.getMessage());
		}
	}
	//send username from a client(front-end) to a serverthread(back-end)
	public void sendUsername(Username username) {
		System.out.println("sendUsername() called with username = " + username.getUsername() );
        try {
			oos.writeObject(username);
			oos.flush();
		} catch (IOException ioe) {
			System.out.println("ioe: " + ioe.getMessage());
		}
	}
	
	//send password from a client(front-end) to a serverthread(back-end)
	public void sendPassword(Password password) {
		System.out.println("sendPassword() called with password = " + password.getPassword() );
        try {
			oos.writeObject(password);
			oos.flush();
		} catch (IOException ioe) {
			System.out.println("ioe: " + ioe.getMessage());
		}
	}
	
	//send player from a client(front-end) to a serverthread(back-end)
	public void sendPlayer(Player player) {
		try {
			oos.writeObject(player);
			oos.flush();
		} catch (IOException ioe) {
			System.out.println("ioe: " + ioe.getMessage());
		}
	}
}
