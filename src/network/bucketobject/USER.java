package network.bucketobject;

import Common.Tool;

public class USER {

	public String username;
	public String password;

	public String nickname;
	public int type;

	public USER(String username, String password, String nickname, int type) {

		setNickname(nickname);
		setPassword(password);
		setType(type);
		setUsername(username);

	}

	public USER(String username, String password) {

		this(username, password,"",0);

	}

	public USER() {
		this("", "", "", 0);
	}

	public void setType(int type) {
		this.type = type;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public int getType() {
		return type;
	}

	public String getNickname() {
		return nickname;
	}

	public String getPassword() {
		return password;
	}

	public String getUsername() {
		return username;
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return Tool.toJson(this);
	}

}
