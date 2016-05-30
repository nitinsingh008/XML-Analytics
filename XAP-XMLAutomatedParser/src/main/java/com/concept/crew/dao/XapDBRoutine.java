package com.concept.crew.dao;

import java.io.File;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.apache.derby.drda.NetworkServerControl;
import org.apache.log4j.Logger;

import com.concept.crew.info.DBDetails;
import com.concept.crew.util.Constants;
import com.concept.crew.util.FrameworkSettings;
import com.concept.crew.util.Constants.DatabaseType;

public class XapDBRoutine 
{
	private static Logger 		logger 			= Logger.getLogger(XapDBRoutine.class);	
	private static String 		DB_DRIVER ;
	private static String 		DB_CONNECTION;
	private static String 		DB_USER;
	private static String 		DB_PASSWORD ;
	private static Connection 	conn ;
	private FrameworkSettings projectSetting;
	
	public static void initializeDBRoutine(String dbType, 
										   String jdbcUrl, 
										   String user, 
										   String password,
										   FrameworkSettings projectSetting) 
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
			DB_DRIVER = "org.apache.derby.jdbc.EmbeddedDriver";

			File xapDatabaseLocation =  new File(Constants.databaseLoc);
			if(!xapDatabaseLocation.exists())
			{
				xapDatabaseLocation.mkdirs();
			}
		    // -------------------------------------------
		    // URL format is		
			// 'jdbc:derby:C:\XAP\xapDatabase; create=true; user=username; password=password';
		    // -------------------------------------------
			String dbName  = projectSetting.getProjectName();
			
			if(user != null && user != "" && password != null )
			{
				jdbcUrl = "jdbc:derby:" + xapDatabaseLocation + "/" + dbName  + ";create=true; user= " + user + "; password=" + password;
			}
			else
			{
				user 	 = "";
				password = "";
				jdbcUrl = "jdbc:derby:" + xapDatabaseLocation + "/" + dbName + ";create=true";
			}
		}
		else
		{
			// DB Driver for MS SQL Server
			//DB_DRIVER = "com.mysql.jdbc.Driver";
		}
		
		DB_CONNECTION = jdbcUrl;
		DB_USER = user;
		DB_PASSWORD = password;
		projectSetting.setDbDetails(new DBDetails(jdbcUrl, user, password, dbType, DB_DRIVER));
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
				startDerbyEmbeddedServer();
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
	
	public static void startDerbyEmbeddedServer()
	{
		NetworkServerControl server = null;

	    try 
	    {
	        server = new NetworkServerControl(InetAddress.getByName("localhost"), 1527);
		    try 
		    {
				server.ping();
			} 
		    catch (Exception e) 
		    {
				server.start(null);
			}
	        
	    } 
	    catch (UnknownHostException e1) 
	    {
	        e1.printStackTrace();
	    } catch (Exception e1) 
	    {
	        e1.printStackTrace();
	    }
	}	
	
/*	public static void main(String args[])
	{
		XapDBRoutine.initializeDBRoutine(DatabaseType.ORACLE.toString(), "TNSEntry", "username", "password");
		boolean dbConnected = XapDBRoutine.testAndValidateDBConnection();
		System.out.println(dbConnected);
	}*/
	
/*	public static void main(String args[])
	{
		FrameworkSettings projectSetting = new FrameworkSettings("firstOne");
		XapDBRoutine.initializeDBRoutine(DatabaseType.JavaDB_DERBY.toString(), "AnyURL", "username", "password",projectSetting);
		boolean dbConnected = XapDBRoutine.testAndValidateDBConnection();
		
		System.out.println(dbConnected);
	}*/
	
	public static void main(String args[])
	{
		startDerbyEmbeddedServer();
 
		
		startDerbyEmbeddedServer();
		
	}
	
	
	
}
