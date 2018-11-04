package project.server;

import java.io.Serializable;

public class Password implements Serializable{
	public static final long serialVersionUID = 1;
	private String password;
	
	public Password(String password) {
		this.password = password;
	}
	public String getPassword() {
		return password;
	}
}
