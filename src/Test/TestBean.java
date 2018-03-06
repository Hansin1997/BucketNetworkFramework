package Test;

import bucket.database.BucketObject;
import bucket.database.Database;
import bucket.database.Mongo;

public class TestBean extends BucketObject{

	public String name;
	public int year;
	public double offset;
	protected int haha = 10;
	
	public TestBean() {
		super();
//		setName("Hansin");
//		setYear(22);
//		setOffset(3.1415);
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public void setOffset(double offset) {
		this.offset = offset;
	}
	
	public double getOffset() {
		return offset;
	}
	
	public void setYear(int year) {
		this.year = year;
	}
	
	public int getYear() {
		return year;
	}
	
	public String toJson() {
		return name + " " + year;
	}
	
	public static void main(String[] args) throws Exception {
		Database db;
		db = new Mongo("127.0.0.1", 27017);
		db.connect();

		db.useDb("db1");

		
		
		db.close();
	}
}
