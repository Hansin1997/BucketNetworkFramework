package Database;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import network.bucketmodle.USER;
import network.bucketobject.ChangeQuery;
import network.bucketobject.DeleteQuery;
import network.bucketobject.Query;
import network.bucketobject.QueryResult;

public class DatabaseManager {

	private Connection conn;

	private String mysql_host;
	private String mysql_username;
	private String mysql_password;
	private int mysql_port;
	private String mysql_database;

	private final String USERS_TABLE = "USER";

	public DatabaseManager() {
		mysql_host = "localhost";
		mysql_username = "root";
		mysql_password = "";
		mysql_port = 3306;
		mysql_database = "bucket";
	}

	public DatabaseManager(String MySQL_Username, String MySQL_Password) {
		mysql_host = "localhost";
		mysql_username = MySQL_Username;
		mysql_password = MySQL_Password;
		mysql_port = 3306;
		mysql_database = "bucket";
	}

	public DatabaseManager(String MySQL_Username, String MySQL_Password, String DataBase) {
		mysql_host = "localhost";
		mysql_username = MySQL_Username;
		mysql_password = MySQL_Password;
		mysql_port = 3306;
		mysql_database = DataBase;
	}

	public DatabaseManager(String MySQL_HOST, String MySQL_Username, String MySQL_Password, int MySQL_Port,
			String DataBase) {
		mysql_host = MySQL_HOST;
		mysql_username = MySQL_Username;
		mysql_password = MySQL_Password;
		mysql_port = MySQL_Port;
		mysql_database = DataBase;
	}

	public void ConnectMySQL() throws ClassNotFoundException, SQLException {
		if(conn != null && !conn.isClosed())
			conn.close();
		
		Class.forName("com.mysql.jdbc.Driver");
		String url = "jdbc:mysql://" + mysql_host + ":" + mysql_port + "/" + mysql_database
				+ "?useUnicode=true&characterEncoding=utf-8";
		conn = DriverManager.getConnection(url, mysql_username, mysql_password);

	}

	public void CloseMySQL() {
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public boolean ChangeUserPassword(String username, String password, String newpassword) {
		if (Check(username, password) == 1) {
			String sql = "update " + USERS_TABLE + " set password = ? where username = ? and password = ?;";
			try {
				PreparedStatement stmt2 = conn.prepareStatement(sql);
				stmt2.setString(1, newpassword);
				stmt2.setString(2, username);
				stmt2.setString(3, password);
				stmt2.executeUpdate();

				stmt2.close();

				return true;
			} catch (SQLException e) {
				return false;
			}

		} else {
			return false;
		}
	}

	public int Check(String username, String password) {
		try {
			Statement stmt = conn.createStatement();
			String sql = "select * from " + USERS_TABLE;
			ResultSet rs = stmt.executeQuery(sql);

			int[] columnIndex = new int[] { rs.findColumn("username"), rs.findColumn("password") };
			while (rs.next()) {
				if (username.equals(rs.getObject(columnIndex[0]))) {
					if (password.equals(rs.getObject(columnIndex[1]))) {
						return 1;
					} else {
						return -1;
					}

				}
			}
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
			return -2;
		}
		return 0;
	}
	
	public void Delete(DeleteQuery query)
	{

		try {

			Statement stmt = conn.createStatement();
			stmt.execute(query.toSQL());

			stmt.close();
		} catch (SQLException e) {

		}
		
	}
	
	public void Change(ChangeQuery query)
	{

		try {

			Statement stmt = conn.createStatement();
			stmt.execute(query.toSQL());

			stmt.close();
		} catch (SQLException e) {

		}
		
	}

	public QueryResult Query(Query query) {
		QueryResult result = new QueryResult();
		ArrayList<JsonObject> array = new ArrayList<JsonObject>();
		boolean isUserTable = query.getTable_name().equals(USER.class.getSimpleName());
		try {
			Gson gson = new GsonBuilder().create();
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(query.toSQL());
			
			ResultSetMetaData meta = rs.getMetaData();
			while (rs.next()) {
				JsonObject obj = new JsonObject();

				if(!query.isJustCount())
				{
					for (int i = 0; i < meta.getColumnCount(); i++) {
						String key = meta.getColumnName(i + 1);
						
						if(!(isUserTable && key.equals("password")))
							obj.add(key, gson.fromJson(gson.toJson(rs.getObject(rs.findColumn(key))), JsonElement.class));
						else
							obj.add(key,gson.fromJson("", JsonElement.class));
					}
				}
				array.add(obj);
				
				if (query.getCount() > -1 && array.size() >= query.getCount())
					break;
			}
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			result.setError(e.toString());
		}
		result.setCount(array.size());
		
		if(query.isJustCount())
			array.clear();
		
		result.setResults(array);
		return result;
	}
//
//	public boolean Sign(String username, String password) {
//
//		Statement stmt;
//		String sql;
//		try {
//
//			stmt = conn.createStatement();
//			if (!TableExisted(USERS_TABLE)) {
//				
//				sql = getSQL("CREATE_USERS");
//				stmt.execute(sql);
//			}
//
//			stmt.close();
//
//			if (Check(username, password) != 0) {
//				return false;
//			}
//
//			sql = "INSERT " + USERS_TABLE + " (username,password) VALUES (?,?);";
//
//			PreparedStatement stmt2 = conn.prepareStatement(sql);
//			stmt2.setString(1, username);
//			stmt2.setString(2, password);
//
//			stmt2.executeUpdate();
//
//			stmt2.close();
//		} catch (SQLException e) {
//			System.err.println(e);
//
//			return false;
//		}
//
//		return true;
//	}

	public boolean isTableExisted(String TableName) {
		DatabaseMetaData meta;
		try {
			meta = conn.getMetaData();
			ResultSet rs = meta.getTables(null, null, TableName, null);
			return rs.next();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			return false;
		}

	}
//
//	private String getSQL(String key) {
//		String re = "";
//		switch (key) {
//		case "CREATE_USERS":
//			re = "CREATE TABLE " + USERS_TABLE + " (username tinytext,password tinytext,type int,nickname tinytext)";
//			break;
//
//		}
//		return re;
//	}

	public void SQLexecute(String sql) throws SQLException {
		Statement stmt = null;
		try {
			stmt = conn.createStatement();
			stmt.execute(sql);
		} catch (SQLException e) {

		}

		if (stmt != null)
			stmt.close();

	}


	public boolean isClose(){
		if(conn == null)
			return true;
		
		try {
			return conn.isClosed();
		} catch (SQLException e) {
			e.printStackTrace();
			return true;
		}
	}
	
	public Connection getConnection() {
		return conn;
	}
}
