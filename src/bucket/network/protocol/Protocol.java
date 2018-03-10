package bucket.network.protocol;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Map;

/**
 * 协议抽象类
 * 
 * @author Hansin
 * @version 2017/11/29
 */
public abstract class Protocol {

	/**
	 * 协议名
	 */
	protected String protocolName; // 协议名

	/**
	 * 协议版本号
	 */
	protected String protocolVersion; // 协议版本号

	/**
	 * 协议重要信息
	 */
	protected Map<String, Object> protocolInfo; // 协议重要信息

	/**
	 * 协议头部信息
	 */
	protected Map<String, String> protocolHeader; // 协议头部信息

	/**
	 * 套接字对象
	 */
	private Socket socket;

	private InputStream in; // 输入流
	private OutputStream out; // 输出流
	private boolean server; // 是否服务端

	/**
	 * 默认编码
	 */
	protected String encode;

	public Protocol() {
		this.server = false;
		this.encode = "UTF-8";
	}

	/**
	 * 构造函数
	 * 
	 * @param socket
	 *            套接字对象
	 * 
	 * @throws IOException
	 */
	public Protocol(Socket socket) throws IOException {
		this.socket = socket;
		this.in = new BufferedInputStream(socket.getInputStream());
		this.out = new BufferedOutputStream(socket.getOutputStream());
		this.server = false;
		this.encode = "UTF-8";
	}

	/**
	 * 构造函数
	 * 
	 * @param socket
	 *            套接字对象
	 * @param in
	 *            传入输入流
	 * @param out
	 *            传入输出流
	 * 
	 * @throws IOException
	 */
	public Protocol(Socket socket, InputStream in, OutputStream out) throws IOException {
		this.socket = socket;
		this.in = in;
		this.out = out;
		this.server = false;
		this.encode = "UTF-8";
	}

	/**
	 * 协议检查
	 * 
	 * @return 真表示符合本协议并且完成握手，假则非本协议。
	 * @throws Throwable
	 */
	public abstract boolean handshake() throws Throwable;

	/**
	 * 发送数据包
	 * 
	 * @param bytes
	 *            要发送的数据
	 */
	public abstract void send(byte[] bytes) throws Throwable;

	/**
	 * 读取数据包
	 * 
	 * @return 返回读取到的数据
	 */
	public abstract byte[] load() throws Throwable;

	/**
	 * 发送字符串数据
	 * 
	 * @param data
	 *            字符串数据
	 * @throws Throwable
	 */
	public void send(String data) throws Throwable {
		this.send(data.getBytes(getEncode()));
	}

	/**
	 * 读取字符串数据
	 * 
	 * @return 字符串数据
	 * @throws Throwable
	 */
	public String loadString() throws Throwable {
		return new String(this.load(), getEncode());
	}

	/**
	 * 读取一个字节
	 * 
	 * @return 返回读取的字节
	 * @throws Throwable
	 */
	public int read() throws Throwable {
		return getIn().read();
	};

	/**
	 * 读取一组字节
	 * 
	 * @param bytes
	 *            将读取的字节存放进此数组
	 * @return 返回实际读取长度
	 * @throws Throwable
	 */
	public int read(byte[] bytes) throws Throwable {
		return getIn().read(bytes);
	}

	/**
	 * 读取一组字节
	 * 
	 * @param bytes
	 *            将读取的字节存放进此数组
	 * @param start
	 *            起始索引
	 * @param length
	 *            读取长度
	 * @return 返回实际读取长度
	 * @throws Throwable
	 */
	public int read(byte[] bytes, int start, int length) throws Throwable {
		return getIn().read(bytes, start, length);
	}

	/**
	 * 写入一个字节
	 * 
	 * @param b
	 *            被写入的字节
	 * @throws Throwable
	 */
	public void write(int b) throws Throwable {
		getOut().write(b);
	}

	/**
	 * 写入一组字节
	 * 
	 * @param bytes
	 *            被写入的字节
	 * @throws Throwable
	 */
	public void write(byte[] bytes) throws Throwable {
		getOut().write(bytes);
	}

	/**
	 * 写入一组字节
	 * 
	 * @param bytes
	 *            被写入的字节
	 * @param start
	 *            起始索引
	 * @param length
	 *            写入长度
	 * @throws Throwable
	 */
	public void write(byte[] bytes, int start, int length) throws Throwable {
		getOut().write(bytes, start, length);
	}

	/**
	 * 清空缓冲区
	 * 
	 * @throws Throwable
	 */
	public void flush() throws Throwable {
		getOut().flush();
	}

	/**
	 * 以某个字节为分割符号，读取该符号之前的数据
	 * 
	 * @param b
	 *            分割字节
	 * @return 返回读取的数据
	 */
	public byte[] read(int b) throws Throwable {
		ByteArrayOutputStream o = new ByteArrayOutputStream();
		int bb;
		while ((bb = read()) != b) {
			o.write(bb);
		}
		return o.toByteArray();
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

	public Map<String, String> getProtocolHeader() {
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

	public void setProtocolHeader(Map<String, String> protocolHeader) {
		this.protocolHeader = protocolHeader;
	}

	protected void setIn(InputStream in) {
		this.in = in;
	}

	public InputStream getIn() {
		return in;
	}

	/**
	 * 设置套接字对象(非必要请勿使用此方法)
	 */
	public void setSocket(Socket socket, InputStream in, OutputStream out) {
		this.socket = socket;
		this.in = in;
		this.out = out;
	}

	protected void setOut(OutputStream out) {
		this.out = out;
	}

	public OutputStream getOut() {
		return out;
	}

	public Socket getSocket() {
		return socket;
	}

	/**
	 * 获取默认编码
	 * 
	 * @return
	 */
	public String getEncode() {
		return encode;
	}

	/**
	 * 设置默认编码
	 * 
	 * @param encode
	 */
	public void setEncode(String encode) {
		this.encode = encode;
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
