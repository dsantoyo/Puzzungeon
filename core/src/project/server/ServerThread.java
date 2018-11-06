package project.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;


//a serverThread object(back-end) is connected to a client(front-end)
public class ServerThread extends Thread{ 

	private ObjectInputStream ois;
	private ObjectOutputStream oos;
	private Username username;
	private Password password;
	private Player player;
	private Server server;
	
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
					

					/*
					   Back-end login/register features/validation should be done here
					   
					   
					   //JDBCType database = new JDBCType();
					    //String errorMessage = database.errorMessage();
					*/	
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
			System.out.println("ioe: " + ioe.getMessage());
			
			//if the connection to this severthread is lost
			//reset corresponding player object in server's playerVec
			//send a "has left" message
			//remove this serverThread from server's serverThreads vector
			server.updateServerPlayer(serverThreadPlayerID, new Player("default"));
			server.broadcastMessage(new ChatMessage(serverThreadPlayerName, " has left."));
			server.serverThreads.remove(this);
			
			//need to work on this
			
		}catch(ClassNotFoundException cnfe) {
			System.out.println("cnfe: " + cnfe.getMessage());
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
			System.out.println("ioe: " + ioe.getMessage());
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
			System.out.println("ioe: " + ioe.getMessage());
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
			System.out.println("ioe: " + ioe.getMessage());
		}
	}
	
	//send otherPlayer from back-end to front-end
	public void updateOtherPlayer(Player otherPlayer) {
			
		try {
			oos.writeObject(otherPlayer);
			oos.flush();
			oos.reset();
		} catch (IOException ioe) {
			System.out.println("ioe: " + ioe.getMessage());
		}
	}
	
	//return localPlayerID in this serverThread
	public int getServerThreadPlayerID() {
		return serverThreadPlayerID;
	}
}
