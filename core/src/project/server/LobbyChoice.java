package project.server;

import java.io.Serializable;

public class LobbyChoice implements Serializable{
	public static final long serialVersionUID = 1;
	public String choice;
	public String code;
	
	public LobbyChoice(String choice, String code) {
		this.choice = choice;
		this.code = code;
	}
	
}
