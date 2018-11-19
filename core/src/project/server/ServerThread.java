package project.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.sql.SQLException;
import java.util.HashSet;

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
	private LobbyChoice lobbyChoice;
	private GameRoomCode gameRoomCode;
	
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
		
		gameRoomCode = new GameRoomCode("");
		player = new Player("");
		
		try {
			ois = new ObjectInputStream(socket.getInputStream());
			oos = new ObjectOutputStream(socket.getOutputStream());
			
			try {
				oos.writeObject("test");
				oos.flush();
				oos.reset();
			} catch (IOException ioe) {
				System.out.println("serverthread: ServerThread() ioe: " + ioe.getMessage());
			}
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
				System.out.println();
				
				//sending object from client(front-end) to this serverthread(back-end)
				Object object = ois.readObject();
				
				//if a Chatmessage object is sent to this serverthread
				if(object instanceof ChatMessage) {
					ChatMessage cm = (ChatMessage)object;
					if( cm != null) {
						//send new message to the server
						server.broadcastMessage(cm, gameRoomCode.code);
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
					
					System.out.println("serverthread: username = "+ usernameStr);
					System.out.println("serverthread: password = "+ passswordStr);
					System.out.println("serverthread: login/register = "+ loginRegisterStr);
					
					//database validation
					JDBCType database = new JDBCType(usernameStr, passswordStr, loginRegisterStr);;

					Boolean databaseConnectionResult = database.connectionSet();
					
					System.out.println("serverthread: database connection result = " + databaseConnectionResult);
					if(!databaseConnectionResult) {
						
						try {
							System.out.println("serverthread: connection to database failed.");
							oos.writeObject(new LoginResult(false, "Connection to the database failed",0));
							oos.flush();
							oos.reset();
							continue;
						} catch (IOException ioe) {
							System.out.println("serverthread: check db ioe: " + ioe.getMessage());
						}
					}

					System.out.println("serverthread: trying to login...");
					System.out.println("serverthread: current player0 name on server: " + server.playerVec.get(0).playerName);
					System.out.println("serverthread: current player0 id on server: " + server.playerVec.get(0).playerID);
					System.out.println("serverthread: current player1 name on server: " + server.playerVec.get(1).playerName);
					System.out.println("serverthread: current player1 id on server: " + server.playerVec.get(1).playerID);
					
					//send server validation result from this serverthread back to its client
					
					// from loginScreen
					if (loginRegisterStr.equals("login") && !(database.PlayerValidation(usernameStr, passswordStr))) { // this condition has to be changed
						try {
							System.out.println("serverthread: denied. Check username/password");
							oos.writeObject(new LoginResult(false, "Check username/password",0));
							oos.flush();
							oos.reset();
						} catch (IOException ioe) {
							System.out.println("serverthread: check db ioe: " + ioe.getMessage());
						} 
					}
					// from registerScreen
					else if (loginRegisterStr.equals("register") && (database.exists(usernameStr))) { // this condition has to be changed
						try {
							System.out.println("serverthread: denied. Failed to register.");
							oos.writeObject(new LoginResult(false, "Failed to register.",0));
							oos.flush();
							oos.reset();
						} catch (IOException ioe) {
							System.out.println("serverthread: check db ioe: " + ioe.getMessage());
						}	
					}
					
					else { // allow the client to login
						
						//read past score from the database
						int pastScore = database.getHighScore();
						System.out.println("serverthread: past score = " + pastScore);	
						try {
							System.out.println("serverthread: logged in.");
							clientLoginState = true;
							oos.writeObject(new LoginResult(true, "Login/Register done", pastScore));
							oos.flush();
							oos.reset();
							
							//send past score
							
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
						
						if(player.disconnect) {
							throw new IOException();
						}
						
						serverThreadPlayerName = player.playerName;
						
						//if a new player is being added to the server
						if(player.playerID == -1) {
							//send player to the server and read its playerVec size
							System.out.println("serverthread: adding player for game room " + gameRoomCode.code);
							int newID = server.addServerPlayer(player, gameRoomCode.code);
							//set up PlayerID on client side
							serverThreadPlayerID = newID;
							setLocalPlayerID(newID);
							server.updateServerPlayer(player.playerID, player, gameRoomCode.code);
						}
						else {
							//update player on the server
							server.updateServerPlayer(player.playerID, player, gameRoomCode.code);
						}
					}
				}
				
				if(object instanceof PieceID) {
					PieceID pid = (PieceID)object;
					if(serverThreadPlayerID == 0) {
						
						server.gameRoomMap.get(gameRoomCode.code).reLock.lock();
						
						try {
						
							server.gameRoomMap.get(gameRoomCode.code).serverThreads.get(1).sendPiece(pid);
						}finally {
							server.gameRoomMap.get(gameRoomCode.code).reLock.unlock();
						}
					}
					if(serverThreadPlayerID == 1) {
						server.gameRoomMap.get(gameRoomCode.code).reLock.lock();
						
						try {
							server.gameRoomMap.get(gameRoomCode.code).serverThreads.get(0).sendPiece(pid);
						}
						finally {
							server.gameRoomMap.get(gameRoomCode.code).reLock.unlock();
						}
					}
				}
				
				if(object instanceof LobbyChoice) {
					lobbyChoice = (LobbyChoice)object;
					if(lobbyChoice != null) {
						if(lobbyChoice.choice.equals("new game")) {
							System.out.println("serverthread: player asking from a new romm");
							gameRoomCode = new GameRoomCode(server.generateGameRoomCode());
							sendGameRoomCode(gameRoomCode);
							
							GameRoom gameroom = new GameRoom(gameRoomCode.code);
							gameroom.serverThreads.add(this);
							
							server.gameRoomMap.put(gameRoomCode.code, gameroom);
	
						}
						if(lobbyChoice.choice.equals("random game")) {
							System.out.println("serverthread: player asking from a random romm");
							
							//find an available game room
							Boolean foundAvailable = false;
							for (String code : server.gameRoomMap.keySet()){
						        //iterate over keys
						        System.out.println("serverthread: checking if room " + code + " is available");
						        
						        //if find an available game room
						        if(!server.isGameFull(code) && (!server.gameRoomMap.get(code).lock)){
						        	//assign this serverthread to the gameroom
						        							        	
						        	server.gameRoomMap.get(code).reLock.lock();
									
									try {
										server.gameRoomMap.get(code).serverThreads.add(this);
										
									}finally {
										server.gameRoomMap.get(code).reLock.unlock();
									}

						        	gameRoomCode.code = code;
						        	//return game code to the client
						        	foundAvailable = true;
						        	System.out.println("serverthread: found room "+code+" empty. sending code back to client.");
						        	sendGameRoomCode(gameRoomCode);
						        	break;
						        }
						    }
							if(!foundAvailable) {
								sendGameRoomCode(new GameRoomCode("no empty room"));
							}
						}
						if(lobbyChoice.choice.equals("use code")) {
							System.out.println("serverthread: player asking to join room " + lobbyChoice.code);
							String code = lobbyChoice.code;
							Boolean roomAvailable = false;
							
							if(server.gameRoomMap.containsKey(code)) {
								if(!server.isGameFull(code)){
						        	//assign this serverthread to the gameroom
									
									server.gameRoomMap.get(code).reLock.lock();
									
									try {
										server.gameRoomMap.get(code).serverThreads.add(this);
										
									}finally {
										server.gameRoomMap.get(code).reLock.unlock();
									}

						        	gameRoomCode.code = code;
						        	//return game code to the client
						        	roomAvailable = true;
						        	System.out.println("serverthread: found room "+code+" available. sending code back to client.");
						        	sendGameRoomCode(gameRoomCode);
								}
						     
							}
							
							if(!roomAvailable) {
								sendGameRoomCode(new GameRoomCode("room not available"));
							}
							
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
			
			if(clientLoginState != null) {
				if(clientLoginState) {
					server.updateServerPlayer(serverThreadPlayerID, new Player("default"), gameRoomCode.code);
					server.broadcastMessage(new ChatMessage(serverThreadPlayerName, " has left.", true), gameRoomCode.code);
				}
			}
			
			if(serverThreadPlayerID == 0) {
				
				server.gameRoomMap.get(gameRoomCode.code).reLock.lock();
				
				try {
				
					if (server.gameRoomMap.get(gameRoomCode.code).playerVec.get(1).playerID == -1) {
						server.gameRoomMap.remove(gameRoomCode.code);
					}
					
				}finally {
					server.gameRoomMap.get(gameRoomCode.code).reLock.unlock();
				}
			}
			
			else if(serverThreadPlayerID == 1) {
				
				server.gameRoomMap.get(gameRoomCode.code).reLock.lock();
				
				try {
					
					if (server.gameRoomMap.get(gameRoomCode.code).playerVec.get(0).playerID == -1) {
						server.gameRoomMap.remove(gameRoomCode.code);
					}
				
				}finally {
					server.gameRoomMap.get(gameRoomCode.code).reLock.unlock();
				}
			}
			
			server.serverThreads.remove(this);
			
			//need to work on this
			
		}catch(ClassNotFoundException cnfe) {
			System.out.println("serverthread: run() cnfe: " + cnfe.getMessage());
		} catch (SQLException sqle) {
			System.out.println("sserverthread: run() sqle: " + sqle.getMessage());
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
			HashSet<Integer> playerPieceSet;
			
			server.gameRoomMap.get(gameRoomCode.code).reLock.lock();
			
			try {

				if(ID == 0) {
					playerPieceSet = server.gameRoomMap.get(gameRoomCode.code).player0PieceSet;
				}
				else {
					playerPieceSet = server.gameRoomMap.get(gameRoomCode.code).player1PieceSet;
				}
			
			}finally {
				server.gameRoomMap.get(gameRoomCode.code).reLock.unlock();
			}
			
			PlayerIDnPieceSet pips = new PlayerIDnPieceSet(ID, playerPieceSet);
			oos.writeObject(pips);
			oos.flush();
		} catch (IOException ioe) {
			System.out.println("serverthread: setLocalPlayerID() ioe: " + ioe.getMessage());
		}
	}
	
	//send otherPlayer from back-end to front-end
	public void updateOtherPlayer(Player otherPlayer) {
		
		server.gameRoomMap.get(gameRoomCode.code).reLock.lock();
		
		try {
			
			if(server.gameRoomMap.get(gameRoomCode.code).playerVec.get(1).readyState && server.gameRoomMap.get(gameRoomCode.code).playerVec.get(0).readyState) {
				server.gameRoomMap.get(gameRoomCode.code).lock = true;
			}
		
			
		}finally {
			server.gameRoomMap.get(gameRoomCode.code).reLock.unlock();
		}

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
	
	public void sendGameRoomCode(GameRoomCode code) {
		try {
			oos.writeObject(code);
			oos.flush();
			oos.reset();
		} catch (IOException ioe) {
			System.out.println("serverthread: sendGameRoomCode() ioe: " + ioe.getMessage());
		}
	}
	
	public void sendPiece(PieceID pieceID) {
		try {
			oos.writeObject(pieceID);
			oos.flush();
			oos.reset();
		} catch (IOException ioe) {
			System.out.println("serverthread: sendPiece() ioe: " + ioe.getMessage());
		}
	}
	
}
