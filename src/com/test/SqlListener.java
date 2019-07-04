package com.test;
import java.util.*;

public class SqlListener extends Observable
{
	public SqlListener(){
		
		
	}
	public void call(String f){
		this.notifyObservers(f);
		this.setChanged();
	}
}
