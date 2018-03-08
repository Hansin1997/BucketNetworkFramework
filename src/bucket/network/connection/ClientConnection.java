package bucket.network.connection;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;

import bucket.listener.EventListener;
import bucket.network.protocol.Protocol;

/**
 * 客户端连接类
 * 
 * @author Hansin1997
 * @version 2018/3/8
 */
public class ClientConnection extends Connection {

	public ClientConnection(Protocol Protocol, EventListener listener) {
		setListener(listener);
		setProtocol(Protocol);
		setSocket(Protocol.getSocket());
		try {
			in = new BufferedInputStream(this.socket.getInputStream());
			out = new BufferedOutputStream(this.socket.getOutputStream());
		} catch (Exception e) {
			if (listener != null)
				listener.onException(this, e);
			else
				e.printStackTrace();
		}
	}

	@Override
	public void run() {
		try {
			Protocol p = getProtocol();
			if (!p.handshake())
				throw new Exception("握手失败");

			byte data[];
			while ((data = protocol.load()) != null) {
				listener.onDataCome(this, data);
			}

		} catch (Throwable e) {
			if (listener != null)
				listener.onException(this, e);
			else
				e.printStackTrace();
		} finally {
			if (listener != null)
				listener.onDisconnect(this);
			else
				try {
					socket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}

	}

}
