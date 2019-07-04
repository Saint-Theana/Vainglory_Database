package com.test;
import java.sql.DriverManager;
import java.io.*;
import org.json.*;
import java.text.*;
import java.util.*;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;


public class VipThread extends Thread
{
	String api_ip = Util.http_dns("api.dc01.gamelockerapp.com");
	

	SqlListener listener;
	Connection conn;
	Keystore store;

	private SqlFactory factory;
	
	public VipThread(SqlFactory _factory,Keystore _store,SqlListener _listener){
		this.factory=_factory;
		this.store=_store;
			}
	
	@Override public void run(){
		try
		{
			Class.forName("org.mariadb.jdbc.Driver");
			Connection conn_vip = DriverManager.getConnection("jdbc:mariadb://localhost:3306/vipaccount","root","1721115102Qq");
			while(true){
				Statement stm = conn_vip.createStatement();  
				ResultSet rs = stm.executeQuery("SELECT * FROM players");
				while(rs.next()){
					Util.log("立即更新");
					String name = rs.getString("name");
					StartRequestMatchesByName(name);
					stm.executeUpdate("delete from players where name = \""+name+"\";");
					
				}
				stm.close();
				this.sleep(1000);
			}
		
		
		}
		catch (Exception e)
		{e.printStackTrace();}
		
	}
	
	
	
	


	private void StartRequestMatchesByName(String name)
	{
		Util.log("查询ID:"+ name);
		try
		{
			int retry =0;
			String data = Util.Auth_Curl(null,this.listener,"https://"+api_ip+"/shards/cn/matches?sort=-createdAt&page[limit]=5&filter[playerNames]="+name,store.getkey());
			JSONObject root = new JSONObject(data);
			JSONArray root_data = root.getJSONArray("data");
			for(int i =0;i<root_data.length();i++){
				JSONObject root_data$x = root_data.getJSONObject(i);
				final String root_data$x_id = root_data$x.getString("id");
				if(!this.factory.MatcheRecorded(root_data$x_id)){
					Util.log("新数据: "+root_data$x_id);
					this.factory.record(root_data$x_id);
				}
			}
			Util.log("休眠");
			this.sleep(3000);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

	}
	
}
