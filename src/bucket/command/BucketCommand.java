package bucket.command;

/**
 * Bucket命令类 本类都有execute()方法用作命令执行
 * 
 * @author Hansin1997
 * @version 2017/12/4
 */
public abstract class BucketCommand {

	/**
	 * 命令名
	 */
	public String command;

	/**
	 * 命令数据
	 */
	public Object value;

	/**
	 * 执行
	 */
	public abstract void execute();

	public String getCommand() {
		return command;
	}

	public Object getValue() {
		return value;
	}

	public void setCommand(String command) {
		this.command = command;
	}

	public void setValue(Object value) {
		this.value = value;
	}
}
