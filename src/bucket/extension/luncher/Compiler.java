package bucket.extension.luncher;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

import javax.tools.JavaCompiler;
import javax.tools.JavaCompiler.CompilationTask;

import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

/**
 * 项目编译器
 * 
 * @author Hansin
 *
 */
public class Compiler {

	private JavaCompiler compiler;
	private StandardJavaFileManager manager;
	private File file;
	private String bin;
	private String src;
	private String luncherClass;
	private ArrayList<String> ops;

	/**
	 * 
	 * @param src
	 *            项目路径
	 * @param luncher
	 *            启动文件名
	 * @param bin
	 *            编译输出路径
	 * @throws Exception
	 */
	public Compiler(String src, String luncher, String bin) throws Exception {

		compiler = ToolProvider.getSystemJavaCompiler();
		if (compiler == null)
			throw new Exception("JavaCompiler is unable,plese run on jdk 1.8");

		manager = compiler.getStandardFileManager(null, null, null);
		file = new File(src + "/" + luncher);
		this.bin = bin;
		this.src = src;
		luncherClass = getFileFrontName(luncher).replace('\\', '/').replace('/', '.');
		ops = new ArrayList<String>();
		ops.add("-Xlint:unchecked");
		ops.add("-d");
		ops.add(bin);
		ops.add("-sourcepath");
		ops.add(this.src);
		ops.add("-encoding");
		ops.add("UTF-8");

	}

	/**
	 * 编译
	 * 
	 * @return
	 * @throws RuntimeException
	 */
	public boolean compile() throws RuntimeException {
		Iterable<? extends JavaFileObject> it = manager.getJavaFileObjects(file);
		CompilationTask task = compiler.getTask(null, manager, null, ops, null, it);
		return task.call();
	}

	public void clean() throws Exception {
		File file = new File(bin);
		File[] files = file.listFiles();
		for (File f : files)
			f.delete();
	}

	public Class<?> rebuild() throws Exception {
		clean();
		compile();
		return load();
	}

	/**
	 * 载入启动类
	 * 
	 * @return
	 * @throws Exception
	 */
	public Class<?> load() throws Exception {
		File path = new File(bin);
		URLClassLoader loader = new URLClassLoader(new URL[] { path.toURI().toURL() });
		List<String> list = listAllFilename(bin, null);
		for (String f : list) {
			loader.loadClass(getFileFrontName(f));
		}
		Class<?> result = loader.loadClass(luncherClass);
		loader.close();

		return result;

	}

	/**
	 * 获取不带后缀的文件名
	 * 
	 * @param filename
	 * @return
	 */
	public static String getFileFrontName(String filename) {
		int i = filename.lastIndexOf(".");
		if (i == -1)
			return filename;
		return filename.substring(0, i);
	}

	/**
	 * 获取目录下所有类文件
	 * 
	 * @param path
	 * @param root
	 * @return
	 */
	public static List<String> listAllFilename(String path, String root) {
		File f = new File(path);
		if (root == null)
			root = "";
		ArrayList<String> result = new ArrayList<>();
		if (f.isDirectory()) {
			File[] files = f.listFiles();
			for (File file : files) {
				if (file.isDirectory()) {
					result.addAll(listAllFilename(file.getPath(), file.getName() + "."));
				} else {
					result.add(root + file.getName());
				}
			}
			return result;
		} else {
			result.add(root + f.getName());
			return result;
		}
	}
}
