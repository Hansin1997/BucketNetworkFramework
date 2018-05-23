package bucket.database.common.bmob;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bucket.database.BucketObject;
import bucket.database.Database;
import bucket.database.common.bmob.api.BmobApp;
import bucket.database.common.bmob.api.BmobConsole;
import bucket.database.common.mysql.MySQL;
import bucket.database.exception.ObjectNotFoundException;
import bucket.database.query.Query;

public class Bmob extends Database {

	private BmobConsole console;
	private boolean connected;
	private Map<String, BmobApp> databases;
	private BmobApp db;

	public Bmob() {
		super("", 0);
		databases = new HashMap<>();
	}

	/**
	 * 
	 * @param bmobEmail
	 *            Bmob邮箱账号
	 * @param bmobPassword
	 *            Bmob密码
	 */
	public Bmob(String bmobEmail, String bmobPassword) {
		this();
		console = new BmobConsole(bmobEmail, bmobPassword);
	}

	@Override
	public boolean isConnected() {
		// TODO Auto-generated method stub
		return connected;
	}

	@Override
	public void connect() throws Exception {

		List<BmobApp> apps = console.getApps();
		for (BmobApp app : apps)
			databases.put(app.getAppName(), app);
		connected = true;
	}

	/**
	 * 连接数据库
	 * 
	 * @param applicationId
	 *            应用id
	 * @param masterKey
	 *            应用masterKey
	 * @throws Exception
	 */
	@Override
	public void connect(String applicationId, String masterKey) throws Exception {

		db = new BmobApp();
		db.setApplicationId(applicationId);
		db.setMasterKey(masterKey);
		if (console != null)
			connect();

	}

	@Override
	public void close() {
		console = null;
		databases.clear();
		db = null;
		connected = false;
	}

	@Override
	public void useDb(String appName) throws Exception {
		if (databases != null) {
			db = databases.get(appName);
			if (db == null)
				throw new NullPointerException("BmobApp doesn't exist");
		}
	}

	@Override
	public <T extends BucketObject> List<T> find(Class<T> clazz, Query query, long limit) throws Exception {
		String tableName = ((query == null || query.table() == null) ? clazz.newInstance().getTableName()
				: query.table());
		String SQL = MySQL.Query2PreSQL(query, tableName, limit);
		if (SQL.length() > 1)
			SQL = SQL.substring(0, SQL.length() - 2);
		List<T> list = db.getObjectsBQL(SQL, MySQL.Query2ValueArray(query).toString(), clazz);
		for (T obj : list)
			obj.setDatabase(this);
		return list;
	}

	@Override
	public void insert(BucketObject obj) throws Exception {
		String id = db.addObject(obj.getTableName(), obj);
		if (id != null) {
			obj.setId(id);
		}
	}

	@Override
	public void remove(BucketObject obj) throws Exception {
		String msg = db.deleteObject(obj.getTableName(), obj.getId().toString());
		if (msg == null)
			throw new ObjectNotFoundException("删除失败");
	}

	@Override
	public void update(BucketObject obj) throws Exception {
		String ua = db.updateObject(obj.getTableName(), obj, obj.getId().toString());
		if (ua == null)
			throw new ObjectNotFoundException("更新失败");
	}

}
