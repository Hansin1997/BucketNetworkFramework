package bucket.network.protocol;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 简易Http协议
 * 
 * @author Hansin1997
 * @version 2017/12/4
 */
public class HttpProtocol extends Protocol {

	/**
	 * 默认服务端正则匹配字符串 （http://www.xx.com/）
	 */
	private static final String HANDSHAKE_CHECK_REGEX_SERVER_DEFAULT = "^(POST|GET) (/) (HTTP)/([0-9]\\.[0-9])$";
	/**
	 * 通用服务端正则匹配字符串
	 */
	private static final String HANDSHAKE_CHECK_REGEX_SERVER = "^(POST|GET) (/[-A-Za-z0-9+&@#/%?=~_|!:,.;]+[-A-Za-z0-9+&@#/%=~_|]) (HTTP)/([0-9]\\.[0-9])$";
	/**
	 * 默认服务端正则匹配器
	 */
	public static final Pattern HANDSHAKE_CHECK_PATTERN_SERVER_DEFAULT = Pattern
			.compile(HttpProtocol.HANDSHAKE_CHECK_REGEX_SERVER_DEFAULT);
	/**
	 * 通用服务端正则匹配器
	 */
	public static final Pattern HANDSHAKE_CHECK_PATTERN_SERVER = Pattern
			.compile(HttpProtocol.HANDSHAKE_CHECK_REGEX_SERVER);
	/**
	 * 通用客户端正则匹配字符串
	 */
	private static final String HANDSHAKE_CHECK_REGEX_CLIENT = "^(HTTP)/([0-9]\\.[0-9]) (.+)$";
	/**
	 * 通用客户端正则匹配器
	 */
	public static final Pattern HANDSHAKE_CHECK_PATTERN_CLIENT = Pattern
			.compile(HttpProtocol.HANDSHAKE_CHECK_REGEX_CLIENT);
	/**
	 * 请求方法
	 */
	public static final String INFO_METHOD = "METHOD";
	/**
	 * GET参数标志
	 */
	public static final String INFO_GET = "GET";
	/**
	 * POST参数标志
	 */
	public static final String INFO_POST = "POST";
	/**
	 * PATH参数
	 */
	public static final String INFO_PATH = "PATH";

	public HttpProtocol(Socket socket) throws IOException {
		super(socket);
	}

	public HttpProtocol(Socket socket, InputStream in, OutputStream out) throws IOException {
		super(socket, in, out);
	}

	/**
	 * 握手检查
	 * 
	 * @param str
	 *            HTTP报文首行
	 * @return 握手成功与否
	 * @throws Throwable
	 *             异常
	 */
	private boolean checkHandshake(String str) throws Throwable {
		
		Matcher m = isServer() ? HANDSHAKE_CHECK_PATTERN_SERVER.matcher(str)
				: HANDSHAKE_CHECK_PATTERN_CLIENT.matcher(str);
		if (!m.find())
			if (isServer()) {
				m = HANDSHAKE_CHECK_PATTERN_SERVER_DEFAULT.matcher(str);
				if (!m.find())
					return false;
			} else
				return false;
		HashMap<String, Object> info = new HashMap<String, Object>();
		HashMap<String, String> get = new HashMap<String, String>();
		if (isServer()) {
			info.put(INFO_METHOD, m.group(1).trim());

			String path = m.group(2);
			// ------------------------------------------------------
			String[] tmp2 = path.split("\\u003F"), tmp;// 分割url中的问号
			if (tmp2.length == 2) {
				path = tmp2[0];
				tmp = tmp2[1].split("&");
				for (String data : tmp) {
					String[] kv = data.split("=");
					if (kv.length == 2)
						get.put(kv[0], kv[1]);
				}
			}
			// ------------------------------------------------------
			info.put(INFO_PATH, path);
			info.put(INFO_GET, get);
			super.setProtocolInfo(info);
			super.setProtocolName(m.group(3));
			super.setProtocolVersion(m.group(4));
		} else {
			super.setProtocolInfo(info);
			super.setProtocolName(m.group(1));
			super.setProtocolVersion(m.group(2));
		}

		return true;
	}

	@Override
	public boolean handshake() throws Throwable {

		HashMap<String, String> header = new HashMap<String, String>();
		super.setProtocolHeader(header);
		String str = null, first = null;

		while ((str = new String(read('\n'))) != null) {

			str = str.trim();
			String tmp[] = str.split(":", 2);
			if (tmp.length == 1) {// 当str只切割出一个子串的时候

				if (first == null) {// 条件等同于str为第一行
					first = str;
					continue;
				} else {
					break;// header部分基本读取完成，跳出循环。
				}
			}

			header.put(tmp[0].trim(), tmp[1].trim());// 存header
		}

		if (first == null || !checkHandshake(first)) {
			return false;// 握手失败返回假
		}

		HashMap<String, String> post = new HashMap<String, String>();
		StringBuffer buff = new StringBuffer();
		if (isServer()) {
			String contLen = header.get("Content-Length");
			if (contLen != null) {
				int b;
				int contentLenth = Integer.parseInt(contLen);
				for (int i = 0; i < contentLenth; i++) {
					b = read();
					if (b == -1)
						break;
					buff.append((char) b);
				}

				String kvs[] = buff.toString().split("&");

				for (String kv_ : kvs) {
					String kv[] = kv_.split("=");
					if (kv.length == 2) {
						post.put(kv[0], kv[1]);
					}
				}

			}

		}
		super.getProtocolInfo().put(INFO_POST, post);

		return true;
	}

	@Override
	public void send(byte[] bytes) throws Throwable {
		write(bytes);
		flush();
	}

	@Override
	public byte[] load() throws Throwable {

		if (!isServer()) {

			String contLen = getProtocolHeader().get("Content-Length");

			if (contLen != null) {
				int contentLenth = Integer.parseInt(contLen);
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				byte buff[] = new byte[255];

				int b = -1;
				for (int i = 0; i < contentLenth; i += buff.length) {

					b = read(buff, 0, buff.length);

					if (b != -1) {
						out.write(buff, 0, b);
					} else
						break;
				}
				out.flush();
				byte[] data = out.toByteArray();
				if (data.length == 0)
					return null;
				return data;
			} else {

				ByteArrayOutputStream out = new ByteArrayOutputStream();
				byte buff[] = new byte[255];
				int b = -1;
				while ((b = read(buff, 0, buff.length)) != -1) {
					out.write(buff, 0, b);

					if (b < buff.length)
						break;

				}
				out.flush();
				byte[] data = out.toByteArray();
				if (data.length == 0)
					return null;
				return data;
			}

		}
		return null;
	}

	/**
	 * 输出服务端响应头
	 * 
	 * @param statusCode
	 *            状态吗，如: "404 Not Found"
	 * @param header
	 *            HTTP Header 表
	 */
	public void parseServerHeader(String statusCode, Map<String, Object> header) throws Throwable {
		PrintWriter wter = new PrintWriter(getOut());
		wter.print(getProtocolName());
		wter.print('/');
		wter.print(getProtocolVersion());
		wter.print(' ');
		wter.println(statusCode);
		if (header != null)
			for (Entry<String, Object> kv : header.entrySet()) {
				wter.print(kv.getKey());
				wter.print(": ");
				wter.println(kv.getValue());
			}

		wter.println();
		wter.flush();
	}

	/**
	 * 输出客户端请求头
	 * 
	 * @throws Throwable
	 */
	public void parseClientHeader(Map<String, Object> header) throws Throwable {
		if (getProtocolName() == null)
			setProtocolName("HTTP");
		if (getProtocolVersion() == null)
			setProtocolVersion("1.1");
		PrintWriter wter = new PrintWriter(getOut());

		String path = null;
		if (getProtocolInfo().get(INFO_PATH) == null)
			path = "/";
		else
			path = (String) getProtocolInfo().get(INFO_PATH);

		if (getProtocolInfo().get(INFO_GET) != null) {

			@SuppressWarnings("unchecked")
			Map<String, String> getMap = (Map<String, String>) getProtocolInfo().get(INFO_GET);
			Iterator<Entry<String, String>> it = getMap.entrySet().iterator();
			if (it.hasNext())
				path += "?";
			while (it.hasNext()) {
				Entry<String, String> kv = it.next();
				path += kv.getKey() + "=" + kv.getValue();
				if (it.hasNext())
					path += "&";
			}

		}

		wter.println(getProtocolInfo().get(INFO_METHOD) + " " + path + " "
				+ getProtocolName() + "/" + getProtocolVersion());

		wter.println("Host: " + getSocket().getInetAddress().getHostName() + ":" + getSocket().getPort());
		if (header != null)
			for (Entry<String, String> kv : getProtocolHeader().entrySet()) {
				wter.print(kv.getKey());
				wter.print(": ");
				wter.println(kv.getValue());
			}
		wter.println();
		wter.flush();
	}

	@SuppressWarnings("unchecked")
	public Map<String, String> GET() {
		if (this.getProtocolInfo() == null)
			this.setProtocolInfo(new HashMap<String, Object>());
		this.getProtocolInfo().put(INFO_METHOD, INFO_GET);
		if (this.getProtocolInfo().get(INFO_GET) == null)
			this.getProtocolInfo().put(INFO_GET, new HashMap<String, String>());
		return (Map<String, String>) this.getProtocolInfo().get(INFO_GET);
	}

	@SuppressWarnings("unchecked")
	public Map<String, String> POST() {
		if (this.getProtocolInfo() == null)
			this.setProtocolInfo(new HashMap<String, Object>());
		this.getProtocolInfo().put(INFO_METHOD, INFO_POST);
		if (this.getProtocolInfo().get(INFO_POST) == null)
			this.getProtocolInfo().put(INFO_POST, new HashMap<String, String>());
		return (Map<String, String>) this.getProtocolInfo().get(INFO_POST);
	}

	public Map<String, String> GET(String path) {
		Map<String, String> map = this.GET();
		this.getProtocolInfo().put(INFO_PATH, path);
		return map;
	}

	public Map<String, String> POST(String path) {
		Map<String, String> map = this.POST();
		this.getProtocolInfo().put(INFO_PATH, path);
		return map;
	}

	@Override
	public Map<String, String> getProtocolHeader() {
		if (protocolHeader == null)
			protocolHeader = new HashMap<>();
		return super.getProtocolHeader();
	}

	@Override
	public Map<String, Object> getProtocolInfo() {
		if (protocolInfo == null)
			protocolInfo = new HashMap<>();
		return super.getProtocolInfo();
	}

}
