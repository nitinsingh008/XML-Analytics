package com.concept.crew.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import com.concept.crew.util.Constants.DatabaseType;

public class XapDBRoutine {
	
	private static String DB_DRIVER ;
	private static String DB_CONNECTION;
	private static String DB_USER;
	private static String DB_PASSWORD ;
	private static Connection conn ;
	
	public static void initializeDBRoutine(String dbType, String jdbcUrl, String user, String password) {
		
		if(DatabaseType.ORACLE.toString().equals(dbType)){
			DB_DRIVER = "oracle.jdbc.driver.OracleDriver";
		}else{
			DB_DRIVER = "com.mysql.jdbc.Driver";
		}
		DB_CONNECTION = jdbcUrl;
		DB_USER = user;
		DB_PASSWORD = password;
	}

	public boolean testDBConnection(){
		if(conn == null){
			initializeConnection();
			return true;
		}
		return true;
	}

	private static void initializeConnection(){		
		if(conn != null)
			return;
		
		try {
			Class.forName(DB_DRIVER);
			
			conn = DriverManager.getConnection(DB_CONNECTION, DB_USER, DB_PASSWORD);
			
			if(conn != null){
				System.out.println("Connection to DB successful");
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static void cleanUp(){
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static Connection getConnection(){
		if(conn == null){
			initializeConnection();
			return conn;
		}
		return conn;
	}
}
