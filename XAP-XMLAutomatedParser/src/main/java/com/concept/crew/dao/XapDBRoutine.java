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
	
	public static void initializeDBRoutine(String dbType, 
										   String jdbcUrl, 
										   String user, 
										   String password) 
	{
		
		if(DatabaseType.ORACLE.toString().equals(dbType))
		{
			DB_DRIVER = "oracle.jdbc.driver.OracleDriver";
		}
		else
		{
			DB_DRIVER = "com.mysql.jdbc.Driver";
		}
		DB_CONNECTION = jdbcUrl;
		DB_USER = user;
		DB_PASSWORD = password;
	}

	public static boolean testAndValidateDBConnection()
	{
		boolean connected = false;
		if(DB_DRIVER == null)
		{
			System.out.println("Select Database Type [Oracle | SQL Server]");
			return false;
		}
		if(DB_CONNECTION == null)
		{
			System.out.println("Enter correct TNS Entry for database");
			return false;
		}
		if(DB_USER == null)
		{
			System.out.println("Dabtase user can't be empty");
			return false;
		}
		if(DB_PASSWORD == null)
		{
			System.out.println("Dabtase password can't be empty");
			return false;
		}
		
		connected = initializeConnection();

		return connected;
	}

	private static boolean initializeConnection()
	{		
		boolean connected = false;
		if(conn != null)
			return true;
		
		try 
		{
			Class.forName(DB_DRIVER);
			
			conn = DriverManager.getConnection(DB_CONNECTION, DB_USER, DB_PASSWORD);
			
			if(conn != null)
			{
				System.out.println("Connection to DB successful");
				connected = true;
			}
		} catch (ClassNotFoundException e) 
		{
			e.printStackTrace();
		} 
		catch (SQLException e) 
		{
			e.printStackTrace();
		}
		return connected;
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
	
	public static void main(String args[])
	{
		XapDBRoutine.initializeDBRoutine(DatabaseType.ORACLE.toString(), "TNSEntry", "username", "password");
		boolean dbConnected = XapDBRoutine.testAndValidateDBConnection();
		System.out.println(dbConnected);
	}
}
