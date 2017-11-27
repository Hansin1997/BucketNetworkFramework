package network.bucketobject;

import java.util.List;

import com.google.gson.JsonObject;

import Common.Tool;
import network.command.BucketCommand;
import network.command.client.ClientCommand;

public class QueryResult {

	public int count;
	public List<JsonObject> results;
	public String error;
	
	public void setError(String error) {
		this.error = error;
	}
	
	public String getError() {
		return error;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public void setResults(List<JsonObject> results) {
		this.results = results;
	}

	public int getCount() {
		return count;
	}

	public List<JsonObject> getResults() {
		return results;
	}

	public BucketCommand toClientCommand(int Sign) {
		ClientCommand clm = new ClientCommand();
		clm.setCommand(this.getClass().getSimpleName());
		clm.setValues(this);
		clm.setSign(Sign);
		return clm;
	}

	public String toJSON() {
		return Tool.toJson(this);
	}
}
