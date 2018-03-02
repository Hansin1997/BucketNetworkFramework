package bucket.database;

/**
 * 数据库
 * 
 * @author Hansin1997
 * @version 2017/12/4
 */
public abstract class Database {

	public static final String TYPE_MONGO = "MONGO";
	public static final String TYPE_MYSQL = "MYSQL";

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

	/**
	 * 默认构造函数
	 * 
	 * @param host
	 *            数据库主机
	 * @param port
	 *            数据库端口
	 */
	public Database(String host, int port) {
		setDbHost(host);
		setDbPort(port);
	}

	/**
	 * 是否已连接数据库
	 * 
	 * @return 是否已连接
	 */
	public abstract boolean isConnected();

	/**
	 * 连接数据库
	 */
	public abstract void connect();

	/**
	 * 连接数据库
	 * 
	 * @param username
	 *            用户名
	 * @param password
	 *            密码
	 */
	public abstract void connect(String username, String password);

	/**
	 * 关闭数据库连接
	 */
	public abstract void close();

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

	/**
	 * 实例化一个BucketObject对象
	 * 
	 * @param clazz
	 *            BucketObject子类
	 * @return BucketObject对象
	 * @throws Exception
	 *             异常
	 */
	public <T extends BucketObject> T instantiate(Class<T> clazz) throws Exception {
		T result = clazz.getConstructor().newInstance();
		result.setDatabase(this);
		return result;
	}

	/**
	 * 选择数据库
	 * 
	 * @param databaseName
	 *            数据库名
	 */
	public abstract void useDb(String databaseName) throws Exception;

	/**
	 * 插入对象
	 * 
	 * @param obj
	 *            对象
	 * @throws Exception
	 *             异常
	 */
	public abstract void insert(BucketObject obj) throws Exception;

	/**
	 * 更新对象
	 * 
	 * @param obj
	 *            对象
	 * @throws Exception
	 *             异常
	 */
	public abstract void update(BucketObject obj) throws Exception;
}
