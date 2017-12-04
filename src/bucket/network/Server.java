package bucket.network;

import java.lang.reflect.Constructor;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

import bucket.application.Application;
import bucket.database.DatabaseManager;
import bucket.network.connection.ServerConnection;

public class Server implements RejectedExecutionHandler{

	private List<ServerConnection> list;
	private ThreadPoolExecutor pool;
	private int corePoolSize;
	private int maximumPoolSize;
	private int port;
	private boolean running;
	private DatabaseManager database;
	private String appClassName;
	
	public Server(String appClassName) {
		this(appClassName,6656,null, 512, 1024);
	}
	
	public Server(String appClassName,int port) {
		this(appClassName,port,null, 512, 1024);
	}
	
	public Server(String appClassName,int port,DatabaseManager databaseManager) {
		this(appClassName,port,databaseManager, 512, 1024);
	}

	public Server(String appClassName,int port,DatabaseManager databaseManager, int corePoolSize, int maximumPoolSize) {
		this.appClassName = appClassName;
		this.port = port;
		this.database = databaseManager;
		this.corePoolSize = corePoolSize;
		this.maximumPoolSize = maximumPoolSize;
		this.list = Collections.synchronizedList(new ArrayList<ServerConnection>());
		this.pool = (ThreadPoolExecutor) Executors.newCachedThreadPool();
		this.pool.setCorePoolSize(this.corePoolSize);
		this.pool.setMaximumPoolSize(this.maximumPoolSize);
		this.pool.setRejectedExecutionHandler(this);
	}

	public void start() throws Exception {
		setRunning(true);
		ServerSocket serverSocket = new ServerSocket(this.port);
		while(isRunning()) {
			Socket socket = serverSocket.accept();
			
			@SuppressWarnings("rawtypes")
			Constructor c = Class.forName(appClassName).getConstructor(Server.class);
			
			ServerConnection conn = new ServerConnection(socket, (Application)c.newInstance(this));
			
			add(conn);
			this.pool.execute(conn);
		}
		serverSocket.close();
		setRunning(false);
	}

	public synchronized void stop() {
		for (ServerConnection conn : list) {
			try {
				conn.getSocket().close();
				list.remove(conn);
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}
		
		pool.shutdownNow();
		setRunning(false);

	}

	public synchronized boolean add(ServerConnection conn) {
		return list.add(conn);
	}

	public synchronized boolean remove(ServerConnection conn) {
		return list.remove(conn);
	}

	public synchronized ServerConnection get(int index) {
		return list.get(index);
	}

	@Override
	public void rejectedExecution(Runnable arg0, ThreadPoolExecutor arg1) {

		try {
			ServerConnection conn = (ServerConnection) arg0;
			remove(conn);
			conn.getSocket().close();
		} catch (Throwable e) {
			e.printStackTrace();
		}

	}
	
	//------------------------------------
	
	public synchronized int getCount(){
		return list.size();
	}
	
	public List<ServerConnection> getList() {
		return list;
	}
	
	public void setRunning(boolean running) {
		this.running = running;
	}
	
	public boolean isRunning() {
		return running;
	}
	
	public DatabaseManager getDatabase() {
		return database;
	}
	
	public void setDatabase(DatabaseManager database) {
		this.database = database;
	}
	
	public String getAppClassName() {
		return appClassName;
	}
	
	public void setAppClassName(String appClassName) {
		this.appClassName = appClassName;
	}

	//---------------------------------------


}
