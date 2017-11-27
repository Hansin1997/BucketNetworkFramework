package network.connection;

import java.sql.SQLException;
import java.util.ArrayList;

import com.google.gson.JsonArray;

import Common.Tool;
import Database.DatabaseManager;
import network.bucketmodle.USER;
import network.bucketobject.Query;
import network.bucketobject.QueryResult;

public class Checker {
	
	public String command;
	public USER user;
	
	public Checker(USER user)
	{
		this.user = user;
	}
	
	public void setCommand(String command) {
		this.command = command;
	}
	
	public void setUser(USER user) {
		this.user = user;
	}
	
	public String getCommand() {
		return command;
	}
	
	public USER getUser() {
		return user;
	}
	
	public static Checker createLogin(USER u)
	{
		Checker re = new Checker(u);
		re.setCommand("LOGIN");
		return re;
	}
	
	public static Checker createSignin(USER u)
	{
		Checker re = new Checker(u);
		re.setCommand("SIGNIN");
		return re;
	}
	
	public USER doCheck(DatabaseManager db)
	{
		if(command == null || user ==null)
			return null;
		
		USER re = null;


		Query q = new Query();
		q.addQuery("username", "=\'" + user.username + "\'");
		q.setTable_name(USER.class.getSimpleName());
		QueryResult result = null;
		switch(command)
		{
			case "LOGIN":
				q.addQuery("password", "=\'" + user.password + "\'");
				result = db.Query(q);

				if(result.getCount() > 0)
					re = Tool.object2E(result.getResults().get(0), USER.class);
				break;
			case "SIGNIN":
				q.setJustCount(true);
				result = db.Query(q);
				if(result.getCount() == 0)
				{
					user.setType(1);
					ArrayList<USER> tmp = new ArrayList<USER>();
					tmp.add(user);
					JsonArray array = Tool.object2E(tmp, JsonArray.class);
					try {
						db.SQLexecute(Tool.arrayInsert2SQL(USER.class.getSimpleName(),array ));
						re = user;
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
				break;
		}
		
		return re;
	}
	
	public String toJSON()
	{
		return Tool.toJson(this);
	}
	
}
