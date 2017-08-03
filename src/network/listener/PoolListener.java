package network.listener;

import network.connection.Connection;

public abstract class PoolListener {
	
	public abstract void push(String username,Connection conn);

}
