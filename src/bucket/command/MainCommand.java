package bucket.command;

/**
 * 入口命令
 * 
 * @author Hansin
 *
 */
public class MainCommand extends ExecutableCommand {

	/**
	 * 执行原子命令包名 （com.xx.）
	 */
	protected transient String packageName;

	/**
	 * 原子命令类名
	 */
	public String command;

	/**
	 * 公开的传输标识,用于回调
	 */
	public long id;

	public MainCommand() {

	}

	public MainCommand(String packageName) {
		setPackageName(packageName);
	}

	@Override
	public void execute() {
		try {
			String className = packageName + "." + this.command;
			className = className.replace("..", ".");
			Class<?> c = Class.forName(className);
			ExecutableCommand cmd = (ExecutableCommand) c.newInstance();
			cmd.value = this.value;
			cmd.setId(this.getId());
			cmd.setConnection(this.connection);
			cmd.setApplication(this.application);
			cmd.execute();
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	public String getCommand() {
		return command;
	}

	public void setCommand(String command) {
		this.command = command;
	}

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}


	public void setId(long sign) {
		// TODO Auto-generated method stub
		this.id = sign;
	}


	public long getId() {
		// TODO Auto-generated method stub
		return this.id;
	}
}
