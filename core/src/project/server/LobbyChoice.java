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

public class LobbyChoice implements Serializable{
	public static final long serialVersionUID = 1;
	public String choice;
	public String code;
	
	public LobbyChoice(String choice, String code) {
		this.choice = choice;
		this.code = code;
	}
	
}
