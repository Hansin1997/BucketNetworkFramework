package bucket.extension.http;

import java.net.ServerSocket;
import java.util.Arrays;

import bucket.util.CMD;
import bucket.util.Log;
import demo.http.HttpServer;

public class WebServer {

	public static void main(String[] args) throws Exception {

		CMD cmd = new CMD(args);

		if (!cmd.isCmdExExist("root")) {
			Log.e("未指定html根目录,请添加命令行参数‘-root 网页根目录’");
			return;
		}

		int port = cmd.isCmdExExist("port") ? Integer.valueOf(cmd.getCmdEx("port")) : 8080;
		String wwwroot = cmd.getCmdEx("root");
		String[] defaultFile;
		Log.setDebugging(cmd.isCmdExExist("debug"));

		if (cmd.getCmdLength() > 0) {
			defaultFile = new String[cmd.getCmdLength()];
			for (int i = 0; i < defaultFile.length; i++) {
				defaultFile[i] = cmd.getCmd(i);
			}
		} else {
			defaultFile = new String[] { "index.htm", "index.html", "index.php", "default.html" };
		}

		Log.echo("服务开启", "端口: " + port, "目录: " + wwwroot, "默认文档: " + Arrays.deepToString(defaultFile),
				"时间: " + Log.date());

		ServerSocket serverSocket = new ServerSocket(port);
		HttpServer s = new HttpServer();
		s.setWwwroot(wwwroot);
		s.setDefaultFile(defaultFile);
		s.start(serverSocket);
	}
}
