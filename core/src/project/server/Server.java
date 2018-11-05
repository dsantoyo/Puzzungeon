package project.server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Vector;

public class Server {
	
	//each serverThread represents a connected client
	private Vector<ServerThread> serverThreads;
	
	//a vector to store the last 3 messages on server (could be redundant)
	private Vector<ChatMessage> messageVec;
	
	//a vector to store Player objects
	private Vector<Player> playerVec;
	
	//private boolean allConnected;
	
	public Server(int port) {
		
		//a vector to store Player objects
		playerVec = new Vector<Player>();
		
		//a vector to store the last 3 messages on server (could be redundant)
		messageVec = new Vector<ChatMessage>(3);
		messageVec.add(new ChatMessage("", ""));
		messageVec.add(new ChatMessage("", ""));
		messageVec.add(new ChatMessage("", ""));
		//allConnected = false;
		
		//Display the priave IP of the server.
		//Necessary information to build connection between different machines
		InetAddress inetAddress;
		try {
			inetAddress = InetAddress.getLocalHost();
			System.out.println("Server IP Address: " + inetAddress.getHostAddress());
		} catch (UnknownHostException e) {
			
			e.printStackTrace();
		}
		
		//Getting connection request from clients
		ServerSocket ss = null;
		try {
			System.out.println("trying to bind to port " + port);
			ss = new ServerSocket(port);
			System.out.println("bound to port " + port);
			
			serverThreads = new Vector<ServerThread>();
			
			while(true) { // constantly waiting for any new connection
				Socket s = ss.accept();
				System.out.println("Connection from " + s.getInetAddress());
				
				//assign the socket(connected to a client) to a serverThread
				ServerThread st = new ServerThread(s, this);
				
				// start thread in its constructor
				serverThreads.add(st); // we have a serverThread for every client
				
				/*
				if (serverThreads.size() > 2 && allConnected == false) {
					System.out.println("Enough connections.");
					allConnected = true;
					for (int i = 0; i < serverThreads.size(); i++) {
						serverThreads.get(i).sendConnNum(2);
					}
				}
				*/
			}
		}catch (IOException ioe) {
			System.out.println("ioe:"+ioe.getMessage());
		}finally {
			try {
				if(ss != null) {
					ss.close();
				}
			}catch (IOException ioe) {
				System.out.println("ioe:"+ioe.getMessage());
			}
		}
	}

	//get new message from a serverthread and send to every client
	public void broadcast(ChatMessage cm) {
		
		if(cm != null) {
			
			//remove the oldest message
			messageVec.remove(0);
			
			// add the newest message
			messageVec.add(cm);
			
			//sd the newest message to every client
			for(ServerThread thread : serverThreads) {
					thread.sendMessage(messageVec.get(2));
			}
		}
	}
	
	//get Player object from a new serverthread and store it in playerVec
	// returns the size of playerVec on the server
	public int addServerPlayer(Player player) {
		if(player != null) {
			player.playerID = playerVec.size();
			playerVec.add(player);
			System.out.println("new player with username = " + player.playerName +" and playerID = " + player.playerID + " is added");
			System.out.println("now playerVec size = " + playerVec.size());
		}
		return playerVec.size();
	}
	
	//reading player object from serverthread and update it in server's playerVec
	public void updateServerPlayer(int playerID, Player player) {
		playerVec.set(playerID,player);

		for(Player playertemp : playerVec) {
			System.out.println("player " + playertemp.playerID + " ready state = " + playertemp.readyState);
		}
	}
	
	//check the readyState of every player on the server
	public void checkReadyState() {
		
		Boolean allReady = true;
		
		for(Player playerIterate : playerVec) {
			if(!playerIterate.readyState) {
				allReady = false;
			}
		}
		if(playerVec.size() < 2) {
			allReady = false;
		}
		//broadcast the overall ready state to every serverThread
		for(ServerThread thread : serverThreads) {
				thread.broadCastReadyState(allReady);
		}
	}
	
	public static void main(String [] args) {
		
		//hard-coded. need to be changed
		new Server(6789);
	}
}
