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

public class PieceSetResponse implements Serializable{
	public static final long serialVersionUID = 1;
	public HashSet<Integer> player0PieceSet;
	public HashSet<Integer> player1PieceSet;
	
	public int randomPuzzleID;
	
	public PieceSetResponse(HashSet<Integer> player0PieceSet, HashSet<Integer> player1PieceSet, int randomPuzzleID) {
		this.player0PieceSet = player0PieceSet;
		this.player1PieceSet = player1PieceSet;
		this.randomPuzzleID = randomPuzzleID;
	}
}
