package bucket.nio;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 主反应堆
 */
public class Boss extends EventLoop {

	Constructor<?> constructor; // Handler的构造器
	protected ExecutorService pool; // worker线程池
	protected ServerSocketChannel serverSocketChannel; // ServerSocket通道
	protected Worker[] workers; // 工作反应堆数组
	protected int workerSign; // 下一个被使用的工作反应堆数组下标
	protected Acceptor acceptor; // 客户接收器
	protected Class<?> handlerClass; // Handler类

	/**
	 * 主反应堆
	 *
	 * @param inetAddress
	 *            监听地址
	 * @param workerNum
	 *            工作反应堆数量
	 * @param handlerClass
	 *            业务类
	 * @param <T>
	 *            业务类泛型
	 * @throws IOException
	 * @throws NoSuchMethodException
	 */
	public <T extends Handler> Boss(InetSocketAddress inetAddress, int workerNum, Class<T> handlerClass)
			throws IOException, NoSuchMethodException {
		super();
		serverSocketChannel = ServerSocketChannel.open();
		serverSocketChannel.bind(inetAddress);

		this.handlerClass = handlerClass;
		constructor = handlerClass.getConstructor(SocketChannel.class);
		acceptor = new Acceptor(this);

		pool = Executors.newFixedThreadPool(workerNum);
		workers = new Worker[workerNum];
		workerSign = 0;
		for (int i = 0; i < workerNum; i++) {
			workers[i] = new Worker(this, 1000);
			pool.execute(workers[i]);
		}
		serverSocketChannel.configureBlocking(false);
		serverSocketChannel.register(getSelector(), SelectionKey.OP_ACCEPT, acceptor);
	}

	@Override
	public void dispatch(SelectionKey key) {
		((Runnable) key.attachment()).run();
	}

	/**
	 * 提取一个Worker 简单的负载均衡
	 *
	 * @return
	 */
	public Worker getWorker() {
		if (workerSign >= workers.length)
			workerSign = 0;
		return workers[workerSign++];
	}
}
