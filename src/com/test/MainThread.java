package com.test;
import org.json.*;
import java.util.*;
import org.json.JSONObject;
import org.json.*;
import java.text.*;
import java.io.*;
import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.Statement;
import javax.net.ssl.*;


public class MainThread extends Thread
{

	String api_ip = Util.http_dns("api.dc01.gamelockerapp.com");
	Connection conn;
	SqlListener listener;
	Keystore store;

	private SqlFactory factory;
	public MainThread(SqlFactory _factory,Keystore _store)
	{
		this.factory = _factory;
		this.store = _store;
		this.setName("Pull Players Thread");
		try
		{
			Util.trustAllHttpsCertificates();
			HttpsURLConnection.setDefaultHostnameVerifier(Util.hv);
		}
		catch (Exception e)
		{}
	}

	public boolean running=true;

	public void run()
	{

		while (true)
		{
			if (this.running)
			{
				try
				{
					String data =  Util.Auth_Curl(null, this.listener, "https://" + api_ip + "/shards/cn/matches?sort=-createdAt", store.getkey());
					if (data != null)
					{
						JSONObject root = new JSONObject(data);
						JSONArray root_included = root.getJSONArray("included");
						for (int i =0;i < root_included.length();i++)
						{
							JSONObject root_included$x = root_included.getJSONObject(i);
							if (root_included$x.getString("type").equals("player"))
							{
								String root_included$x_id = root_included$x.getString("id");
								String root_included$x_attributes_name = root_included$x.getJSONObject("attributes").getString("name");
								this.factory.record_player(root_included$x_id, root_included$x_attributes_name);
							}
						}
						JSONArray root_data = root.getJSONArray("data");
						for (int i =0;i < root_data.length();i++)
						{
							JSONObject root_data$x = root_data.getJSONObject(i);
							String root_data$x_id = root_data$x.getString("id");
							if (!this.factory.MatcheRecorded(root_data$x_id))
							{
								Util.log("新数据: " + root_data$x_id);
								this.factory.record(root_data$x_id);
							}
						}
						Util.log("休眠");
						this.sleep(5000);
					}
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		}


	}

	

}
