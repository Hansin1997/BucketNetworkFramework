package network.command.server;

import java.sql.SQLException;

import Common.Tool;
import network.bucketobject.QueryResult;

public class DataSaver extends DataCommand {

	@Override
	public void execute() {

		String tn = table.getTable_name();
		
		
		try {
			db.SQLexecute(Tool.table2SQL(table));
			db.SQLexecute(Tool.arrayInsert2SQL(tn, values));

		} catch (SQLException e) {
			e.printStackTrace();
			
		}

	}



}
