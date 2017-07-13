package network.listener;

import java.lang.reflect.ParameterizedType;
import java.util.List;


import Common.Tool;
import network.bucketobject.QueryResult;
import network.command.client.ClientCommand;
import network.connection.Connection;

public abstract class QueryListener<T> extends ClientListener{
	
	
	
	public abstract void onResultsCome(Connection conn, int Count, List<T> Objs);

	@SuppressWarnings("unchecked")
	@Override
	public void onDataCome(Connection conn, ClientCommand message) {
		ClientCommand cm = message;
		QueryResult result = Tool.object2E(cm.getValues(), QueryResult.class);

		Class<T> entityClass = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass())
				.getActualTypeArguments()[0];

		List<T> a = Tool.ObjectList(result.getResults(), entityClass);
		this.onResultsCome(conn, result.getCount(), a);

	}

	@Override
	public void onDisconnection(Connection conn) {
		// TODO Auto-generated method stub

	}
	
	
	

}
