package project.server;

import java.io.Serializable;

public class ReadyState implements Serializable{
	public static final long serialVersionUID = 1;
	private Boolean readystate;
	
	public ReadyState(Boolean readystate) {
		this.readystate = readystate;
	}
	public Boolean getReadyState() {
		return readystate;
	}
}
