package project.server;

import java.io.Serializable;

public class LoginResult implements Serializable{
	public static final long serialVersionUID = 1;
	private Boolean loginResult;
	private String message;
	
	
	public LoginResult(Boolean loginResult, String message) {
		this.loginResult = loginResult;
		this.message = message;
	}
	public Boolean getLoginResult() {
		return loginResult;
	}
	public String getMessage() {
		return message;
	}
}
