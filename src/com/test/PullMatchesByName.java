package com.test;
import javax.net.ssl.*;
import java.util.*;
import java.io.*;
import java.sql.Connection;
import org.json.*;
import java.text.*;
import java.sql.DriverManager;
import java.sql.Statement;

public class PullMatchesByName extends Thread
{
	
	String api_ip = Util.http_dns("api.dc01.gamelockerapp.com");
	
	String name ="";
	String id = "";
	SqlListener listener;
	Keystore store;

	private SqlFactory factory;
	public PullMatchesByName(SqlFactory _factory,SqlListener _listener,Keystore _store,String _name,String _id){
		this.listener=_listener;
		this.name=_name;
		this.id=_id;
		this.factory=_factory;
		this.store=_store;
	}
	
	
	
	
	public void run() {
		this.StartRequestMatchesByName(this.name,this.id);
	}
	
	
	
	
	


	private void StartRequestMatchesByName(String name, String id)
	{
		Util.log("查询ID:"+ name);
		try
		{
			int retry =0;
			String data = Util.Auth_Curl(id,this.listener,"https://"+api_ip+"/shards/cn/matches?sort=-createdAt&page[limit]=5&filter[playerIds]="+id,store.getkey());
			if(data==null){
				return;
			}
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
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

	}
	
}
