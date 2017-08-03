package network.command.server;

import java.io.IOException;

import network.bucketobject.Query;
import network.bucketobject.QueryResult;
import network.command.BucketCommand;
import network.command.client.ClientCommand;
import network.connection.UserConnection;

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
		
		QueryResult result = db.Query(query);
		ClientCommand pkg = (ClientCommand) result.toClientCommand(sign);
		
		
		try {
			conn.send(pkg.toJSON());
		} catch (IOException e) {
		}

	}
	

}
