package project.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.sql.SQLException;


//a serverThread object(back-end) is connected to a client(front-end)
public class ServerThread extends Thread{ 

	private ObjectInputStream ois;
	private ObjectOutputStream oos;
	private Username username;
	private Password password;
	private LoginRegister loginRegister;
	private Player player;
	private Server server;
	private Boolean clientLoginState;
	
	/* the localPlayerID of this serverThread's client's localPlayer
	 * server uses this to know which player in server's playerVec is 
	 * this client's otherPlayer
	 */
	private int serverThreadPlayerID;
	private String serverThreadPlayerName;
	
	//when a client tries to connect to the server,
	//ServerThread constructor will be called by the server 
	public ServerThread(Socket socket, Server server) {
		
		//linked the server with this ServerThread object
		this.server = server;
		
		try {
			ois = new ObjectInputStream(socket.getInputStream());
			oos = new ObjectOutputStream(socket.getOutputStream());
			this.start();
			
		} catch(IOException ioe) {
			System.out.println("ioe: " + ioe.getMessage());
		}
	}

	public void run() {
		
		//keep checking if any object is sent from the client
		try {
			while(true) {
				
				//call the server to check overall ready state for both players
				server.checkAllReadyState();
				
				//sending object from client(front-end) to this serverthread(back-end)
				Object object = ois.readObject();
				
				//if a Chatmessage object is sent to this serverthread
				if(object instanceof ChatMessage) {
					ChatMessage cm = (ChatMessage)object;
					if( cm != null) {
						//send new message to the server
						server.broadcastMessage(cm);
					}
				}
				
				//if an Username/Password combination is sent to this serverthread
				if(object instanceof Username) {
					username = (Username)object;
					
					object = ois.readObject();
					password = (Password)object;
					
					object = ois.readObject();
					loginRegister = (LoginRegister)object;
					
					String usernameStr = username.getUsername();
					String passswordStr = password.getPassword();
					String loginRegisterStr = loginRegister.getloginRegister();
					
					System.out.println("serverThread: username = "+ usernameStr);
					System.out.println("serverThread: password = "+ passswordStr);
					System.out.println("serverThread: login/register = "+ loginRegisterStr);
					
					/*
					   Back-end login/register features/validation should be done here
					   
					   
					   //JDBCType database = new JDBCType();
					    //String errorMessage = database.errorMessage();
					*/	
					JDBCType database = new JDBCType(usernameStr, passswordStr, loginRegisterStr);
					database.something();

					System.out.println("trying to login...");
					System.out.println("serverThread: current player0 name on server: " + server.playerVec.get(0).playerName);
					System.out.println("serverThread: current player0 id on server: " + server.playerVec.get(0).playerID);
					System.out.println("serverThread: current player1 name on server: " + server.playerVec.get(1).playerName);
					System.out.println("serverThread: current player1 id on server: " + server.playerVec.get(1).playerID);
					
					//send server validation result from this serverthread back to its client
					
					// from loginScreen
					if (loginRegisterStr.equals("login") && !(database.PlayerValidation(usernameStr, passswordStr))) { // this condition has to be changed
						try {
							System.out.println("serverthread: denied. Check username/password");
							oos.writeObject(new LoginResult(false, "Check username/password"));
							oos.flush();
							oos.reset();
						} catch (IOException ioe) {
							System.out.println("serverthread: check db ioe: " + ioe.getMessage());
						} 
					}
					// from registerScreen
					else if (loginRegisterStr.equals("register") && !(database.exists(usernameStr))) { // this condition has to be changed
						System.out.println("bhehehehhehhe");
						try {
							System.out.println("serverthread: denied. Failed to register.");
							oos.writeObject(new LoginResult(false, "Failed to register."));
							oos.flush();
							oos.reset();
						} catch (IOException ioe) {
							System.out.println("serverthread: check db ioe: " + ioe.getMessage());
						}	
					}
					
					else if (server.isGameFull()) { // is 2 players are already in the game
						try {
							System.out.println("serverthread: denied. game is full.");
							oos.writeObject(new LoginResult(false, "Game is Full."));
							oos.flush();
							oos.reset();
						} catch (IOException ioe) {
							System.out.println("serverthread: isGameFull(): " + ioe.getMessage());
						}
					}
					else { // allow the client to login
						System.out.println("if we can log in");
						try {
							System.out.println("logged in.");
							clientLoginState = true;
							oos.writeObject(new LoginResult(true, "Login/Register done"));
							oos.flush();
							oos.reset();
						} catch (IOException ioe) {
							System.out.println("serverthread: check db ioe: " + ioe.getMessage());
						}
					}
					database.close();
				}
				
				//if a Player object is sent to this serverthread
				if(object instanceof Player) {
					player = (Player)object;
					if(player != null) {
						serverThreadPlayerName = player.playerName;
						
						//if a new player is being added to the server
						if(player.playerID == -1) {
							//send player to the server and read its playerVec size
							int newID = server.addServerPlayer(player);
							//set up PlayerID on client side
							serverThreadPlayerID = newID;
							setLocalPlayerID(newID);
							server.updateServerPlayer(player.playerID, player);
						}
						else {
							//update player on the server
							server.updateServerPlayer(player.playerID, player);
						}
					}
				}
								
			}
		}catch(IOException ioe) {
			System.out.println("serverthread: run() ioe: " + ioe.getMessage());
			
			//if the connection to this severthread is lost
			//reset corresponding player object in server's playerVec
			//send a "has left" message
			//remove this serverThread from server's serverThreads vector
			
			if(clientLoginState) {
				server.updateServerPlayer(serverThreadPlayerID, new Player("default"));
				server.broadcastMessage(new ChatMessage(serverThreadPlayerName, " has left.", true));
			}
			server.serverThreads.remove(this);
			
			//need to work on this
			
		}catch(ClassNotFoundException cnfe) {
			System.out.println("serverthread: run() cnfe: " + cnfe.getMessage());
		} catch (SQLException sqle) {
			System.out.println("sqle: " + sqle.getMessage());
		}
	}


/*   methods below are called by the server on every serverThread
 *   to send updates from back-end(server/serverThreads) to front-end(client)
 */
	
	//receive new message from the server and send to this serverthread's client
	public void sendMessage(ChatMessage cm) {
		try {
			oos.writeObject(cm);
			oos.flush();
		}catch(IOException ioe) {
			System.out.println("serverthread: sendMessage() ioe: " + ioe.getMessage());
		}
	}
	
	//get called after a client wants to add a new player to the server
	//set up PlayerID on client side(front-end). ID = index-1 in server's playerVec(back-end)
	public void setLocalPlayerID(int ID) {
		try {
			Integer IDInt = new Integer(ID); 
			oos.writeObject(IDInt);
			oos.flush();
		} catch (IOException ioe) {
			System.out.println("serverthread: setLocalPlayerID() ioe: " + ioe.getMessage());
		}
	}
	
	//send ReadyState from this serverthread to the client
	public void broadcastReadyState(Boolean readyState) {
		try {
			ReadyState rs = new ReadyState(readyState);
			oos.writeObject(rs);
			oos.flush();
			oos.reset();
		} catch (IOException ioe) {
			System.out.println("serverthread: broadcastReadyState() ioe: " + ioe.getMessage());
		}
	}
	
	//send otherPlayer from back-end to front-end
	public void updateOtherPlayer(Player otherPlayer) {
		try {
			oos.writeObject(otherPlayer);
			oos.flush();
			oos.reset();
		} catch (IOException ioe) {
			System.out.println("serverthread: updateOtherPlayer() ioe: " + ioe.getMessage());
		}
	}
	
	//return localPlayerID in this serverThread
	public int getServerThreadPlayerID() {
		return serverThreadPlayerID;
	}
}
