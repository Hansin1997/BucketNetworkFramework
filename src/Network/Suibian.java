package Network;

class Suibian
{
	public String name;
	public String address;
	public int ID = 100;
	
	public Suibian(String name,String address)
	{
		setAddress(address);
		setName(name);
	}
	
	public void setAddress(String address) {
		this.address = address;
	}
	
	public String getAddress() {
		return address;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
}