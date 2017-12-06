package Test;

import java.io.Serializable;

public class TestBean {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public String name;
	public int year;
	public double offset;
	
	public TestBean() {
		setName("Hansin");
		setOffset(3.1415);
		setYear(20);
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
}
