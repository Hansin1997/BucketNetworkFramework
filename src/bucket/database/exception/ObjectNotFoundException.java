package bucket.database.exception;

/**
 * 对象未找到异常，当操作的BucketObject在数据库不存在时触发
 * 
 * @author Hansin
 *
 */
public class ObjectNotFoundException extends Exception {

	/**
	 * 自动生成
	 */
	private static final long serialVersionUID = 1887225894554105827L;

	public ObjectNotFoundException() {
		super();
	}

	public ObjectNotFoundException(String str) {
		super(str);
	}

}
