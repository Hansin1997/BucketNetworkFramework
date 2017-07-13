package network.bucketobject;

public class Data {
	public String key;
	public Object value;

	public Data(String key, Object value) {
		setKey(key);
		setValue(value);
	}

	public Data() {
		this("", "");
	}

	public String getKey() {
		return key;
	}

	public Object getValue() {
		return value;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public void setValue(Object value) {
		this.value = value;
	}

}
