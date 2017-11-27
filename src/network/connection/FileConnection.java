package network.connection;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.Socket;

import com.google.gson.JsonParseException;

import Common.Tool;
import Database.DatabaseManager;
import network.bucketmodle.USER;
import network.command.client.ClientCommand;
import network.listener.BucketListener;

public class FileConnection extends Connection {

	public static String rootPath = "bucket-upload"; // �ļ������Ŀ¼

	private boolean isServer;

	private String username;

	public FileConnection(Socket socket, DatabaseManager db, BucketListener messageListener) throws IOException {

		super(socket, db, messageListener);
	}

	public void setServer(boolean isServer) {
		this.isServer = isServer;
	}

	@Override
	public void startListen() throws IOException {
		if (isServer) {
			socket.setSoTimeout(2000);

			if (check(readLine())) {
				socket.setSoTimeout(0);
				start();
			} else {
				socket.close();
			}
		} else {
			super.startListen();
		}

	}

	private void start() throws IOException {
		String infoStr;
		boolean success = false;

		ClientCommand cc = new ClientCommand();
		cc.setCommand("SAVE");
		if ((infoStr = readLine()) != "EOF") {
			try {
				FileInformation fileInfo = FileInformation.fromJSON(infoStr);
				if (fileInfo == null)
					throw new JsonParseException("FileInformation parse error!");

				long len;
				len = fileInfo.getSize();
				File f = Tool.createFile(rootPath + "/" + username + "/" + fileInfo.getPath());

				FileOutputStream fout = new FileOutputStream(f);
				BufferedOutputStream o = new BufferedOutputStream(fout);
				int b = 0;
				for (int i = 0; i < len; i++) {
					b = in.read();
					if (b == -1)
						break;
					o.write(b);
				}
				o.flush();
				o.close();
				fout.close();

				success = true;
			} catch (NumberFormatException | JsonParseException | IOException | NullPointerException e) {
				e.printStackTrace();
			}
		}

		cc.setValues(success ? "SUCCESS" : "FAIL");
		
		try {
			send(cc);
		} catch (IOException e) {
			e.printStackTrace();
		}

		if (super.listener != null) {
			listener.onDataCome(this, success ? "SUCCESS" : "FAIL");
		}

	}

	private boolean check(String str) throws UnsupportedEncodingException, IOException {
		Checker checker = Tool.JSON2E(str, Checker.class);
		if (checker == null)
			return false;

		checker.setCommand("LOGIN");
		USER checkerUser = checker.doCheck(db);
		if (checkerUser == null) {
			return false;
		} else {
			username = checkerUser.username;
			return true;
		}

	}

	public void sendFile(String localPath, String serverPath) throws IOException {
		sendFile(new File(localPath), serverPath);
	}

	public void sendFile(File file, String serverPath) throws IOException {
		FileInformation fileInfo = new FileInformation(serverPath, file.length());
		writeLine(fileInfo.toJSON());
		BufferedInputStream IN = new BufferedInputStream(new FileInputStream(file));
		int b;
		while ((b = IN.read()) != -1) {
			out.write(b);
		}
		out.flush();
		IN.close();
		
	}

	public void sendFile(byte[] data, String serverPath) throws IOException {
		FileInformation fileInfo = new FileInformation(serverPath, data.length);
		writeLine(fileInfo.toJSON());
		out.write(data);
		out.flush();
	}

	public void login(USER user) throws IOException {
		writeLine(Checker.createLogin(user).toJSON());
	}

	public String getUsername() {
		return username;
	}

	public static void setRootPath(String rootPath) {
		FileConnection.rootPath = rootPath;
	}

	public static String getRootPath() {
		return rootPath;
	}

}
