package bucket.database;

import com.mongodb.MongoClient;

/**
 * mongodb 数据库
 * 
 * @author hansin
 *
 */
public class Mongo extends Database {

	/**
	 * 数据库连接对象
	 */
	private MongoClient mongo;

	/**
	 * 默认构造函数
	 * 
	 * @param host
	 *            mongo主机
	 * @param port
	 *            mongo端口
	 */
	public Mongo(String host, int port) {
		super(host, port);
		setDbType(Database.TYPE_MONGO);
	}

	@Override
	public boolean isConnected() {
		return mongo != null;
	}

	@Override
	public void connect() {
		mongo = new MongoClient(getDbHost(), getDbPort());
	}

	@Override
	public void close() {
		if (mongo != null) {
			mongo.close();
			mongo = null;
		}
	}

}
