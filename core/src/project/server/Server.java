package project.server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Vector;

import project.server.ChatMessage;

public class Server {
	
	private Vector<ServerThread> serverThreads;
	private Vector<ChatMessage> messageVec;

	
	public Server(int port) {
		
		messageVec = new Vector<ChatMessage>(3);
		messageVec.add(new ChatMessage("", ""));
		messageVec.add(new ChatMessage("", ""));
		messageVec.add(new ChatMessage("", ""));
		
		InetAddress inetAddress;
		try {
			inetAddress = InetAddress.getLocalHost();
			System.out.println("Server IP Address: " + inetAddress.getHostAddress());
		} catch (UnknownHostException e) {
			
			e.printStackTrace();
		}
		
		ServerSocket ss = null;
		
		try {
			System.out.println("trying to bind to port " + port);
			
			ss = new ServerSocket(port);
			
			System.out.println("bound to port " + port);
			
			serverThreads = new Vector<ServerThread>();
			
			while(true) { // constantly waiting for any new connection
				Socket s = ss.accept();
				System.out.println("Connection from " + s.getInetAddress());
				
				ServerThread st = new ServerThread(s, this);
				
				// start thread in its constructor
				serverThreads.add(st); // we have a serverThread for every client
			}
		}catch (IOException ioe) {
			System.out.println("ioe:"+ioe.getMessage());
		}finally {
			try {
				if(ss != null) {
					ss.close();
				}
			}catch (IOException ioe) {
				System.out.println("ioe:"+ioe.getMessage());
			}
			
		}
	}
	

	//get new message from serverthread and send to every client
	public void broadcast(ChatMessage cm) {
		
		if(cm != null) {
			
			//remove the oldest message
			messageVec.remove(0);
			
			// add the newest message
			messageVec.add(cm);
			
			
			//sd the newest message to every client
			for(ServerThread thread : serverThreads) {
					thread.sendMessage(messageVec.get(2));
			}
		}
		
	}

	public static void main(String [] args) {
		
		//hard-coded. need to be changed
		new Server(6789);
	}
}
