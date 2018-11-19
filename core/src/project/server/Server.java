package project.server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Random;
import java.util.Vector;

public class Server {
	
	//each serverThread represents a connected client
	public Vector<ServerThread> serverThreads;
	
	//a vector to store the last 3 messages on server (could be redundant)
	private Vector<ChatMessage> messageVec;
	
	//a vector to store Player objects
	public Vector<Player> playerVec;
	
	//private boolean allConnected;
	
	public HashMap<String, GameRoom> gameRoomMap;
	
	public Server(int port) {
		
		gameRoomMap = new HashMap<String, GameRoom>();
		
		//a vector to store Player objects
		playerVec = new Vector<Player>();
		playerVec.add(new Player("default"));
		playerVec.add(new Player("default"));
		
		//a vector to store the last 3 messages on server (could be redundant)
		messageVec = new Vector<ChatMessage>(4);
		messageVec.add(new ChatMessage("", "",false));
		messageVec.add(new ChatMessage("", "",false));
		messageVec.add(new ChatMessage("", "",false));
		messageVec.add(new ChatMessage("", "",false));
		//allConnected = false;
		
		//Display the priave IP of the server.
		//Necessary information to build connection between different machines
		InetAddress inetAddress;
		try {
			inetAddress = InetAddress.getLocalHost();
			System.out.println("server: server IP Address: " + inetAddress.getHostAddress());
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
			System.out.println("server: trying to bind to port " + port);
			ss = new ServerSocket(port);
			System.out.println("server: bound to port " + port);
			
			serverThreads = new Vector<ServerThread>();
			
			while(true) { // constantly waiting for any new connection
				Socket s = ss.accept();
				System.out.println("server: connection from " + s.getInetAddress());
				
				//assign the socket(connected to a client) to a serverThread
				ServerThread st = new ServerThread(s, this);
				
				// start thread in its constructor
				serverThreads.add(st); // we have a serverThread for every client
				System.out.println("server: serverThreads size: " + serverThreads.size());

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
	public void broadcastMessage(ChatMessage cm, String roomCode) {
		
		System.out.println("server: broadcastMessage() called with roomCode = " + roomCode);
		
		if(cm != null) {
			
			gameRoomMap.get(roomCode).reLock.lock();
			
			try {
				
				//remove the oldest message
				gameRoomMap.get(roomCode).messageVec.remove(0);
				
				// add the newest message
				gameRoomMap.get(roomCode).messageVec.add(cm);
				System.out.println("server: server new message for room "+roomCode+" : " + cm.getUsername()+" "+cm.getMessage());
				//send the newest message to every serverThread
				for(ServerThread thread : gameRoomMap.get(roomCode).serverThreads) {
					thread.sendMessage(gameRoomMap.get(roomCode).messageVec.get(3));
				}
			
			}finally {
				gameRoomMap.get(roomCode).reLock.unlock();
			}
			
		}
	}
	
	/* For adding a new player onto the server.
	 * Get a Player object from a new serverThread and store it in server's playerVec
	 * Use the size of playerVec on the server after the addition as playerID
	 * Send this playerID back to front-end
	 */
	public int addServerPlayer(Player player, String roomCode) {
		
		if(gameRoomMap.size() == 0) {
			System.out.println("server: addServerPlayer 0 rooms");
		}
		if(player != null) {
			
			gameRoomMap.get(roomCode).reLock.lock();
			
			try {
				
				//find an available spot in playerVec
				for(int i = 0; i < 2; i++) {
					if(gameRoomMap.get(roomCode).playerVec.get(i).playerID == -1) {
						player.playerID = i;
						gameRoomMap.get(roomCode).playerVec.set(i, player);
						break;
					}
				}
				
				System.out.println("server: room " + roomCode +"new player: username = " + player.playerName +", playerID = " + player.playerID);
				
			}finally {
				gameRoomMap.get(roomCode).reLock.unlock();
			} 
			
		}
		return player.playerID;
	}
	
	/* For updating an existing server on the server.
	 * Get player object from a serverthread and update it in server's playerVec
	 * User playerID to acees its counterpart in server's playerVec
	 */
	public void updateServerPlayer(int playerID, Player player, String roomCode) {
		
		gameRoomMap.get(roomCode).reLock.lock();
		
		try {
		
			gameRoomMap.get(roomCode).playerVec.set(playerID,player);

			System.out.println("server: server updated room "+roomCode+" player: username = " + player.playerName +", playerID = " + player.playerID);
			
		}finally {
			gameRoomMap.get(roomCode).reLock.unlock();
		} 

		//when a player is updated in server's playerVec,
		//also update front-end's otherPlayer
		sendServerOtherPlayer(roomCode);
	}
	
	/* For updating every client's otherPlayer 
	 * Send corresponding player object in server's playerVec to each serverThread
	 */
	public void sendServerOtherPlayer(String roomCode) {
		
		gameRoomMap.get(roomCode).reLock.lock();
		
		try {
			
			if(gameRoomMap.get(roomCode).playerVec.size() == 2) {
				for(ServerThread thread : gameRoomMap.get(roomCode).serverThreads) {
					int localPlayerID = thread.getServerThreadPlayerID();
					if(localPlayerID == 0) {
						thread.updateOtherPlayer(gameRoomMap.get(roomCode).playerVec.get(1));
					}
					else {
						thread.updateOtherPlayer(gameRoomMap.get(roomCode).playerVec.get(0));
					}
				}
			}
		
		}finally {
			gameRoomMap.get(roomCode).reLock.unlock();
		} 

	}
	
	public Boolean isGameFull(String roomCode) {
		
		gameRoomMap.get(roomCode).reLock.lock();
		
		try {
			
			System.out.println("server: isGameFull() called on room " + roomCode);
			System.out.println(gameRoomMap.get(roomCode).playerVec.get(0).playerID);
			System.out.println(gameRoomMap.get(roomCode).playerVec.get(1).playerID);
			
			return ((gameRoomMap.get(roomCode).playerVec.get(0).playerID != -1) && (gameRoomMap.get(roomCode).playerVec.get(1).playerID != -1));
		
		}finally {
			gameRoomMap.get(roomCode).reLock.unlock();
		} 

	}
	
	public String generateGameRoomCode() {
		String code = "";
		Random r = new Random();
		while(true){
			char c1 = (char)(r.nextInt(26) + 'A');
			char c2 = (char)(r.nextInt(26) + 'A');
			char c3 = (char)(r.nextInt(26) + 'A');
			char c4 = (char)(r.nextInt(26) + 'A');
			code = Character.toString(c1) + Character.toString(c2) + Character.toString(c3) + Character.toString(c4);
			if(!gameRoomMap.containsKey(code)) {
				break;
			}
		}
		System.out.println("server: generate GameRoomCode str = " + code);
		return code;
	}
	
	public static void main(String [] args) {
		
		//hard-coded. need to be changed
		new Server(6789);
	}
}
