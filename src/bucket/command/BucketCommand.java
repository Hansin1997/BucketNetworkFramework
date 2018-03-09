package bucket.command;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Bucket命令类 本类都有execute()方法用作命令执行
 * 
 * @author Hansin1997
 * @version 2017/12/4
 */
public abstract class BucketCommand {

	/**
	 * 执行
	 */
	public abstract void execute();
	
	public String toJSON() {
		Gson gson = new GsonBuilder().create();
		return gson.toJson(this);
	}

}
