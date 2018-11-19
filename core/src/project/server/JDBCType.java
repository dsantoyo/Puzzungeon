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
	private int userID;
	private int score;
	//private boolean login = false;
	
	public JDBCType(String username, String password, String determine) 
	{
		this.username = username;
		this.password = password;
		this.userID = 0;
		this.score = 0;
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
	
	public int getHighScore() throws SQLException {
		rs = st.executeQuery("SELECT * from highscore_table where user1 ='"+username+"' OR user2= '"+username+"'" );
		
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
		++userID;
		ps = conn.prepareStatement("INSERT INTO user_table (userID, username, userpassword)" + " VALUES (?, ?, ?)");
		ps.setInt(1, userID);
		ps.setString(2, username);
		ps.setString(3, password);
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