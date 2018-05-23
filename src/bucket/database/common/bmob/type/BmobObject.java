package bucket.database.common.bmob.type;

import java.util.Date;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class BmobObject extends BmobPointer {

	protected static final String TYPE = "Object";
	protected BmobACL ACL;
	protected Date createdAt;
	protected Date updatedAt;

	public BmobObject() {
		super();
		setClassName(this.getClass().getSimpleName());
		set__type(TYPE);
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public Date getUpdatedAt() {
		return updatedAt;
	}

	public BmobACL getACL() {
		return ACL;
	}

	public void setACL(BmobACL aCL) {
		ACL = aCL;
	}

	@Override
	public Gson getGson() {
		return new GsonBuilder().registerTypeAdapter(BmobACL.class, new BmobACL.BmobAclAdapter()).create();
	}

}
