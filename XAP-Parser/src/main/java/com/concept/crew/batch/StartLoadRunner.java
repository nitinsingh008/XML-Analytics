package com.concept.crew.batch;


import org.apache.log4j.Logger;

import com.concept.crew.dao.LoaderDBRoutine;
import com.concept.crew.processor.LoadProcessor;
import com.concept.crew.util.ConfigCache;

public class StartLoadRunner 
{
	private static final Logger log = Logger.getLogger(StartLoadRunner.class);
	
	public static void main(String[] args) 
	{
		try 
		{	
			/*
			 * a) Initialize Cache 
			 * b) Parse XML
			 * c) Insert into RAW tables	
			 */
			//ConfigCache.getInstance().initializeCache();	
			LoadProcessor.execute();

		} 
		catch (Exception ex) 
		{
			log.error("Fatal Exception occured" +ex);			
			if(ConfigCache.getInstance().getPropertyMapValue("EMAIL_TO") != null)			
			{
				String environment 	= LoaderDBRoutine.getEnvironment();	
				String subject 		= "Loader Service :";
				
				//EmailLogger.sendEmailOnFailure(environment, subject, ex);
							
			}						
			return;
		}				
		
		log.info("********* Completed ********* ");
	}
}
