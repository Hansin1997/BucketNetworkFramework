package bucket.database;

import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

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
	 * 数据库对象
	 */
	private MongoDatabase db;

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

	@Override
	public void useDb(String databaseName) throws Exception {
		if (mongo == null)
			throw new Exception("Connection closed!");
		db = mongo.getDatabase(databaseName);
		if (db == null)
			throw new Exception("Use Database Fail!");
	}

	@Override
	public void insert(BucketObject obj) throws Exception {
		if (db == null)
			throw new Exception("Database unselected!");
		MongoCollection<Document> coll = db.getCollection(obj.getTableName());
		Document doc = new Document();
		doc.putAll(obj.getFields());
		coll.insertOne(doc);
	}

}
