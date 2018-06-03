package bucket.nio;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.util.Iterator;

/**
 * 定时反应堆
 */
public abstract class TimeEventLoop extends EventLoop {

	protected int time;

	public TimeEventLoop(int time) throws IOException {
		super();
		this.time = time;
	}

	@Override
	public void run() {
		int count;
		try {
			while (!Thread.interrupted()) {
				count = selector.select(time);// 同步阻塞等待事件到来
				if (count > 0) {
					Iterator<SelectionKey> iter = selector.selectedKeys().iterator();
					while (iter.hasNext()) {
						SelectionKey key = iter.next();
						iter.remove();
						dispatch(key);// 处理事件
					}
				}
				prossess();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
