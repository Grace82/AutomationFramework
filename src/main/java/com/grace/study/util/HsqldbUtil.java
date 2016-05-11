package com.grace.study.util;

import java.sql.Connection;
import java.util.Locale;

import com.grace.study.util.loghelper.LoggedTestCase;


public class HsqldbUtil extends LoggedTestCase{
	private static final String HOST = "";
	private static final int PORT = 9002;
	private static final String DB_NAME="";
	private static final String DB_PATH="";
	private static final String USER_NMAE="";
	private static final String PASSWORD="";
	private static final int MODE=0;
	private static final int SERVER_MODE=0; //server mode 
	private static final int STAND_ALONE_MODE=1; //in-Process
	
	public static boolean startHSQL(){
		return true;
	}
	
	public static boolean stopHSQL(){
		return false;
	}
	
	public static Connection getConnection(){
		Connection conn = null;
		return conn;
	}
	
	public static String getString(String stringID){
		return null;
	}
	
	public static String getString(Locale locale, String stringID){
		return null;
	}
}
