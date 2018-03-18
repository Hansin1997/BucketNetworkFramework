package bucket.network.protocol;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 简易HTTP代理协议
 * 
 * @author Hansin
 *
 */
public class HttpProxyProtocol extends Protocol {

	/**
	 * 服务端HTTPS正则匹配字符串
	 */
	private static final String HANDSHAKE_CHECK_HTTPS_REGEX_SERVER = "^(CONNECT) ([-A-Za-z0-9+&@#/%?=~_|!:,.;]+[-A-Za-z0-9+&@#/%=~_|]:[0-9]+) (HTTP)/([0-9]\\.[0-9])$";

	/**
	 * 服务端普通正则匹配字符串
	 */
	private static final String HANDSHAKE_CHECK_REGEX_SERVER = "^(GET|POST|HEAD|OPTIONS|PUT|DELETE|TRACE) (http://[-A-Za-z0-9+&@#/%?=~_|!:,.;]+[-A-Za-z0-9+&@#/%=~_|]) (HTTP)/([0-9]\\.[0-9])$";

	/**
	 * 默认服务端正则匹配器
	 */
	public static final Pattern HANDSHAKE_CHECK_PATTERN_SERVER_DEFAULT = Pattern
			.compile(HttpProxyProtocol.HANDSHAKE_CHECK_REGEX_SERVER);

	/**
	 * 默认服务端TTPS正则匹配器
	 */
	public static final Pattern HANDSHAKE_CHECK_HTTPS_PATTERN_SERVER_DEFAULT = Pattern
			.compile(HttpProxyProtocol.HANDSHAKE_CHECK_HTTPS_REGEX_SERVER);

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
	public HttpProxyProtocol() {
		super();
	}

	/**
	 * 构造函数
	 * 
	 * @param socket
	 *            套接字对象
	 * @throws IOException
	 */
	public HttpProxyProtocol(Socket socket) throws IOException {
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
	public HttpProxyProtocol(Socket socket, InputStream in, OutputStream out) throws IOException {
		super(socket, in, out);
	}

	protected boolean checkHandshake(String str) {

		Matcher m = isServer() ? HANDSHAKE_CHECK_PATTERN_SERVER_DEFAULT.matcher(str)
				: HANDSHAKE_CHECK_PATTERN_SERVER_DEFAULT.matcher(str);
		if (!m.find())
			if (isServer()) {
				m = HANDSHAKE_CHECK_HTTPS_PATTERN_SERVER_DEFAULT.matcher(str);
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
			info.put(INFO_PATH, m.group(2));
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

		HashMap<String, List<String>> header = new HashMap<String, List<String>>();
		super.setProtocolHeader(header);
		String str = null, first = null;

		while ((str = new String(read('\n'))) != null) {

			str = str.trim();

			String tmp[] = str.split(": ", 2);
			if (tmp.length == 1) {// 当str只切割出一个子串的时候

				if (first == null) {// 条件等同于str为第一行
					first = str;
					continue;
				} else {
					break;// header部分基本读取完成，跳出循环。
				}
			}

			List<String> h = header.get(tmp[0].trim());
			if (h == null) {
				h = new ArrayList<String>();
				header.put(tmp[0].trim(), h);
			}
			h.add(tmp[1].trim());
		}

		if (first == null || !checkHandshake(first)) {
			return false;// 握手失败返回假
		}

		HashMap<String, String> post = new HashMap<String, String>();
		StringBuffer buff = new StringBuffer();
		if (isServer()) {
			String contLen = null;
			if (header.get("Content-Length") != null)
				contLen = header.get("Content-Length").get(0);
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

	/**
	 * 代理协议暂无数据读取
	 */
	@Override
	public byte[] load() throws Throwable {
		return null;
	}

}
