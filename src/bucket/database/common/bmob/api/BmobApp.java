package bucket.database.common.bmob.api;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import bucket.database.common.bmob.type.BmobACL;
import bucket.database.common.bmob.type.BmobObject;

/**
 * Bmob应用类
 * 
 * @author Hansin
 *
 */
public class BmobApp {

	private static final String API_GET_SCHEMAS = "https://api.bmob.cn/1/schemas";
	private static final String API_GET_OBJECT = "https://api.bmob.cn/1/classes";
	private static final String API_GET_BQL = "https://api.bmob.cn/1/cloudQuery";

	private static final String HEADER_APPLICATION_ID = "X-Bmob-Application-Id";
	private static final String HEADER_APPLICATION_MASTER_KEY = "X-Bmob-Master-Key";
	@SuppressWarnings("unused")
	private static final String HEADER_APPLICATION_REST_KEY = "X-Bmob-REST-API-Key";
	private static final String HEADER_APPLICATION_ConentType = "Content-Type";
	private static final String HEADER_APPLICATION_ConentType_VALUE = "application/json";

	/**
	 * appName
	 */
	protected String appName;

	/**
	 * app Id
	 */
	protected String applicationId;

	/**
	 * restful Key
	 */
	protected String restKey;

	/**
	 * master Key
	 */
	protected String masterKey;

	/**
	 * access Key
	 */
	protected String accessKey;

	/**
	 * secret Key
	 */
	protected String secretKey;

	/**
	 * app 是否可用，0表示不可用，1表示可用
	 */
	protected int status;

	/**
	 * 是否允许通过api建表，0表示允许，1表示不允许
	 */
	protected int notAllowedCreateTable;

	public JsonObject getSchemas() throws MalformedURLException, IOException {
		HttpsURLConnection conn = (HttpsURLConnection) new URL(API_GET_SCHEMAS).openConnection();
		conn.addRequestProperty(HEADER_APPLICATION_ID, applicationId);
		conn.addRequestProperty(HEADER_APPLICATION_MASTER_KEY, masterKey);
		InputStream in = conn.getInputStream();
		Gson gson = new GsonBuilder().create();
		return gson.fromJson(new BufferedReader(new InputStreamReader(in)), JsonObject.class);
	}

	public JsonObject getSchema(String tableName) throws MalformedURLException, IOException {
		HttpsURLConnection conn = (HttpsURLConnection) new URL(API_GET_SCHEMAS + "/" + tableName).openConnection();
		conn.addRequestProperty(HEADER_APPLICATION_ID, applicationId);
		conn.addRequestProperty(HEADER_APPLICATION_MASTER_KEY, masterKey);
		InputStream in = conn.getInputStream();
		Gson gson = new GsonBuilder().create();
		return gson.fromJson(new BufferedReader(new InputStreamReader(in)), JsonObject.class);
	}

	public JsonObject getObject(String tableName, String objectId) throws MalformedURLException, IOException {
		HttpsURLConnection conn = (HttpsURLConnection) new URL(API_GET_OBJECT + "/" + tableName + "/" + objectId)
				.openConnection();
		conn.addRequestProperty(HEADER_APPLICATION_ID, applicationId);
		conn.addRequestProperty(HEADER_APPLICATION_MASTER_KEY, masterKey);
		InputStream in = conn.getInputStream();
		Gson gson = new GsonBuilder().create();
		return gson.fromJson(new BufferedReader(new InputStreamReader(in)), JsonObject.class);
	}

	public <T extends BmobObject> T getObject(String tableName, String objectId, Class<T> clazz)
			throws MalformedURLException, IOException {
		HttpsURLConnection conn = (HttpsURLConnection) new URL(API_GET_OBJECT + "/" + tableName + "/" + objectId)
				.openConnection();
		conn.addRequestProperty(HEADER_APPLICATION_ID, applicationId);
		conn.addRequestProperty(HEADER_APPLICATION_MASTER_KEY, masterKey);
		InputStream in = conn.getInputStream();
		Gson gson = new GsonBuilder().registerTypeAdapter(BmobACL.class, new BmobACL.BmobAclAdapter()).create();
		return gson.fromJson(new BufferedReader(new InputStreamReader(in)), clazz);
	}

	public List<JsonObject> getObjects(String tableName, String extend) throws MalformedURLException, IOException {
		HttpsURLConnection conn = (HttpsURLConnection) new URL(API_GET_OBJECT + "/" + tableName + extend)
				.openConnection();
		conn.addRequestProperty(HEADER_APPLICATION_ID, applicationId);
		conn.addRequestProperty(HEADER_APPLICATION_MASTER_KEY, masterKey);
		InputStream in = conn.getInputStream();
		Gson gson = new GsonBuilder().create();
		JsonObject jo = gson.fromJson(new BufferedReader(new InputStreamReader(in)), JsonObject.class);
		JsonArray re = jo.get("results").getAsJsonArray();
		List<JsonObject> list = new ArrayList<>();
		for (int i = 0; i < re.size(); i++) {
			list.add(re.get(i).getAsJsonObject());
		}
		return list;
	}

	public <T> List<T> getObjects(String tableName, String extend, Class<T> clazz)
			throws MalformedURLException, IOException {
		HttpsURLConnection conn = (HttpsURLConnection) new URL(API_GET_OBJECT + "/" + tableName + extend)
				.openConnection();
		conn.addRequestProperty(HEADER_APPLICATION_ID, applicationId);
		conn.addRequestProperty(HEADER_APPLICATION_MASTER_KEY, masterKey);
		InputStream in = conn.getInputStream();
		Gson gson = new GsonBuilder().registerTypeAdapter(BmobACL.class, new BmobACL.BmobAclAdapter()).create();
		JsonObject jo = gson.fromJson(new BufferedReader(new InputStreamReader(in)), JsonObject.class);
		JsonArray re = jo.get("results").getAsJsonArray();
		List<T> list = new ArrayList<>();
		for (int i = 0; i < re.size(); i++) {
			list.add(gson.fromJson(re.get(i), clazz));
		}
		return list;
	}

	public String addObject(String tableName, Object obj) throws MalformedURLException, IOException {
		Gson gson = new GsonBuilder().registerTypeAdapter(BmobACL.class, new BmobACL.BmobAclAdapter()).create();
		JsonObject json = gson.fromJson(gson.toJson(obj), JsonObject.class);
		HttpsURLConnection conn = (HttpsURLConnection) new URL(API_GET_OBJECT + "/" + tableName).openConnection();
		conn.setRequestMethod("POST");
		conn.addRequestProperty(HEADER_APPLICATION_ID, applicationId);
		conn.addRequestProperty(HEADER_APPLICATION_MASTER_KEY, masterKey);
		conn.addRequestProperty(HEADER_APPLICATION_ConentType, HEADER_APPLICATION_ConentType_VALUE);

		conn.setDoOutput(true);
		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));

		json.remove("__type");
		json.remove("className");
		writer.write(json.toString());
		writer.flush();
		writer.close();
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

			JsonObject jo = gson.fromJson(reader, JsonObject.class);

			if (jo != null && jo.get("objectId") != null) {
				return jo.get("objectId").getAsString();
			} else {
				return null;
			}
		} catch (Exception e) {
			if (conn.getResponseCode() != 200) {
				int b;
				while ((b = conn.getErrorStream().read()) != -1)
					System.out.write(b);
				System.out.println();
			} else {
				e.printStackTrace();
			}
			throw e;
		}

	}

	public String updateObject(String tableName, Object obj, String objectId)
			throws MalformedURLException, IOException {
		Gson gson = new GsonBuilder().registerTypeAdapter(BmobACL.class, new BmobACL.BmobAclAdapter()).create();
		JsonObject json = gson.fromJson(gson.toJson(obj), JsonObject.class);
		HttpsURLConnection conn = (HttpsURLConnection) new URL(API_GET_OBJECT + "/" + tableName + "/" + objectId)
				.openConnection();
		conn.setRequestMethod("PUT");
		conn.addRequestProperty(HEADER_APPLICATION_ID, applicationId);
		conn.addRequestProperty(HEADER_APPLICATION_MASTER_KEY, masterKey);
		conn.addRequestProperty(HEADER_APPLICATION_ConentType, HEADER_APPLICATION_ConentType_VALUE);

		conn.setDoOutput(true);
		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));

		json.remove("__type");
		json.remove("className");
		json.remove("objectId");
		writer.write(json.toString());
		writer.flush();
		writer.close();
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

			JsonObject jo = gson.fromJson(reader, JsonObject.class);

			if (jo != null && jo.get("updatedAt") != null) {
				return jo.get("updatedAt").getAsString();
			} else {
				return null;
			}
		} catch (Exception e) {
			if (conn.getResponseCode() != 200) {
				int b;
				while ((b = conn.getErrorStream().read()) != -1)
					System.out.write(b);
				System.out.println();
			} else {
				e.printStackTrace();
			}
			throw e;
		}

	}

	public String deleteObject(String tableName, String objectId) throws MalformedURLException, IOException {
		Gson gson = new GsonBuilder().registerTypeAdapter(BmobACL.class, new BmobACL.BmobAclAdapter()).create();
		HttpsURLConnection conn = (HttpsURLConnection) new URL(API_GET_OBJECT + "/" + tableName + "/" + objectId)
				.openConnection();
		conn.setRequestMethod("DELETE");
		conn.addRequestProperty(HEADER_APPLICATION_ID, applicationId);
		conn.addRequestProperty(HEADER_APPLICATION_MASTER_KEY, masterKey);
		conn.addRequestProperty(HEADER_APPLICATION_ConentType, HEADER_APPLICATION_ConentType_VALUE);

		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

			JsonObject jo = gson.fromJson(reader, JsonObject.class);

			if (jo != null && jo.get("msg") != null) {
				return jo.get("msg").getAsString();
			} else {
				return null;
			}
		} catch (Exception e) {
			if (conn.getResponseCode() != 200) {
				int b;
				while ((b = conn.getErrorStream().read()) != -1)
					System.out.write(b);
				System.out.println();
			}
			throw e;
		}

	}

	public <T> List<T> getObjectsBQL(String BQL, String values, Class<T> clazz)
			throws MalformedURLException, IOException {
		HttpsURLConnection conn = (HttpsURLConnection) new URL(API_GET_BQL + "?bql=" + URLEncoder.encode(BQL, "utf-8")
				+ "&values=" + URLEncoder.encode(values, "utf-8")).openConnection();
		conn.addRequestProperty(HEADER_APPLICATION_ID, applicationId);
		conn.addRequestProperty(HEADER_APPLICATION_MASTER_KEY, masterKey);
		conn.connect();
		try {
			if (conn.getResponseCode() != 200) {
				int b;
				while ((b = conn.getErrorStream().read()) != -1) {
					System.out.write(b);
				}
				System.out.println();

			} else {
				InputStream in = conn.getInputStream();
				Gson gson = new GsonBuilder().registerTypeAdapter(BmobACL.class, new BmobACL.BmobAclAdapter()).create();
				JsonObject jo = gson.fromJson(new BufferedReader(new InputStreamReader(in)), JsonObject.class);
				JsonArray re = jo.get("results").getAsJsonArray();
				List<T> list = new ArrayList<>();
				for (int i = 0; i < re.size(); i++) {
					list.add(gson.fromJson(re.get(i), clazz));
				}
				return list;
			}
		} catch (IOException e) {
			throw e;
		}
		return null;

	}

	/**
	 * 是否允许API建表
	 * 
	 * @return
	 */
	public boolean isAllowedCreateTable() {
		return notAllowedCreateTable == 0;
	}

	/**
	 * 应用是否可用
	 * 
	 * @return
	 */
	public boolean vaild() {
		return status == 1;
	}

	public String getAppName() {
		return appName;
	}

	@Override
	public String toString() {
		return "BmobApp: " + appName + ",vaild: " + status;
	}

	public String getApplicationId() {
		return applicationId;
	}

	public void setApplicationId(String applicationId) {
		this.applicationId = applicationId;
	}

	public String getAccessKey() {
		return accessKey;
	}

	public void setAccessKey(String accessKey) {
		this.accessKey = accessKey;
	}

	public String getRestKey() {
		return restKey;
	}

	public void setRestKey(String restKey) {
		this.restKey = restKey;
	}

	public String getMasterKey() {
		return masterKey;
	}

	public void setMasterKey(String masterKey) {
		this.masterKey = masterKey;
	}

	public static class GetObjectsResult<T> {
		protected T[] results;
	}

}
