package com.concept.crew.dao.loaderUtil;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.concept.crew.util.ConnectionPool;
import com.concept.crew.util.DBRoutine;
import com.concept.crew.util.DBRoutinePool;
import com.concept.crew.util.ResultSetRowMapper;


public class LoaderDBRoutine 
{
	private static 			DBRoutine 				coreDBRoutine;
	private static final 	String 					poolName 		    = "LOADDB";
	private static final 	String				    PROG_NAME			= "LOADER-SERVICE";
	private static final 	String				    MAX_CONNECTIONS		= "10";	

	private static String DB_DRIVER ;
	private static String DB_CONNECTION;
	private static String DB_USER;
	private static String DB_PASSWORD ;
	
	public static void initializeDBRoutine(String dbType, 
										   String jdbcUrl, 
										   String user, 
										   String password) 
	{
		
/*		if(DatabaseType.ORACLE.toString().equals(dbType))
		{
			DB_DRIVER = "oracle.jdbc.driver.OracleDriver";
		}
		else if(DatabaseType.MySQL.toString().equals(dbType))
		{
			DB_DRIVER = "com.mysql.jdbc.Driver";
		}
		else
		{
			// DB Driver for MS SQL Server
			//DB_DRIVER = "com.mysql.jdbc.Driver";
		}*/
		
		DB_CONNECTION = jdbcUrl;
		DB_USER 	  = user;
		DB_PASSWORD   = password;
		
		createCoreDBRoutine();
	}
	
	public static DBRoutine getCoreDBRoutine() 
	{
		return coreDBRoutine;
	}

	public static String getPoolname() 
	{
		return poolName;
	}	

	/*
	 *  Reading properties for creating connection with CORE_FFED
	 */
	private static DBRoutine createCoreDBRoutine() 
	{
		
		Properties coreDBProperties = new Properties();
	
		coreDBProperties.setProperty("JDBCURL", DB_CONNECTION);
		coreDBProperties.setProperty("DBUSER",  DB_USER);
		coreDBProperties.setProperty("DBPASS",  DB_PASSWORD);
		coreDBProperties.setProperty("MAX_CONNECTIONS", MAX_CONNECTIONS);
		coreDBProperties.setProperty("MAX_CONNECTION_WAIT_TIME","5000");
		coreDBProperties.setProperty("PROGRAM_NAME",PROG_NAME);
		
		ConnectionPool.initialize(poolName, coreDBProperties);
		coreDBRoutine = new DBRoutinePool(poolName);
		//coreDBRoutine.setProfiling(true);	
		
		return coreDBRoutine;
	}
	

	

	public static Map<String,String> testConnectivity()
			throws SQLException 
	{		
		String queryToExecute = "SELECT SYSDATE FROM DUAL";
	
		final Map<String,String> bbgPropertyMap = new HashMap<String,String>();
		
		List<Object> paramList = new ArrayList<Object>();		
	
		coreDBRoutine.executeQuery(queryToExecute, new ResultSetRowMapper<String>() 
			{
				@Override
				public String mapRow(ResultSet result,int rowNum, String[] headers)
						throws SQLException 
				{
					return null;
				}
			}, paramList);
	  
			
		return bbgPropertyMap;
	}
}
