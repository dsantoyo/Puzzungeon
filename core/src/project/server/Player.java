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
	public Player(String username){
		this.username = username;
		this.playerID = 0;
	}
	
	public String username;
	
	//playerID = the index of the player in server's playerVec
	public int playerID;
	
	//Avatar avatar ?
	//Score score ?
	
	//other in-game objects that the server should know...
}

