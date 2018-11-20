package project.server;

import java.io.Serializable;

public class PieceSetRequest implements Serializable{
	public static final long serialVersionUID = 1;
	public int playerID;
	public String code;
	
	public PieceSetRequest(String code) {
		this.code = code;
	}
}
