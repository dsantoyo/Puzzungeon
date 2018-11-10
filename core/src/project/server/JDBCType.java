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
	private String determine;
	private int userID;
	private int score;
	//private boolean login = false;
	
	public JDBCType(String username, String password, String determine) 
	{
		this.username = username;
		this.password = password;
		this.determine = determine;
		this.userID = 0;
		this.score = 0;
	}
	
	public void something() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection("jdbc:mysql://localhost/game_database?user=root&password=root&useSSL=false");
			st = conn.createStatement();
			
			if(determine.equals("register") && !exists(username)) {
				++userID;
				ps = conn.prepareStatement("INSERT INTO user_table (userID, username, userpassword)" + " VALUES (?, ?, ?)");
				ps.setInt(1, userID);
				ps.setString(2, username);
				ps.setString(3, password);
				ps.execute();
			}
			else {
				if(!exists(username)) {
					//login = false?
					
				}
				else if(PlayerValidation(username, password)) {
					//login = true;
					score = getHighScore();
				}
			}
			
		} catch (SQLException sqle) {
			System.out.println("SQLException: " + sqle.getMessage());
		} catch (ClassNotFoundException cnfe) {
			System.out.println("ClassNotFoundException: " + cnfe.getMessage());
		} 
		//finally {
//			try {
//				if(rs != null) {
//					rs.close();
//				}
//				if(st != null) {
//					st.close();
//				}
//				if(ps != null) {
//					ps.close();
//				}
//				if(conn != null) {
//					conn.close();
//				}
//			} catch(SQLException sqle) {
//				System.out.println("sqle: " + sqle.getMessage());
//			}
//		}
	}
	
	public int getHighScore() throws SQLException {
		rs = st.executeQuery("SELECT * from highscore_table where user1 ='"+username+"'");
		
		while(rs.next()) {
			//String user2 = rs.getString("user2");
			
			score = rs.getInt("score");
		}
		
		return score;
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