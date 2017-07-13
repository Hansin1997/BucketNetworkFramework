package network.command.server;

import java.io.IOException;
import java.util.ArrayList;

import com.google.gson.JsonObject;

import Common.Gobal;
import Common.Tool;
import network.ClientThread;
import network.bucketobject.Query;
import network.bucketobject.QueryResult;
import network.bucketobject.USER;
import network.command.BucketCommand;
import network.connection.UserConnection;

public class GetOnlineListCommand extends BucketCommand {

	@Override
	public void execute() {
		UserConnection conn = client;
		if (conn == null)
			return;

		BucketCommand c = toClientCommand();
		try {
			conn.send(c);
		} catch (IOException e) {

		}

	}

	public BucketCommand toServerCommand() {
		MainCommand mc = new MainCommand();
		mc.setSign(sign);
		mc.setCommand(getClass().getName());
		mc.setValues(this);
		return mc;
	}

	public BucketCommand toClientCommand() {
		return getOnlineList().toClientCommand(sign);
	}

	public static QueryResult getOnlineList() {
		QueryResult result = new QueryResult();
		ArrayList<JsonObject> array = new ArrayList<JsonObject>();

		for (ClientThread c : Gobal.getPool().getClient()) {
			String username = c.getConnection().getUsername();
			if (username != null) {
				Query q = new Query();
				q.setTable_name(USER.class.getSimpleName());
				q.setCount(-1);

				q.addQuery("username", "=\'" + username + "\'");
				QueryResult r = Gobal.db.Query(q);
				if (r.count < 1)
					continue;

				USER u = Tool.object2E(r.getResults().get(0), USER.class);
				u.setPassword("");
				array.add(Tool.object2E(u, JsonObject.class));

			}
		}

		result.setCount(array.size());
		result.setResults(array);

		return result;
	}

}
