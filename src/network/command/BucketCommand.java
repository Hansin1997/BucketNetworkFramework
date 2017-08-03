package network.command;

import Common.Tool;
import Database.DatabaseManager;
import network.SocketPool;
import network.connection.UserConnection;

public abstract class BucketCommand {

	public transient UserConnection client;
	public transient DatabaseManager db;
	public transient SocketPool pool;

	public int sign = 0;

	public BucketCommand(int sign) {
		setSign(sign);
	}

	public BucketCommand() {
		setSign(0);
	}

	public int getSign() {
		return sign;
	}

	public void setSign(int sign) {
		this.sign = sign;
	}

	abstract public void execute();

	public String toJSON() {
		return Tool.toJson(this);
	}

}
