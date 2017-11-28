
package network.pool;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import Database.DatabaseManager;
import network.connection.Connection;
import network.connection.FileConnection;
import network.listener.BucketListener;
import network.pool.thread.FileThread;

public class FileSocketPool extends Pool{

	private int maxCount;
	private List<FileThread> client;
	private DatabaseManager db;
	//private List<ClientThread> waitedClient;

	@SuppressWarnings("unchecked")
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
		synchronized (client) {
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

	}
	
	
	public boolean remove(FileThread t) {
		synchronized (client) {
			t.getConnection().finish();
			return client.remove(t);
		}
	}

	
	public boolean remove(Connection conn){
		FileThread find = getClientFromConnection(conn);
		if (find != null){
			return remove(find);
			
		}else
			return false;
	}

	public FileThread getClientFromConnection(Connection conn) {
		synchronized (client) {
			for (int i = 0; i < client.size(); i++) {
				if (client.get(i).getConnection().equals(conn))
					return client.get(i);
			}
			return null;
		}
	}

	public FileConnection getUserConnection(String Username) {
		if (Username == null)
			return null;
		synchronized (client) {
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
	}

	public int getOnlineCount() {
		synchronized (client) {
			return client.size();
		}
		
	}

	@Override
	public void broadcast(String str) {
		// TODO Auto-generated method stub
		
	}


	@SuppressWarnings("unchecked")
	@Override
	public FileConnection getConnection(String str) {
		// TODO Auto-generated method stub
		return null;
	}






}