package bucket.database.common.bmob.api;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

/**
 * Bmob控制台
 * 
 * @author Hansin
 *
 */
public class BmobConsole {

	/**
	 * 用户邮箱
	 */
	protected String email;

	/**
	 * 用户密码
	 */
	protected String password;

	private static final String API_GET_APPS = "https://api.bmob.cn/1/apps";
	private static final String HEADER_EMAIL = "X-Bmob-Email";
	private static final String HEADER_PASSWORD = "X-Bmob-Password";

	public BmobConsole(String email, String password) {
		this.email = email;
		this.password = password;
	}

	/**
	 * 获取所有应用
	 * 
	 * @return
	 * @throws MalformedURLException
	 * @throws IOException
	 * @throws GetAppsException
	 */
	public List<BmobApp> getApps()
			throws MalformedURLException, IOException, GetAppsException, JsonSyntaxException, JsonIOException {
		HttpsURLConnection conn = (HttpsURLConnection) new URL(API_GET_APPS).openConnection();
		conn.addRequestProperty(HEADER_EMAIL, email);
		conn.addRequestProperty(HEADER_PASSWORD, password);
		InputStream in = conn.getInputStream();
		Gson gson = new GsonBuilder().create();
		GetAppsResult results = gson.fromJson(new BufferedReader(new InputStreamReader(in)), GetAppsResult.class);
		in.close();
		if (results == null)
			throw new GetAppsException("Null result");
		return results.results;
	}

	public BmobApp getApp(String applicationId)
			throws MalformedURLException, IOException, GetAppsException, JsonSyntaxException, JsonIOException {
		HttpsURLConnection conn = (HttpsURLConnection) new URL(API_GET_APPS + "/" + applicationId).openConnection();
		conn.addRequestProperty(HEADER_EMAIL, email);
		conn.addRequestProperty(HEADER_PASSWORD, password);
		InputStream in = conn.getInputStream();
		Gson gson = new GsonBuilder().create();
		BmobApp results = gson.fromJson(new BufferedReader(new InputStreamReader(in)), BmobApp.class);
		in.close();
		return results;
	}

	protected class GetAppsResult {

		protected List<BmobApp> results;

	}

	public class GetAppsException extends Exception {

		/**
		 * 
		 */
		private static final long serialVersionUID = -4384149105548019376L;

		public GetAppsException(String str) {
			super(str);
		}
	}
}
