package demo.httpproxy;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;

import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.List;
import java.util.Map.Entry;

import bucket.application.Application;
import bucket.network.Server;
import bucket.network.connection.Connection;
import bucket.network.connection.UnknowProtocolException;
import bucket.network.protocol.Protocol;
import bucket.util.Log;

/**
 * Http代理应用Demo
 * 
 * @author Hansin
 *
 */
public class HttpProxyApplication extends Application {

	/**
	 * 中转等待标志
	 */
	protected boolean flag;
	Socket conn;
	Protocol pro;
	BufferedInputStream in;
	BufferedOutputStream out;

	public HttpProxyApplication(Server server) {
		super(server);
	}

	@Override
	public void onConnect(Connection connection) {
		Log.d("代理用户连接    " + connection.getProtocol());
		try {
			String method = connection.getProtocol().getProtocolInfo().get("METHOD").toString();
			// 分析代理请求方法
			switch (method) {
			case "CONNECT":
				CONNECT(connection);
				break;
			default:
				GET(connection, method);
			}
		} catch (Throwable e) {

			e.printStackTrace();
		} finally {
			try {
				connection.getProtocol().flush();
			} catch (Throwable e) {
			}
		}

		onDisconnect(connection);
	}

	/**
	 * TCP代理（适用于HTTPS等加密方式）
	 * 
	 * @param connection
	 *            连接对象
	 * @throws Throwable
	 */
	protected void CONNECT(final Connection connection) throws Throwable {
		// 分析中转地址与端口
		String[] hp = (connection.getProtocol().getProtocolInfo().get("PATH").toString().split(":", 2));
		String host = hp[0];
		int port = (hp.length > 1 ? Integer.parseInt(hp[1]) : 443);

		// 建立中转连接
		conn = new Socket(host, port);

		in = new BufferedInputStream(conn.getInputStream());
		out = new BufferedOutputStream(conn.getOutputStream());

		// 输出代理成功信息
		connection.getProtocol().send("HTTP/1.1 200 Connection Established\r\n\r\n");

		flag = false; // 设置标志
		new Thread() {
			public void run() {
				int b;
				// 进行输出中转
				try {
					byte[] data = new byte[1024];
					while ((b = connection.getProtocol().read(data, 0, data.length)) != -1) {
						out.write(data, 0, b);
						out.flush();
					}
					conn.shutdownOutput();

				} catch (Throwable e) {

				}

			};
		}.start();

		new Thread() {
			public void run() {
				int b;
				// 进行输入中转
				try {
					byte[] data = new byte[1024];
					while ((b = in.read(data, 0, data.length)) != -1) {
						connection.getProtocol().write(data, 0, b);
						connection.getProtocol().flush();
					}
					connection.getSocket().shutdownOutput();

				} catch (Throwable e) {

				}
				flag = true;

			};
		}.start();

		// 等待连接标志结束
		while (!flag)
			Thread.sleep(10);

		conn.close();// 关闭中转连接

	}

	/**
	 * 普通HTTP代理
	 * 
	 * 
	 * @param connection
	 *            连接对象
	 * @param method
	 *            请求方法：GET、POST等
	 * @throws IOException
	 * @throws UnknownHostException
	 */
	protected void GET(Connection connection, String method) throws UnknownHostException, IOException {

		int b;
		byte[] data = new byte[1024];
		pro = connection.getProtocol();
		String host = connection.getProtocol().getProtocolInfo().get("HOST").toString();
		int port = (int) connection.getProtocol().getProtocolInfo().get("PORT");
		// 建立中转Http连接
		conn = new Socket(host, port);

		try {

			out = new BufferedOutputStream(conn.getOutputStream());
			in = new BufferedInputStream(conn.getInputStream());

			StringBuffer strBuff = new StringBuffer();
			strBuff.append(pro.getProtocolInfo().get("METHOD") + " " + pro.getProtocolInfo().get("path") + " "
					+ pro.getProtocolName() + "/" + pro.getProtocolVersion() + "\r\n");

			// 禁止长连接
			List<String> C = pro.getProtocolHeader().remove("Proxy-Connection");
			if (C != null) {
				C.clear();
				C.add("close");
				pro.getProtocolHeader().put("Connection", C);
			}
			// 设置请求头
			for (Entry<String, List<String>> kv : connection.getProtocol().getProtocolHeader().entrySet()) {
				for (String v : kv.getValue()) {
					strBuff.append(kv.getKey() + ": " + v + "\r\n");
				}

			}
			strBuff.append("\r\n");

			out.write(strBuff.toString().getBytes(pro.getEncode()));

			out.flush();

			new Thread() {
				public void run() {
					int b;
					byte[] data = new byte[1024];
					try {
						while ((b = pro.read(data, 0, data.length)) != -1) {
							out.write(data, 0, b);
							out.flush();
						}

						conn.shutdownOutput();
					} catch (Throwable e) {

					}

				};
			}.start();

			while ((b = in.read(data, 0, data.length)) != -1) {
				pro.write(data, 0, b);
				pro.flush();
			}

			pro.getSocket().shutdownOutput();
			pro.getSocket().close();
		} catch (Throwable e) {
			// 输出错误
			try {
				connection.getProtocol().write(("HTTP/1.1 500 Error\r\n").getBytes("UTF-8"));
				connection.getProtocol().send("\r\n" + "<div align='center'><h1>500 Error</h1><p>" + e.toString()
						+ "</p><p>Bucket Netwrok Framework</p><p>" + new Date() + "</p></div>");
			} catch (Throwable e1) {

			}

		}
		conn.close();

	}

	@Override
	public void onDataCome(Connection connection, byte[] data) {

	}

	@Override
	public void onDisconnect(Connection connection) {
		server.remove(connection);// 释放资源
	}

	@Override
	public void onException(Connection connection, Throwable e) {
		// 打印异常信息
		if (e.getClass().equals(SocketException.class) || e.getClass().equals(SocketTimeoutException.class)) {
			onDisconnect(connection);
		} else if (e.getClass().equals(UnknowProtocolException.class)) {
			if (Log.isDebugging())
				Log.e(e);
		} else
			Log.e(e);
	}
}
