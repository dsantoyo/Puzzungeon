package project.puzzungeon;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Vector;

import project.server.ChatMessage;
import project.server.Password;
import project.server.Player;
import project.server.ReadyState;
import project.server.Username;

public class Client {
	
	public Socket s;
	public ObjectInputStream ois;
	public ObjectOutputStream oos;
	public int port;
	public String hostname;
	public Vector<ChatMessage> messageVec;
	public String clientUsername;
	
	//the ready state of player1 AND player2. Updated by the server
	public Boolean bothPlayerReady;
	
	//client's own player
	public Player localPlayer;
	
	//the other player. constantly updated by the server
	public Player otherPlayer;
	
	//constructor
	public Client(String hostname, int port) {
		this.hostname = hostname;
		this.port = port;
		this.clientUsername = "default";
		this.bothPlayerReady = false;
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
			
			//add localPlayer to the server
			localPlayer = new Player(clientUsername);
			updatePlayer();
				
		} catch (IOException ioe) {
			System.out.println("ioe: " + ioe.getMessage());
		}
		
		//use a thread to receive objects from the assigned erverThread
				new Thread(new Runnable(){
					
		            @Override
		            public void run() {
		            	
		            	try {
		            		while(true){
		            			Object object = ois.readObject();
		            			
		            			//if the serverThread sends a ChatMessage to the client
		            			if(object instanceof ChatMessage) {
		            				ChatMessage newMessage = (ChatMessage)object;
		            				messageVec.remove(0);
		            				messageVec.add(newMessage);
		            			}
		            			//if the serverThread sends the size of server PlayerVec to the client
		            			if(object instanceof Integer) {
		            				Integer integer = (Integer)object;
		            				localPlayer.playerID = integer.intValue();
		            				System.out.println("localPlayerID = " + localPlayer.playerID);
		            			}
		            			
		            			//if the serverThread sends the readyState of player1 AND player2
		            			if(object instanceof ReadyState) {
		            				System.out.println("update ReadyState");
		            				ReadyState rs = (ReadyState)object;
		            				bothPlayerReady = rs.getReadyState();
		            			}
		            			
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
	
	//send/update a player from a client(front-end) to a serverthread(back-end)
	public void updatePlayer() {
		
		System.out.println("player ready state in client = " + localPlayer.readyState);
		try {
			oos.writeObject(localPlayer);
			oos.flush();
			//discard any cached references. this shit took me 1.5 hours to debug...
			oos.reset();
		} catch (IOException ioe) {
			System.out.println("ioe: " + ioe.getMessage());
		}
	}
}
