package project.server;

import java.io.Serializable;

public class PieceID implements Serializable{
	public static final long serialVersionUID = 1;
	public int id;
	
	public PieceID(int ID) {
		this.id = ID;
	}

}
