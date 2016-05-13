package com.concept.crew.processor;

import java.io.File;
import java.util.List;

import javax.naming.OperationNotSupportedException;

import org.apache.log4j.Logger;

import com.concept.crew.info.jaxb.StartingTag;
import com.concept.crew.parser.XMLParsingUtil;
import com.concept.crew.util.Constants.RequestType;

public class LoadProcessor 
{
	private static final Logger log = Logger.getLogger(LoadProcessor.class);
	private static final 	String 					ISO_8859_2 				= "ISO-8859-2"; 

	public static void execute()	throws Exception
	{
		try 
		{		
		     String xmlFileToBeParsed = "C:\\XAP/SampleBonds1.xml";
		    
		     StartingTag startingTag = null;
		     File file;
		     try 
		     {
				 file = new File(xmlFileToBeParsed);
		     
				 log.info("Processing file " + xmlFileToBeParsed);
		         
				 if (file != null) 
		         {	
					 // Parsing - Unmarshalling
					 startingTag = XMLParsingUtil.unmarshall(file.getCanonicalPath(), ISO_8859_2);		            		            
		            
		             // Save all Incoming Data into Tables
					 List<String> pkey = LoadSaveProcessor.saveData(startingTag);						 					 
				 }
				 startingTag = null;
			 } 
		     catch (Exception e) 
		     {
				 log.error("Error processing XML file" + e.getMessage());	
				 log.error(e,e);
			 }			  
		}
		catch(Exception e) 
		{
			log.error("Error processing the TEST file /var/tmp/*_bonds.zip. " + e.getMessage());	
		}					

	}
}
