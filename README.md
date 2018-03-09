# Bucket网络应用框架
### 一个简单易用的网络应用框架

> 简单、快速地搭建一个网络服务，如：
>* 普通Socket服务:实时在线聊天，在线网络游戏等。
>* 简易Http服务:Web程序,HTTP代理等。
>* WebSocket服务:实时视频弹幕，数据实时监控等。
	
## 示例

### 创建一个Http服务

#### Application类

 应用类 服务接受的每一个客户都将创建一个Application对象进行事件处理。
>* 接口方法:
>
>* void onConnect(Connection connection); //握手完成后调用此方法
>* void onDataCome(Connection connection, byte data[]); //数据到达时调用此方法
>* void onDisconnect(Connection connection); //连接结束时调用此方法
>* void onException(Connection connection, Throwable e); //发生异常时调用此方法

示例:
```java
package demo;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.Date;

import bucket.application.Application;
import bucket.network.Server;
import bucket.network.connection.Connection;
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
	public void onConnect(Connection connection) {
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
				hp.send(("<div align='center'><h1>404 Not Found</h1><p>BNF v0.1</p><p>" + new Date() + "<p></div>")
						.getBytes());
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
	public void onDataCome(Connection connection, byte[] data) {

	}

	@Override
	public void onDisconnect(Connection conn) {

		// 连接断开时从线程池释放资源
		server.remove(conn);

	}

	@Override
	public void onException(Connection connection, Throwable e) {
		// 打印异常信息
		if (e.getClass().equals(SocketException.class) || e.getClass().equals(SocketTimeoutException.class))
			onDisconnect(connection);
		else
			e.printStackTrace();
	}

}
```
#### 创建HTTP服务

Application类只是规划了由客户产生的各种事件的行为蓝图，接下来时如何创建一个服务。
```java
package demo;

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

		//创建一个Server，绑定HttpApplicationDemo类，以及8080端口
		Server s = new Server(HttpApplicationDemo.class.getName(), 8080);
		
		//为Server添加支持的协议
		s.addProtocol(HttpProtocol.class);
		
		//启动服务，开始监听
		s.start();

	}
}
```


