package com.concept.crew.apis;

import java.io.File;

import com.concept.crew.info.DBColumns;
import com.concept.crew.processor.JaxbTableGenerator;
import com.concept.crew.processor.TableGenerator;
import com.concept.crew.util.AutomationHelper;
import com.concept.crew.util.JaxbInfoGenerator;
import com.google.common.collect.Multimap;

public class StartAutomation 
{

	public static void main(String[] args) throws Exception
	{
		if(!validateInputs(args)){
			return;			
		}
		File xsdFile = new File(args[0]); 
		
		String XSD_SCHEMA = xsdFile.getName();
		
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
		
		//AutomationHelper.compileJaxbInfos();		
		//AutomationHelper.copyFiles();		

		// 4. Generate tables from XSD
		System.out.println("Generating Table Scripts from target/<ProjectName>-1.0.jar");
		tableGenerator(XSD_SCHEMA);
		
		// 5. Generate loaders automatically - Main/Schedules
		
		// 6. Last Step = > Build Maven project again
		System.out.println("Build Maven project");
		AutomationHelper.buildMavenProject();
	}

	/*
	 * Generate tables from XSD or Info       - Tables
	 * Script will be created at /resource Folder
	 */
	public static void tableGenerator(String xsdName) throws Exception
	{
		TableGenerator generator =  new JaxbTableGenerator(xsdName);
		
		// RAW Table
		Multimap<String, DBColumns> tableMap = generator.parse(false);	
		generator.tableScripts(tableMap, "RAW");

		
		// TODO
		// can Create Separate sql scripts for each table (Optional)
		// Login to Database and create table (Drop and recreate tables)
	}	
	
	private static boolean validateInputs(String[] args){
		if(args.length == 0){
			System.out.println("Provide correct XSD path");
			return false;
		}
		File f = new File(args[0]);
		if(!f.isFile()){
			System.out.println("Provide correct XSD path");
			return false;
		}
		return true;
	}
	
}
