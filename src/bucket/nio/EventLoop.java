package bucket.nio;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Iterator;

/**
 * 反应堆
 */
public abstract class EventLoop implements Runnable {

	/**
	 * 选择器
	 */
	protected Selector selector;

	public EventLoop() throws IOException {
		selector = Selector.open();
	}

	@Override
	public void run() {
		int count;
		try {
			while (!Thread.interrupted()) {
				count = selector.select();// 同步阻塞等待事件到来
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

	/**
	 * 获取Selector
	 * 
	 * @return
	 */
	public Selector getSelector() {
		return selector;
	}

	/**
	 * 处理额外事件
	 */
	public void prossess() {

	}

	/**
	 * 处理Channel事件
	 * 
	 * @param key
	 */
	public abstract void dispatch(SelectionKey key);

}
