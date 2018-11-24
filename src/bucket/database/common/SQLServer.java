package bucket.database.common;

import java.lang.reflect.Field;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;

import bucket.database.BucketObject;
import bucket.database.exception.DatabaseConnectionException;

/**
 * SQL Server Database
 * 
 * @author Hansin1997
 *
 */
public class SQLServer extends MySQL {

	public SQLServer(String host, int port) throws ClassNotFoundException {
		super(host, port);
	}

	protected void createBucketObjectTable(BucketObject obj) throws Exception {
		ArrayList<Field> fileds = BucketObject.getAllFields(obj.getClass());
		StringBuffer sql = new StringBuffer();
		sql.append("CREATE TABLE " + obj.getTableName() + " ( id INT NOT NULL identity(1,1)");

		for (Field filed : fileds) {
			sql.append(" , " + filed.getName() + " " + typeTransform(filed.getType().getSimpleName()));
		}
		sql.append(" , PRIMARY KEY (id));");
		PreparedStatement ps = conn.prepareStatement(new String(sql.toString().getBytes(), "UTF-8"));
		ps.execute();

	}

	protected boolean isTableExist(String tableName) throws Exception {
		PreparedStatement ps = conn.prepareStatement("select * from sys.tables where name like ?;");
		ps.setString(1, "" + tableName + "");
		return ps.executeQuery().next();
	}

	@Override
	public void useDb(String databaseName) throws Exception {
		if (!isConnected())
			throw new DatabaseConnectionException("MySQL is disconnected");
		PreparedStatement ps = conn.prepareStatement("USE " + databaseName + ";");
		try {
			ps.execute();
		} catch (SQLException e) {
			ps = conn.prepareStatement("CREATE DATABASE " + databaseName + ";");
			ps.execute();
			useDb(databaseName);
		}
	}

	@Override
	public void connect() throws Exception {
		connect("sa", "123456");
	}

	@Override
	public void connect(String username, String password) throws Exception {
		String connectionString = "jdbc:sqlserver://" + getDbHost() + ":" + getDbPort() + ";" + "user=" + username + ";"
				+ "password=" + password + ";";
		conn = DriverManager.getConnection(connectionString, username, password);
	}

}
