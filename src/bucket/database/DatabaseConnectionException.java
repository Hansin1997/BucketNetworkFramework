package bucket.database;

/**
 * 数据库连接异常
 * 
 * @author Hansin
 *
 */
public class DatabaseConnectionException extends Exception {

	/**
	 * 自动生成
	 */
	private static final long serialVersionUID = 7320880677609625216L;

	public DatabaseConnectionException() {
		super();
	}

	public DatabaseConnectionException(String str) {
		super(str);
	}
}
