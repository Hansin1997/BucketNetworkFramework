package network.bucketobject;

import java.util.ArrayList;
import java.util.List;

import Common.Tool;
import network.command.BucketCommand;
import network.command.server.ChangeQueryCommand;
import network.command.server.MainCommand;

public class ChangeQuery extends Query{
	
	public List<Data> newdata;
	
	public void addData(String key,String value){
		newdata.add(new Data(key,value));
	}
	
	public ChangeQuery() {
		super("",-1);
		newdata = new ArrayList<Data>();
	}
	
	@Override
	public String toJSON() {
		return Tool.toJson(this);
	}
	
	public void setNewdata(List<Data> newdata) {
		this.newdata = newdata;
	}
	
	public List<Data> getNewdata() {
		return newdata;
	}

	@Override
	public String toSQL() {
		
		if (newdata.size() == 0) {
			return "";
		}
		
		String head = "UPDATE " + table_name + " SET ";
		String mid = "";
		for (int i = 0; i < newdata.size(); i++) {

			Data d = newdata.get(i);
			mid += d.getKey() + d.getValue();
			if (i < newdata.size() - 1)
				mid += " , ";
		}
		String foot = " WHERE ";
		for (int i = 0; i < values.size(); i++) {

			Data d = values.get(i);
			foot += d.getKey() + d.getValue();
			if (i < values.size() - 1)
				foot += " AND ";
		}

		String sql = head + mid + foot + ";";
		return sql;
	}

	@Override
	public BucketCommand toServerCommand(int sign) {
		MainCommand mac = new MainCommand();
		ChangeQueryCommand quc = new ChangeQueryCommand();
		quc.setQuery(this);
		mac.setValues(quc);
		mac.setSign(sign);
		mac.setCommand(quc.getClass().getName());
		return mac;
	}

	@Override
	public BucketCommand toServerCommand() {
		return toServerCommand(0);
	}

}
