package network;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;

import Database.DatabaseManager;
import network.command.server.MainCommand;
import network.connection.Connection;
import network.connection.UserConnection;
import network.listener.BucketListener;

public class TestServer {

	FileSocketPool fpool;
	SocketPool pool;
	DatabaseManager db;

	public TestServer(String MySQL_Username, String MySQL_Password, String DataBase, final int port){

		db = new DatabaseManager(MySQL_Username, MySQL_Password, DataBase);
		pool = new SocketPool(1024,db);
		fpool = new FileSocketPool(1024,db);

		//客户主线程
		new Thread() {
			public void run() {

				ServerSocket s;
				try {
					s = new ServerSocket(port);
					while (true) {
						Socket c = s.accept();

						if (db.isClose())
							db.ConnectMySQL();

						final BucketListener listener = new BucketListener() {

							@Override
							public void onDataCome(network.connection.Connection c, String message) {

								try {
									if (db.isClose())
										db.ConnectMySQL();
								} catch (ClassNotFoundException e1) {
									e1.printStackTrace();
								} catch (SQLException e1) {
									e1.printStackTrace();
								}

								UserConnection connection = (UserConnection) c;
								Gson gson = new GsonBuilder().create();

								try {
									MainCommand bo = gson.fromJson(message, MainCommand.class);
									bo.client = connection;
									bo.db = db;
									bo.pool = pool;
									bo.execute();
								} catch (IllegalStateException | JsonParseException e) {
									e.printStackTrace();
								}
								

							}

							@Override
							public void onDisconnection(network.connection.Connection conn) {
								pool.remove(conn);

							}
							
							
						};

						pool.add(c, listener);

					}
				} catch (IOException e2) {
					e2.printStackTrace();
				} catch (ClassNotFoundException e2) {
					e2.printStackTrace();
				} catch (SQLException e2) {
					e2.printStackTrace();
				}

			};
		}.start();

		
		//客户文件上传主线程
		new Thread() {
			public void run() {
				ServerSocket s;
				try {
					s = new ServerSocket(port + 1);
					while (true) {
						Socket c = s.accept();

						try {
							if (db.isClose())
								db.ConnectMySQL();
						} catch (ClassNotFoundException | SQLException e) {
							e.printStackTrace();
						}

						BucketListener listener = new BucketListener() {

							@Override
							public void onDisconnection(Connection conn) {
								System.out.println("Remove");
								fpool.remove(conn);
							}

							@Override
							public void onDataCome(Connection conn, String message) {

								System.out.println("Remove2");
								fpool.remove(conn);

							}
						};
						fpool.add(c, listener);
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			};
		}.start();

	}

}
