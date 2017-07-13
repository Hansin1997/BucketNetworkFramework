package Network.BucketObject;

import Common.Tool;
import Network.Connection.UserConnection;

public abstract class BucketCommand {
	
	public UserConnection client;
	
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
	
	public String toJSON()
	{
		return Tool.toJson(this);
	}

}
