package bucket.network.protocol;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Bucket简易协议
 * 
 * @author Hansin1997
 * @version 2017/12/4
 */
public class BucketProtocol extends Protocol {

	/**
	 * 默认构造函数
	 */
	public BucketProtocol() {
		super();
		setProtocolName("BUCKET");
		setProtocolVersion("1.0");
		setProtocolHeader(new HashMap<String, List<String>>());
		setProtocolInfo(new HashMap<String, Object>());
	}

	/**
	 * 构造函数
	 * 
	 * @param socket
	 *            套接字对象
	 * @throws IOException
	 */
	public BucketProtocol(Socket socket) throws IOException {
		super(socket);
		setProtocolName("BUCKET");
		setProtocolVersion("1.0");
		setProtocolHeader(new HashMap<String, List<String>>());
		setProtocolInfo(new HashMap<String, Object>());
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
	public BucketProtocol(Socket socket, InputStream in, OutputStream out) throws IOException {
		super(socket, in, out);
		setProtocolName("BUCKET");
		setProtocolVersion("1.0");
		setProtocolHeader(new HashMap<String, List<String>>());
		setProtocolInfo(new HashMap<String, Object>());
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean handshake() throws Throwable {
		getIn().mark(8192);
		Gson gson = new GsonBuilder().create();
		if (isServer()) {

			String str = null;
			str = new String(read('\n'), getEncode()).trim();
			if (!str.equals("BUCKET/1.0")) {
				getIn().reset();
				return false;
			}

			if ((str = new String(read('\n'), getEncode()).trim()) != null) {
				Map<String, ?> m = null;
				try {
					m = gson.fromJson(str, Map.class);
				} catch (Exception e) {
					e.printStackTrace();
				}
				if (m != null) {
					protocolInfo.putAll(m);
				}
			}

		} else {
			write("BUCKET/1.0\n".getBytes(getEncode()));
			if (getProtocolInfo() != null)
				write((gson.toJson(getProtocolInfo()) + "\n").getBytes(getEncode()));
			else
				write("\n".getBytes(getEncode()));
			flush();

		}

		return true;
	}

	@Override
	public void send(byte[] bytes) throws Throwable {
		int length = bytes.length;
		write((length + "\n").getBytes(getEncode()));
		for (int i = 0; i < length; i++)
			write(bytes[i]);
		flush();
	}

	@Override
	public byte[] load() throws Throwable {

		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		byte[] line = read('\n');
		if (line == null)
			return null;
		String str = new String(line, getEncode());

		int length;
		try {
			length = Integer.valueOf(str);
		} catch (NumberFormatException e) {
			return load();
		}

		byte bf[] = new byte[255];
		int b = 0;

		for (int i = 0; i < length; i += b) {
			b = read(bf);
			if (b == -1)
				break;
			else
				bout.write(bf, 0, b);
		}

		bout.flush();
		byte[] data = bout.toByteArray();
		if (data.length == 0)
			return null;
		return data;
	}

}
