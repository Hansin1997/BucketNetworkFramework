package network.bucketobject;

import java.util.ArrayList;
import java.util.List;

import Common.Tool;
import network.command.BucketCommand;
import network.command.server.MainCommand;
import network.command.server.QueryCommand;

public class Query {

	public String table_name;
	public List<Data> values;
	public int count;
	public boolean justCount; // 是否只查询数目

	public Query() {
		this("", -1);
	}
	
	

	public Query(String table_name, int count) {
		setTable_name(table_name);
		setCount(count);
		values = new ArrayList<Data>();
	}

	public void setJustCount(boolean justCount) {
		this.justCount = justCount;
	}
	
	public boolean isJustCount() {
		return justCount;
	}
	
	public void setCount(int count) {
		this.count = count;
	}

	public int getCount() {
		return count;
	}

	public void addQuery(String key, Object value) {
		values.add(new Data(key, value));
	}

	public void setTable_name(String table_name) {
		this.table_name = table_name;
	}

	public void setValues(List<Data> values) {
		this.values = values;
	}

	public List<Data> getValues() {
		return values;
	}

	public String getTable_name() {
		return table_name;
	}

	public String toJSON() {
		return Tool.toJson(this);
	}

	public String toSQL() {
		if (values.size() == 0) {
			return "SELECT * FROM " + table_name + ";";
		}

		String head = "SELECT * FROM " + table_name + " WHERE ";

		String mid = "";

		for (int i = 0; i < values.size(); i++) {

			Data d = values.get(i);
			mid += d.getKey() + d.getValue();
			if (i < values.size() - 1)
				mid += " AND ";
		}

		String sql = head + mid + ";";
		return sql;
	}

	public BucketCommand toServerCommand(int sign) {
		MainCommand mac = new MainCommand();
		QueryCommand quc = new QueryCommand();
		quc.setQuery(this);
		mac.setValues(quc);
		mac.setSign(sign);
		mac.setCommand(quc.getClass().getName());
		return mac;
	}

	public BucketCommand toServerCommand() {
		return toServerCommand(0);
	}

}
