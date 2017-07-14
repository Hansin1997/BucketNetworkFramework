package network;

import java.io.IOException;
import java.net.Socket;

import Common.Gobal;
import network.connection.UserConnection;
import network.listener.BucketListener;

public class ClientThread extends Thread {

	private UserConnection connection;

	public ClientThread(Socket socket, BucketListener listener) throws IOException {
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
			try {
				Gobal.getPool().remove(connection);
			} catch (IOException e1) {
			}
		}

	}

}
