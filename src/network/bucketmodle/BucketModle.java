package network.bucketmodle;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import Common.Tool;
import network.bucketobject.ChangeQuery;
import network.bucketobject.Data;
import network.bucketobject.DeleteQuery;
import network.command.server.DataSaver;
import network.command.server.MainCommand;
import network.connection.Connection;

public class BucketModle {
	
	public long id;	//主键
	private transient Connection connection;
	private transient String tableName;
	
	public BucketModle() {
		setTableName(this.getClass().getSimpleName());
	}
	
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	
	public String getTableName() {
		return tableName;
	}
	
	public void setConnection(Connection connection) {
		this.connection = connection;
	}
	
	public Connection getConnection() {
		return connection;
	}
	
	public void setId(long id) {
		this.id = id;
	}
	
	public long getId() {
		return id;
	}
	
	public void save() throws IOException , NullPointerException {
		ArrayList<Object> array = new ArrayList<Object>();
		array.add(this);
		MainCommand mc = new MainCommand();
		DataSaver ds = new DataSaver();
		ds.setTable(Tool.object2Table(this));
		ds.setValues(Tool.List2JsonArray(array));
		mc.setCommand(ds.getClass().getName());
		mc.setValues(ds);
		getConnection().send(mc);
	}
	
	public void update() throws IOException , NullPointerException, IllegalArgumentException, IllegalAccessException {
		ChangeQuery query = new ChangeQuery();
		query.setTable_name(getTableName());
		query.addQuery("id=",getId());
		
		List<Data> list = (Tool.object2List(this));
		for(Data dd : list) {
			query.addData(dd.getKey() + "=", "\"" + dd.getValue() + "\"");
		}
		getConnection().send(query.toServerCommand());
	}
	
	public void delete() throws IOException , NullPointerException {
		DeleteQuery query = new DeleteQuery();
		query.setTable_name(getTableName());
		query.addQuery("id=", getId());
		
		getConnection().send(query.toServerCommand());
	}
}
