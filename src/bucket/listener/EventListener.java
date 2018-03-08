package bucket.listener;

import bucket.network.connection.Connection;

/**
 * 事件监听器
 * 
 * @author Hansin1997
 * @version 2017/12/4
 */
public interface EventListener {

	/**
	 * 握手完成后调用此方法
	 * 
	 * @param protocol
	 *            参数为协议对象
	 */
	void onConnect(Connection connection);

	/**
	 * 数据到达时调用此方法
	 * 
	 * @param data
	 *            参数为数据
	 */
	void onDataCome(Connection connection, byte data[]);

	/**
	 * 连接结束时调用此方法
	 * 
	 * @param protocol
	 *            参数为协议对象
	 */
	void onDisconnect(Connection connection);

	/**
	 * 发生异常时调用此方法
	 * 
	 * @param e
	 *            参数为异常对象
	 */
	void onException(Connection connection, Throwable e);
}
