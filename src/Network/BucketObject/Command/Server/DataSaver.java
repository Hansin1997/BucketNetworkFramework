package Network.BucketObject.Command.Server;

import java.sql.SQLException;


import Common.Gobal;
import Common.Tool;

public class DataSaver extends DataCommand{
	
	@Override
	public void execute() {
		
		String tn = table.getTable_name();
		try {
			Gobal.db.SQLexecute(Tool.table2SQL(table));
			Gobal.db.SQLexecute(Tool.arrayInsert2SQL(tn, values));
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}
	


}
