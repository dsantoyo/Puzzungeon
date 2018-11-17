package project.server;

import java.util.Vector;

public class GameRoom {
	
		//each serverThread represents a connected client
		public Vector<ServerThread> serverThreads;
		
		//a vector to store the last 3 messages on server (could be redundant)
		public Vector<ChatMessage> messageVec;
		
		//a vector to store Player objects
		public Vector<Player> playerVec;
		
		public String code;
	
	GameRoom(String code){
		
		serverThreads = new Vector<ServerThread>();
		
		this.code = code;
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

	}
}
