package project.server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Vector;

public class Server {
	
	//each serverThread represents a connected client
	public Vector<ServerThread> serverThreads;
	
	//a vector to store the last 3 messages on server (could be redundant)
	private Vector<ChatMessage> messageVec;
	
	//a vector to store Player objects
	private Vector<Player> playerVec;
	
	//private boolean allConnected;
	
	public Server(int port) {
		
		//a vector to store Player objects
		playerVec = new Vector<Player>();
		playerVec.add(new Player("default"));
		playerVec.add(new Player("default"));
		
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
			
			/*
			//use this to find an available port automatically
			  
			//find an available port
			System.out.println("finding a free port...");
			ss = new ServerSocket(0);
			int port = ss.getLocalPort();
			System.out.println("server bound to port: " + port); 
			
			*/
			
			
			//connect server to a hard-coded port
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
				System.out.println("serverThreads size = " + serverThreads.size());
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

/*
 *  Evey Server method below is called by a serverThread,
 *	when this serverThread wants the server to do something
 */
	
	
	/*  Get new message from a serverThread
	 *  Update this new message to server's messageVec
	 *  Broadcast this new message to every serverThread
	 */
	public void broadcastMessage(ChatMessage cm) {
		
		if(cm != null) {
			
			//remove the oldest message
			messageVec.remove(0);
			
			// add the newest message
			messageVec.add(cm);
			System.out.println("Server new message: " + cm.getUsername()+" "+cm.getMessage());
			//send the newest message to every serverThread
			for(ServerThread thread : serverThreads) {
					thread.sendMessage(messageVec.get(2));
			}
		}
	}
	
	/* For adding a new player onto the server.
	 * Get a Player object from a new serverThread and store it in server's playerVec
	 * Use the size of playerVec on the server after the addition as playerID
	 * Send this playerID back to front-end
	 */
	public int addServerPlayer(Player player) {
		if(player != null) {
			
			//find an available spot in playerVec
			for(int i = 0; i < 2; i++) {
				if(playerVec.get(i).playerID == -1) {
					player.playerID = i;
					playerVec.set(i, player);
					break;
				}
			}
			
			System.out.println("Server new player: username = " + player.playerName +", playerID = " + player.playerID);
		}
		return player.playerID;
	}
	
	
	/* For updating an existing server on the server.
	 * Get player object from a serverthread and update it in server's playerVec
	 * User playerID to acees its counterpart in server's playerVec
	 */
	public void updateServerPlayer(int playerID, Player player) {
		playerVec.set(playerID,player);

		System.out.println("Server updated player: username = " + player.playerName +", playerID = " + player.playerID);
		
		//when a player is updated in server's playerVec,
		//also update front-end's otherPlayer
		sendServerOtherPlayer();
	}
	
	/* Get called when a serverThread wants to know if every player is ready to play
	 * Check the readyState of every player on the server
	 * Call every serverThread to to update ove
	 */
	public void checkAllReadyState() {
		
		Boolean allReady = true;
		
		for(Player playerIterate : playerVec) {
			if(!playerIterate.readyState) {
				allReady = false;
			}
		}

		//broadcast the overall ready state to every serverThread
		for(ServerThread thread : serverThreads) {
				thread.broadcastReadyState(allReady);
		}
	}
	
	/* For updating every client's otherPlayer 
	 * Send corresponding player object in server's playerVec to each serverThread
	 */
	public void sendServerOtherPlayer() {
		if(playerVec.size() == 2) {
			for(ServerThread thread : serverThreads) {
				int localPlayerID = thread.getServerThreadPlayerID();
				if(localPlayerID == 0) {
					thread.updateOtherPlayer(playerVec.get(1));
				}
				else {
					thread.updateOtherPlayer(playerVec.get(0));
				}
			}
		}
	}
	
	public static void main(String [] args) {
		
		//hard-coded. need to be changed
		new Server(6789);
	}
}
