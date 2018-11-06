package project.server;

import java.io.Serializable;

public class LoginResult implements Serializable{
	public static final long serialVersionUID = 1;
	private Boolean loginResult;
	
	public LoginResult(Boolean loginResult) {
		this.loginResult = loginResult;
	}
	public Boolean getLoginResult() {
		return loginResult;
	}
}
