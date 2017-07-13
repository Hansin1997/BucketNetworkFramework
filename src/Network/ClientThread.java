package Network;

import java.io.IOException;
import java.net.Socket;

import Network.Connection.UserConnection;

public class ClientThread extends Thread{
	
	private UserConnection connection;
	
	public ClientThread(Socket socket,BucketListener listener) throws IOException
	{
		connection = new UserConnection(socket, listener);
		connection.setServer(true);
	}
	
	public UserConnection getConnection() {
		return connection;
	}
	
	
	@Override
	public void run() {
		try {
			connection.startListen();
		} catch (IOException e) {
			
		}
		
		
	}
	

}
