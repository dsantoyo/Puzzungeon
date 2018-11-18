package project.server;

import java.io.Serializable;
import java.util.HashSet;

public class PlayerIDnPieceSet implements Serializable{
	public static final long serialVersionUID = 1;
	public int id;
	public  HashSet<Integer> playerPieceSet;
	
	public PlayerIDnPieceSet(int ID, HashSet<Integer> playerPieceSet) {
		this.id = ID;
		this.playerPieceSet = playerPieceSet;
	}

}
