package bucket.database;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import bucket.database.exception.DatabaseConnectionException;

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
		return getAllFields(this, getClass());
	}

	public static Map<String, Object> getAllFields(Object obj, Class<?> clazz)
			throws IllegalArgumentException, IllegalAccessException {
		HashMap<String, Object> fields = new HashMap<String, Object>();
		Class<?> c = clazz;

		Field[] fs = c.getDeclaredFields();
		for (Field f : fs) {
			if (clazz.equals(BucketObject.class))
				break;
			f.setAccessible(true);

			if (!Modifier.isStatic(f.getModifiers()) && f.get(obj) != null)
				fields.put(f.getName(), f.get(obj));
		}
		if (!clazz.equals(BucketObject.class))
			fields.putAll(getAllFields(obj, clazz.getSuperclass()));
		return fields;
	}

	public static ArrayList<Field> getAllFields(Class<?> clazz)
			throws IllegalArgumentException, IllegalAccessException {
		ArrayList<Field> ls = new ArrayList<>();
		Class<?> c = clazz;

		Field[] fs = c.getDeclaredFields();
		for (Field f : fs) {
			if (clazz.equals(BucketObject.class))
				break;
			f.setAccessible(true);

			if (!Modifier.isStatic(f.getModifiers()))
				ls.add(f);
		}
		if (!clazz.equals(BucketObject.class))
			ls.addAll(getAllFields(clazz.getSuperclass()));
		return ls;
	}

	public static Field getField(String key, Class<?> clazz) {
		try {
			return clazz.getField(key);
		} catch (NoSuchFieldException e) {
			try {
				return clazz.getDeclaredField(key);
			} catch (NoSuchFieldException e1) {
				if (clazz.equals(BucketObject.class)) {
					return null;
				} else
					return getField(key, clazz.getSuperclass());
			}
		}
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

		Gson gson = getGson();
		Set<Entry<String, Object>> set = fields.entrySet();
		for (Entry<String, Object> kv : set) {

			if (kv.getValue() == null)
				continue;
			Field f = getField(kv.getKey(), getClass());
			if (f != null) {
				f.setAccessible(true);

				if (kv.getValue().getClass().equals(String.class) && !f.getType().equals(String.class))
					f.set(this, gson.fromJson(kv.getValue().toString(), f.getType()));
				else
					try {
						f.set(this, kv.getValue());
					} catch (IllegalArgumentException e) {
						f.set(this, gson.fromJson(kv.getValue().toString(), f.getType()));
					}
			}
		}
	}

	/**
	 * 打印信息
	 */
	public void print() {
		System.out.println("-----------------------------------");
		System.out.println(tableName);
		System.out.println(" * id:\t" + getId());
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

		if (this.getId() == null) {
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

	@Override
	public String toString() {
		return toJSON();
	}

	public Gson getGson() {
		return new Gson();
	}

	/**
	 * 获取属性表，包括ID
	 * 
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> getMapWithID() throws Exception {
		Map<String, Object> map = getFields();
		Object iidd = getId();
		if (iidd != null) {
			if (iidd instanceof Integer)
				map.put("id", iidd);
			else
				map.put("id", iidd.toString());
		}
		return map;
	}

}
