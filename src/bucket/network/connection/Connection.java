package bucket.network.connection;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import bucket.listener.EventListener;
import bucket.network.protocol.Protocol;

/**
 * 连接类
 * 
 * @author Hansin
 *
 */
public abstract class Connection implements Runnable {

	/**
	 * 协议对象
	 */
	protected Protocol protocol;

	/**
	 * 套接字对象
	 */
	protected Socket socket;

	/**
	 * 输入流
	 */
	protected InputStream in;

	/**
	 * 输出流
	 */
	protected OutputStream out;

	/**
	 * 事件监听器
	 */
	protected EventListener listener;

	/**
	 * 设置套接字对象
	 * 
	 * @param socket
	 */
	public void setSocket(Socket socket) {
		this.socket = socket;
	}

	/**
	 * 获取套接字对象
	 * 
	 * @return
	 */
	public Socket getSocket() {
		return socket;
	}

	/**
	 * 获取输入流
	 * 
	 * @return
	 */
	public InputStream getIn() {
		return in;
	}

	/**
	 * 获取输出流
	 * 
	 * @return
	 */
	public OutputStream getOut() {
		return out;
	}

	/**
	 * 设置输入流
	 * 
	 * @param in
	 */
	public void setIn(InputStream in) {
		this.in = in;
	}

	/**
	 * 设置输出流
	 * 
	 * @param out
	 */
	public void setOut(OutputStream out) {
		this.out = out;
	}

	/**
	 * 设置协议对象
	 * 
	 * @param protocol
	 */
	public void setProtocol(Protocol protocol) {
		this.protocol = protocol;
	}

	/**
	 * 获取协议对象
	 * 
	 * @return
	 */
	public Protocol getProtocol() {
		return protocol;
	}

	/**
	 * 设置监听器对象
	 * 
	 * @param listener
	 */
	public void setListener(EventListener listener) {
		this.listener = listener;
	}

	/**
	 * 获取监听器对象
	 * 
	 * @return
	 */
	public EventListener getListener() {
		return listener;
	}

	/**
	 * 发送数据
	 * 
	 * @param data
	 * @throws Throwable
	 */
	public void send(byte[] data) throws Throwable {
		getProtocol().send(data);
	}

}
