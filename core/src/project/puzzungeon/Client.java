package project.puzzungeon;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Vector;

import project.server.ChatMessage;
import project.server.GameRoomCode;
import project.server.LobbyChoice;
import project.server.LoginRegister;
import project.server.LoginResult;
import project.server.Password;
import project.server.PieceID;
import project.server.Player;
import project.server.PlayerIDnPieceSet;
import project.server.ReadyState;
import project.server.Username;

public class Client {
	
	public Socket s;
	public ObjectInputStream ois;
	public ObjectOutputStream oos;
	public int port;
	public String hostname;
	public Vector<ChatMessage> messageVec;
	public String clientUsername;
	
	//the ready state of player1 AND player2. Updated by the server
	public Boolean bothPlayerReady;
	
	public Boolean loginState;
	public String loginStateMessage;
	public Boolean connectState;
	public Boolean disconnect;
	
	public String gameRoomCode;
	
	//client's own player
	public Player localPlayer;
	
	//the other player. constantly updated by the server
	public Player otherPlayer; 
	
	//player login username/password
	public String username;
	public String password;
	public int incomingPieceID;
	
	//constructor
	public Client(String hostname, int port) {
		this.hostname = hostname;
		this.port = port;
		this.clientUsername = "default";
		this.bothPlayerReady = false;
		this.loginState = false;
		this.loginStateMessage = "";
		this.connectState = false;
		this.disconnect = false;
		this.gameRoomCode = "";
		this.incomingPieceID = -1;
	}
	
	//setting up connection between a client and the server
	public Boolean connect() {
		
		try {
			System.out.println("client: Trying to connect to " + hostname + ":" + port);
			s = new Socket(hostname,port);
			
			oos = new ObjectOutputStream(s.getOutputStream());
			ois = new ObjectInputStream(s.getInputStream());
			
			//test connection to server
			System.out.println("client: Testing if the connection is established");
			@SuppressWarnings("unused")
			Object TestObject = ois.readObject();
			
			connectState = true;
			System.out.println("client: Connected to " + hostname + ":" + port);
			
			//only store the last 3 messages on the client side
			messageVec = new Vector<ChatMessage>(4);
			messageVec.add(new ChatMessage("", "", false));
			messageVec.add(new ChatMessage("", "", false));
			messageVec.add(new ChatMessage("", "", false));
			messageVec.add(new ChatMessage("", "", false));
			
			//add localPlayer to the server
			//localPlayer = new Player(clientUsername);
			//otherPlayer = new Player("default");
			//updatePlayer();
				
		} catch (IOException ioe) {
			System.out.println("client: connection() ioe: " + ioe.getMessage());
			return false;
		} catch (ClassNotFoundException cnfe) {
			System.out.println("client: connection() cnfe: " + cnfe.getMessage());
		}
		
		//use a thread to receive objects from the assigned serverThread
				new Thread(new Runnable(){
					
		            @Override
		            public void run() {
		            	
		            	try {
		            		while(true){
		            			Object object = ois.readObject();
		            			
		            			//if the serverThread sends a ChatMessage to the client
		            			if(object instanceof ChatMessage) {
		            				ChatMessage newMessage = (ChatMessage)object;
		            				messageVec.remove(0);
		            				messageVec.add(newMessage);
		            			}
		            			//if the serverThread sends the size of server PlayerVec to the client
		            			if(object instanceof PlayerIDnPieceSet) {
		            				PlayerIDnPieceSet pips  = (PlayerIDnPieceSet)object;
		            				localPlayer.playerID = pips.id;
		            				localPlayer.playerPieceSet = pips.playerPieceSet;
		            				System.out.println("client: server assigned playerid = " + pips.id);
		            				System.out.print("client: server assigned pieceset = ");
		            				System.out.print("client: pieces in the set: " + pips.playerPieceSet);
		            			}
		            					            					            			
		            			if(loginState) {
		            				//if the serverThread sends back otherPlayer
		            				if(object instanceof Player) {
		            					otherPlayer = (Player)object;
		            					System.out.println();
		            					System.out.println("client: players updated by Server.");
		            					System.out.println("client: localPlayer is "+localPlayer.playerName);
		            					System.out.println("client: localPlayer ready state is " + localPlayer.readyState);
		            					System.out.println("client: otherPlayer is "+otherPlayer.playerName);
		            					System.out.println("client: otherPlayer ready state is " + otherPlayer.readyState);
		            					bothPlayerReady = localPlayer.readyState && otherPlayer.readyState;
		            					
		            					if(disconnect) {
		            						throw new IOException();
		            					}
		            				}
		            			}
		            			
		            			if(object instanceof PieceID) {
		            				PieceID pid = (PieceID)object;
		            				incomingPieceID = pid.id;
		            				System.out.println("client: Incoming piece ID = " + pid.id);
		            			}
		            			
		            			//if the serverThread sends back login result
		            			if(object instanceof LoginResult) {
		            				
		            				LoginResult rs = (LoginResult)object;
		            				loginState = rs.getLoginResult();
		            				loginStateMessage = rs.getMessage();
		            				System.out.println("client: update loginState = "+loginState);
		            				if(!loginState) {
		            					System.out.println("client: failed to log in");
		            					System.out.println(loginStateMessage);
		            				}
		            				else {
		            					localPlayer = new Player(clientUsername);
		            					System.out.println("client: localPlayer highest score = " + Integer.toString(rs.pastScore));
		            					localPlayer.pastScore = rs.pastScore;
		            					otherPlayer = new Player("default");
		            					System.out.println("client: logged in ");
		            				}
		            			}
		            			
		            			if(object instanceof GameRoomCode) {
		            				GameRoomCode grc = (GameRoomCode)object;
		            				gameRoomCode = grc.code;
		            				if(!grc.code.equals("no empty room") && !grc.code.equals("room not available") && !grc.code.equals("")) {
		            					updatePlayer();
		            					System.out.println("client: got gameroomcode from server = " + gameRoomCode);
		            				}
		            				else if(grc.code.equals("no empty room")){
		            					System.out.println("client: 0 gameroom available");
		            				}
		            				else if(grc.code.equals("room not available")) {
		            					System.out.println("client: gameroom not available");
		            				}
		            			}
		            		}
		            	}catch(IOException ioe) {
		            		System.out.println("client: Thread run() ioe: " + ioe.getMessage());
		            		ioe.printStackTrace();
		            		System.out.println("client: Thread run() LOST CONNECTION.");
		            		connectState = false;
		            		try {
								oos.close();
								ois.close();
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
		            		
		            	}catch (ClassNotFoundException cnfe) {
		            		System.out.println("client: Thread run() cnfe: " + cnfe.getMessage());
		            	}
		            }
		        }).start(); //start the thread;

		return true;
	}
	
	//send message from a client(front-end) to a serverthread(back-end)
	public void sendMessage(ChatMessage cm) {
        try {
			oos.writeObject(cm);
			oos.flush();
		} catch (IOException ioe) {
			System.out.println("client: sendMessage() ioe: " + ioe.getMessage());
		}
	}
	
	//send username from a client(front-end) to a serverthread(back-end)
	public void sendUsername(Username username) {
        try {
			oos.writeObject(username);
			oos.flush();
		} catch (IOException ioe) {
			System.out.println("client: sendUsername() ioe: " + ioe.getMessage());
		}
	}
	
	//send password from a client(front-end) to a serverthread(back-end)
	public void sendPassword(Password password) {
        try {
			oos.writeObject(password);
			oos.flush();
		} catch (IOException ioe) {
			System.out.println("client: sendPassword() ioe: " + ioe.getMessage());
		}
	}
	
	
	//send loginRegister from a client(front-end) to a serverthread(back-end)
		public void sendLoginRegister(LoginRegister loginRegister) {
	        try {
				oos.writeObject(loginRegister);
				oos.flush();
			} catch (IOException ioe) {
				System.out.println("client: sendLoginRegister ioe: " + ioe.getMessage());
			}
		}
	
	//send/update a player from a client(front-end) to a serverthread(back-end)
	public void updatePlayer() {
		try {
			oos.writeObject(localPlayer);
			oos.flush();
			//discard any cached references.
			oos.reset();
		} catch (IOException ioe) {
			System.out.println("client: updatePlayer() ioe: " + ioe.getMessage());
		}
	}
	
	//send lobby choice from a client to a serverThread
	public void sendLobbyChoice(LobbyChoice lobbyChoice) {
		try {
			System.out.println("client: sendLobbyChoice: " + lobbyChoice.choice);
			oos.writeObject(lobbyChoice);
			oos.flush();
			oos.reset();
		} catch (IOException ioe) {
			System.out.println("client: sendLobbyChoice() ioe: " + ioe.getMessage());
		}
	}
	
	public void sendPiece(int pieceID) {
		try {
			System.out.println("client: sendPiece: id = " + pieceID );
			oos.writeObject(new PieceID(pieceID));
			oos.flush();
			oos.reset();
		} catch (IOException ioe) {
			System.out.println("client: sendPiece() ioe: " + ioe.getMessage());
		}
	}
}
