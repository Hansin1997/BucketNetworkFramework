package Network.BucketObject.Command.Server;

import java.util.Date;

import Common.Gobal;
import Common.Tool;
import Network.Message;
import Network.BucketObject.BucketCommand;

public class MessageCommand extends BucketCommand{
	
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
		
		
		if(client.username != null && !client.equals(""))
		{
			message.setSender(client.username);
		}
		
		Gobal.getPool().broadcast(Tool.toJson(message.toClientCommand()));

		
	}
	
	

}
