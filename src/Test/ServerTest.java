package Test;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import network.connection.ServerConnection;
import network.listener.EventListener;
import network.protocol.BucketProtocol;
import network.protocol.HttpProtocol;
import network.protocol.WebSocketProtocol;

public class ServerTest {

	public static void main(String[] args) throws Throwable {


		ServerSocket server = new ServerSocket(6656);
		ServerConnection.ProtocolList.add(WebSocketProtocol.class.getName());
		ServerConnection.ProtocolList.add(HttpProtocol.class.getName());
		ServerConnection.ProtocolList.add(BucketProtocol.class.getName());
		
		ThreadPoolExecutor pool = (ThreadPoolExecutor)Executors.newFixedThreadPool(500);
		
		while(true) {
			
			Socket socket = server.accept();
			
			EventListener lis = new EventListener() {


				@Override
				public void onConnect(ServerConnection connection) {
					
					System.out.println(connection.getProtocol() + "   IN");
					if(connection.getProtocol().getProtocolName().equals("HTTP")) {
						
						try {
							HttpProtocol p = (HttpProtocol)connection.getProtocol();
							
							File f = new File("/usr/local/nginx/html" + p.getProtocolInfo().get("PATH"));
							if(f.exists()) {
								if(f.isDirectory()) {
									p.parseServerHeader("404 Not Found", null);
									p.send("404 Not Found".getBytes());
								}else {
									

									
									p.parseServerHeader("200 OK", null);
									BufferedInputStream in = new BufferedInputStream(new FileInputStream(f));
									int b;
									while((b = in.read()) != -1)
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
					
				}

				@Override
				public void onDisconnect(ServerConnection connection) {
					System.out.println(connection.getProtocol() + "   OUT");
					
				}

				@Override
				public void onException(ServerConnection connection, Throwable e) {
					e.printStackTrace();
					
				}
			};
			if(pool.getActiveCount() >= 500)
				socket.close();
			else
				pool.submit(new ServerConnection(socket, lis));
			
		}
	}
}
