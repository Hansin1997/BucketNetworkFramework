package Network;

import Network.BucketObject.Command.Client.ClientCommand;
import Network.Connection.Connection;

public abstract class LoginListener extends ClientListener{
	
	
	public abstract void onDone(Connection conn,boolean success);
	

	@Override
	public  void  onDataCome(Connection conn, ClientCommand message) {
		System.out.println(message.toJSON());
		this.onDone(conn, message.values.toString().equals("SUCCESS"));
		
		
	}

	@Override
	public void onDisconnection(Connection conn) {
		// TODO Auto-generated method stub
		
	}
	
	
	

}