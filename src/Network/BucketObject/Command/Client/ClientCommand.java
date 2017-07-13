package Network.BucketObject.Command.Client;

import Network.BucketObject.BucketCommand;

public class ClientCommand extends BucketCommand{
	
	public String command;
	public Object values;
	
	public void setCommand(String command) {
		this.command = command;
	}
	
	public String getCommand() {
		return command;
	}
	
	public void setValues(Object values) {
		this.values = values;
	}
	
	public Object getValues() {
		return values;
	}

	@Override
	public void execute() {
		
	}

}
