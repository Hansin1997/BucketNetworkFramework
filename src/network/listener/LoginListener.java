package network.listener;

import network.command.client.ClientCommand;
import network.connection.Connection;

public abstract class LoginListener extends ClientListener {

	public abstract void onDone(Connection conn, boolean success);

	@Override
	public void onDataCome(Connection conn, ClientCommand message) {
		this.onDone(conn, message.values.toString().equals("SUCCESS"));

	}

	@Override
	public void onDisconnection(Connection conn) {
		// TODO Auto-generated method stub

	}

}