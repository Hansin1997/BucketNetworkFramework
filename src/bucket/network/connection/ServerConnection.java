package bucket.network.connection;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Constructor;
import java.net.Socket;
import java.util.List;

import bucket.listener.EventListener;
import bucket.network.protocol.Protocol;

/**
 * 服务端连接类
 * 
 * @author Hansin1997
 * @version 2017/12/4
 */
public class ServerConnection extends Connection {

	/**
	 * 协议列表
	 */
	protected List<String> ProtocolList;

	/**
	 * 握手超时
	 */
	protected int handshakeTimeout;

	/**
	 * 通讯超时
	 */
	protected int timeout;

	public ServerConnection(List<String> ProtocolList, Socket s, EventListener listener) {
		setSocket(s);
		setListener(listener);
		this.ProtocolList = ProtocolList;
		try {
			in = new BufferedInputStream(socket.getInputStream());
			out = new BufferedOutputStream(socket.getOutputStream());
		} catch (Exception e) {
			if (listener != null)
				listener.onException(this, e);
			else
				e.printStackTrace();
		}
		this.handshakeTimeout = 2000;
		this.timeout = 0;
	}

	@Override
	public void run() {
		try {

			if (!autoSelct())
				throw new UnknowProtocolException("无法识别该协议");

			byte data[];
			while ((data = protocol.load()) != null) {
				if (listener != null)
					listener.onDataCome(this, data);
			}

		} catch (Throwable e) {
			if (listener != null)
				listener.onException(this, e);
			else
				e.printStackTrace();
		} finally {
			listener.onDisconnect(this);
			try {
				socket.close();
			} catch (IOException e) {
				System.out.println(e);
			}
		}

	}

	/**
	 * 自动选择协议并初始化
	 * 
	 * @return 是否成功
	 * @throws Throwable
	 */
	@SuppressWarnings({ "rawtypes" })
	private boolean autoSelct() throws Throwable {
		Protocol p;
		for (String className : ProtocolList) {
			try {
				Constructor cons = Class.forName(className).getConstructor(Socket.class, InputStream.class,
						OutputStream.class);
				p = (Protocol) cons.newInstance(socket, in, out);
				p.setServer(true);
				socket.setSoTimeout(handshakeTimeout);
				if (p.handshake()) {
					socket.setSoTimeout(timeout);
					setProtocol(p);
					listener.onConnect(this);
					return true;
				}

			} catch (Throwable e) {
				if (listener != null)
					listener.onException(this, e);
				else
					e.printStackTrace();
			}
		}
		return false;
	}

	/**
	 * 设置握手超时
	 * 
	 * @param handshakeTimeout
	 */
	public void setHandshakeTimeout(int handshakeTimeout) {
		this.handshakeTimeout = handshakeTimeout;
	}

	/**
	 * 获取握手超时
	 * 
	 * @return
	 */
	public int getHandshakeTimeout() {
		return handshakeTimeout;
	}

	/**
	 * 设置通讯超时
	 * 
	 * @param timeout
	 */
	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	/**
	 * 获取通讯超时
	 * 
	 * @return
	 */
	public int getTimeout() {
		return timeout;
	}
}
