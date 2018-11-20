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

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class JDBCType {
	Connection conn = null;
	Statement st = null;
	PreparedStatement ps = null;
	ResultSet rs = null;
	
	private String username;
	private String password;

	//private int score;
	//private boolean login = false;
	
	public JDBCType(String username, String password) 
	{
		this.username = username;
		this.password = password;
	//	this.score = 0;
	}
	
	public Boolean connectionSet() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection("jdbc:mysql://localhost/Puzzungeon_database?allowPublicKeyRetrieval=true&user=root&password=root&useSSL=false");
			st = conn.createStatement();
			
		} catch (SQLException sqle) {
			System.out.println("SQLException: " + sqle.getMessage());
			return false;
			//we should exit/go to error screen here
		} catch (ClassNotFoundException cnfe) {
			System.out.println("ClassNotFoundException: " + cnfe.getMessage());
			//we should exit/go to error screen here
			return false;
		} 
		
		return true;
	}
	
	public int getScore() throws SQLException {
        int user1 = 0;
        int score1 = 0;
        //int score2 = 0;
        
        ps = conn.prepareStatement("SELECT * FROM user_table WHERE username = ?");
        ps.setString(1, username);
        rs= ps.executeQuery();
        
        while(rs.next()) {
            user1 = rs.getInt("userID");
        }
        
        ps = conn.prepareStatement("SELECT * FROM highscore_table WHERE user1=" + user1);
        //ps.setInt(1, user1);
        rs = ps.executeQuery();
        
        while(rs.next()) {
            if(user1 == rs.getInt("user1")) {
                score1 = rs.getInt("score");
            }
            
            if(user1 == (rs.getInt("user1"))) {
                if(rs.getInt("score") < score1) {
                    score1 = rs.getInt("score");
                }
            }
        }
        
        if(score1 != 0) {
            return score1;
        }
        
        return 0;
    }

	public void setScore(int score, String username1, String username2) throws Exception {
		//int pastScore = database.getScore();
		//username = username1;
		//int score1 = getScore();
		
//		ps = conn.prepareStatement("SELECT * from highscore_table where user1='" + username1 + "' AND user2= '"+username2+"'");
//		rs = ps.executeQuery();
		int user1 = 0;
		int user2 = 0;
		
		ps = conn.prepareStatement("SELECT * FROM user_table WHERE username = ?");
		ps.setString(1, username1);
		rs= ps.executeQuery();
		
		while(rs.next()) {
			user1 = rs.getInt("userID");
		}
		
		ps = conn.prepareStatement("SELECT * FROM user_table WHERE username = ?");
		ps.setString(1, username2);
		rs= ps.executeQuery();
		
		while(rs.next()) {
			user2 = rs.getInt("userID");
		}
		
		try {
			
			ps = conn.prepareStatement("INSERT INTO highscore_table (user1, user2, score)" + " VALUES (?, ?, ?)");
			ps.setInt(1, user1);
			ps.setInt(2, user2);
			ps.setInt(3, score);
			ps.execute();
		
		} catch (SQLException sqle) {
			System.out.println ("SQLException: " + sqle.getMessage());
		}
		
	}


	public boolean PlayerValidation(String username, String password) throws SQLException {
		//Checks if username and password matches
		ps = conn.prepareStatement("SELECT * from user_table where username='" + username + "' AND userpassword= '"+password+"'");
		rs = ps.executeQuery();
		if(rs != null) {
		try {
			while(rs.next()) {
				if(username.equals(rs.getString("username")) && password.equals(rs.getString("userpassword"))) {
					return true;
				}
			}
		} catch(SQLException sqle) {
			System.out.println("sqle: " + sqle.getMessage());
		}
		}
	
		return false;
	}
	
	public boolean exists(String username) throws SQLException {
		//scans through database if account is in database. If not, return false
		
		ps = conn.prepareStatement("SELECT * FROM user_table WHERE username = ?");
		ps.setString(1, username);
		rs= ps.executeQuery();
		
		try {
			while(rs.next()) {
				if(username.equals(rs.getString("username")))
				{
					return true;
				}
				System.out.println(rs.getString("username"));
			}
		} catch (SQLException sqle) {
			System.out.println ("SQLException: " + sqle.getMessage());
		}
		//++userID;
		ps = conn.prepareStatement("INSERT INTO user_table (username, userpassword)" + " VALUES (?, ?)");
		//ps.setInt(1, userID);
		ps.setString(1, username);
		ps.setString(2, password);
		ps.execute();
		
		return false;
	}
	
	public void close() {
		try {
			if(rs != null) {
				rs.close();
			}
			if(st != null) {
				st.close();
			}
			if(ps != null) {
				ps.close();
			}
			if(conn != null) {
				conn.close();
			}
		} catch(SQLException sqle) {
			System.out.println("sqle: " + sqle.getMessage());
		}
	}
}