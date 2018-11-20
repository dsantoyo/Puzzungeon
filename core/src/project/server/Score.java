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

public class Score implements Serializable{
    public static final long serialVersionUID = 1;
    public int time;
    public String username1;
    public String username2;
    
    
    public Score(int time, String username1, String username2) {
        this.time = time;
        this .username1 = username1;
        this.username2 = username2;
    }

}