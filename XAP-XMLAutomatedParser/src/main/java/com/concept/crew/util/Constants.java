package com.concept.crew.util;

public class Constants 
{
	public static final String mavenProjectPath = "C:/XAP";
	public static final String packageToCompile 		= "com.concept.crew.info";
	public static final String packageName 		= "com.concept.crew.info.jaxb";
	public static final String pojoPackageName 		= "com.concept.crew.info.pojo";
	public static final String dirSrcResource   = "src/main/resources/";
	public static final String xsdLocalPath     = mavenProjectPath + "/" + "temp";
	public static final String settingsFilePath = "settings.xml";
	
	// Maven Specific
	public static final String m2_repository     = mavenProjectPath + "/" + "m2_repository";
	
	public static final String logFilePath       = mavenProjectPath + "/" + "Logs" + "/" + "xap.log";
	
	public enum DatabaseType
	{
		ORACLE,
		MySQL,
		SQL_Server;
	}
	
	public enum inputType{
		XML,
		DELIMITED;
	}
	
	public static final String delimiterTableHeaderPrefi = "DELIMITER_";
}