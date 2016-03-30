package com.concept.crew.apis;

import java.io.File;

import com.concept.crew.info.DBColumns;
import com.concept.crew.util.JaxbInfoGenerator;
import com.concept.crew.util.TableGenerator;
import com.google.common.collect.Multimap;

public class StartAutomation 
{

	public static void main(String[] args) throws Exception
	{
		//String XSD_SCHEMA = "markit_bond.xsd";
		String XSD_SCHEMA = "CustomersOrders.xsd";
		
		/*
		 * Generate jaxb (Info) Classes from XSD
		 */
		final Thread currentThread = Thread.currentThread();
		final ClassLoader contextClassLoader = currentThread.getContextClassLoader();

		File file = new File(contextClassLoader.getResource(XSD_SCHEMA).getFile()); //resource folder
		
		JaxbInfoGenerator gen = new JaxbInfoGenerator();
		gen.generateInfos(file.getAbsolutePath());

		/*
		 * Generate tables from XSD or Info       - Tables
		 */				
		tableGeneratorFromXSD(XSD_SCHEMA);
		
		// Generate loaders automatically - Main/Schedules
	}

	public static void tableGeneratorFromXSD(String xsdName) throws Exception
	{
		TableGenerator generator =  new TableGenerator(xsdName);
		
		// RAW Table
		Multimap<String, DBColumns> tableMap = generator.parseSchema(false);	
		generator.tableScripts(tableMap, "RAW");
		
		// Main Table
		tableMap = generator.parseSchema(true);	
		generator.tableScripts(tableMap, "MAIN");		
		
		// TODO
		// can Create Separate sql scripts for each table (Optional)
		// Login to Database and create table (Drop and recreate tables)
	}	
	
	
}
