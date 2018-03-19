package bucket.network.protocol;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;

public class NoneProtocol extends Protocol {

	public NoneProtocol() {
		super();
		setProtocolName("NONE");
		setProtocolVersion("0.0");
		setProtocolHeader(new HashMap<String, List<String>>());
		setProtocolInfo(new HashMap<String, Object>());
	}

	public NoneProtocol(Socket socket) throws IOException {
		super(socket);
		setProtocolName("NONE");
		setProtocolVersion("0.0");
		setProtocolHeader(new HashMap<String, List<String>>());
		setProtocolInfo(new HashMap<String, Object>());
	}

	public NoneProtocol(Socket socket, InputStream in, OutputStream out) throws IOException {
		super(socket, in, out);
		setProtocolName("NONE");
		setProtocolVersion("0.0");
		setProtocolHeader(new HashMap<String, List<String>>());
		setProtocolInfo(new HashMap<String, Object>());
	}

	@Override
	public boolean handshake() throws Throwable {
		return true;
	}

	@Override
	public void send(byte[] bytes) throws Throwable {
		getOut().write(bytes);
		flush();
	}

	@Override
	public byte[] load() throws Throwable {
		if (getSocket() == null || getSocket().isClosed())
			return null;
		try {
			Thread.sleep(500);
		} catch (Exception e) {
		}

		return new byte[0];

		// byte[] data = new byte[256];
		// int b = read(data, 0, data.length);
		// if (b == -1)
		// return null;
		// return Arrays.copyOf(data, b);
	}

}
