package com.test;
import java.util.*;
import java.io.*;
import java.sql.*;

public class ExtraThread extends Thread
{
	List<Thread> thread = new ArrayList<Thread>();
	SqlListener listener;
	Keystore store;

	private SqlFactory factory;
	public ExtraThread(SqlFactory _factory,Keystore _store,SqlListener _listener){
		this.listener=_listener;
		this.setName("Pull Matches Thread");
		this.store=_store;
		this.factory=_factory;
	}
	
	public void run(){
		while(true){
			try
			{
				ResultSet rs = this.factory.executeQuery("SELECT player_name,player_id FROM Players ORDER BY rand()");
				while(rs.next()){
					while(!this.threadavailable()){
						this.sleep(500);
					}
					PullMatchesByName pullthread = new PullMatchesByName(this.factory,this.listener,this.store,rs.getString("player_name"),rs.getString("player_id"));
					this.thread.add(pullthread);
					pullthread.start();
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}

	private boolean threadavailable()
	{
		if(this.thread.size()< 5){
			return true;
		}
		for(Thread one_thread:this.thread){
			if(!one_thread.isAlive()){
				this.thread.remove(one_thread);
				return true;
			}
		}
		return false;
	}
}
