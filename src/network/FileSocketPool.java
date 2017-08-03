
package network;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import Database.DatabaseManager;
import network.connection.Connection;
import network.connection.FileConnection;
import network.listener.BucketListener;

public class FileSocketPool {

	private int maxCount;
	private List<FileThread> client;
	private DatabaseManager db;
	//private List<ClientThread> waitedClient;

	public List<FileThread> getClient() {
		return client;
	}

	public void setClient(List<FileThread> client) {
		this.client = client;
	}

	public FileSocketPool(DatabaseManager db) {
		this(500,db);
	}

	public FileSocketPool(int maxCount,DatabaseManager db) {
		client = Collections.synchronizedList(new ArrayList<FileThread>());
		//waitedClient = Collections.synchronizedList(new ArrayList<ClientThread>());
		this.db = db;
		this.maxCount = maxCount;
	}

	public void setMaxCount(int maxCount) {
		this.maxCount = maxCount;
	}

	public int getMaxCount() {
		return maxCount;
	}

	public boolean add(Socket socket, BucketListener listener) throws IOException {

		if (client.size() < maxCount) {
			FileThread t = new FileThread(socket,db, listener);
			client.add(t);
			t.start();
			return true;
		} else {

			socket.close();
			return false;
		}
	}

	public void remove(FileThread t) {

		t.getConnection().finish();
		client.remove(t);
	}

	public void remove(Connection conn){
		FileThread find = getClientFromConnection(conn);
		if (find != null){
			remove(find);
		}
	}

	public FileThread getClientFromConnection(Connection conn) {
		for (int i = 0; i < client.size(); i++) {
			if (client.get(i).getConnection().equals(conn))
				return client.get(i);
		}
		return null;
	}

	public FileConnection getUserConnection(String Username) {
		if (Username == null)
			return null;
		for (int i = 0; i < client.size(); i++) {
			if(client.get(i).getConnection().getUsername() == null)
			{
				continue;
			}
			if (client.get(i).getConnection().getUsername().equals(Username))
				return client.get(i).getConnection();
		}
		return null;
	}

	public int getOnlineCount() {
		return client.size();
	}


}