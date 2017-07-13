package network.bucketobject;

import Common.Tool;
import network.command.BucketCommand;
import network.command.server.DeleteQueryCommand;
import network.command.server.MainCommand;

public class DeleteQuery extends Query{

	@Override
	public String toJSON() {
		return Tool.toJson(this);
	}

	@Override
	public String toSQL() {
		if (values.size() == 0) {
			return "DELETE FROM " + table_name + ";";
		}

		String head = "DELETE FROM " + table_name + " WHERE ";

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

	@Override
	public BucketCommand toServerCommand(int sign) {
		MainCommand mac = new MainCommand();
		DeleteQueryCommand quc = new DeleteQueryCommand();
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
