package demo.httpproxy;

import java.net.ServerSocket;

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

		ServerSocket serverSocket = new ServerSocket(8080);

		// 创建服务，绑定HttpProxyApplicationDemo以及6666端口
		Server s = new Server(HttpProxyApplicationDemo.class.getName());

		// 添加HttpProxyProtocol协议
		s.addProtocol(HttpProxyProtocol.class);

		// 监听
		s.start(serverSocket);

	}
}
