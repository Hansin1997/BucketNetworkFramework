package bucket.network.protocol;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.HashMap;

/**
 * Bucket简易协议
 * 
 * @author Hansin1997
 * @version 2017/12/4
 */
public class BucketProtocol extends Protocol {

	public BucketProtocol(Socket socket) throws IOException {
		super(socket);
		setProtocolName("BUCKET");
		setProtocolVersion("1.0");
		setProtocolHeader(new HashMap<String, String>());
		setProtocolInfo(new HashMap<String, Object>());
	}

	public BucketProtocol(Socket socket, InputStream in, OutputStream out) throws IOException {
		super(socket, in, out);
		setProtocolName("BUCKET");
		setProtocolVersion("1.0");
		setProtocolHeader(new HashMap<String, String>());
		setProtocolInfo(new HashMap<String, Object>());
	}

	@Override
	public boolean handshake() throws Throwable {
		getIn().mark(8192);
		if (isServer()) {
			BufferedReader reader = new BufferedReader(new InputStreamReader(getIn()));

			String str = null;
			str = reader.readLine();
			if (!str.equals("BUCKET/1.0")) {
				getIn().reset();
				return false;
			}

		} else {
			write("BUCKET/1.0\n".getBytes());
			flush();
		}

		return true;
	}

	@Override
	public void send(byte[] bytes) throws Throwable {

		write(String.valueOf(bytes.length + "\n").getBytes());
		write(bytes);
		flush();
	}

	@Override
	public byte[] load() throws Throwable {
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		String str = new String(read('\n'));
		int length = Integer.valueOf(str);

		byte bf[] = new byte[255];
		int b = 0;

		for (int i = 0; i < length; i += bf.length) {
			b = read(bf);
			if (b == -1)
				break;
			else
				bout.write(bf, 0, b);
		}

		bout.flush();
		return bout.toByteArray();
	}

}
