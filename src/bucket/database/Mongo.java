package bucket.database;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
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
	public void connect(String username, String password) {
		System.out.println("MongoDB connect without login");
		connect();

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
			throw new DatabaseConnectionException("Connection closed!");
		db = mongo.getDatabase(databaseName);
		if (db == null)
			throw new DatabaseConnectionException("Use Database Fail!");
	}

	@Override
	public void insert(BucketObject obj) throws Exception {
		if (db == null)
			throw new DatabaseConnectionException("Database unselected!");
		MongoCollection<Document> coll = db.getCollection(obj.getTableName());
		Document doc = new Document();
		doc.putAll(obj.getFields());
		doc.remove("id");

		coll.insertOne(doc);
		obj.setId(doc.getObjectId("_id"));
	}

	@Override
	public <T extends BucketObject> T instantiate(Class<T> clazz) throws Exception {
		T bobj = super.instantiate(clazz);

		return bobj;
	}

	@Override
	public void update(BucketObject obj) throws Exception {

		if (db == null)
			throw new DatabaseConnectionException("Database unselected!");

		MongoCollection<Document> coll = db.getCollection(obj.getTableName());
		Document query = new Document();
		query.append("_id", obj.getId());

		Map<String, Object> fields = obj.getFields();

		if (coll.find(query).first() == null) {
			throw new ObjectNotFoundException("MongoDB Object Not Found! _id:" + obj.getId());
		}
		Document upd = new Document(fields);
		coll.updateOne(query, new Document("$set", upd));

	}

	@Override
	public void remove(BucketObject obj) throws Exception {
		if (db == null)
			throw new DatabaseConnectionException("Database unselected!");

		MongoCollection<Document> coll = db.getCollection(obj.getTableName());
		Document query = new Document();
		query.append("_id", obj.getId());

		if (coll.find(query).first() == null) {
			throw new ObjectNotFoundException("MongoDB Object Not Found! _id:" + obj.getId());
		}

		coll.findOneAndDelete(query);

	}

	@Override
	public <T extends BucketObject> List<T> find(Class<T> clazz, Query query, long limit) throws Exception {
		if (db == null)
			throw new DatabaseConnectionException("Database unselected!");

		ArrayList<T> result = new ArrayList<T>();
		Field[] fields = clazz.getFields();

		if (query == null) {
			MongoCollection<Document> coll = db.getCollection(clazz.getSimpleName());
			FindIterable<Document> r = coll.find();
			
			for (Document doc : r) {
				T t = instantiate(clazz);
				for (Field field : fields) {

					field.set(t, doc.get(field.getName()));
				}
				result.add(t);

			}

			
			
		}
		return result;
	}

}
