package bucket.database;

import org.bson.BsonDocument;
import org.bson.Document;
import org.bson.conversions.Bson;

import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoIterable;
import com.mongodb.client.model.Filters;

/**
 * 数据库管理器
 * 
 * @author Hansin1997
 * @version 2017/12/4
 */
public abstract class Database {

	/**
	 * 数据库类型
	 */
	protected String dbType;
	/**
	 * 数据库主机
	 */
	protected String dbHost;
	/**
	 * 数据库端口
	 */
	protected int dbPort;

	public static void main(String[] args) {

		MongoClient c = new MongoClient("localhost");
		MongoDatabase db = c.getDatabase("test");
		MongoIterable<String> list = db.listCollectionNames();
		for (String s : list) {
			MongoCollection<Document> doc = db.getCollection(s);
			FindIterable<Document> f = doc.find();
			Bson b1 = Filters.and(Filters.eq("name", "邹威"), Filters.lte("year", 20));
			Bson b2 = BsonDocument.parse("{ name : \"邹威\" }");
			
			f = doc.find(b2);
			System.out.println(b1);

			for (Document d : f)
				System.out.println((d.toJson()));
		}
		c.close();

	}
	
	/**
	 * 设置数据库类型
	 * 
	 * @param dbType
	 *            数据库类型
	 */
	protected void setDbType(String dbType) {
		this.dbType = dbType;
	}

	/**
	 * 获取数据库类型
	 * 
	 * @return 数据库类型
	 */
	public String getDbType() {
		return dbType;
	}

	/**
	 * 设置数据库主机名
	 * 
	 * @param dbHost
	 *            主机名
	 */
	public void setDbHost(String dbHost) {
		this.dbHost = dbHost;
	}

	/**
	 * 获取数据库主机名
	 * 
	 * @return 主机名
	 */
	public String getDbHost() {
		return dbHost;
	}

	/**
	 * 设置数据库端口号
	 * 
	 * @param dbPort
	 *            端口号
	 */
	public void setDbPort(int dbPort) {
		this.dbPort = dbPort;
	}

	/**
	 * 获取数据库端口
	 * 
	 * @return 端口号
	 */
	public int getDbPort() {
		return dbPort;
	}
}
