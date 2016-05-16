package com.concept.crew.batch;


import org.apache.log4j.Logger;

import com.concept.crew.dao.LoaderDBRoutine;
import com.concept.crew.processor.LoadProcessor;

public class StartLoadRunner 
{
	private static final Logger log = Logger.getLogger(StartLoadRunner.class);
	
	public static void main(String[] args) 
	{
		try 
		{	

			if(args.length == 3)
			{
				String jdbcUrl = args[0];
				String username = args[1];
				String password = args[2];		
				
				LoaderDBRoutine.initializeDBRoutine("Oracle", jdbcUrl, username, password);
				
				LoaderDBRoutine.testConnectivity();
				
				LoadProcessor.execute();
			}
			else
			{
				System.out.println("Provide jdbcurl | username | password");
			}
		} 
		catch (Exception ex) 
		{
			log.error("Fatal Exception occured" +ex);								
			return;
		}				
		
		log.info("********* Completed ********* ");
	}
}
