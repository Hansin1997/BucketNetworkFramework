package bucket.database.common.bmob.type;

import java.lang.reflect.Type;
import java.util.HashMap;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.reflect.TypeToken;

public class BmobACL extends HashMap<String, HashMap<String, Boolean>> {

	public static final String ACCESS_READ = "read";
	public static final String ACCESS_WRITE = "write";
	public static final String ACCESS_PUBLIC = "*";

	/**
	 * 
	 */
	private static final long serialVersionUID = -6263075004149626959L;

	public BmobACL() {
		setPublicReadAccess(true);
		setPublicWriteAccess(true);
	}

	public void setPublicReadAccess(boolean access) {
		setAccess(ACCESS_PUBLIC, ACCESS_READ, access);
	}

	public void setPublicWriteAccess(boolean access) {
		setAccess(ACCESS_PUBLIC, ACCESS_WRITE, access);
	}

	public boolean getPublicReadAccess() {
		return getAccess(ACCESS_PUBLIC, ACCESS_READ);
	}

	public boolean getPublicWriteAccess() {
		return getAccess(ACCESS_PUBLIC, ACCESS_WRITE);
	}

	/**
	 * 获取某人某个权限
	 * 
	 * @param who
	 *            某人
	 * @param access
	 *            某权限
	 * @return 是否有该权限
	 */
	public boolean getAccess(String who, String access) {
		HashMap<String, Boolean> m = get(who);
		if (m == null || m.get(access) == null)
			return false;
		else
			return m.get(access);
	}

	/**
	 * 设置某人某个权限
	 * 
	 * @param who
	 *            某人
	 * @param access
	 *            某权限
	 * @param ac
	 *            是否有该权限
	 */
	public void setAccess(String who, String access, boolean ac) {
		HashMap<String, Boolean> m = get(who);
		if (m == null)
			m = new HashMap<>();
		m.put(access, ac);
		put(who, m);
	}

	@Override
	public String toString() {
		Gson gson = new GsonBuilder().create();
		return gson.toJson(this);
	}

	public static class BmobAclAdapter implements JsonSerializer<BmobACL>, JsonDeserializer<BmobACL> {

		@Override
		public BmobACL deserialize(JsonElement arg0, Type arg1, JsonDeserializationContext arg2)
				throws JsonParseException {
			Type type = new TypeToken<HashMap<String, HashMap<String, Boolean>>>() {
			}.getType();
			Gson gson = new Gson();
			HashMap<String, HashMap<String, Boolean>> m = gson.fromJson(arg0, type);
			BmobACL acl = new BmobACL();
			acl.clear();
			acl.putAll(m);
			return acl;
		}

		@Override
		public JsonElement serialize(BmobACL arg0, Type arg1, JsonSerializationContext arg2) {
			Gson gson = new Gson();
			return gson.fromJson(arg0.toString(), JsonElement.class);
		}

	}
}
