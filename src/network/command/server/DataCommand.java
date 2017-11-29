package network.command.server;

import com.google.gson.JsonArray;

import network.bucketobject.Table;
import network.command.BucketCommand;

public abstract class DataCommand extends BucketCommand {

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
