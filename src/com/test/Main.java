package com.test;
import java.util.*;
import java.io.*;
import java.sql.*;



public class Main {
	
	
	
	
	
	public static void main(String[] args) {
		 
		
		try
		{
			Keystore store = new Keystore();
			
			Class.forName("org.mariadb.jdbc.Driver");

			SqlListener listener = new SqlListener();
			
			Connection conn = DriverManager.getConnection("jdbc:mariadb://localhost:3306/VaingloryData","root","1721115102Qq");
			
			SqlExecutor executor = new SqlExecutor(conn);
			
			SqlFactory factory = new SqlFactory(store,listener,conn);
			
			listener.addObserver(executor);
			
			MainThread mainthread = new MainThread(factory,store);

			mainthread.start();

			ExtraThread extrathread = new ExtraThread(factory,store,listener);

			extrathread.start();
			
			VipThread vipthread = new VipThread(factory,store,listener);
			
			vipthread.start();
			

		}
		catch (Exception e)
		{
			e.printStackTrace();

		}
	}
	
	
	

	

}
