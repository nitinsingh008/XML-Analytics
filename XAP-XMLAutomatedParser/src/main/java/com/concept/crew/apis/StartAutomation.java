package com.concept.crew.apis;

import java.io.File;

import com.concept.crew.info.DBColumns;
import com.concept.crew.processor.JaxbTableGenerator;
import com.concept.crew.processor.TableGenerator;
import com.concept.crew.util.AutomationHelper;
import com.concept.crew.util.JaxbInfoGenerator;
import com.concept.crew.util.XSDParseRequest;
import com.google.common.collect.Multimap;

public class StartAutomation 
{

	public static void start(XSDParseRequest request, 
							 Boolean 		 createScripts , 
							 Boolean 		 createTable, 
							 Boolean 		 createFramework) throws Exception
	{
		File xsdFile = new File(request.getParsedXSDPath()); 
		
		if(createScripts)
		{
			//1. Create maven project
			System.out.println("Creating maven project");
			AutomationHelper.createMavenProject(xsdFile);
			
			//2. Generate Jaxb object in new maven project from the input XSD
			System.out.println("Generating Jaxb object in new maven project from the input XSD");		
			JaxbInfoGenerator gen = new JaxbInfoGenerator();
			gen.generateInfos(xsdFile.getAbsolutePath());

			//3. Build Maven project
			System.out.println("Build Maven project");
			AutomationHelper.buildMavenProject();
			
			// 4. Generate tables from XSD
			System.out.println("Generating Table Scripts from target/<ProjectName>-1.0.jar");
			tableGenerator(xsdFile);
			
			// 5. Generate loaders automatically - Main/Schedules
			
			// 6. Last Step = > Build Maven project again
			System.out.println("Build Maven project");
			AutomationHelper.buildMavenProject();
		}

		if(createFramework)
		{
			
		}
		
		if(createTable)
		{
			
		}
	}

	/*
	 * Generate tables from XSD or Info       - Tables
	 * Script will be created at /resource Folder
	 */
	public static void tableGenerator(File xsdFile) throws Exception
	{
		TableGenerator generator =  new JaxbTableGenerator(xsdFile.getName());
		String rootNode = generator.fetchRootNode(xsdFile);
		// RAW Table
		Multimap<String, DBColumns> tableMap = generator.parse(false);	
		generator.tableScripts(tableMap, "RAW", rootNode);

		
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
		start(request, Boolean.TRUE, Boolean.TRUE, Boolean.TRUE);
	}
	
	public static void main(String[] args) throws Exception
	{
		XSDParseRequest request = new XSDParseRequest();
		request.setParsedXSDPath(args[0]);
		doAll(request);
	}
}
