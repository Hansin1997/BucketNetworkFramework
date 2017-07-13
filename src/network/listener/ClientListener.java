package network.listener;

import network.command.client.ClientCommand;
import network.connection.Connection;

public abstract class ClientListener {
	public abstract void onDataCome(Connection conn, ClientCommand message);

	public abstract void onDisconnection(Connection conn);
}
