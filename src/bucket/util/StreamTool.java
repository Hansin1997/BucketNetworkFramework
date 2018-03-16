package bucket.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

/**
 * 输入输出流工具
 * 
 * @author Hansin
 *
 */
public class StreamTool {

	/**
	 * 读一行数据
	 * 
	 * @param in
	 * @return
	 * @throws IOException
	 */
	public static byte[] readLine(InputStream in) throws IOException {
		ByteArrayOutputStream o = new ByteArrayOutputStream();

		int b, len;
		final byte[] strbyte = "\r\n".getBytes();
		byte[] data = new byte[strbyte.length];

		// 首先进行第一次读取，将data存满
		len = 0;// 标记已读长度
		do {
			b = in.read(data, 0, data.length - len);

			// 这里处理数据太短，data不满就结束读取的情况
			if (b == -1) {
				if (len == 0)
					return null;
				else if (Arrays.equals(strbyte, data))
					return new byte[0];
				else
					return data;
			} else {
				len += b;
			}

		} while (len < data.length);

		while (true) {
			if (Arrays.equals(strbyte, data)) {
				break;
			}
			b = in.read();
			if (b == -1) {
				if (Arrays.equals(strbyte, data)) {
					break;
				} else {
					o.write(data, 0, data.length);
					break;
				}
			} else {
				o.write(data[0]);
				for (int i = 0; i < data.length - 1; i++) {
					data[i] = data[i + 1];
				}
				data[data.length - 1] = (byte) b;
			}

		}
		o.flush();
		byte[] d = o.toByteArray();
		if (b == -1)
			return null;
		else
			return d;
	}
}
