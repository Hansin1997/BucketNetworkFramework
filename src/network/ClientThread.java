package network;

import java.io.IOException;
import java.net.Socket;

import Database.DatabaseManager;
import network.connection.UserConnection;
import network.listener.BucketListener;
import network.listener.PoolListener;

public class ClientThread extends Thread {

	private UserConnection connection;

	public ClientThread(Socket socket,DatabaseManager db,PoolListener poolListener,BucketListener listener) throws IOException {
		connection = new UserConnection(socket,db,poolListener, listener);
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
			e.printStackTrace();
		}finally {
			
			if(connection != null && connection.getListener() != null)
				connection.getListener().onDisconnection(connection);
		
		}

	}

}
