package project.server;

import java.io.Serializable;
import java.util.HashSet;

public class PieceSetResponse implements Serializable{
	public static final long serialVersionUID = 1;
	public HashSet<Integer> player0PieceSet;
	public HashSet<Integer> player1PieceSet;
	
	public PieceSetResponse(HashSet<Integer> player0PieceSet, HashSet<Integer> player1PieceSet) {
		this.player0PieceSet = player0PieceSet;
		this.player1PieceSet = player1PieceSet;
	}
}
