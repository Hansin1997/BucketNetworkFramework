package bucket.nio;

import java.lang.reflect.Constructor;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

/**
 * 客户接收器
 */
public class Acceptor implements Runnable {

	protected Constructor<?> constuctor;
	protected ServerSocketChannel serverSocketChannel;
	protected Boss boss;

	public Acceptor(Boss boss) throws NoSuchMethodException {
		this.serverSocketChannel = boss.serverSocketChannel;
		this.boss = boss;
		constuctor = boss.handlerClass.getConstructor(SocketChannel.class);
	}

	@Override
	public void run() {
		try {
			SocketChannel socketChannel = serverSocketChannel.accept();
			if (socketChannel != null) {
				socketChannel.configureBlocking(false);
				Worker worker = boss.getWorker();// 从主反应堆获取一个Worker
				worker.add(socketChannel);// 将客户加入worker的待处理队列
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
