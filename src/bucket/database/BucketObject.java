package bucket.database;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
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
	public String tableName;

	/**
	 * 主键
	 */
	public long id;

	/**
	 * 所属数据库
	 */
	protected Database db;

	/**
	 * 默认构造函数
	 */
	public BucketObject() {
		if (tableName == null)
			tableName = this.getClass().getSimpleName();

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
	 * 获取属性
	 * 
	 * @return 属性表
	 * @throws Exception 异常
	 */
	public Map<String, Object> getFields() throws Exception {
		HashMap<String, Object> fields = new HashMap<String, Object>();
		Class<?> c = this.getClass();
		Field[] fs = c.getFields();
		for (Field f : fs) {
			if (f.isAccessible())
				fields.put(f.getName(), f.get(this));
		}

		return fields;
	}

	public void print() {
		System.out.println(tableName);
		System.out.println("---id\t" + id);
		System.out.println("---Fields:");

		try {
			Map<String, Object> fs = getFields();
			Set<Entry<String, Object>> set = fs.entrySet();

			for (Entry<String, Object> kv : set) {
				System.out.println(" ** " + kv.getKey() + ":\t" + kv.getValue());
			}

		} catch (Exception e) {
			System.out.println(" ** Expection!");
		}

	}

}
