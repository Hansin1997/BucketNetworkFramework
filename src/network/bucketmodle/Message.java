package network.bucketmodle;

import java.util.Date;

import network.command.BucketCommand;
import network.command.client.ClientCommand;
import network.command.server.MainCommand;
import network.command.server.MessageCommand;

public class Message extends BucketModle{

	public String sender;

	public String receiver;
	public String content;

	public String type;

	public Date sendTime;
	

	public Message() {
		super();
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

	public BucketCommand toServerCommand() {
		MainCommand mac = new MainCommand();
		MessageCommand mec = new MessageCommand();
		mec.setMessage(this);
		mac.setValues(mec);
		mac.setCommand(mec.getClass().getName());
		return mac;
	}

	public BucketCommand toClientCommand() {
		ClientCommand clm = new ClientCommand();
		clm.setCommand(this.getClass().getSimpleName());
		clm.setValues(this);
		return clm;
	}

}
