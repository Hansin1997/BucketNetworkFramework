package Network;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import Common.Gobal;
import Database.DatabaseManager;
import Network.BucketObject.Command.Server.MainCommand;
import Network.Connection.UserConnection;
//
//new Thread(){
//	public void run() {
//		Socket a = c;
//		try {
//			
//			final UserConnection conn = new UserConnection(a,new MessageListener() {
//				
//
//				@Override
//				public void onMessageCome(Network.Connection.Connection c, String message) {
//					UserConnection connection = (UserConnection)c;
//					Gson gson = new GsonBuilder().create();
//					
//					System.out.println(message);
//					try
//					{
//						MainCommand bo = gson.fromJson(message, MainCommand.class);
//						bo.setDb(db);
//						
//						bo.setClient(connection.getUsername());
//						bo.execute();
//					}catch(IllegalStateException | com.google.gson.JsonSyntaxException e)
//					{
//						
//					}
//					
//				}
//
//				@Override
//				public void onDisconnection(Network.Connection.Connection conn) {
//					// TODO Auto-generated method stub
//					
//				}
//			});
//			conn.startListen();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
//	}
//	}.start();
public class Test {


	
	public static void main(String[] args) throws IOException, ClassNotFoundException, SQLException
	{
		
		  SocketPool pool;
		  DatabaseManager db;

		db = new DatabaseManager("root", "845612500","bucket");
		pool = new SocketPool();
		
		Gobal.setDb(db);
		Gobal.setPool(pool);
		
		
		db.ConnectMySQL();
		
		
		@SuppressWarnings("resource")
		ServerSocket s = new ServerSocket(6654);
		while(true)
		{
			Socket c = s.accept();
			
			final BucketListener listener = new BucketListener() {
				

				@Override
				public void onDataCome(Network.Connection.Connection c, String message) {
					UserConnection connection = (UserConnection)c;
					Gson gson = new GsonBuilder().create();
					
					try
					{
						MainCommand bo = gson.fromJson(message, MainCommand.class);
						bo.client = (connection);
						bo.execute();
					}catch(IllegalStateException | com.google.gson.JsonSyntaxException e)
					{
						
					}
					
				}

				@Override
				public void onDisconnection(Network.Connection.Connection conn) {
					pool.remove(conn);
					
				}
			};
			
			pool.add(c, listener);
	
		}
	}
}
