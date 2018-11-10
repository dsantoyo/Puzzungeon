package project.server;

import java.io.Serializable;

public class LoginResult implements Serializable{
	public static final long serialVersionUID = 1;
	private Boolean loginResult;
	private String message;
	public int pastScore;
	
	
	public LoginResult(Boolean loginResult, String message, int pastScore) {
		this.loginResult = loginResult;
		this.message = message;
		this.pastScore = pastScore;
	}
	public Boolean getLoginResult() {
		return loginResult;
	}
	public String getMessage() {
		return message;
	}
}
