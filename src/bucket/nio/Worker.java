package bucket.nio;

import java.io.IOException;
import java.nio.channels.CancelledKeyException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * 工作反应堆
 */
public class Worker extends TimeEventLoop {

	/**
	 * 需要加入的SocketChannel队列
	 */
	Queue<SocketChannel> queue;
	/**
	 * 主反应堆
	 */
	Boss boss;

	public Worker(Boss boss, int time) throws IOException {
		super(time);
		queue = new ConcurrentLinkedQueue<>();
		this.boss = boss;
	}

	/**
	 * 加入SocketChannel
	 *
	 * @param socketChannel
	 */
	public synchronized void add(SocketChannel socketChannel) {
		queue.add(socketChannel);
		if (queue.size() > 0)
			selector.wakeup();
	}

	@Override
	public void prossess() {
		SocketChannel socketChannel;

		while ((socketChannel = queue.poll()) != null) {
			try {
				Handler handler = (Handler) boss.constructor.newInstance(socketChannel);
				socketChannel.register(selector, SelectionKey.OP_READ, handler);
			} catch (Exception e) {
				e.printStackTrace();
				try {
					socketChannel.close();
				} catch (IOException e1) {

				}
			}
		}
	}

	@Override
	public void dispatch(SelectionKey key) {
		SocketChannel channel = (SocketChannel) key.channel();
		try {
			Handler handler = (Handler) key.attachment();
			try {

				if (key.isReadable()) {
					handler.read(key);
				}

				if (key.isWritable()) {
					handler.write(key);
				}

			} catch (Exception e) {
				if (e instanceof CancelledKeyException)
					return;
				else
					handler.error(key, e);
			}

		} catch (Exception e) {
			e.printStackTrace();
			try {
				channel.close();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
	}
}
