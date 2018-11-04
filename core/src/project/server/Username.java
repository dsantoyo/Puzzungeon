package project.server;

import java.io.Serializable;

public class Username implements Serializable{
	public static final long serialVersionUID = 1;
	private String username;
	
	public Username(String username) {
		this.username = username;
	}
	public String getUsername() {
		return username;
	}
}
