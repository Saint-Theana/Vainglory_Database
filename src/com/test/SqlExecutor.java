package com.test;
import java.util.*;
import java.sql.*;

public class SqlExecutor extends Thread implements Observer
{
	
	public Statement _stm ;

	
	@Override
	public void update(Observable p1, Object p2)
	{
		try
		{
			this._stm.executeUpdate(p2.toString());
		}
		catch (SQLException e)
		{e.printStackTrace();}
	}

	

	public SqlExecutor(Connection conn){
		try
		{
			 this._stm= conn.createStatement();
		}
		catch (SQLException e)
		{e.printStackTrace();}  
	}
	
	@Override
	public void run(){
		
	}
	
	
}
