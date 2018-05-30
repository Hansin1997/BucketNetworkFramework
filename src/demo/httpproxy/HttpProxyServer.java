package demo.httpproxy;

import java.net.ServerSocket;

import bucket.network.Server;
import bucket.network.protocol.HttpProxyProtocol;
import bucket.util.Log;

/**
 * Http代理测试Demo
 * 
 * @author Hansin1997
 *
 */
public class HttpProxyServer extends Server {

	/**
	 * 入口方法（测试）
	 * 
	 * @param args
	 * @throws Throwable
	 */
	public static void main(String[] args) throws Throwable {

		Log.setDebugging(true);// 开启调试模式

		ServerSocket serverSocket = new ServerSocket(6666);// 创建ServerSocket,绑定6666端口

		HttpProxyServer s = new HttpProxyServer();// 创建服务

		s.start(serverSocket);// 启动监听

	}

	public HttpProxyServer() {
		super();
		setAppClass(HttpProxyApplication.class);// HttpProxyApplication
		addProtocol(HttpProxyProtocol.class);// 添加HttpProxyProtocol协议
	}
}
