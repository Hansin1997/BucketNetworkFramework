package Test;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.List;

import bucket.application.Application;
import bucket.network.Server;
import bucket.network.connection.ServerConnection;
import bucket.network.protocol.HttpProtocol;

public class TestApplication extends Application {

	public TestApplication(Server server) {
		super(server);
	}

	@Override
	public void onConnect(ServerConnection connection) {

		System.out.println(connection.getProtocol() + "   IN");
		if (connection.getProtocol().getProtocolName().equals("HTTP")) {

			try {
				HttpProtocol p = (HttpProtocol) connection.getProtocol();

				File f = new File("/usr/local/nginx/html" + p.getProtocolInfo().get("PATH"));
				if (f.exists()) {
					if (f.isDirectory()) {
						p.parseServerHeader("404 Not Found", null);
						p.send("404 Not Found".getBytes());
					} else {

						p.parseServerHeader("200 OK", null);
						BufferedInputStream in = new BufferedInputStream(new FileInputStream(f));
						int b;
						while ((b = in.read()) != -1)
							p.write(b);
						in.close();

					}
				} else {
					p.parseServerHeader("404 Not Found", null);
					p.send("404 Not Found".getBytes());
				}
				p.flush();
				p.getSocket().close();
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}

	}

	@Override
	public void onDataCome(ServerConnection connection, byte[] data) {
		System.out.println(new String(data));
		List<ServerConnection> list = server.getList();
		synchronized (list) {

			for (ServerConnection c : list) {
				try {
					c.getProtocol()
							.send("{\"command\":\"Danmu\",\"values\":{\"content\":\"你妈买皮\"}}".getBytes());
				} catch (Throwable e) {
					c.getListener().onException(c, e);
				}
			}
		}

	}

	@Override
	public void onDisconnect(ServerConnection conn) {
		System.out.println(conn.getProtocol() + "   OUT");
		server.remove(conn);

	}

	@Override
	public void onException(ServerConnection connection, Throwable e) {
		e.printStackTrace();

	}

}
