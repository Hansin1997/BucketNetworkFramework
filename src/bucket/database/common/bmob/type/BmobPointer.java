package bucket.database.common.bmob.type;

import java.util.Map;

/**
 * BmobPointer
 * 
 * @author Hansin
 *
 */
public class BmobPointer extends BmobBase {

	protected static final String TYPE = "Pointer";

	protected String objectId;
	protected String className;

	@Override
	public Object getId() {
		if (objectId != null)
			return objectId;
		else
			return super.getId();
	}

	@Override
	public void setId(Object id) {
		if (id != null)
			setObjectId(id.toString());
		super.setId(id);
	}

	public BmobPointer() {
		super();
		setClassName(className);
		set__type(TYPE);
	}

	@Override
	public Map<String, Object> getFields() throws Exception {
		Map<String, Object> fs = super.getFields();
		fs.remove("className");
		return fs;
	}

	public BmobPointer(BmobObject bmobObject) {
		setObjectId(bmobObject.getObjectId());
		setClassName(bmobObject.getClassName());
		set__type(TYPE);
	}

	public void setObjectId(String objectId) {
		this.objectId = objectId;
	}

	public String getObjectId() {
		return objectId;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	@Override
	public String getTableName() {
		if (getClassName() != null)
			return getClassName();
		else
			return super.getTableName();
	}

}
