package bucket.extension.http;

import java.io.IOException;
import java.net.ServerSocket;

import bucket.util.CMD;
import bucket.util.Log;
import demo.httpproxy.HttpProxyServer;

public class HttpProxy {

	public static void main(String[] args) throws IOException, Exception {

		CMD cmd = new CMD(args);

		int port = cmd.isCmdExExist("port") ? Integer.valueOf(cmd.getCmdEx("port")) : 6666;
		Log.setDebugging(cmd.isCmdExExist("debug"));

		Log.echo("代理服务开启", "端口: " + port, "时间: " + Log.date());
		HttpProxyServer server = new HttpProxyServer();
		server.start(new ServerSocket(port));
	}
}
