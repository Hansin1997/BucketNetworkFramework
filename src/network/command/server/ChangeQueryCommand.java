package network.command.server;

import network.bucketobject.ChangeQuery;
import network.bucketobject.Query;
import network.command.BucketCommand;

public class ChangeQueryCommand extends BucketCommand{
	
	public ChangeQuery query;
	
	public void setQuery(ChangeQuery query) {
		this.query = query;
	}
	
	public Query getQuery() {
		return query;
	}
	
	
	public void execute() {
		
		db.Change(query);
	}

}
