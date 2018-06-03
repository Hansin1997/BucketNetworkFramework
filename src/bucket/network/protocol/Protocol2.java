package bucket.network.protocol;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import bucket.listener.EventListener;
import bucket.nio.Handler;

/**
 * 协议抽象类
 * 
 * @author Hansin
 * @version 2017/11/29
 */
public abstract class Protocol2 extends Handler {

	public static final int HANDSHAKE_SUCCESSED = 1;
	public static final int HANDSHAKE_WAITING = 0;
	public static final int HANDSHAKE_FAILED = -1;

	/**
	 * 协议状态
	 */
	protected int handShakeStatus;

	/**
	 * 协议名
	 */
	protected String protocolName;

	/**
	 * 协议版本号
	 */
	protected String protocolVersion;

	/**
	 * 协议信息
	 */
	protected Map<String, Object> protocolInfo;

	/**
	 * 协议头信息
	 */
	protected Map<String, List<String>> protocolHeader;
	
	protected EventListener eventListener;

	/**
	 * 是否服务端
	 */
	private boolean server;

	/**
	 * 编码
	 */
	protected String encoding;

	public Protocol2(SocketChannel socketChannel,EventListener eventListener) {
		super(socketChannel);
		this.server = false;
		this.encoding = "UTF-8";
		this.eventListener = eventListener;
		this.handShakeStatus = HANDSHAKE_WAITING;
	}

	public abstract int handshake() throws Throwable;

	/**
	 * 发送数据包
	 * 
	 * @param bytes
	 *            要发送的数据
	 */
	public abstract void send(byte[] bytes) throws Throwable;

	@Override
	public void read(SelectionKey key) throws IOException {
		SocketChannel sc = getSocketChannel();
		
	}

	@Override
	public void write(SelectionKey key) throws IOException {
		// TODO Auto-generated method stub

	}
	
	public void close() throws IOException {
		getSocketChannel().close();
	}

	/**
	 * 获取协议名
	 * 
	 * @return 返回协议名
	 */
	public String getProtocolName() {
		return protocolName;
	}

	public String getProtocolVersion() {
		return protocolVersion;
	}

	public Map<String, Object> getProtocolInfo() {
		return protocolInfo;
	}

	public Map<String, List<String>> getProtocolHeader() {
		return protocolHeader;
	}

	public void setProtocolName(String protocolName) {
		this.protocolName = protocolName;
	}

	public void setProtocolVersion(String protocolVersion) {
		this.protocolVersion = protocolVersion;
	}

	public void setProtocolInfo(Map<String, Object> protocolInfo) {
		this.protocolInfo = protocolInfo;
	}

	public void setProtocolHeader(Map<String, List<String>> protocolHeader) {
		this.protocolHeader = protocolHeader;
	}

	/**
	 * 获取编码
	 * 
	 * @return
	 */
	public String getEncoding() {
		return encoding;
	}

	/**
	 * 设置编码
	 * 
	 * @param encode
	 */
	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	/**
	 * 设置服务端
	 * 
	 * @param server
	 */
	public void setServer(boolean server) {
		this.server = server;
	}

	/**
	 * 是否服务端
	 * 
	 * @return
	 */
	public boolean isServer() {
		return server;
	}

	@Override
	public String toString() {
		StringBuffer buff = new StringBuffer();
		buff.append("Protocol: ");
		if (this.protocolName != null)
			buff.append(this.protocolName);
		buff.append(", Version: ");
		if (this.protocolVersion != null)
			buff.append(this.protocolVersion);
		buff.append(", Info: ");
		if (this.protocolInfo != null)
			buff.append(this.protocolInfo);
		return buff.toString();
	}

}
