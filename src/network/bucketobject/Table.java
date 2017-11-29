package network.bucketobject;

import java.util.List;

import Common.Tool;

public class Table {

	public String table_name;
	public List<Data> values;

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

}
