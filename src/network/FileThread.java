package network;

import java.io.IOException;
import java.net.Socket;

import network.connection.FileConnection;
import network.listener.BucketListener;

public class FileThread extends Thread {

	private FileConnection connection;

	public FileThread(Socket socket, BucketListener listener) throws IOException {
		connection = new FileConnection(socket, listener);
		connection.setServer(true);
	}

	public FileConnection getConnection() {
		return connection;
	}

	@Override
	public void run() {
		try {
			connection.startListen();
		} catch (IOException e) {

			e.printStackTrace();
		}

	}

}
