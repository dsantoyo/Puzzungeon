/*CSCI201 Final Project

Project Name: Puzzungeon
Project Number: 7
Project Category: Game

Daniel Santoyo: dsantoyo@usc.edu USC ID: 6926712177
Hayley Pike: hpike@usc.edu USC ID: 8568149839
Yi(Ian) Sui: ysui@usc.edu USC ID: 2961712187
Ekta Gogri: egogri@usc.edu USC ID: 9607321862
*/

package project.server;

import java.io.Serializable;
import java.util.HashSet;

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
		this.pastScore = 0;
		this.outgoingPiece = -1;
		this.incomingPiece = -1;
		this.correctPieceCount = 0;
		this.playerPieceSet = new HashSet<Integer>();
		this.isFinished = false;
		this.gameCounter = 0;
	}
	
	public String playerName;
		
	//playerID = the index of the player in server's playerVec
	public int playerID;
	
	// is this player ready to play the game?
	public Boolean readyState;
	
	public Boolean disconnect;
	
	public int pastScore;
	
	//other in-game objects that the server should know...
	//Avatar avatar ?
	//Score score ?
	
	public int outgoingPiece;
	public int incomingPiece;
	
	public HashSet<Integer> playerPieceSet;
	
	public int correctPieceCount;
	
	public Boolean isFinished;
	
	public int gameCounter;
	
}

