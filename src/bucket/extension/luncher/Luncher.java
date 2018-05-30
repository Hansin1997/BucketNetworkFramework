package bucket.extension.luncher;

import java.io.FileNotFoundException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import bucket.extension.luncher.FileChangeListener.EventListener;
import bucket.extension.luncher.Luncher.LuncherRunnable.LuncherListener;
import bucket.network.Server;
import bucket.util.CMD;
import bucket.util.Log;

/**
 * 服务启动器
 * 
 * @author Hansin
 *
 */
public class Luncher {
	public static CMD cmd;
	private static Class<?> c;
	private static Server server;
	private static Compiler compiler;
	private static Thread t;
	private static int port;
	private static FileChangeListener fcl;

	public static void main(String[] args) {
		cmd = new CMD(args);
		Log.setDebugging(cmd.isCmdExExist("debug"));

		Method[] mds = Luncher.Console.class.getMethods();

		List<String> list = new ArrayList<>();
		for (Method m : mds)
			if (Modifier.isStatic(m.getModifiers()))
				list.add(m.getName());
		String[] methods = new String[list.size()];
		list.toArray(methods);

		try {
			compiler = new Compiler(cmd.getCmdEx("src"), cmd.getCmdEx("luncher"), cmd.getCmdEx("bin"));
			Console.compile();
			Console.load();
			Console.start();

			if (Log.isDebugging()) {
				fcl = createRebuildThread();
				fcl.start();
			}

			Scanner s = new Scanner(System.in);
			System.out.print("BucketEx>");
			while (s.hasNext()) {
				String line = s.nextLine();
				if (line.equals("compile")) {
					Console.compile();
				} else if (line.equals("load")) {
					Console.load();
				} else if (line.equals("start")) {
					Console.start();
				} else if (line.equals("stop")) {
					Console.stop();
				} else if (line.equals("exit")) {
					System.exit(0);
				} else if (line.equals("clean")) {
					Console.clean();
				} else if (line.equals("rebuild")) {
					Console.rebuild();
				} else {
					Log.echo("Command List", methods);
				}
				System.out.print("BucketEx>");

			}
			s.close();
		} catch (Exception e) {
			Log.e(e);
		}
	}

	public static class Console {
		public static void compile() {
			try {
				if (compiler.compile())
					Log.p("compile success");
				else
					Log.e("compile fail");

			} catch (Exception e) {
				Log.e(e);
			}
		}

		public static void load() {
			try {
				c = compiler.load();
				Log.p("loaded");
			} catch (Exception e) {
				Log.e("load Fail", e.toString());
			}
		}

		public static void start() throws FileNotFoundException {
			if (t != null) {
				Log.e("Task running,please stop first.");
				return;
			}
			if (!cmd.isCmdExExist("port")) {
				port = 8080;
			} else {
				try {
					port = Integer.valueOf(cmd.getCmdEx("port"));
				} catch (Exception e) {
					Log.e(e);
					port = 8080;
				}
			}
			Log.p("Start with port: " + port);
			t = createLuncherThread(c);
			t.start();
		}

		public static void stop() {
			if (t == null) {
				Log.d("Task aready stop.");
			} else {
				server.stop();
				t.interrupt();
				t = null;
				server = null;
			}
		}

		public static void clean() {
			try {
				compiler.clean();
				Log.d("Clean success");
			} catch (Exception e) {
				Log.e("Clean fail", e.toString());
			}
		}

		public static void rebuild() {
			try {
				stop();
				c = compiler.rebuild();
				Log.p("Rebuild success");
				start();
			} catch (Exception e) {
				Log.e("Rebuild fail", e.toString());
			}
		}

	}

	public static FileChangeListener createRebuildThread() throws FileNotFoundException {
		return new FileChangeListener(cmd.getCmdEx("src"), new EventListener() {
			@Override
			public boolean onFileChanged() {
				Console.rebuild();
				return false;
			}
		});
	}

	public static Thread createLuncherThread(Class<?> c) {
		return new Thread(new LuncherRunnable(c, new LuncherListener() {

			@Override
			public void onFinished() {
				Log.d("BucketEx Finish");
				t = null;
			}

			@Override
			public void onException(Exception e) {
				Log.e(e);
				t = null;
			}

			@Override
			public void onStart() {
				Log.d("BucketEx Running");
			}
		}));
	}

	public static class LuncherRunnable implements Runnable {

		private Class<?> clazz;
		LuncherListener listener;

		public LuncherRunnable(Class<?> clazz, LuncherListener listener) {
			this.clazz = clazz;
			this.listener = listener;
		}

		@Override
		public void run() {
			try {
				server = (Server) clazz.newInstance();
				listener.onStart();
				server.start(new ServerSocket(port));
				listener.onFinished();
			} catch (Exception e) {
				listener.onException(e);
			}
		}

		public static interface LuncherListener {

			void onStart();

			void onException(Exception e);

			void onFinished();
		}

	}

	

}
