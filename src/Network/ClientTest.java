package Network;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

import Common.Tool;
import Network.BucketObject.BucketCommand;
import Network.BucketObject.TestTable;
import Network.BucketObject.Command.Client.ClientCommand;
import Network.BucketObject.Command.Server.DataSaver;
import Network.BucketObject.Command.Server.MainCommand;
import Network.Connection.Connection;
import Network.Connection.USER;
import Network.Connection.UserConnection;
import Database.*;


public class  ClientTest extends BucketListener{
	
	private UserConnection conn;
	private HashMap<Integer,ClientListener> business;
	private MessageListener messageListener;
	
	public static void main(String[] args) throws UnknownHostException, IOException
	{
		ClientTest c = new ClientTest("dustlight.cn",6654);
		c.setMessageListener(new MessageListener() {
			
			@Override
			public void onMessageCome(Connection conn, Message msg) {
				
				System.out.println("消息来了!\t" +  msg.getContent() + "\t - " + msg.getSender() );
				
				
			}
		});
		
		
		c.Login(new USER("845612500","1s23456"),new LoginListener() {
			
			@Override
			public void onDone(Connection conn, boolean success) {
				System.out.println("登陆" + success);
				
			}
		});
		
		Query q = new Query();
		
		
		c.Query(q,new QueryListener<USER>() {

			@Override
			public void onResultsCome(Connection conn, int Count, List<USER> Objs) {
				for(USER o : Objs)
				{
					
					System.out.println(o.getNickname() + " - " + o.getPassword());
				}
				
			}
		});
		
		TestTable a = new TestTable();
		a.setA("翰新 的");
		a.setB("!#123!#");
		a.setC(3.141512313123f);
		a.setD(3.141512313123);
		c.Update(a);
		
		@SuppressWarnings("resource")
		Scanner sc = new Scanner(System.in);
		
		while(sc.hasNext())
		{
			Message msg = new Message();
			msg.setSender("翰新");
			msg.setContent(sc.next());
			c.sendMessage(msg);

		}
		
		
	}
	
	public void setMessageListener(MessageListener messageListener) {
		this.messageListener = messageListener;
	}
	
	public ClientTest(String host,int port) throws UnknownHostException, IOException
	{
		business = new HashMap<Integer,ClientListener>();
		
		Socket s = new Socket(host,port);
		conn = new UserConnection(s, this);
		
		new Thread(){
			public void run() {
				try {
					conn.startListen();
				} catch (IOException e) {
					e.printStackTrace();
				}
			};
		}.start();
	}
	
	public void sendMessage(Message msg) throws IOException
	{
		conn.send(msg.toServerCommand());
	}
	
	@SuppressWarnings({ "unused", "unchecked" })
	public <T> void Query(Query q,QueryListener<T> listener)  throws IOException
	{
		Class <T> entityClass = (Class <T>) ((ParameterizedType) listener.getClass().getGenericSuperclass()).getActualTypeArguments()[0];
		q.setTable_name(entityClass.getSimpleName());
		int Sign = listener.hashCode();
		BucketCommand mm;
		if(listener != null)
		{
			mm = q.toServerCommand(Sign);
			addBuss(Sign, listener);
		}else{
			mm = q.toServerCommand();
		}
		conn.send(mm);
	}
	
	public void Query(Query q) throws IOException
	{
		Query(q,null);
	}
	
	private void addBuss(int sign,ClientListener listener)
	{
		business.put(sign, listener);
	}
	
	private void removeBuss(int sign)
	{
		business.remove(sign);
	}
	
	public void Login(USER user,LoginListener listener) throws IOException
	{
		conn.login(user,listener);
	}
	
	public void Update(Object o) throws IOException
	{
		
		MainCommand mc = new MainCommand();
		
		DataSaver ds = new DataSaver();
		
		ds.setTable(Tool.object2Table(o));
		ArrayList<Object> a = new ArrayList<Object>();
		a.add(o);
		ds.setValues(Tool.List2JsonArray(a));
		mc.setCommand(ds.getClass().getName());
		mc.setValues(ds);
		
		
		conn.send(mc);
	}
	

	@Override
	public void onDataCome(Connection conn, String message) {

		UserConnection uconn = (UserConnection)conn;
		ClientCommand cm = Tool.JSON2E(message, ClientCommand.class);
		
		System.out.println("-----------------------" + cm.getCommand()+"---------------------------------------------------------------------");

		if(messageListener != null && cm.getCommand().equals(Message.class.getSimpleName()))
		{
			messageListener.onDataCome(conn, cm);
		}else if(uconn != null && uconn.getLoginListener() != null && cm.getCommand().equals("LOGIN"))
		{
			uconn.getLoginListener().onDataCome(uconn, cm);
		}else{
			if(cm.sign == 0)
			{
				

				
			}
			else
			{
				ClientListener l = business.get(cm.getSign());
				if(l != null)
				{
					l.onDataCome(conn, cm);
					removeBuss(cm.getSign());
				}
			}
		}
		


		
		

		
	}

	@Override
	public void onDisconnection(Connection conn) {
		System.out.println("断线了");
		
	}

}
