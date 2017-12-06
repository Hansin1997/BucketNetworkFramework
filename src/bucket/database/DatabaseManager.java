package bucket.database;

import org.bson.BSON;
import org.bson.BSONEncoder;
import org.bson.BSONObject;
import org.bson.BasicBSONObject;
import org.bson.BsonBinary;
import org.bson.BsonBinaryWriter;
import org.bson.BsonJavaScript;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.json.JsonWriterSettings;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mongodb.Mongo;
import com.mongodb.MongoBulkWriteException;
import com.mongodb.MongoClient;
import com.mongodb.MongoException;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoIterable;
import com.mongodb.client.model.Filters;
import com.mongodb.util.JSON;

import Test.TestBean;

/**
 * 数据库管理器
 * 
 * @author Hansin1997
 * @version 2017/12/4
 */
public abstract class DatabaseManager {

	public static void main(String[] args) {

		MongoClient c = new MongoClient("localhost");
		MongoDatabase db = c.getDatabase("test");
		MongoIterable<String> list = db.listCollectionNames();
		for (String s : list) {
			MongoCollection<Document> doc = db.getCollection(s);
			FindIterable<Document> f = doc.find();
			Bson b1 = Filters.and(Filters.eq("name","邹威"),Filters.lte("year", 20));
			Gson g = new GsonBuilder().create();
			
			//Bson b2 = g.fromJson(b1.toString(), Bson.class);
			System.out.println(b1);
			System.out.println();
			
			f = doc.find(b1);
			//doc.
			for (Document d : f)
				System.out.println(d.toJson(JsonWriterSettings.builder().build()));
		}

	}
}
