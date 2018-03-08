package bucket.database;

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
			e.printStackTrace();
		}
	}

	@Override
	public void useDb(String databaseName) throws Exception {
		if (!isConnected())
			throw new DatabaseConnectionException("MySQL is disconnected");
		PreparedStatement ps = conn.prepareStatement("use " + databaseName + ";");
		ps.execute();

	}

	@Override
	public <T extends BucketObject> T instantiate(Class<T> clazz) throws Exception {
		T bobj = super.instantiate(clazz);
		return bobj;
	}

	@Override
	public <T extends BucketObject> List<T> find(Class<T> clazz, Query query, long limit) throws Exception {
		ArrayList<T> result = new ArrayList<T>();
		T obj = clazz.newInstance();
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT * FROM " + obj.getTableName() + " ");
		if (query != null) {
			sql.append("WHERE ");
			Query q = query;
			while (q != null) {
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

		PreparedStatement ps = conn.prepareStatement(sql.toString(), Statement.RETURN_GENERATED_KEYS);

		if (query != null) {
			int i = 1;
			Query q = query;
			while (q != null) {
				ps.setObject(i, q.getBody().getValue());
				q = q.getNext();
				i++;

			}

		}

		ps.executeQuery();
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
			result.add(t);
		}

		return result;
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

		PreparedStatement ps = conn.prepareStatement(sql.toString(), Statement.RETURN_GENERATED_KEYS);

		int i = 1;
		for (Entry<String, Object> kv : set) {
			if (kv.getValue() == null)
				continue;
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
			throw new ObjectNotFoundException("MongoDB Object Not Found! _id:" + obj.getId());
		}

	}

	@Override
	public void update(BucketObject obj) throws Exception {
		StringBuffer sql = new StringBuffer();
		sql.append("UPDATE " + obj.getTableName() + " SET ");
		Map<String, Object> fileds = obj.getFields();
		Set<Entry<String, Object>> set = fileds.entrySet();
		Iterator<Entry<String, Object>> it = set.iterator();
		while (it.hasNext()) {
			Entry<String, Object> kv = it.next();
			if(it.hasNext())
				sql.append(kv.getKey() + "=?, ");
			else
				sql.append(kv.getKey() + "=? ");
		}
		sql.append("WHERE id=" + obj.getId() + ";");
		PreparedStatement ps = conn.prepareStatement(sql.toString(), Statement.RETURN_GENERATED_KEYS);
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
		Field[] fileds = obj.getClass().getFields();

		StringBuffer sql = new StringBuffer();
		sql.append("CREATE TABLE " + obj.getTableName() + " ( `id` INT NOT NULL AUTO_INCREMENT");

		for (Field filed : fileds) {
			sql.append(" , " + filed.getName() + " " + typeTransform(filed.getType().getSimpleName()));
		}

		sql.append(" , PRIMARY KEY (`id`)) ENGINE = InnoDB CHARACTER SET utf8 COLLATE utf8_bin;");

		PreparedStatement ps = conn.prepareStatement(sql.toString());

		ps.execute();

	}

	/**
	 * JAVA与MySQL的类型转换常量表
	 */
	protected static final String[][] TYPES = { { "String", "TEXT" } };

	/**
	 * JAVA与MySQL的类型转换方法
	 * 
	 * @param type
	 *            类型名
	 * @return
	 */
	protected static String typeTransform(String type) {
		for (String[] types : TYPES) {
			if (type.equals(types[0]))
				return types[1];
		}
		return type;
	}

}
