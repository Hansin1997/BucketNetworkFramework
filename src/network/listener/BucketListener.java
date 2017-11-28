package network.listener;

import network.connection.Connection;

public abstract class BucketListener {
	public abstract void onDataCome(Connection conn, String message);

	public abstract void onDisconnection(Connection conn);
	
	public void onException(Exception e)
	{
		
	}
}
