package bucket.network.protocol;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.codec.digest.DigestUtils;

/**
 * WebSocket协议类
 * 
 * @author Hansin1997
 * @version 2017/12/1
 */
public class WebSocketProtocol extends Protocol {

	public static final String POXY_HEADER_SERVER = "^(HTTP)/([0-9]\\.[0-9]) 101 WebSocket Protocol Handshake$";
	public static final String POXY_HEADER_CLIENT = "^(GET) (/[-A-Za-z0-9+&@#/%?=~_|!:,.;]+[-A-Za-z0-9+&@#/%=~_|]) (HTTP)/([0-9]\\.[0-9])$";
	public static final Pattern HANDSHAKE_CHECK_PATTERN_SERVER = Pattern.compile(WebSocketProtocol.POXY_HEADER_CLIENT);
	public static final Pattern HANDSHAKE_CHECK_PATTERN_CLIENT = Pattern.compile(WebSocketProtocol.POXY_HEADER_SERVER);
	public static final String INFO_METHOD = "METHOD";
	public static final String INFO_GET = "GET";
	public static final String INFO_POST = "POST";
	public static final String INFO_PATH = "PATH";
	public static final String[][] ckHeader = new String[][] { { "Upgrade", "websocket" },
			{ "Connection", "Upgrade" } };

	private boolean fin;
	private byte rsv[];
	private byte opc[];
	private boolean mask;
	private long payloadLength;
	private byte maskKey[];
	private byte payload[];

	public WebSocketProtocol(Socket socket) throws IOException {
		super(socket);
		super.setProtocolInfo(new HashMap<String, Object>());
		super.setProtocolHeader(new HashMap<String, String>());
		init();
	}

	public WebSocketProtocol(Socket socket, InputStream in, OutputStream out) throws IOException {
		super(socket, in, out);
		super.setProtocolInfo(new HashMap<String, Object>());
		super.setProtocolHeader(new HashMap<String, String>());
		init();
	}

	// 计算响应密钥
	private String getWebSocketAccept() {
		String webSocketKey = getProtocolHeader().get("Sec-WebSocket-Key");
		if (webSocketKey == null)
			return "";
		byte[] result = DigestUtils.sha1(webSocketKey + "258EAFA5-E914-47DA-95CA-C5AB0DC85B11");
		return Base64.getEncoder().encodeToString(result);
	}

	// 服务端响应头
	private void echoServerHeader() throws Throwable {
		PrintWriter wter = new PrintWriter(getOut());
		wter.println("HTTP/1.1 101 WebSocket Protocol Handshake");
		for (String[] kv : ckHeader) {
			wter.println(kv[0] + ": " + kv[1]);
		}
		String SWA = getWebSocketAccept();
		getProtocolHeader().put("Sec-WebSocket-Accept", SWA);
		wter.println("Sec-WebSocket-Accept: " + SWA);
		wter.println();
		wter.flush();
		getOut().flush();
	}

	// 生成客户端请求头
	private void echoClientHeader() throws Throwable {
		PrintWriter wter = new PrintWriter(getOut());
		wter.println("GET " + getProtocolInfo().get(INFO_PATH) + " HTTP/1.1");
		wter.println("Host: " + getSocket().getInetAddress().getHostName() + ":" + getSocket().getPort());
		for (String[] kv : ckHeader) {
			wter.println(kv[0] + ": " + kv[1]);
		}
		String SWK = DigestUtils.md5Hex(String.valueOf(Math.random()));
		getProtocolHeader().put("Sec-WebSocket-Key", SWK);
		wter.println("Sec-WebSocket-Key: " + SWK);
		wter.println();
		wter.flush();
		getOut().flush();
	}

	private boolean handshakeCheck(String str) throws Throwable {
		Matcher m = isServer() ? HANDSHAKE_CHECK_PATTERN_SERVER.matcher(str)
				: HANDSHAKE_CHECK_PATTERN_CLIENT.matcher(str);

		if (!m.find())
			return false;

		for (String[] kv : ckHeader) {
			if (getProtocolHeader().get(kv[0]) == null || !getProtocolHeader().get(kv[0]).equals(kv[1]))

				return false;
		}

		Map<String, Object> info = getProtocolInfo();
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
			info.put(INFO_GET, get);
			info.put(INFO_PATH, path);
			super.setProtocolName(m.group(3) + " WebSocket");
			super.setProtocolVersion(m.group(4));

			String SWK = getProtocolHeader().get("Sec-WebSocket-Key");
			if (SWK == null)
				return false;

		} else {
			super.setProtocolName(m.group(1) + " WebSocket");
			super.setProtocolVersion(m.group(2));
			String SWA = getProtocolHeader().get("Sec-WebSocket-Accept");
			if (SWA == null || !SWA.equals(getWebSocketAccept()))
				return false;
		}

		return true;
	}

	@Override
	public boolean handshake() throws Throwable {
		getIn().mark(8192 * 8);
		BufferedReader reader = new BufferedReader(new InputStreamReader(getIn()));

		Map<String, String> header = getProtocolHeader();

		if (!isServer())
			echoClientHeader();

		String str = null, first = null;
		while ((str = reader.readLine()) != null) {

			String tmp[] = str.split(":", 2);
			if (tmp.length == 1) {
				if (first == null) {
					first = str;
					continue;
				} else {

					break;// 协议头结束
				}

			}

			header.put(tmp[0].trim(), tmp[1].trim());
		}

		if (!handshakeCheck(first)) {
			getIn().reset();
			return false;// 握手失败
		}
		if (isServer())
			echoServerHeader();

		return true;
	}

	@Override
	public void send(byte[] bytes) throws Throwable {
		build(bytes);
		if (isServer()) {
			serverPush(getOut());
		} else {
			clientPush(getOut());
		}

	}

	@Override
	public byte[] load() throws Throwable {
		if (isServer()) {
			if (serverLoad(getIn())) {
				return decode();
			}
		} else {
			if (clientLoad(getIn()))
				return decode();
		}
		return null;
	}

	private void init() {
		fin = true;
		rsv = null;
		opc = null;
		maskKey = null;
		payload = null;
		payloadLength = 0;
		mask = false;
	}

	private void serverPush(OutputStream outputStream) throws IOException {
		DataOutputStream out = new DataOutputStream(outputStream);

		out.writeByte((byte) 0x81);

		if (payloadLength <= 125) {
			out.writeByte((byte) payloadLength);
		} else if (payloadLength > 125 && payloadLength <= 0xffff) {
			out.writeByte((byte) 126);
			out.writeShort((int) payloadLength);
		} else {
			out.writeByte((byte) 127);
			out.writeLong(payloadLength);
		}

		out.write(payload);
		out.flush();
	}

	private boolean serverLoad(InputStream inputStream) throws IOException {
		init();
		int b;
		byte bs[];
		DataInputStream in = new DataInputStream(inputStream);

		b = in.read();
		if (b == -1)
			return false;
		bs = getBooleanArray((byte) b);

		fin = bs[0] == 1 ? true : false;

		rsv = Arrays.copyOfRange(bs, 1, 4);
		opc = Arrays.copyOfRange(bs, 4, 8);

		b = in.read();
		if (b == -1)
			return false;
		bs = getBooleanArray((byte) b);

		mask = bs[0] == 1 ? true : false;
		byte tmp[] = Arrays.copyOfRange(bs, 1, 8);
		payloadLength = bytes2Long(tmp);

		int lengthOffset = 0;
		if (payloadLength == 126) {
			lengthOffset = 2;
		} else if (payloadLength == 127) {
			lengthOffset = 8;
		}

		if (lengthOffset != 0) {
			tmp = new byte[lengthOffset * 8];
			for (int i = 0; i < lengthOffset; i++) {
				b = in.read();
				if (b == -1)
					return false;
				bs = getBooleanArray((byte) b);
				for (int j = 0; j < 8; j++)
					tmp[i * 8 + j] = bs[j];
			}
			payloadLength = bytes2Long(tmp);
		}

		if (mask) {
			maskKey = new byte[4];
			for (int i = 0; i < 4; i++) {
				b = in.read();
				if (b == -1)
					return false;
				maskKey[i] = (byte) b;
			}
		}

		payload = new byte[(int) payloadLength];
		for (int i = 0; i < payloadLength; i++) {
			b = in.read();
			if (b == -1)
				return false;
			payload[i] = (byte) b;
		}

		if (getOpc() == 8)
			return false;
		return true;
	}

	private boolean clientLoad(InputStream inputStream) throws IOException {
		init();
		int b;
		DataInputStream in = new DataInputStream(inputStream);

		b = in.read();
		if (b == -1)
			return false;
		if (b != 0x81)
			throw new IOException();

		b = in.read();
		if (b == -1)
			return false;

		payloadLength = b;
		if (payloadLength == 126) {
			payloadLength = in.readShort();
		} else if (payloadLength == 127) {
			payloadLength = in.readLong();
		}

		payload = new byte[(int) payloadLength];
		for (int i = 0; i < payloadLength; i++) {
			b = in.read();
			if (b == -1)
				return false;
			payload[i] = (byte) b;
		}

		return true;
	}

	private byte[] decode() {
		if (!mask)
			return payload;
		byte result[] = new byte[(int) payloadLength];
		for (int i = 0; i < payloadLength; i++)
			result[i] = (byte) (payload[i] ^ maskKey[i % 4]);

		return result;
	}

	public String getWebSocketInfo() {
		StringBuffer buff = new StringBuffer();

		buff.append("fin:" + fin);
		buff.append(", mask:" + mask);
		if (rsv != null)
			buff.append(", rsv:" + bytes2Long(rsv));
		if (opc != null)
			buff.append(", opc:" + getOpc());
		if (maskKey != null)
			buff.append(", mask-key:" + Arrays.toString(maskKey));
		buff.append(", payload-length:" + payloadLength);
		buff.append(" payload:" + new String(decode()));
		return buff.toString();

	}

	private long getOpc() {
		return bytes2Long(opc);
	}

	private void clientPush(OutputStream outputStream) throws IOException {
		DataOutputStream out = new DataOutputStream(outputStream);

		byte bs[] = new byte[8];
		bs[0] = (byte) (fin ? 1 : 0);
		for (int i = 1; i <= 3; i++)
			bs[i] = rsv[i - 1];

		for (int i = 4; i <= 7; i++)
			bs[i] = opc[i - 4];
		out.write(bytes2Int(bs));

		if (payloadLength <= 125) {
			bs = getBooleanArray((byte) payloadLength);
			bs[0] = (byte) (mask ? 1 : 0);

			out.write(bytes2Int(bs));
		} else if (payloadLength > 125 && payloadLength <= 0xffff) {
			bs = getBooleanArray((byte) 126);
			bs[0] = (byte) (mask ? 1 : 0);

			out.write(bytes2Int(bs));
			out.writeShort((short) payloadLength);
		} else {
			bs = getBooleanArray((byte) 127);
			bs[0] = (byte) (mask && (maskKey != null) ? 1 : 0);

			out.write(bytes2Int(bs));
			out.writeLong(payloadLength);
		}

		if (mask && maskKey != null) {
			out.write(maskKey);
		}

		out.write(payload);
		out.flush();
	}

	private static long bytes2Long(byte bytes[]) {
		long result = 0;
		for (int i = 0; i < bytes.length; i++) {
			result = result << 1;
			result += bytes[i];
		}
		return result;
	}

	private static int bytes2Int(byte bytes[]) {
		int result = 0;
		for (int i = 0; i < bytes.length; i++) {
			result = result << 1;
			result += bytes[i];
		}
		return result;
	}

	private static byte[] getBooleanArray(byte b) {
		byte[] array = new byte[8];
		for (int i = 7; i >= 0; i--) {
			array[i] = (byte) (b & 1);
			b = (byte) (b >> 1);
		}
		return array;
	}

	public void build(byte data[]) {

		this.payload = data;
		this.fin = true;
		this.mask = false;
		this.maskKey = new byte[] { (byte) 255, (byte) 255, (byte) 255, (byte) 255 };
		this.opc = new byte[] { 0, 0, 0, 1 };
		this.payloadLength = data.length;
		this.rsv = new byte[] { 0, 0, 0 };
	}

}
