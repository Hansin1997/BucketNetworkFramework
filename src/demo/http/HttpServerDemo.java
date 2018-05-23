package demo.http;

import java.io.FileInputStream;
import java.net.ServerSocket;
import java.security.KeyStore;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;

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

		ServerSocket serverSocket = getServerSocket(443);

		Server s = new Server(HttpApplicationDemo.class.getName());
		s.addProtocol(HttpProtocol.class);
		s.start(serverSocket);

	}

	private static SSLServerSocket getServerSocket(int thePort) {
		SSLServerSocket s = null;
		try {
			String key = "keystore/SSLKey"; // 要使用的证书名
			char keyStorePass[] = "123456".toCharArray(); // 证书密码
			char keyPassword[] = "123456".toCharArray(); // 证书别称所使用的主要密码
			KeyStore ks = KeyStore.getInstance("JKS"); // 创建JKS密钥库
			ks.load(new FileInputStream(key), keyStorePass);
			// 创建管理JKS密钥库的X.509密钥管理器
			KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
			kmf.init(ks, keyPassword);
			SSLContext sslContext = SSLContext.getInstance("SSLv3");
			sslContext.init(kmf.getKeyManagers(), null, null);

			// 根据上面配置的SSL上下文来产生SSLServerSocketFactory,与通常的产生方法不同
			SSLServerSocketFactory factory = sslContext.getServerSocketFactory();
			s = (SSLServerSocket) factory.createServerSocket(thePort);
		} catch (Exception e) {
			System.out.println(e);
		}
		return (s);
	}

}
