package bucket.database.common.bmob.type;

public class BmobUser extends BmobObject {

	public static final String CLASS_NAME = "_User"; 
	
	protected String username;
	protected String password;

	protected Boolean emailVerified;
	protected String email;

	protected String mobilePhoneNumber;
	protected Boolean mobilePhoneNumberVerified;

	public BmobUser() {
		super();
		setClassName(CLASS_NAME);
		setTableName(CLASS_NAME);
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Boolean getEmailVerified() {
		return emailVerified;
	}

	public Boolean getMobilePhoneNumberVerified() {
		return mobilePhoneNumberVerified;
	}

	public String getMobilePhoneNumber() {
		return mobilePhoneNumber;
	}

	public void setMobilePhoneNumber(String mobilePhoneNumber) {
		this.mobilePhoneNumber = mobilePhoneNumber;
	}

}
