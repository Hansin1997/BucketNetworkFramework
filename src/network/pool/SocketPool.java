package network.pool;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import Database.DatabaseManager;
import network.connection.Connection;
import network.connection.UserConnection;
import network.listener.BucketListener;
import network.listener.PoolListener;
import network.pool.thread.ClientThread;

public class SocketPool extends Pool{

	private int maxCount;
	private List<ClientThread> client;
	private DatabaseManager db;
	//private List<ClientThread> waitedClient;

	@SuppressWarnings("unchecked")
	public List<ClientThread> getClient() {
		return client;
	}

	public void setClient(List<ClientThread> client) {
		this.client = client;
	}

	public SocketPool(DatabaseManager db) {
		this(500,db);
	}

	public SocketPool(int maxCount,DatabaseManager db) {
		client = Collections.synchronizedList(new ArrayList<ClientThread>());
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
			ClientThread t = new ClientThread(socket,db,new PoolListener() {
				
				@Override
				public void push(String username, Connection conn) {
					UserConnection lastConn;
					while((lastConn = getConnection(username)) != null && conn != lastConn)
						remove(lastConn);
					
				}
			}, listener);
			
			client.add(t);
			t.start();
			return true;
		} else {

			socket.close();
			return false;
		}
	}

	public boolean remove(ClientThread t) {

		t.getConnection().finish();
		return client.remove(t);
	}

	public boolean remove(Connection conn) {
		ClientThread find = getClientFromConnection(conn);
		if (find != null)
			return remove(find);
		else
			return false;
	}

	public ClientThread getClientFromConnection(Connection conn) {
		for (int i = 0; i < client.size(); i++) {
			if (client.get(i).getConnection().equals(conn))
				return client.get(i);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public UserConnection getConnection(String Username) {
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
	
	public List<UserConnection> getUserConnections() {
		ArrayList<UserConnection> result = new ArrayList<UserConnection>();
		for (int i = 0; i < client.size(); i++) {
			result.add(client.get(i).getConnection());
		}
		return result;
	}

	public int getOnlineCount() {
		return client.size();
	}

	public void broadcast(String str) {
		for(ClientThread c : client) {
			try {

				c.getConnection().send(str);

			} catch (IOException e) {
				remove(c);
			}
		}
	}

}

