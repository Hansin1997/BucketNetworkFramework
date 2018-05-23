package bucket.database.common.mysql;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.mysql.jdbc.exceptions.jdbc4.MySQLSyntaxErrorException;

import bucket.database.BucketObject;
import bucket.database.Database;
import bucket.database.exception.DatabaseConnectionException;
import bucket.database.exception.ObjectNotFoundException;
import bucket.database.query.Query;
import bucket.database.query.QueryQueue;

/**
 * MySQL数据库类
 * 
 * @author Hansin
 *
 */
public class MySQL extends Database {

	/**
	 * 连接对象
	 */
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
		conn = DriverManager.getConnection(
				"jdbc:mysql://" + getDbHost() + ":" + getDbPort() + "?useUnicode=true&characterEncoding=utf-8", "root",
				"");

	}

	@Override
	public void connect(String username, String password) throws Exception {
		conn = DriverManager.getConnection(
				"jdbc:mysql://" + getDbHost() + ":" + getDbPort() + "?useUnicode=true&characterEncoding=utf-8",
				username, password);
	}

	@Override
	public void close() {
		try {
			conn.close();
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	@Override
	public void useDb(String databaseName) throws Exception {
		if (!isConnected())
			throw new DatabaseConnectionException("MySQL is disconnected");
		PreparedStatement ps = conn.prepareStatement("USE " + databaseName + ";");
		try {
			ps.execute();
		} catch (MySQLSyntaxErrorException e) {
			// 当数据库不存在
			ps = conn.prepareStatement(
					"CREATE DATABASE " + databaseName + " DEFAULT CHARACTER SET utf8 COLLATE utf8_bin;");
			ps.execute();
			useDb(databaseName);
		}

	}

	@Override
	public <T extends BucketObject> T instantiate(Class<T> clazz) throws Exception {
		T bobj = super.instantiate(clazz);
		return bobj;
	}

	@Override
	public <T extends BucketObject> List<T> find(Class<T> clazz, Query query, long limit) throws Exception {
		ArrayList<T> result = new ArrayList<T>();

		String tableName = ((query == null || query.table() == null) ? clazz.newInstance().getTableName()
				: query.table());

		PreparedStatement ps = conn.prepareStatement(Query2PreSQL(query, tableName, limit),
				Statement.RETURN_GENERATED_KEYS);

		if (query != null && query.getQueue() != null && query.getQueue().getBody() != null
				&& query.getQueue().getBody().getSymbol() != null) {
			int i = 1;
			QueryQueue q = query.getQueue();
			while (q != null && q.getBody() != null && q.getBody().getSymbol() != null) {
				ps.setObject(i, q.getBody().getValue());
				q = q.getNext();
				i++;

			}

		}

		try {
			ps.executeQuery();
		} catch (MySQLSyntaxErrorException e) {
			e.printStackTrace();
			return null;
		}
		ResultSet set = ps.getResultSet();
		ResultSetMetaData meta = set.getMetaData();

		while (set.next()) {
			Map<String, Object> fileds = new HashMap<String, Object>();
			for (int i = 1; i <= meta.getColumnCount(); i++) {
				String key = meta.getColumnName(i);
				fileds.put(key, set.getObject(i));
			}
			T t = instantiate(clazz);
			t.setFields(fileds);
			t.setId(fileds.get("id"));
			t.setTableName(tableName);
			result.add(t);
		}

		return result;
	}

	public static String Query2PreSQL(Query query, String tableName, long limit) throws UnsupportedEncodingException {
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT * FROM " + tableName + " ");
		if (query != null && query.getQueue() != null && query.getQueue().getBody() != null
				&& query.getQueue().getBody().getSymbol() != null) {
			sql.append("WHERE ");
			QueryQueue q = query.getQueue();
			while (q != null && q.getBody() != null && q.getBody().getSymbol() != null) {
				sql.append(q.getBody().getKey());
				sql.append(q.getBody().getSymbol());
				sql.append("?");

				if (q.getQueryType() != null) {
					sql.append(" " + q.getSymbol() + " ");
				}
				q = q.getNext();

			}
			sql.append(" ");
		}

		if (limit != -1) {
			sql.append("LIMIT " + limit + ";");
		} else {
			sql.append(";");
		}
		return new String(sql.toString().getBytes(), "UTF-8");
	}

	public static JsonArray Query2ValueArray(Query query) throws UnsupportedEncodingException {
		Gson gson = new Gson();
		JsonArray arr = new JsonArray();
		if (query != null && query.getQueue() != null && query.getQueue().getBody() != null
				&& query.getQueue().getBody().getSymbol() != null) {
			QueryQueue q = query.getQueue();
			while (q != null && q.getBody() != null && q.getBody().getSymbol() != null) {
				arr.add(gson.fromJson(gson.toJson(q.getBody().getValue()), JsonElement.class));
				q = q.getNext();

			}
		}
		return arr;
	}

	@Override
	public void insert(BucketObject obj) throws Exception {
		if (!isTableExist(obj.getTableName()))
			createBucketObjectTable(obj);

		StringBuffer sql = new StringBuffer();
		StringBuffer sqlpart1 = new StringBuffer();
		StringBuffer sqlpart2 = new StringBuffer();
		sql.append("INSERT INTO " + obj.getTableName() + " ");

		Map<String, Object> fileds = obj.getFields();
		Set<Entry<String, Object>> set = fileds.entrySet();

		sqlpart1.append("(");
		sqlpart2.append("(");
		boolean first = true;

		for (Entry<String, Object> kv : set) {
			if (kv.getValue() == null)
				continue;
			if (first) {
				sqlpart1.append(kv.getKey());
				sqlpart2.append("?");
				first = false;
			} else {
				sqlpart1.append("," + kv.getKey());
				sqlpart2.append(",?");
			}

		}
		sqlpart1.append(")");
		sqlpart2.append(");");
		sql.append(sqlpart1);
		sql.append(" VALUES ");
		sql.append(sqlpart2);

		System.out.println(sql.toString());
		PreparedStatement ps = conn.prepareStatement(new String(sql.toString().getBytes(), "UTF-8"),
				Statement.RETURN_GENERATED_KEYS);

		int i = 1;
		for (Entry<String, Object> kv : set) {

			if (kv.getValue() == null)
				continue;
			if (kv.getValue().getClass().equals(ArrayList.class)) {

				ps.setString(i, kv.getValue().toString());
			} else
				ps.setObject(i, kv.getValue());
			i++;

		}
		ps.execute();

		ResultSet result = ps.getGeneratedKeys();
		if (result.next()) {
			obj.setId(result.getInt(1));
		}

	}

	@Override
	public void remove(BucketObject obj) throws Exception {
		PreparedStatement ps = conn.prepareStatement("DELETE FROM " + obj.getTableName() + " WHERE id=" + obj.getId());
		if (ps.executeUpdate() == 0) {
			throw new ObjectNotFoundException("MySQL Object Not Found! id:" + obj.getId());
		}

	}

	@Override
	public void update(BucketObject obj) throws Exception {
		if (!isTableExist(obj.getTableName()))
			createBucketObjectTable(obj);

		StringBuffer sql = new StringBuffer();
		sql.append("UPDATE " + obj.getTableName() + " SET ");
		Map<String, Object> fileds = obj.getFields();
		Set<Entry<String, Object>> set = fileds.entrySet();
		Iterator<Entry<String, Object>> it = set.iterator();
		while (it.hasNext()) {
			Entry<String, Object> kv = it.next();
			if (it.hasNext())
				sql.append(kv.getKey() + "=?, ");
			else
				sql.append(kv.getKey() + "=? ");
		}
		sql.append("WHERE id=" + obj.getId() + ";");
		PreparedStatement ps = conn.prepareStatement(new String(sql.toString().getBytes(), "UTF-8"),
				Statement.RETURN_GENERATED_KEYS);
		int i = 1;
		for (Entry<String, Object> kv : set) {
			ps.setObject(i, kv.getValue());
			i++;
		}
		ps.execute();
	}

	/**
	 * 判断表是否存在
	 * 
	 * @param tableName
	 *            表名
	 * @return
	 * @throws Exception
	 *             异常
	 */
	protected boolean isTableExist(String tableName) throws Exception {
		PreparedStatement ps = conn.prepareStatement("show tables like ?;");
		ps.setString(1, tableName);
		return ps.executeQuery().next();
	}

	/**
	 * 创建BucketObject对应的MySQL数据表
	 * 
	 * @param obj
	 *            BucketObject
	 * @throws Exception
	 *             异常
	 */
	protected void createBucketObjectTable(BucketObject obj) throws Exception {

		ArrayList<Field> fileds = BucketObject.getAllFields(obj.getClass());

		StringBuffer sql = new StringBuffer();
		sql.append("CREATE TABLE " + obj.getTableName() + " ( `id` INT NOT NULL AUTO_INCREMENT");

		for (Field filed : fileds) {
			sql.append(" , " + filed.getName() + " " + typeTransform(filed.getType().getSimpleName()));
		}

		sql.append(" , PRIMARY KEY (`id`)) ENGINE = InnoDB CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;");

		PreparedStatement ps = conn.prepareStatement(new String(sql.toString().getBytes(), "UTF-8"));

		ps.execute();

	}

	/**
	 * JAVA与MySQL的类型转换常量表
	 */
	protected static final String[][] TYPES = { { "String", "TEXT" }, { "Date", "datetime" },
			{ "Boolean", "boolean" } };

	/**
	 * JAVA与MySQL的类型转换方法
	 * 
	 * @param type
	 *            类型名
	 * @return
	 */
	protected static String typeTransform(String type) {
		for (String[] types : TYPES) {
			if (type.toUpperCase().equals(types[0].toUpperCase()))
				return types[1];
		}
		System.out.println("Type Unknow:" + type);
		return "TEXT";
	}

}
