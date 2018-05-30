package bucket.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 命令解析器
 * 
 * @author Hansin
 *
 */
public class CMD {

	private List<String> cmd;
	private Map<String, String> map;

	public CMD(String[] args) {
		cmd = new ArrayList<>();
		map = new HashMap<>();

		for (int i = 0; i < args.length; i++) {
			if (args[i].length() > 1 && args[i].charAt(0) == '-') {
				if (i < args.length - 1) {

					if (args[i + 1].length() > 1 && args[i + 1].charAt(0) == '-') {
						map.put(args[i].substring(1), null);
					} else {
						map.put(args[i].substring(1), args[++i]);
					}
				} else {
					map.put(args[i].substring(1), null);
				}
			} else {
				cmd.add(args[i]);
			}
		}
	}

	public String getCmd(int index) {
		return cmd.get(index);
	}

	public int getCmdLength() {
		return cmd.size();
	}

	public String getCmdEx(String key) {
		return map.get(key);
	}

	public boolean isCmdExExist(String key) {
		return map.containsKey(key);
	}

	public String[] getCMD() {
		String[] r = new String[cmd.size()];
		cmd.toArray(r);
		return r;
	}

	@Override
	public String toString() {
		return "cmd: " + cmd.toString() + ",map: " + map.toString();
	}
}