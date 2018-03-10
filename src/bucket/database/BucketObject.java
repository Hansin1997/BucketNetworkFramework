package bucket.database;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Set;

/**
 * Bucket对象抽象类，实现对象与数据库操作
 * 
 * @author Hansin
 * 
 */
public abstract class BucketObject {

	/**
	 * 对应数据库的表名
	 */
	protected transient String tableName;

	/**
	 * 主键
	 */
	protected transient Object id;

	/**
	 * 所属数据库
	 */
	protected transient Database db;

	/**
	 * 默认构造函数
	 */
	public BucketObject() {
		setTableName(this.getClass().getSimpleName());
	}

	/**
	 * 获取数据库表名
	 * 
	 * @return 数据库表名
	 */
	public String getTableName() {
		return tableName;
	}

	/**
	 * 设置数据库表名(默认为本类不含包名的名字)
	 * 
	 * @param tableName
	 *            数据库表名
	 */
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	/**
	 * 设置所属数据库
	 * 
	 * @param db
	 *            所属数据库
	 */
	public void setDatabase(Database db) {
		this.db = db;
	}

	/**
	 * 获取所属数据库
	 * 
	 * @return 所属数据库
	 */
	public Database getDatabase() {
		return db;
	}

	/**
	 * 设置主键值
	 * 
	 * @param id
	 */
	public void setId(Object id) {
		this.id = id;
	}

	/**
	 * 获取主键值
	 * 
	 * @return 主键值
	 */
	public Object getId() {
		return id;
	}

	/**
	 * 获取属性
	 * 
	 * @return 属性表
	 * @throws Exception
	 *             异常
	 */
	public Map<String, Object> getFields() throws Exception {
		HashMap<String, Object> fields = new HashMap<String, Object>();
		Class<?> c = this.getClass();
		Field[] fs = c.getFields();
		for (Field f : fs) {

			// if (f.isAccessible())
			fields.put(f.getName(), f.get(this));
		}

		return fields;
	}

	/**
	 * 设置属性
	 * 
	 * @param fields
	 *            属性
	 * @throws Exception
	 *             异常
	 */
	public void setFields(Map<String, Object> fields) throws Exception {
		Class<?> c = this.getClass();
		Field[] fs = c.getFields();
		for (Field f : fs) {
			f.set(this, fields.get(f.getName()));
		}
	}

	/**
	 * 打印信息
	 */
	public void print() {
		System.out.println("-----------------------------------");
		System.out.println(tableName);
		System.out.println(" * id:\t" + id);
		System.out.println(" * Fields:");

		try {
			Map<String, Object> fs = getFields();
			Set<Entry<String, Object>> set = fs.entrySet();

			for (Entry<String, Object> kv : set) {
				System.out.println("    * " + kv.getKey() + ":\t" + kv.getValue());
			}

		} catch (Exception e) {
			System.out.println("    * Expection!");
		}
		System.out.println("-----------------------------------");
	}

	/**
	 * 保存对象(若对象在所属数据库不存在则插入)
	 * 
	 * @throws Exception
	 *             异常
	 */
	public void save() throws Exception {
		if (db == null) {
			throw new DatabaseConnectionException("db is null!");
		}

		if (this.id == null) {
			db.insert(this);
		} else {
			db.update(this);
		}

	}

	/**
	 * 删除对象
	 * 
	 * @throws Exception
	 *             异常
	 */
	public void remove() throws Exception {
		if (db == null) {
			throw new DatabaseConnectionException("db is null!");
		}

		db.remove(this);
	}

	public String toJSON() {
		Gson gson = new GsonBuilder().create();
		return gson.toJson(this);
	}

}
