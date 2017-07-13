package Network;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import Network.Connection.Connection;
import Network.Connection.UserConnection;

public class SocketPool {
	
	private int maxCount;
	private List<ClientThread> client;
	
	public List<ClientThread> getClient() {
		return client;
	}
	
	public void setClient(List<ClientThread> client) {
		this.client = client;
	}
	
	
	
	public SocketPool()
	{
		this(500);
	}
	
	public SocketPool(int maxCount)
	{
		client = Collections.synchronizedList(new ArrayList<ClientThread>());
		this.maxCount = maxCount;
	}
	
	public void setMaxCount(int maxCount) {
		this.maxCount = maxCount;
	}
	
	public int getMaxCount() {
		return maxCount;
	}
	
	
	public boolean add(Socket socket,BucketListener listener) throws IOException
	{
		
		if(client.size() < maxCount)
		{
			ClientThread t = new ClientThread(socket, listener);
			client.add(t);
			t.start();
			return true;
		}else{
			
			socket.close();
			return false;
		}
	}
	
	public void remove(ClientThread t)
	{
		
		client.remove(t);
	}
	
	public void remove(Connection conn)
	{
		ClientThread find = getClientFromConnection(conn);
		if(find != null)
			remove(find);
	}
	
	public ClientThread getClientFromConnection(Connection conn)
	{
		for(int i = 0;i < client.size();i++)
		{
			if(client.get(i).getConnection().equals(conn))
				return client.get(i);
		}
		return null;
	}
	
	public UserConnection getUserConnection(String Client)
	{
		if(Client == null)
			return null;
		for(int i = 0;i < client.size();i++)
		{
			if(client.get(i).getConnection().getUsername().equals(Client))
				return client.get(i).getConnection();
		}
		return null;
	}
	
	public int getOnlineCount()
	{
		return client.size();
	}
	
	public void broadcast(String str)
	{
		for(int i = 0;i < client.size();i++)
		{
			try {
				
				client.get(i).getConnection().send(str);
				
			} catch (IOException e) {
				
			}
		}
	}

}
