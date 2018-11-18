package project.server;

import java.util.HashSet;
import java.util.Random;
import java.util.Vector;

public class GameRoom {
	
		//each serverThread represents a connected client
		public Vector<ServerThread> serverThreads;
		
		//a vector to store the last 3 messages on server (could be redundant)
		public Vector<ChatMessage> messageVec;
		
		//a vector to store Player objects
		public Vector<Player> playerVec;
		
		public String code;
		
		public HashSet<Integer> player0PieceSet = new HashSet<Integer>();
		public HashSet<Integer> player1PieceSet = new HashSet<Integer>();
	
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
		
		generatePieceSets();

	}
	
	public void generatePieceSets() {
		//pieceSet0 = 1 to 16, pieceSet1 = 17 to 32
				HashSet<Integer> pieceSet0 = new HashSet<Integer>();
				HashSet<Integer> pieceSet1 = new HashSet<Integer>();
				
				for(int i = 1; i < 17; i ++) {
					pieceSet0.add(new Integer(i));
					pieceSet1.add(new Integer(i+16));
				}
						
				Random rand = new Random();
				for(int i = 0; i < 6; i++) {
					int num;
					while(true) {
						num = rand.nextInt(16) + 1;
						if(pieceSet0.contains(num)) {
							break;
						}
					}
						player1PieceSet.add(new Integer(num));
						pieceSet0.remove(num);
						
					while(true) {
						num = rand.nextInt(16) + 1;
						num += 16;
						if(pieceSet1.contains(num)) {
							break;
						}
					}
						player0PieceSet.add(new Integer(num));
						pieceSet1.remove(num);
				}
				player0PieceSet.addAll(pieceSet0);
				player1PieceSet.addAll(pieceSet1);
				//System.out.println("player0PieceSet = " + player0PieceSet);
				//System.out.println("player1PieceSet = " + player1PieceSet);
	}
}
