package project.server;

import java.io.Serializable;

public class LoginRegister implements Serializable{
	public static final long serialVersionUID = 1;
	private String loginRegister;
	
	public LoginRegister(String loginRegister) {
		this.loginRegister = loginRegister;
	}
	public String getloginRegister() {
		return loginRegister;
	}
}
