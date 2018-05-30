package bucket.network;

import java.io.IOException;
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
import bucket.database.Database;
import bucket.network.connection.Connection;
import bucket.network.connection.ServerConnection;

/**
 * 网络服务类
 * 
 * @author Hansin1997
 * @version 2017/12/4
 */
public class Server implements RejectedExecutionHandler {

	/**
	 * ServerConnection列表
	 */
	private List<ServerConnection> list;

	/**
	 * 线程池
	 */
	private ThreadPoolExecutor pool;

	/**
	 * 线程池核心大小
	 */
	private int corePoolSize;

	/**
	 * 线程池最大大小
	 */
	private int maximumPoolSize;

	/**
	 * 服务运行标志
	 */
	private boolean running;

	/**
	 * 数据库管理器
	 */
	private Database database;

	/**
	 * Application类，将会为每一个连接实例化一个该类的对象进行事件处理。
	 */
	private Class<?> appClass;

	/**
	 * 协议列表
	 */
	protected final ArrayList<String> ProtocolList = new ArrayList<String>();

	/**
	 * 服务端Socket
	 */
	protected ServerSocket serverSocket;

	public Server() {
		this(null, null, 512, 1024);
	}

	/**
	 * 构造函数
	 * 
	 * @param appClassName
	 *            Application完整类名，该类名必须是Application类或其子类，将会为每一个连接实例化一个该类的对象进行事件处理。
	 */
	public Server(Class<?> appClass) {
		this(appClass, null, 512, 1024);
	}

	/**
	 * 构造函数
	 * 
	 * @param appClassName
	 *            Application完整类名，该类名必须是Application类或其子类，将会为每一个连接实例化一个该类的对象进行事件处理。
	 * @param databaseManager
	 *            数据库管理器
	 */
	public Server(Class<?> appClass, Database databaseManager) {
		this(appClass, databaseManager, 512, 1024);
	}

	/**
	 * 构造函数
	 * 
	 * @param appClassName
	 *            Application完整类名，该类名必须是Application类或其子类，将会为每一个连接实例化一个该类的对象进行事件处理。
	 * @param databaseManager
	 *            数据库管理器
	 * @param corePoolSize
	 *            线程池核心大小
	 * @param maximumPoolSize
	 *            线程池最大大小
	 */
	public Server(Class<?> appClass, Database databaseManager, int corePoolSize, int maximumPoolSize) {
		this.appClass = appClass;
		// this.port = port;
		this.database = databaseManager;
		this.corePoolSize = corePoolSize;
		this.maximumPoolSize = maximumPoolSize;
		this.list = Collections.synchronizedList(new ArrayList<ServerConnection>());
		this.pool = (ThreadPoolExecutor) Executors.newCachedThreadPool();
		this.pool.setCorePoolSize(this.corePoolSize);
		this.pool.setMaximumPoolSize(this.maximumPoolSize);
		this.pool.setRejectedExecutionHandler(this);
	}

	/**
	 * 开始服务循环(同步)
	 * 
	 * @throws Exception
	 */
	public void start(ServerSocket serverSocket) throws Exception {
		setRunning(true);
		this.serverSocket = serverSocket;

		Constructor<?> c = appClass.getConstructor(Server.class);
		Socket socket;
		while (isRunning()) {

			try {
				socket = serverSocket.accept();
			} catch (java.net.SocketException e) {
				break;
			}
			ServerConnection conn = new ServerConnection(ProtocolList, socket, (Application) c.newInstance(this));
			add(conn);

		}
		serverSocket.close();
		setRunning(false);
	}

	/**
	 * 停止服务循环
	 */
	public synchronized void stop() {
		for (ServerConnection conn : list) {
			try {
				conn.getSocket().close();
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}
		try {
			if (serverSocket != null)
				serverSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		list.clear();
		pool.shutdownNow();
		setRunning(false);

	}

	/**
	 * 加入一个ServerConnection并且提交到线程池
	 * 
	 * @param conn
	 *            ServerConnection
	 * @return 是否成功添加
	 */
	public synchronized boolean add(ServerConnection conn) {
		boolean r = list.add(conn);
		if (r)
			this.pool.execute(conn);
		return r;
	}

	/**
	 * 从列表移除ServerSocket
	 * 
	 * @param conn
	 *            ServerConnection
	 * @return 是否成功移除
	 */
	public synchronized boolean remove(Connection conn) {
		try {
			conn.getSocket().close();
		} catch (IOException e) {
			// e.printStackTrace();
		}
		boolean q1 = this.pool.remove(conn), q2 = list.remove(conn);
		return q1 && q2;
	}

	/**
	 * 获取ServerConnection
	 * 
	 * @param index
	 *            索引
	 * @return 获取的ServerConnection对象
	 */
	public synchronized ServerConnection get(int index) {
		return list.get(index);
	}

	/**
	 * 当线程池无法添加新任务时调用此方法
	 */
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

	// ------------------------------------

	/**
	 * 获取当前ServerConnection数量
	 * 
	 * @return ServerConnection数量
	 */
	public synchronized int getCount() {
		return list.size();
	}

	/**
	 * 获取ServerConnectionl列表
	 * 
	 * @return ServerConnection列表
	 */
	public List<ServerConnection> getList() {
		return list;
	}

	/**
	 * 设置服务运行flag
	 * 
	 * @param running
	 *            flag标志
	 */
	public void setRunning(boolean running) {
		this.running = running;
	}

	/**
	 * 获取服务运行标记
	 * 
	 * @return flag标志
	 */
	public boolean isRunning() {
		return running;
	}

	/**
	 * 获取数据库管理器
	 * 
	 * @return DatabaseManager
	 */
	public Database getDatabase() {
		return database;
	}

	/**
	 * 设置数据库管理器
	 * 
	 * @param database
	 *            数据库管理器
	 */
	public void setDatabase(Database database) {
		this.database = database;
	}

	/**
	 * 获取Application类
	 * 
	 * @return 完整类名
	 */
	public Class<?> getAppClass() {
		return appClass;
	}

	/**
	 * 设置Application类
	 * 
	 * @param appClassName
	 *            完整类名
	 */
	public void setAppClass(Class<?> appClass) {
		this.appClass = appClass;
	}

	// ---------------------------------------

	// /**
	// * 设置端口号
	// *
	// * @param port
	// * 端口号
	// */
	// public void setPort(int port) {
	// this.port = port;
	// }
	//
	// /**
	// * 获取端口号
	// *
	// * @return
	// */
	// public int getPort() {
	// return port;
	// }

	/**
	 * 添加协议支持
	 * 
	 * @param protocol
	 */
	public void addProtocol(Class<?> protocol) {
		addProtocol(protocol.getName());
	}

	/**
	 * 添加协议支持
	 * 
	 * @param protocol
	 */
	public void addProtocol(String protocolClassName) {

		ProtocolList.add(protocolClassName);
	}

	/**
	 * 移除协议支持
	 * 
	 * @param protocol
	 */
	public void removeProtocol(Class<?> protocol) {
		removeProtocol(protocol.getName());
	}

	/**
	 * 移除协议支持
	 * 
	 * @param protocol
	 */
	public void removeProtocol(String protocolClassName) {
		ProtocolList.remove(protocolClassName);
	}

	public void setCorePoolSize(int corePoolSize) {
		this.corePoolSize = corePoolSize;
		this.pool.setCorePoolSize(corePoolSize);
	}

	public void setMaximumPoolSize(int maximumPoolSize) {
		this.maximumPoolSize = maximumPoolSize;
		this.pool.setMaximumPoolSize(maximumPoolSize);
	}

	public ServerSocket getServerSocket() {
		return serverSocket;
	}

}
