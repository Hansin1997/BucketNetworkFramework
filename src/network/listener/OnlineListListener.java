package network.listener;

import java.util.List;

import Common.Tool;
import network.bucketmodle.USER;
import network.bucketobject.QueryResult;
import network.command.client.ClientCommand;
import network.connection.Connection;

public abstract class OnlineListListener extends ClientListener {

	public abstract void onResultsCome(Connection conn, int Count, List<USER> users);

	@Override
	public void onDataCome(Connection conn, ClientCommand message) {
		ClientCommand cm = message;
		QueryResult result = Tool.object2E(cm.getValues(), QueryResult.class);
		List<USER> a = null;
		if(result.getResults() != null)
			a = Tool.ObjectList(result.getResults(), USER.class);
		this.onResultsCome(conn, result.getCount(), a);

	}

	@Override
	public void onDisconnection(Connection conn) {

	}
}
