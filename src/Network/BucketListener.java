package Network;

import Network.Connection.Connection;

public abstract class BucketListener
{
	public abstract void onDataCome(Connection conn,String message);
	public abstract void onDisconnection(Connection conn);
}
