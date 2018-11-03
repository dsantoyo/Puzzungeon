package project.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ServerThread extends Thread{ 

	private ObjectInputStream ois;
	private ObjectOutputStream oos;
	
	private Server server;
	
	public ServerThread(Socket socket, Server server) {
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
		
		//read new message from the client and send to the server
		try {
			while(true) {
				
				ChatMessage cm = (ChatMessage)ois.readObject();
				if( cm != null) {
					
					//send new message to the server
					//server.broadcast(cm);
				}
			}
		}catch(IOException ioe) {
			System.out.println("ioe: " + ioe.getMessage());
		}catch(ClassNotFoundException cnfe) {
			System.out.println("cnfe: " + cnfe.getMessage());
		}
	}
	
	//receive new message from the server and send to every client
	public void sendMessage(ChatMessage cm) {
		
		try {
			oos.writeObject(cm);
			oos.flush();
		}catch(IOException ioe) {
			System.out.println("ioe: " + ioe.getMessage());
		}
	}
}
