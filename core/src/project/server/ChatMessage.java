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

public class ChatMessage implements Serializable{
	public static final long serialVersionUID = 1;
	private String username;
	private String message;
	private Boolean systemMessage;
	
	public ChatMessage(String username, String message, Boolean systemMessage) {
		this.username = username;
		this.message = message;
		this.systemMessage = systemMessage;
	}
	public String getUsername() {
		return username;
	}
	public String getMessage() {
		return message;
	}
	public Boolean isSystemMessage() {
		return systemMessage;
	}
	
}
