package com.concept.crew.batch;


import org.apache.log4j.Logger;

import com.concept.crew.processor.LoadProcessor;

public class StartLoadRunner 
{
	private static final Logger log = Logger.getLogger(StartLoadRunner.class);
	
	public static void main(String[] args) 
	{
		try 
		{	
			/*
			 * a) Parse XML
			 * b) Insert into RAW tables	
			 */
			//LoaderDBRoutine.testConnectivity();
			LoadProcessor.execute();

		} 
		catch (Exception ex) 
		{
			log.error("Fatal Exception occured" +ex);								
			return;
		}				
		
		log.info("********* Completed ********* ");
	}
}
