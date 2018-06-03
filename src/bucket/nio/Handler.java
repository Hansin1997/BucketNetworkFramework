package bucket.nio;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

/**
 * 业务Handler
 */
public abstract class Handler {

	protected SocketChannel socketChannel;

	public Handler(SocketChannel socketChannel) {
		this.socketChannel = socketChannel;
	}

	/**
	 * 读事件
	 * 
	 * @param key
	 * @throws IOException
	 */
	public abstract void read(SelectionKey key) throws IOException;

	/**
	 * 写事件
	 * 
	 * @param key
	 * @throws IOException
	 */
	public abstract void write(SelectionKey key) throws IOException;

	/**
	 * 异常事件
	 * 
	 * @param key
	 * @param e
	 * @throws IOException
	 */
	public abstract void error(SelectionKey key, Exception e) throws IOException;

	public SocketChannel getSocketChannel() {
		return socketChannel;
	}
}
