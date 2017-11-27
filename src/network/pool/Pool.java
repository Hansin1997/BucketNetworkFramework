package network.pool;

import java.util.List;

import network.connection.Connection;

public abstract class Pool {
	
	abstract public int getOnlineCount();

	abstract public void broadcast(String str);
	
	abstract public <T extends Thread> List<T> getClient();
	
	abstract public <T extends Connection> T getConnection(String str);
	
	abstract public boolean remove(Connection conn);
}
