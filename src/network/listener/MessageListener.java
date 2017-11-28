package network.listener;

import Common.Tool;
import network.bucketmodle.Message;
import network.command.client.ClientCommand;
import network.connection.Connection;

public abstract class MessageListener extends ClientListener {

	public abstract void onMessageCome(Connection conn, Message msg);

	@Override
	public void onDataCome(Connection conn, ClientCommand message) {
		ClientCommand cm = message;
		Message msg = Tool.object2E(cm.getValues(), Message.class);
		this.onMessageCome(conn, msg);

	}

	@Override
	public void onDisconnection(Connection conn) {

	}

}
