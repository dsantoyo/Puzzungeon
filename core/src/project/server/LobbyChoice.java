package project.server;

import java.io.Serializable;

public class LobbyChoice implements Serializable{
	public static final long serialVersionUID = 1;
	public String choice;
	
	public LobbyChoice(String choice) {
		this.choice = choice;
	}
}
