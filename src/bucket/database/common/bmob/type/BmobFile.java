package bucket.database.common.bmob.type;

public class BmobFile extends BmobBase {

	public static final String TYPE = "File";

	protected String filename;
	protected String group;
	protected String url;

	public BmobFile() {
		set__type(TYPE);
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

}
