package network.connection;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Constructor;
import java.net.Socket;
import java.util.ArrayList;

import network.listener.EventListener;
import network.protocol.Protocol;

/**
 * 连接类
 * @author Hansin1997
 *
 */
public class ServerConnection implements Runnable {
	
	/**
	 * 协议对象
	 */
	private Protocol protocol;
	private Socket socket;
	private InputStream in;
	private OutputStream out;
	private EventListener listener;

	/**
	 * 协议列表
	 */
	public static final ArrayList<String> ProtocolList = new ArrayList<String>();
	
	public ServerConnection(Socket socket,EventListener listener) {
		setSocket(socket);
		setListener(listener);
		try {
			in = new BufferedInputStream(socket.getInputStream());
			out = new BufferedOutputStream(socket.getOutputStream());
		}catch (Exception e) {
			if(listener != null)
				listener.onException(this,e);
			else
				e.printStackTrace();
		}
	}

	@Override
	public void run() {
		try {
			
			if(!autoSelct())
				throw new Exception("无法识别该协议");
			
			byte data[];
			while((data = protocol.load()) != null) {
				listener.onDataCome(this,data);
			}
			
		}catch (Throwable e) {
			if(listener != null)
				listener.onException(this,e);
			else
				e.printStackTrace();
		}finally {
			if(listener != null)
				listener.onDisconnect(this);
		}
		
	}
	
	
	@SuppressWarnings({"rawtypes" })
	private boolean autoSelct() throws Throwable {
		Protocol p;
		for(String className : ProtocolList) {
			try {
				Constructor cons = Class.forName(className).getConstructor(Socket.class,InputStream.class,OutputStream.class);
				p = (Protocol) cons.newInstance(socket,in,out);
				p.setServer(true);
				socket.setSoTimeout(2000);
				if(p.handshake()) {
					socket.setSoTimeout(0);
					setProtocol(p);
					listener.onConnect(this);
					return true;
				}
					
			} catch (Throwable e) {
				if(listener != null)
					listener.onException(this,e);
				else
					e.printStackTrace();
			}
		}
		return false;
	}
	
	//-----------------------------------------------//

	public void setSocket(Socket socket) {
		this.socket = socket;
	}
	
	public Socket getSocket() {
		return socket;
	}
	
	public InputStream getIn() {
		return in;
	}
	
	public OutputStream getOut() {
		return out;
	}

	public void setIn(InputStream in) {
		this.in = in;
	}
	
	public void setOut(OutputStream out) {
		this.out = out;
	}
	
	public void setProtocol(Protocol protocol) {
		this.protocol = protocol;
	}
	
	public Protocol getProtocol() {
		return protocol;
	}

	public void setListener(EventListener listener) {
		this.listener = listener;
	}
	
	public EventListener getListener() {
		return listener;
	}
	

}
