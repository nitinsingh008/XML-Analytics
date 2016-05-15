package com.concept.crew.apis;

import java.io.File;
import org.apache.log4j.Logger;
import com.concept.crew.dao.XapDBRoutine;
import com.concept.crew.info.DBColumns;
import com.concept.crew.processor.DBScriptRunner;
import com.concept.crew.processor.JaxbTableGenerator;
import com.concept.crew.processor.TableGenerator;
import com.concept.crew.util.AutomationHelper;
import com.concept.crew.util.JaxbInfoGenerator;
import com.concept.crew.util.XSDParseRequest;
import com.google.common.collect.Multimap;

public class StartAutomation 
{
	private static Logger 		logger 			= Logger.getLogger(StartAutomation.class);
	
	public static void doAll(XSDParseRequest request) throws Exception{
		start(request, Boolean.TRUE, Boolean.TRUE, Boolean.TRUE);
	}
	
	public static void createScript(XSDParseRequest request) throws Exception{
		start(request , Boolean.TRUE, Boolean.FALSE, Boolean.FALSE);
	}
	
	public static void createTable(XSDParseRequest request) throws Exception{
		start(request, Boolean.TRUE, Boolean.TRUE, Boolean.FALSE);
	}
	
	public static void createFrameWork(XSDParseRequest request) throws Exception{
		start(request, Boolean.TRUE, request.getCreateTable(), Boolean.TRUE);
	}

	private static void start(XSDParseRequest request, 
							  Boolean 		  createScripts , 
							  Boolean 		  createTable, 
							  Boolean 		  createFramework) throws Exception
	{
		File xsdFile = new File(request.getParsedXSDPath()); 
		
		if(createScripts)
		{
			//1. Create maven project
			logger.warn("Start creating new Maven Project");
			AutomationHelper.createMavenProject(xsdFile);
			
			//2. Generate Jaxb object in new maven project from the input XSD
			logger.warn("Generating JAXB Objects in new maven project");		
			JaxbInfoGenerator gen = new JaxbInfoGenerator();
			gen.generateInfos(xsdFile.getAbsolutePath());

			//3. Build Maven project
			logger.warn("Build Maven project");
			AutomationHelper.buildMavenProject();
			
			// 4. Generate tables from XSD
			logger.warn("Generating Table Scripts");
			tableGenerator(xsdFile, request.getUserName());
			
			// 5. Generate loaders automatically - Main/Schedules
			
			// 6. Last Step = > Build Maven project again
			logger.warn("Build Maven project");
			AutomationHelper.buildMavenProject();
		}

		if(createFramework)
		{
			
		}
		
		if(createTable)
		{
			logger.warn("Creating Tables in schema");
			XapDBRoutine.initializeDBRoutine(request.getDatabaseType(), request.getTnsEntry(), request.getUserName(), request.getPassword());
			
			if(XapDBRoutine.testAndValidateDBConnection())
			{
				DBScriptRunner.executeScripts();
			}
			else
			{
				System.out.println("Db Connectivity Test failed");
			}
			
			logger.warn("Table created....");
		}
	}

	/*
	 * Generate tables from XSD or Info       - Tables
	 * Script will be created at /resource Folder
	 */
	public static void tableGenerator(File xsdFile, String username) throws Exception
	{
		TableGenerator generator =  new JaxbTableGenerator(xsdFile.getName());
		String rootNode = AutomationHelper.fetchRootNode(xsdFile);
		// RAW Table
		Multimap<String, DBColumns> tableMap = generator.parse(false);	
		generator.tableScripts(tableMap, "RAW", rootNode, username);

		
		// TODO
		// can Create Separate sql scripts for each table (Optional)
		// Login to Database and create table (Drop and recreate tables)
	}	
	
	public static boolean validateInputs(String args){
		
		File f = new File(args);
		if(!f.isFile()){
			System.out.println("Provide correct XSD path");
			return false;
		}
		return true;
	}
	
	public static void main(String[] args) throws Exception
	{
		XSDParseRequest request = new XSDParseRequest();
		request.setParsedXSDPath(args[0]);
		request.setDatabaseType("ORACLE");
		request.setTnsEntry("jdbc:oracle:thin:@ (DESCRIPTION = (ADDRESS = (PROTOCOL = TCP)(HOST = lon2odcdvscan01.markit.partners)(PORT = 1521)) (CONNECT_DATA = (SERVER = DEDICATED) (SERVICE_NAME = BRD02DV)))");
		request.setUserName("CORE_REF_DATA");
		request.setPassword("CORE_REF_DATA");
		request.setCreateScript(true);
		doAll(request);
	}
}
