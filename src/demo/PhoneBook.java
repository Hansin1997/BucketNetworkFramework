package demo;

import bucket.database.BucketObject;

/**
 * 电话本类
 * 
 * @author Hansin
 *
 */
public class PhoneBook extends BucketObject {

	public String name;
	public String phone;
	public String QQ;
	public String nickname;
	public String address;
	public byte[] bytes;

	public PhoneBook() {
		super();
	}

}