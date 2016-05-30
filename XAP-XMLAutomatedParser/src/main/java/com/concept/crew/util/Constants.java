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
	public static final String mavenVersion 	 = "apache-maven-3.2.2";
	public static final String mavenHome 	 	 = mavenProjectPath + "/" + mavenVersion;
	public static final String mavenZip 	     = mavenVersion + ".zip";
	
	public static final String logFilePath       = mavenProjectPath + "/" + "Logs" + "/" + "xap.log";
	
	
	// Database Specific
	public static final String databaseLoc     = mavenProjectPath + "/" + "xapDB";
	public static final String debryDb     	   = "db-derby-10.12.1.1.zip";
	public static final String dbBatchFile     = "startDBConsole.bat";
	
	public enum DatabaseType
	{
		ORACLE,
		MySQL,
		SQL_Server,
		JavaDB_DERBY;
	}
	
	public enum inputType{
		XML,
		DELIMITED;
	}
	
	public static final String delimiterTableHeaderPrefi = "DELIMITER_";
}