package network.command.server;

import java.io.IOException;
import java.util.Date;

import Common.Gobal;
import network.bucketobject.Message;
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

		if (client.username != null && !client.equals("")) {
			message.setSender(client.username);

		}

		UserConnection r = Gobal.getPool().getUserConnection(message.receiver);
		if (r != null) {
			try {
				r.send(message.toClientCommand());
			} catch (IOException e) {
			}
		}

	}

}
