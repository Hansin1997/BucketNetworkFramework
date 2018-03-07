package demo;

import bucket.network.Server;
import bucket.network.connection.ServerConnection;
import bucket.network.protocol.HttpProtocol;

/**
 * Http测试Demo
 * 
 * @author Hansin1997
 *
 */
public class HttpServerDemo {

	public static void main(String[] args) throws Throwable {

		ServerConnection.ProtocolList.add(HttpProtocol.class.getName());

		Server s = new Server(HttpApplicationDemo.class.getName(), 8080);
		s.start();

	}
}
