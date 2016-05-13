package com.concept.crew.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.concept.crew.util.ConnectionPool;
import com.concept.crew.util.DBRoutine;
import com.concept.crew.util.DBRoutinePool;
import com.concept.crew.util.ResultSetRowMapper;

public class LoaderDBRoutine 
{
	private static 			Logger 					log 				= Logger.getLogger(LoaderDBRoutine.class);
	private static 			DBRoutine 				coreDBRoutine;
	private static final 	String 					poolName 		    = "BRDDB";
	private static final 	String				    PROG_NAME			= "LOADER-SERVICE";
	private static final 	String				    MAX_CONNECTIONS		= "10";	
	private static    	    String 					environment;
	
	static 
	{
		
		if(DBProps.getProperty("ENV") != null)
		{
			environment = DBProps.getProperty("ENV");
		}
		else
		{
			environment = "QA";
		}
		
		createCoreDBRoutine();
	}
	
	public static String getEnvironment() 
	{
		return environment;
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
		log.info(" ===== Start creating DBRoutine =====");
		
		Properties coreDBProperties = new Properties();
	
		coreDBProperties.setProperty("JDBCURL", DBProps.getProperty("JDBCURL"));
		coreDBProperties.setProperty("DBUSER",  DBProps.getProperty("DBUSER"));
		coreDBProperties.setProperty("DBPASS",  DBProps.getProperty("DBPASS"));
		coreDBProperties.setProperty("MAX_CONNECTIONS", MAX_CONNECTIONS);
		coreDBProperties.setProperty("MAX_CONNECTION_WAIT_TIME",DBProps.getProperty("MAX_CONNECTION_WAIT_TIME"));
		coreDBProperties.setProperty("PROGRAM_NAME",PROG_NAME);
		
		ConnectionPool.initialize(poolName, coreDBProperties);
		coreDBRoutine = new DBRoutinePool(poolName);
		//coreDBRoutine.setProfiling(true);	
		
		return coreDBRoutine;
	}
	

	public static Map<String,String> getAllFeedConfigProperties()
			throws SQLException 
	{		
		String query = "SELECT * FROM CORE_FEED.FEED_CONFIG WHERE (ENV = '?' or ENV is null) AND TYPE = 'SEMANTICS'";
	
		final Map<String,String> bbgPropertyMap = new HashMap<String,String>();
		
		List<Object> paramList = new ArrayList<Object>();		
	
		StringBuffer sBuffer	=	new StringBuffer(query);
		String queryToExecute 	= 	sBuffer.toString().replace("?", environment).intern();
		log.info("Executing query:"+queryToExecute);
		
		coreDBRoutine.executeQuery(queryToExecute, new ResultSetRowMapper<String>() 
			{
				@Override
				public String mapRow(ResultSet result,int rowNum, String[] headers)
						throws SQLException 
				{
					String name 			= result.getString("NAME");					
					String value 	    	= result.getString("VALUE");
					bbgPropertyMap.put(name, value);
					return null;
				}
			}, paramList);
	  
			
		return bbgPropertyMap;
	}
}
