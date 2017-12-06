package bucket.application;

import bucket.listener.EventListener;
import bucket.network.protocol.Server;

/**
 * 应用类 服务接受的每一个客户都将创建一个Application对象进行事件处理
 * 
 * @author Hansin1997
 * @version 2017/12/4
 */
public abstract class Application implements EventListener {

	protected Server server;

	public Application(Server server) {
		this.server = server;
	}

}
