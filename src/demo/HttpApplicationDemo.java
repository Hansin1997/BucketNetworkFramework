package demo;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;

import bucket.application.Application;
import bucket.network.Server;
import bucket.network.connection.ServerConnection;
import bucket.network.protocol.HttpProtocol;

/**
 * HTTP应用示例
 * 
 * @author Hansin
 *
 */
public class HttpApplicationDemo extends Application {

	/**
	 * 默认文档目录
	 */
	public static String wwwroot = "D:\\Documents\\git\\stream-watch-web";

	/**
	 * 默认文件后缀
	 */
	public static String[] defaultFile = { "index.htm", "index.html", "index.php", "default.html" };

	/**
	 * 构造方法
	 * 
	 * @param server
	 *            传入Server对象
	 */
	public HttpApplicationDemo(Server server) {
		super(server);
	}

	@Override
	public void onConnect(ServerConnection connection) {
		// 新的HTTP连接建立时，触发此方法

		HttpProtocol hp = (HttpProtocol) connection.getProtocol();// 将连接协议对象转换为HTTP协议

		String path = wwwroot + hp.getProtocolInfo().get("PATH"); // 获取请求的本地路径

		System.out.println(hp.toString());

		File f = new File(path);

		// 当请求路径为目录时，寻找默认文档
		if (f.isDirectory()) {

			for (int i = 0; ((!f.exists() || f.isDirectory()) && i < defaultFile.length); i++)
				f = new File(path + "/" + defaultFile[i]);// 遍历可用默认文档
		}

		try {

			if (f.exists()) {
				// 当文件存在时产生的响应

				// 发送 Http 200 状态
				hp.parseServerHeader("200 OK", null);

				// 发送数据
				BufferedInputStream in = new BufferedInputStream(new FileInputStream(f));
				int b;
				while ((b = in.read()) != -1)
					hp.write(b);
				in.close();
			} else {
				// 当文件不存在时产生的响应

				// 发送 Http 404 状态
				hp.parseServerHeader("404 Not Found", null);
				hp.send("<h1>404 Not Found</h1><p>BucketNetworkFramework v0.1</p>".getBytes());
			}

		} catch (Throwable e) {
			onException(connection, e);
		}

		try {
			hp.flush();
			hp.getSocket().close();
		} catch (Throwable e) {
			e.printStackTrace();
		}

	}

	@Override
	public void onDataCome(ServerConnection connection, byte[] data) {

	}

	@Override
	public void onDisconnect(ServerConnection conn) {

		// 连接断开时从线程池释放资源
		server.remove(conn);

	}

	@Override
	public void onException(ServerConnection connection, Throwable e) {
		// 打印异常信息
		e.printStackTrace();
	}

}
