package bucket.database.common.bmob.type;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import bucket.database.BucketObject;

public class BmobBase extends BucketObject {

	protected String __type;

	public BmobBase() {
		super();
	}

	@Override
	public Map<String, Object> getFields() throws Exception {
		Map<String, Object> fields = super.getFields();

		Set<Entry<String, Object>> set = fields.entrySet();
		for (Entry<String, Object> kv : set) {
			fit(kv);
		}

		return fields;
	}

	public static void fit(Entry<String, Object> kv) {
		kv.setValue(Fit(kv.getValue()));
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static Object Fit(Object v) {
		if (v instanceof BmobObject) {
			return ((BmobObject) v).toJSON();
		} else if (v instanceof BmobFile) {
			return ((BmobFile) v).toJSON();
		} else if (v instanceof List) {
			List n = new ArrayList<>();
			for (Object V : (List) v) {
				n.add(Fit(V));
			}
			return n;
		} else if (v instanceof BmobACL) {
			return ((BmobACL) v).toString();
		} else {
			return v;
		}
	}

	public String get__type() {
		return __type;
	}

	public void set__type(String __type) {
		this.__type = __type;
	}

}
