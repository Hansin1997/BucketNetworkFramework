package network.command;

import Common.Tool;
import Database.DatabaseManager;
import network.connection.Connection;
import network.pool.Pool;

public abstract class BucketCommand {

	public transient Connection client;
	public transient DatabaseManager db;
	public transient Pool pool;

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
