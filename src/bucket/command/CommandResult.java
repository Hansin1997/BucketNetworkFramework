package bucket.command;

/**
 * 回调类
 * 
 * @author Hansin
 *
 */
public class CommandResult {

	/**
	 * 成功
	 */
	public static final int SUCCESS = 200;

	/**
	 * 错误
	 */
	public static final int ERROR = 100;

	/**
	 * 禁止
	 */
	public static final int FORBIDDEN = 403;

	/**
	 * 忙碌
	 */
	public static final int BUSY = 503;

	/**
	 * 未知响应
	 */
	public static final int UNKNOW = 0;

	/**
	 * 回调状态
	 */
	public int status;

	/**
	 * 消息
	 */
	public String msg;

	/**
	 * 附带数据
	 */
	public Object data;

	public CommandResult() {

	}

	public CommandResult(int status, String msg, Object data) {
		setStatus(status);
		setMsg(msg);
		setData(data);
	}

	public void setData(Object data) {
		this.data = data;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public Object getData() {
		return data;
	}

	public String getMsg() {
		return msg;
	}

	public int getStatus() {
		return status;
	}

	public static CommandResult SUCCESS() {
		CommandResult r = new CommandResult();
		r.setStatus(SUCCESS);
		return r;
	}

	public static CommandResult SUCCESS(String msg) {
		CommandResult r = SUCCESS();
		r.setMsg(msg);
		return r;
	}

	public static CommandResult SUCCESS(Object data) {
		CommandResult r = SUCCESS();
		r.setData(data);
		return r;
	}

	public static CommandResult SUCCESS(String msg, Object data) {
		CommandResult r = new CommandResult(SUCCESS, msg, data);
		return r;
	}

	public static CommandResult FORBIDDEN() {
		CommandResult r = new CommandResult();
		r.setStatus(FORBIDDEN);
		return r;
	}

	public static CommandResult FORBIDDEN(String msg) {
		CommandResult r = FORBIDDEN();
		r.setMsg(msg);
		return r;
	}

	public static CommandResult FORBIDDEN(Object data) {
		CommandResult r = FORBIDDEN();
		r.setData(data);
		return r;
	}

	public static CommandResult FORBIDDEN(String msg, Object data) {
		CommandResult r = new CommandResult(FORBIDDEN, msg, data);
		return r;
	}

	public static CommandResult BUSY() {
		CommandResult r = new CommandResult();
		r.setStatus(BUSY);
		return r;
	}

	public static CommandResult BUSY(String msg) {
		CommandResult r = BUSY();
		r.setMsg(msg);
		return r;
	}

	public static CommandResult BUSY(Object data) {
		CommandResult r = BUSY();
		r.setData(data);
		return r;
	}

	public static CommandResult BUSY(String msg, Object data) {
		CommandResult r = new CommandResult(BUSY, msg, data);
		return r;
	}

	public static CommandResult UNKNOW() {
		CommandResult r = new CommandResult();
		r.setStatus(UNKNOW);
		return r;
	}

	public static CommandResult UNKNOW(String msg) {
		CommandResult r = UNKNOW();
		r.setMsg(msg);
		return r;
	}

	public static CommandResult UNKNOW(Object data) {
		CommandResult r = UNKNOW();
		r.setData(data);
		return r;
	}

	public static CommandResult UNKNOW(String msg, Object data) {
		CommandResult r = new CommandResult(UNKNOW, msg, data);
		return r;
	}

	public static CommandResult ERROR() {
		CommandResult r = new CommandResult();
		r.setStatus(ERROR);
		return r;
	}

	public static CommandResult ERROR(String msg) {
		CommandResult r = ERROR();
		r.setMsg(msg);
		return r;
	}

	public static CommandResult ERROR(Object data) {
		CommandResult r = ERROR();
		r.setData(data);
		return r;
	}

	public static CommandResult ERROR(String msg, Object data) {
		CommandResult r = new CommandResult(ERROR, msg, data);
		return r;
	}

}
