package Network;

import java.util.Date;

import Network.BucketObject.BucketCommand;
import Network.BucketObject.Command.Client.ClientCommand;
import Network.BucketObject.Command.Server.MainCommand;
import Network.BucketObject.Command.Server.MessageCommand;

public class Message {
	
	public String sender;
	
	public String receiver;
	public String content;
	
	public String type;
	
	public Date sendTime;
	
	public Message(){
		sender = receiver = content = type = "";
		
	}
	
	public String getType() {
		return type;
	}
	
	public void setType(String type) {
		this.type = type;
	}
	

	
	public void setContent(String content) {
		this.content = content;
	}
	
	public void setSendTime(Date sendTime) {
		this.sendTime = sendTime;
	}
	
	public String getContent() {
		return content;
	}
	

	
	public Date getSendTime() {
		return sendTime;
	}
	

	public void setReceiver(String receiver) {
		this.receiver = receiver;
	}
	
	public void setSender(String sender) {
		this.sender = sender;
	}
	
	public String getReceiver() {
		return receiver;
	}
	
	public String getSender() {
		return sender;
	}
	
	public BucketCommand toServerCommand()
	{
		MainCommand mac = new MainCommand();
		MessageCommand mec= new MessageCommand();
		mec.setMessage(this);
		mac.setValues(mec);
		mac.setCommand(mec.getClass().getName());
		return mac;
	}
	
	public BucketCommand toClientCommand()
	{
		ClientCommand clm= new ClientCommand();
		clm.setCommand(this.getClass().getSimpleName());
		clm.setValues(this);
		return clm;
	}

}
