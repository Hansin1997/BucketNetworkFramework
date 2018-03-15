package demo.http;

import bucket.network.Server;
import bucket.network.protocol.HttpProtocol;

/**
 * Http测试Demo
 * 
 * @author Hansin1997
 *
 */
public class HttpServerDemo {

	public static void main(String[] args) throws Throwable {


		Server s = new Server(HttpApplicationDemo.class.getName(), 8080);
		s.addProtocol(HttpProtocol.class);
		s.start();

	}
}
