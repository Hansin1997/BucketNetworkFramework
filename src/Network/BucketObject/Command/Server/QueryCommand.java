package Network.BucketObject.Command.Server;

import java.io.IOException;

import Common.Gobal;
import Database.Query;
import Network.QueryResult;
import Network.BucketObject.BucketCommand;
import Network.BucketObject.Command.Client.ClientCommand;
import Network.Connection.UserConnection;

public class QueryCommand extends BucketCommand{

	public Query query;
	
	public void setQuery(Query query) {
		this.query = query;
	}
	
	public Query getQuery() {
		return query;
	}
	
	@Override
	public void execute() {
		UserConnection conn = client;
		if(conn == null)
			return;
		
		QueryResult result = Gobal.db.Query(query);
		ClientCommand pkg = (ClientCommand) result.toClientCommand(sign);
		
		
		try {
			conn.send(pkg.toJSON());
		} catch (IOException e) {
		}

	}
	

}
