package com.concept.crew.util;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.concept.crew.dao.LoaderDBRoutine;

public class ConfigCache 
{
	private static final Logger 			 log 		= Logger.getLogger(ConfigCache.class);
	private static 		 ConfigCache 		 cache		= new ConfigCache();
	
	private ConfigCache()	{}
	public  volatile boolean ready 			= false;
	private volatile boolean isFailed 		= false;
	private volatile boolean initializing 	= false;

	private static Map<String, String> 	propertyMap  = new HashMap<String, String>();
	
	public static ConfigCache getInstance()
	{
		return cache;
	}
	
	public boolean isFailed()
	{
		return isFailed;
	}
	public boolean isInitialized()
	{
		return ready;
	}
	public boolean isInitializing()
	{
		return initializing;
	}
	
	public void initializeCache() throws Exception
	{
		try 
		{
			isFailed 		= false;
			ready 			= false;
			initializing 	= true;
			
			log.info("==========> Initializing Cache <========== ");
			initializePropertyMap();
			ready = true;
			log.info("==========> Initialize Cache finished <==========");
		}
		catch (Exception e)
		{
			isFailed = true;
			throw new Exception(e.getMessage());
		} 
		finally 
		{		
			initializing = false;			
		}
	}


	private void initializePropertyMap() throws Exception 
	{
		propertyMap.clear();
		propertyMap = LoaderDBRoutine.getAllFeedConfigProperties();	
	}

	public String getPropertyMapValue(String key)
	{
		isReady();
		return propertyMap.get(key);
	
	}
	
	
	private void isReady() 
	{
		while(!ready)
		{
			Thread.yield();
		}
	}
	
}
