package network.connection;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.Socket;

import Common.Tool;
import network.bucketobject.USER;
import network.command.client.ClientCommand;
import network.listener.BucketListener;
import network.listener.LoginListener;

public class UserConnection extends Connection {

	public String username;
	private boolean isServer;
	private LoginListener loginListener;

	public void setLoginListener(LoginListener loginListener) {
		this.loginListener = loginListener;
	}

	public LoginListener getLoginListener() {
		return loginListener;
	}

	public void setServer(boolean isServer) {
		this.isServer = isServer;
	}

	public UserConnection(Socket socket, BucketListener messageListener) throws IOException {
		this(socket, messageListener, false);
	}

	public UserConnection(Socket socket, BucketListener messageListener, boolean isServer) throws IOException {
		super(socket, messageListener);
		this.isServer = isServer;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getUsername() {
		return username;
	}

	@Override
	public void startListen() throws IOException {
		if (isServer) {
			socket.setSoTimeout(2000);

			if (check(readLine())) {
				socket.setSoTimeout(0);
				super.startListen();
			} else {
				socket.close();
			}
		} else {
			super.startListen();
		}

	}

	public void login(USER user, LoginListener listener) throws IOException {
		writeLine(Checker.createLogin(user).toJSON());
		loginListener = listener;
	}
	
	public void Signin(USER user, LoginListener listener) throws IOException {
		writeLine(Checker.createSignin(user).toJSON());
		loginListener = listener;
	}

	private boolean check(String str) throws UnsupportedEncodingException, IOException {

		Checker checker = Tool.JSON2E(str, Checker.class);
		if (checker == null)
			return false;
		
		ClientCommand cc = new ClientCommand();

		USER ckeckerUser = checker.doCheck();
		if(ckeckerUser == null)
		{
			cc.setCommand("CONNECT");
			cc.setValues("FAIL");
			send(cc);
			return false;
		}else{
			cc.setCommand("CONNECT");
			cc.setValues("SUCCESS");
			username = ckeckerUser.username;
			send(cc);
			return true;
		}





	}

}
