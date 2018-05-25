package bucket.database;

import java.util.HashMap;
import java.util.Map;

public class DatabaseObject extends BucketObject {

	Map<String, Object> fields;

	@Override
	public Map<String, Object> getFields() throws Exception {
		Map<String, Object> m = new HashMap<>();
		m.putAll(fields);
		return m;
	}

	@Override
	public void setFields(Map<String, Object> fields) throws Exception {
		fields.remove("id");
		this.fields = fields;
	}

	public Map<String, Object> getTrueFiedls() {
		return this.fields;
	}

	@Override
	public String toJSON() {
		return getGson().toJson(fields);
	}
}
