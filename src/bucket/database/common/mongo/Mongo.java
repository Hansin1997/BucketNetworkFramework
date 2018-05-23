package bucket.database.common.mongo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bson.Document;
import org.bson.conversions.Bson;

import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;

import bucket.database.BucketObject;
import bucket.database.Database;
import bucket.database.exception.DatabaseConnectionException;
import bucket.database.exception.ObjectNotFoundException;
import bucket.database.query.Query;
import bucket.database.query.QueryBody;
import bucket.database.query.QueryQueue;

/**
 * Mongo 数据库类
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
			throw new DatabaseConnectionException("Database is unselected!");
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
		Bson filter = Query2Bson(query);

		String tableName = ((query == null || query.table() == null) ? clazz.newInstance().getTableName()
				: query.table());
		MongoCollection<Document> coll = db.getCollection(tableName);
		FindIterable<Document> r = (filter == null ? coll.find() : coll.find(filter));
		for (Document doc : r) {
			T t = instantiate(clazz);
			t.setId(doc.getObjectId("_id"));
			t.setFields(doc);
			
			t.setTableName(tableName);
			result.add(t);
		}

		return result;
	}

	/**
	 * Query转换为Bson
	 * 
	 * @param query
	 * @return
	 */
	public static Bson Query2Bson(Query query) {
		Bson filter = null;
		if (query != null && query.getQueue() != null && query.getQueue().getBody() != null) {
			QueryQueue it = query.getQueue();

			do {
				if (filter == null) {
					filter = QueryBody2Bson(it.getBody());
				}
				if (it.getQueryType() == null)
					break;
				switch (it.getQueryType()) {
				case AND:
					filter = Filters.and(filter, QueryBody2Bson(it.getNext().getBody()));
					break;
				case OR:
					filter = Filters.or(filter, QueryBody2Bson(it.getNext().getBody()));
					break;
				}
				it = it.getNext();
			} while (it.getNext() != null && it.getNext().getBody() != null);

		}
		return filter;
	}

	/**
	 * QueryBody转换为Bson
	 * 
	 * @param body
	 * @return
	 */
	public static Bson QueryBody2Bson(QueryBody body) {
		if (body == null || body.getKey() == null)
			return null;
		Bson filter = null;
		switch (body.getQueueType()) {
		case EQU:
			filter = Filters.eq(body.getKey(), body.getValue());
			break;
		case GRE:
			filter = Filters.gt(body.getKey(), body.getValue());
			break;
		case LES:
			filter = Filters.lt(body.getKey(), body.getValue());
			break;
		case LIKE:
			filter = Filters.in(body.getKey(), body.getValue());
			break;
		}
		return filter;
	}

}
