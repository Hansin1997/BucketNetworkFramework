package bucket.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 调试工具
 * 
 * @author Hansin
 *
 */
public class Log {

	private static boolean isDebugging;

	public static final String line = "-------------------------------------------------------------";

	/**
	 * 输出调试信息(无论是否调试还是发布)
	 * 
	 * @param strings
	 */
	public static void p(String... strings) {
		System.out.println(line);
		System.out.println("Print At " + date());
		System.out.println();
		for (String str : strings)
			System.out.println(str);
		System.out.println(line);
	}

	/**
	 * 输出调试信息(仅在调试状态下)
	 * 
	 * @param strings
	 */
	public static void d(String... strings) {
		if (isDebugging()) {
			System.out.println(line);
			System.out.println("Log   At " + date());
			System.out.println();
			for (String str : strings)
				System.out.println(str);
			System.out.println(line);
		}
	}

	/**
	 * 打印错误信息
	 * 
	 * @param throwables
	 */
	public static void e(Throwable... throwables) {
		if (isDebugging()) {
			System.err.println(line);
			System.err.println("Error At " + date());
			System.err.println();
			for (Throwable e : throwables)
				e.printStackTrace();
			System.err.println(line);
		} else {
			System.err.println(line);
			System.err.println("Error At " + date());
			System.err.println();
			for (Throwable e : throwables)
				System.err.println(e);
			System.err.println(line);
		}
	}

	/**
	 * 打印错误信息
	 * 
	 * @param throwables
	 */
	public static void e(String... strs) {
		System.err.println(line);
		System.err.println("Error At " + date());
		System.err.println();
		for (String str : strs)
			System.err.println(str);
		System.err.println(line);
	}

	@SafeVarargs
	public static <T extends Object> void echo(String title, T... msg) {
		System.out.println(line);
		System.out.println(title);
		System.out.println();
		for (Object obj : msg)
			System.out.println(obj);
		System.out.println(line);
	}

	/**
	 * 是否调试状态
	 * 
	 * @return
	 */
	public static boolean isDebugging() {
		return isDebugging;
	}

	public static void setDebugging() {
		isDebugging = true;
	}

	public static void setDebugging(boolean value) {
		isDebugging = value;
	}

	/**
	 * 获取现在时间
	 * 
	 * @return
	 */
	public static String date() {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return (df.format(new Date()));
	}

}