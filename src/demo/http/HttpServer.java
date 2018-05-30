package demo.http;

import java.net.ServerSocket;

import bucket.network.Server;
import bucket.network.protocol.HttpProtocol;
import bucket.util.Log;

/**
 * Http测试Demo
 * 
 * @author Hansin1997
 *
 */
public class HttpServer extends Server {

	private String wwwroot;
	private String[] defaultFile;

	/**
	 * 入口方法（测试）
	 * 
	 * @param args
	 * @throws Throwable
	 */
	public static void main(String[] args) throws Throwable {
		Log.setDebugging(true);// 开启调试模式

		String wwwroot = "WWWROOT";// 网页根目录
		String[] defaultFile = new String[] { "index.htm", "index.html", "index.php", "default.html" };// 默认文件
		int port = 8080;// 监听端口

		HttpServer server = new HttpServer();
		server.setWwwroot(wwwroot);
		server.setDefaultFile(defaultFile);
		server.start(new ServerSocket(port));

	}

	public HttpServer() {
		super();
		setAppClass(HttpApplication.class);// 设置Application
		addProtocol(HttpProtocol.class);// 添加协议
	}

	public void setWwwroot(String wwwroot) {
		this.wwwroot = wwwroot;
	}

	public String getWwwroot() {
		return wwwroot;
	}

	public void setDefaultFile(String[] defaultFile) {
		this.defaultFile = defaultFile;
	}

	public String[] getDefaultFile() {
		return defaultFile;
	}

}
