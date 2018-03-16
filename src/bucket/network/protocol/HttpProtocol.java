package bucket.network.protocol;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import bucket.util.StreamTool;

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
	private static final String HANDSHAKE_CHECK_REGEX_SERVER_DEFAULT = "^(GET|POST|HEAD|OPTIONS|PUT|DELETE|TRACE) (/) (HTTP)/([0-9]\\.[0-9])$";
	/**
	 * 通用服务端正则匹配字符串
	 */
	private static final String HANDSHAKE_CHECK_REGEX_SERVER = "^(GET|POST|HEAD|OPTIONS|PUT|DELETE|TRACE) (/[-A-Za-z0-9+&@#/%?=~_|!:,.;]+[-A-Za-z0-9+&@#/%=~_|]) (HTTP)/([0-9]\\.[0-9])$";
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

	/**
	 * 默认构造函数
	 */
	public HttpProtocol() {
		super();
	}

	/**
	 * 构造函数
	 * 
	 * @param socket
	 *            套接字对象
	 * @throws IOException
	 */
	public HttpProtocol(Socket socket) throws IOException {
		super(socket);
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

		getPOST();

		return true;
	}

	public static final String CONTENT_TYPE_APPLICATION_X_WWW_FROM_URLENCODED = "application/x-www-form-urlencoded";
	public static final String CONTENT_TYPE_MULTIPART_FROMDATA = "multipart/form-data";

	/**
	 * 获取POST数据
	 * 
	 * @throws Throwable
	 * 
	 */
	protected void getPOST() throws Throwable {
		Map<String, String> header = this.getProtocolHeader();
		HashMap<String, Object> post = new HashMap<String, Object>();
		StringBuffer buff = new StringBuffer();
		String[] contentType = null;
		String ct = header.get("Content-Type");
		if (ct != null) {
			contentType = ct.split(";");
		} else {
			contentType = new String[] { CONTENT_TYPE_APPLICATION_X_WWW_FROM_URLENCODED };
		}
		if (isServer()) {
			if (contentType[0].equals(CONTENT_TYPE_APPLICATION_X_WWW_FROM_URLENCODED)) {
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
			} else if (contentType[0].equals(CONTENT_TYPE_MULTIPART_FROMDATA)) {
				HashMap<String, String> map = new HashMap<>();
				for (int i = 1; i < contentType.length; i++) {
					String[] kv = contentType[i].trim().split("=");
					if (kv.length > 1) {
						map.put(kv[0], kv[1]);
					}
				}
				String boundary = map.get("boundary");
				if (boundary != null) {
					boundary = "--" + boundary;
					String contLen = header.get("Content-Length");
					int count = 4;
					int contentLenth = -1;
					if (contLen != null)
						contentLenth = Integer.parseInt(contLen);
					byte[] data;
					while ((contentLenth == -1 || count < contentLenth) && (data = read(boundary)) != null) {
						HashMap<String, Object> m1 = new HashMap<>();
						String name = null;
						count += boundary.getBytes().length + data.length;
						ByteArrayInputStream bin = new ByteArrayInputStream(data);
						byte[] dt;
						StreamTool.readLine(bin);
						while ((dt = StreamTool.readLine(bin)) != null && dt.length > 0) {
							String s = new String(dt, getEncode());
							String[] kv = s.split(":", 2);
							HashMap<String, String> m2 = new HashMap<>();
							if (kv.length > 1) {
								String[] kv1 = kv[1].split(";");
								for (String s1 : kv1) {
									s1 = s1.trim();

									String[] kv2 = s1.split("=", 2);
									if (kv2.length > 1) {
										if (kv2[1].length() > 1 && kv2[1].charAt(0) == '\"'
												&& kv2[1].charAt(kv2[1].length() - 1) == '\"')
											kv2[1] = kv2[1].substring(1, kv2[1].length() - 1);
										m2.put(kv2[0], kv2[1]);

									} else
										m2.put(null, s1);
								}
							}
							m1.put(kv[0], m2.get(null));
							m2.remove(null);
							if (m2.get("name") != null) {
								name = m2.get("name");
								m2.remove("name");
							}
							m1.putAll(m2);
						}
						if (name != null) {
							post.put(name, m1);
							int b;
							ByteArrayOutputStream o = new ByteArrayOutputStream();
							while ((b = bin.read()) != -1) {
								o.write(b);
							}
							o.flush();
							byte[] olddata = o.toByteArray();
							byte[] newdata = Arrays.copyOf(olddata, olddata.length - 2);
							if (m1.get("Content-Type") == null)
								m1.put("value", new String(newdata, getEncode()));
							else
								m1.put("value", newdata);
						}
					}
				}
			}
		}
		super.getProtocolInfo().put(INFO_POST, post);
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

		wter.println(
				getProtocolInfo().get(INFO_METHOD) + " " + path + " " + getProtocolName() + "/" + getProtocolVersion());

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
