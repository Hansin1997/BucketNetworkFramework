package bucket.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.List;

public class MySQL extends Database {
	
	protected Connection conn;

	/**
	 * 默认构造函数
	 * 
	 * @param host
	 *            MySQL主机
	 * @param port
	 *            MySQL端口
	 * @throws ClassNotFoundException 
	 */
	public MySQL(String host, int port) throws ClassNotFoundException {
		super(host, port);
		setDbType(Database.TYPE_MYSQL);
		Class.forName("com.mysql.jdbc.Driver");
	}

	@Override
	public boolean isConnected() {
		try {
			return !conn.isClosed();
		} catch (Throwable e) {
			return false;
		}
	}

	@Override
	public void connect() throws Exception {
		conn = DriverManager.getConnection("jdbc:mysql://" + getDbHost() + ":" + getDbPort(), "root", "");
		
	}

	@Override
	public void connect(String username, String password) throws Exception {
		conn = DriverManager.getConnection("jdbc:mysql://" + getDbHost() + ":" + getDbPort(), username, password);
	}

	@Override
	public void close() {
		try {
			conn.close();
		} catch (Throwable e) {
			e.printStackTrace();;
		}
		
	}

	@Override
	public void useDb(String databaseName) throws Exception {
		if(!isConnected())
			throw new DatabaseConnectionException("MySQL is disconnected");
		PreparedStatement ps = conn.prepareStatement("use " + databaseName + ";");
		ps.execute();

		
	}

	@Override
	public <T extends BucketObject> List<T> find(Class<T> clazz, Query query, long limit) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void insert(BucketObject obj) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void remove(BucketObject obj) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void update(BucketObject obj) throws Exception {
		// TODO Auto-generated method stub
		
	}

}
