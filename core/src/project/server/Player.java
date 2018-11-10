package project.server;

import java.io.Serializable;

/*
 * 
Each player object represents a player in the game
Each client has two players: localPlayer / otherPlayer

The server has a player vector(thread-safe) to store the players
Each client updates its localPlayer and send it to the server
The server sends the information otherPlayer back to each client

*/

public class Player implements Serializable{
	public static final long serialVersionUID = 1;
	
	public Player(String playerName){
		this.playerName = playerName;
		this.playerID = -1;
		this.readyState = false;
		this.disconnect = false;
	}
	
	public String playerName;
		
	//playerID = the index of the player in server's playerVec
	public int playerID;
	
	// is this player ready to play the game?
	public Boolean readyState;
	
	public Boolean disconnect;
	
	//other in-game objects that the server should know...
	//Avatar avatar ?
	//Score score ?
}

