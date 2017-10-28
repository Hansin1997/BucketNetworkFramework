package network.command.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import Common.Tool;
import network.bucketobject.Message;
import network.bucketobject.Query;
import network.bucketobject.QueryResult;
import network.bucketobject.USER;
import network.command.BucketCommand;
import network.connection.UserConnection;

public class MessageCommand extends BucketCommand {

	public Message message;

	public void setMessage(Message message) {
		this.message = message;
	}

	public Message getMessage() {
		return message;
	}

	@Override
	public void execute() {

		message.setSendTime(new Date());
		
		try {
			UserConnection client = (UserConnection )super.client;
			
			if (client.username != null && !client.username.equals("")) {
				message.setSender(client.username);
			}
			
		}catch(ClassCastException e) {
			
		}
		
		

		
		if (message.getReceiver().equals("")) {
			pool.broadcast(message.toClientCommand().toJSON());
		} else {
			UserConnection r = pool.getConnection(message.receiver);

			try {
				r.send(message.toClientCommand());

			} catch (IOException | NullPointerException e) {

				Query query = new Query();
				query.setTable_name(USER.class.getSimpleName());
				query.addQuery("username", "=\'" + message.receiver + "\'");
				query.setJustCount(true);
				QueryResult result = db.Query(query);
				if (result.getCount() > 0) {
					ArrayList<Message> array = new ArrayList<Message>();
					array.add(message);

					DataSaver ds = new DataSaver();
					ds.db = db;
					ds.pool = pool;
					ds.setTable(Tool.object2Table(array.get(0)));
					ds.setValues(Tool.List2JsonArray(array));
					ds.execute();
				}
			}
		}

	}

}
