package Test;


import network.Server;
import network.connection.ServerConnection;
import network.protocol.BucketProtocol;
import network.protocol.HttpProtocol;
import network.protocol.WebSocketProtocol;

public class ServerTest {

	public static void main(String[] args) throws Throwable {

		
		ServerConnection.ProtocolList.add(WebSocketProtocol.class.getName());
		ServerConnection.ProtocolList.add(HttpProtocol.class.getName());
		ServerConnection.ProtocolList.add(BucketProtocol.class.getName());

		Server s = new Server(TestApplication.class.getName());
		s.start();
		
	}
}
