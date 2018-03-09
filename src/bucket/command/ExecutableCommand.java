package bucket.command;

import bucket.application.Application;
import bucket.network.connection.Connection;

/**
 * 执行原子操作的命令
 * 
 * @author Hansin
 *
 */
public abstract class ExecutableCommand extends BucketCommand {

	/**
	 * 附带数据
	 */
	protected Object value;

	/**
	 * 上下文Application对象
	 */
	protected transient Application application;

	/**
	 * 上下文Connection对象
	 */
	protected transient Connection connection;

	/**
	 * 隐藏的传输标识，用于回调
	 */
	public transient long id;

	public Application getApplication() {
		return application;
	}

	public void setApplication(Application application) {
		this.application = application;
	}

	public Connection getConnection() {
		return connection;
	}

	public void setConnection(Connection connection) {
		this.connection = connection;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public void setId(long sign) {
		this.id = sign;
	}

	public long getId() {
		return id;
	}

}
