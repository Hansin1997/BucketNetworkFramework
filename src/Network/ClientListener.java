package Network;

import Network.BucketObject.Command.Client.ClientCommand;
import Network.Connection.Connection;

public abstract class ClientListener
{
	public abstract void onDataCome(Connection conn,ClientCommand message);
	public abstract void onDisconnection(Connection conn);
}
