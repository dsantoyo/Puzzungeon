package project.puzzungeon;


/*
 * 
Each player object represents a player in the game
Each client has two players: localPlayer / otherPlayer

The server has a player vector/array to store the players
Each client updates its localPlayer and send it to the server
The server sends the information the other player back to each client

*/

public class Player {
	
	Player(String username){
		this.username = username;
	}
	
	String username;
	
	//Avatar avatar ?
	//Score score ?
	
	//other in-game objects that the server should know...
}
