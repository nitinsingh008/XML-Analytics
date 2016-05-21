package com.concept.crew.dao;

import java.io.PrintWriter;
import java.net.InetAddress;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.apache.derby.drda.NetworkServerControl;
import org.apache.log4j.Logger;

import com.concept.crew.util.Constants;
import com.concept.crew.util.Constants.DatabaseType;

public class XapDBRoutine 
{
	private static Logger 		logger 			= Logger.getLogger(XapDBRoutine.class);	
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
		else if(DatabaseType.MySQL.toString().equals(dbType))
		{
			DB_DRIVER = "com.mysql.jdbc.Driver";
		}
		else if(DatabaseType.JavaDB_DERBY.toString().equals(dbType))
		{
		    // -------------------------------------------
		    // URL format is
		    // jdbc:derby:<local directory to save data> //DB_DRIVER = "jdbc:derby:" +  dbLocation + ";create=true";
		    // -------------------------------------------
		   String dbLocationName = Constants.mavenProjectPath + "/" + "database";			
			DB_DRIVER = "org.apache.derby.jdbc.EmbeddedDriver";
			user 	 = "";
			password = "";
			jdbcUrl = "jdbc:derby:" + dbLocationName + ";create=true";
		}
		else
		{
			// DB Driver for MS SQL Server
			//DB_DRIVER = "com.mysql.jdbc.Driver";
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
			logger.warn("Select Database Type [Oracle | SQL Server]");
			return false;
		}
		if(DB_CONNECTION == null)
		{
			logger.warn("Enter correct TNS Entry for database");
			return false;
		}
		if(DB_USER == null)
		{
			logger.warn("Dabtase user can't be empty");
			return false;
		}
		if(DB_PASSWORD == null)
		{
			logger.warn("Dabtase password can't be empty");
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
				logger.warn("Connection to DB successful");
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
	
/*	public static void main(String args[])
	{
		XapDBRoutine.initializeDBRoutine(DatabaseType.ORACLE.toString(), "TNSEntry", "username", "password");
		boolean dbConnected = XapDBRoutine.testAndValidateDBConnection();
		System.out.println(dbConnected);
	}*/
	
/*	public static void main(String args[])
	{
		XapDBRoutine.initializeDBRoutine(DatabaseType.JavaDB_DERBY.toString(), "TNSEntry", "username", "password");
		boolean dbConnected = XapDBRoutine.testAndValidateDBConnection();
		System.out.println(dbConnected);
	}*/
	
	public static void main(String args[]) throws Exception
	{
	   NetworkServerControl nsc = new NetworkServerControl(InetAddress.getByName("localhost"), 1527);
	   nsc.start(new PrintWriter(System.out, true));

	   Class.forName("org.apache.derby.jdbc.EmbeddedDriver");

	   Connection c = DriverManager.getConnection("jdbc:derby:memory:testdb;create=true");
	   System.out.println("working");
	}
}
