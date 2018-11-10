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
