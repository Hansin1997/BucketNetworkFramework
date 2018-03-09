package bucket.network.connection;

/**
 * 未知协议异常
 * 
 * @author Hansin
 *
 */
public class UnknowProtocolException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5253106957168456118L;

	public UnknowProtocolException() {
		super("未知协议");
	}

	public UnknowProtocolException(String str) {
		super(str);
	}
}
