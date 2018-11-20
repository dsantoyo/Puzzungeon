/*CSCI201 Final Project

Project Name: Puzzungeon
Project Number: 7
Project Category: Game

Daniel Santoyo: dsantoyo@usc.edu USC ID: 6926712177
Hayley Pike: hpike@usc.edu USC ID: 8568149839
Yi(Ian) Sui: ysui@usc.edu USC ID: 2961712187
Ekta Gogri: egogri@usc.edu USC ID: 9607321862
*/

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
