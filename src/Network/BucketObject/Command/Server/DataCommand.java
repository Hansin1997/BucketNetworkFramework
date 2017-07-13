package Network.BucketObject.Command.Server;

import com.google.gson.JsonArray;

import Database.Table;
import Network.BucketObject.BucketCommand;

public abstract class DataCommand extends BucketCommand{

	public String command;
	public Table table;
	public JsonArray values;
	

	
	public void setCommand(String command) {
		this.command = command;
	}
	
	public String getCommand() {
		return command;
	}
	
	public void setTable(Table table) {
		this.table = table;
	}
	
	public Table getTable() {
		return table;
	}
	
	public void setValues(JsonArray values) {
		this.values = values;
	}
	
	public JsonArray getValues() {
		return values;
	}
	
	abstract public void execute();

}
