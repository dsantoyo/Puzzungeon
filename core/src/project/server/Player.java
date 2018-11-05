package project.server;

import java.io.Serializable;

/*
 * 
Each player object represents a player in the game
Each client has two players: localPlayer / otherPlayer

The server has a player vector/array to store the players
Each client updates its localPlayer and send it to the server
The server sends the information the other player back to each client

*/

public class Player implements Serializable{
	public static final long serialVersionUID = 1;
	
	public Player(String playerName){
		this.playerName = playerName;
		this.playerID = -1;
		this.readyState = false;
	}
	

	public String playerName;
		
	//playerID = the index of the player in server's playerVec
	public int playerID;
	
	
	public Boolean readyState;
	
	//Avatar avatar ?
	//Score score ?
	
	//other in-game objects that the server should know...
}

