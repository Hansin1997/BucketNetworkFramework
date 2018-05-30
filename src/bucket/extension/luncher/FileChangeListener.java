package bucket.extension.luncher;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class FileChangeListener extends Thread {

	private File file;
	private long interval;
	private Map<String, Long> record;
	private EventListener listener;

	public FileChangeListener(String pathname, EventListener eventListener) throws FileNotFoundException {
		file = new File(pathname);
		if (!file.exists())
			throw new FileNotFoundException("File '" + pathname + "' not found!");

		interval = 1000;
		listener = eventListener;
	}

	public void setInterval(long interval) {
		this.interval = interval;
	}

	@Override
	public void run() {

		try {
			reset(LastModified(file));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		while (!isInterrupted()) {

			try {
				Thread.sleep(interval);
			} catch (InterruptedException e) {
				break;
			}

			try {
				Map<String, Long> newrecord = LastModified(file);

				if (isChange(newrecord)) {
					if (listener.onFileChanged()) {

					} else {
						reset(newrecord);
					}
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
	}

	private boolean isChange(Map<String, Long> target) {
		Set<Entry<String, Long>> set = record.entrySet();
		for (Entry<String, Long> kv : set) {
			Long t = target.get(kv.getKey());
			if (t == null || !t.equals(kv.getValue()))
				return true;
		}
		return false;
	}

	private void reset(Map<String, Long> newRecord) {
		record = newRecord;
	}

	public static Map<String, Long> LastModified(File file) throws FileNotFoundException {
		List<File> ls = ListFile(file);
		int subLength = ((file.isDirectory()) ? file.getPath() + File.separator : file.getParent() + File.separator)
				.length();

		Map<String, Long> result = new HashMap<>();
		for (File f : ls) {
			result.put(f.getPath().substring(subLength), f.lastModified());
		}
		return result;
	}

	public static List<File> ListFile(File file) throws FileNotFoundException {
		if (!file.exists())
			throw new FileNotFoundException(file.getPath());
		List<File> result = new ArrayList<>();
		if (file.isDirectory()) {

			File[] fs = file.listFiles();
			for (File f : fs)
				if (f.isDirectory())
					result.addAll(ListFile(f));
				else
					result.add(f);

		} else {
			result.add(file);
		}
		return result;
	}

	public static interface EventListener {
		/**
		 * 文件更改时触发
		 * 
		 * @return 是否跳过重置
		 */
		boolean onFileChanged();
	}
}
