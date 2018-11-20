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