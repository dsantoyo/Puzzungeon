package project.server;

import java.io.Serializable;

public class GameRoomCode implements Serializable{
	public static final long serialVersionUID = 1;
	public String code;
	
	public GameRoomCode(String code) {
		this.code = code;
	}
}
