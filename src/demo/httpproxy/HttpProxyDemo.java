package demo.httpproxy;

import bucket.network.Server;
import bucket.network.protocol.HttpProxyProtocol;

/**
 * Http代理测试Demo
 * 
 * @author Hansin1997
 *
 */
public class HttpProxyDemo {

	public static void main(String[] args) throws Throwable {

		// 创建服务，绑定HttpProxyApplicationDemo以及6666端口
		Server s = new Server(HttpProxyApplicationDemo.class.getName(), 6666);

		// 添加HttpProxyProtocol协议
		s.addProtocol(HttpProxyProtocol.class);

		// 监听
		s.start();

	}
}
