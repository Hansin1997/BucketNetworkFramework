package network.command.server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import network.command.BucketCommand;

public class MainCommand extends BucketCommand {

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

		BucketCommand bo = null;
		Gson gson = new GsonBuilder().create();

		try {

			bo = (BucketCommand) gson.fromJson(gson.toJson(values), Class.forName(command));
		} catch (JsonSyntaxException | ClassNotFoundException e) {

		}

		if (bo != null) {
			bo.client = client;
			bo.setSign(sign);
			bo.execute();
		}

	}

}
