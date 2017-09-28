package network;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;

import Common.Tool;
import network.bucketobject.Message;
import network.bucketobject.Query;
import network.bucketobject.USER;
import network.command.BucketCommand;
import network.command.client.ClientCommand;
import network.command.server.DataSaver;
import network.command.server.GetOnlineListCommand;
import network.command.server.MainCommand;
import network.connection.Connection;
import network.connection.FileConnection;
import network.connection.UserConnection;
import network.listener.BucketListener;
import network.listener.ClientListener;
import network.listener.LoginListener;
import network.listener.MessageListener;
import network.listener.OnlineListListener;
import network.listener.QueryListener;

public class TestClient extends BucketListener {

	private UserConnection conn;
	private HashMap<Integer, ClientListener> business;
	private BucketListener listener;
	private MessageListener messageListener;
	private String host;
	private int port;
	private USER user;

	public TestClient(String host, int port) throws UnknownHostException, IOException {
		this.host = host;
		this.port = port;
		business = new HashMap<Integer, ClientListener>();

		Socket s = new Socket(host, port);
		conn = new UserConnection(s, this);

		new Thread() {
			public void run() {
				try {
					conn.startListen();
				} catch (IOException e) {
					e.printStackTrace();
				}
			};
		}.start();
	}

	public void setListener(BucketListener listener) {
		this.listener = listener;
	}

	public BucketListener getListener() {
		return listener;
	}

	public void setMessageListener(MessageListener messageListener) {
		this.messageListener = messageListener;
	}

	public void getOnlineList(OnlineListListener listener) throws IOException {
		GetOnlineListCommand mc = new GetOnlineListCommand();

		if (listener != null) {
			int Sign = listener.hashCode();
			addBuss(Sign, listener);
			mc.setSign(Sign);
		}
		conn.send(mc.toServerCommand());
	}

	public void sendMessage(Message msg) throws IOException {
		conn.send(msg.toServerCommand());
	}


	public <T> void Query(Query q, QueryListener<T> listener) throws IOException {
		if(q.getTable_name() == null || q.getTable_name().trim().length() == 0){
			@SuppressWarnings("unchecked")
			Class<T> entityClass = (Class<T>) ((ParameterizedType) listener.getClass().getGenericSuperclass())
					.getActualTypeArguments()[0];
			q.setTable_name(entityClass.getSimpleName());
		}

		BucketCommand mm;
		if (listener != null) {
			int Sign = listener.hashCode();
			mm = q.toServerCommand(Sign);
			addBuss(Sign, listener);
		} else {
			mm = q.toServerCommand();
		}

		conn.send(mm);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void Query(Query q) throws IOException {
		QueryListener l = null;
		Query(q, l);
	}

	public void addBuss(int sign, ClientListener listener) {
		business.put(sign, listener);
	}

	public void removeBuss(int sign) {
		business.remove(sign);
	}

	public void Login(USER user, LoginListener listener) throws IOException {
		this.user = user;
		conn.login(user, listener);
	}

	public void Signin(USER user, LoginListener listener) throws IOException {
		this.user = user;
		conn.Signin(user, listener);
	}

	public void Update(Object o) throws IOException {
		ArrayList<Object> a = new ArrayList<Object>();
		a.add(o);
		Update(a);
	}

	public void Update(ArrayList<Object> array) throws IOException {

		if (array.size() < 1)
			return;

		MainCommand mc = new MainCommand();

		DataSaver ds = new DataSaver();

		ds.setTable(Tool.object2Table(array.get(0)));
		ds.setValues(Tool.List2JsonArray(array));
		mc.setCommand(ds.getClass().getName());
		mc.setValues(ds);

		conn.send(mc);
	}

	public void sendFile(File file, String serverPath,final BucketListener l) throws NullPointerException, UnknownHostException, IOException {
		if (conn == null || user == null)
			throw new NullPointerException();
		Socket s;
		s = new Socket(host, port + 1);
		s.setSoTimeout(2000);
		BucketListener listener = new BucketListener() {

			@Override
			public void onDisconnection(Connection conn) {
				l.onDisconnection(conn);
				conn.finish();
			}

			@Override
			public void onDataCome(Connection conn, String message) {
				l.onDataCome(conn, message);
				conn.finish();

			}
		};
		FileConnection fconn = new FileConnection(s, null, listener);
		fconn.login(user);
		fconn.sendFile(file, serverPath);
		fconn.startListen();
	}

	public void sendFile(String localPath, String serverPath,final BucketListener l)
			throws NullPointerException, UnknownHostException, IOException {
		if (conn == null || user == null)
			throw new NullPointerException();
		Socket s;
		s = new Socket(host, port + 1);
		s.setSoTimeout(2000);
		BucketListener listener = new BucketListener() {

			@Override
			public void onDisconnection(Connection conn) {
				l.onDisconnection(conn);
				conn.finish();
			}

			@Override
			public void onDataCome(Connection conn, String message) {
				l.onDataCome(conn, message);
				conn.finish();

			}
		};
		FileConnection fconn = new FileConnection(s, null, listener);
		fconn.login(user);
		fconn.sendFile(localPath, serverPath);
		fconn.startListen();
	}

	public void sendFile(byte[] data, String serverPath,final BucketListener l)
			throws NullPointerException, UnknownHostException, IOException {
		if (conn == null || user == null)
			throw new NullPointerException();
		Socket s;
		s = new Socket(host, port + 1);
		s.setSoTimeout(2000);
		BucketListener listener = new BucketListener() {

			@Override
			public void onDisconnection(Connection conn) {
				l.onDisconnection(conn);
				conn.finish();
			}

			@Override
			public void onDataCome(Connection conn, String message) {
				l.onDataCome(conn, message);
				conn.finish();

			}
		};
		FileConnection fconn = new FileConnection(s, null, listener);
		fconn.login(user);
		fconn.sendFile(data, serverPath);
		fconn.startListen();
	}

	@Override
	public void onDataCome(Connection conn, String message) {

		UserConnection uconn = (UserConnection) conn;
		ClientCommand cm = Tool.JSON2E(message, ClientCommand.class);
		if (cm == null) {
			System.out.println("ERROR!!  : " + message);
			return;
		}

		if (messageListener != null && cm.getCommand().equals(Message.class.getSimpleName())) {
			messageListener.onDataCome(conn, cm);
		} else if (uconn != null && uconn.getLoginListener() != null && cm.getCommand().equals("CONNECT")) {
			// System.out.println(message);
			uconn.getLoginListener().onDataCome(uconn, cm);
		} else {
			if (cm.sign == 0) {

			} else {
				ClientListener l = business.get(cm.getSign());
				if (l != null) {
					l.onDataCome(conn, cm);
					removeBuss(cm.getSign());
				}
			}
		}

		if (listener != null)
			listener.onDataCome(uconn, message);

	}

	@Override
	public void onDisconnection(Connection conn) {
		if (listener != null)
			listener.onDisconnection(conn);
	}

	public UserConnection getConn() {
		return conn;
	}

}
