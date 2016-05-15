package com.concept.crew.util;

public class Constants 
{
	public static final String mavenProjectPath = "C:/XAP";
	public static final String mavenProjectName = "LoadersFramework";
	public static final String packageName 		= "com.concept.crew.info.jaxb";
	public static final String dirSrcJava 		= mavenProjectPath + "/"+ mavenProjectName + "/src/main/java";
	public static final String dirSrcResource   = "src/main/resources/";
	public static final String compilationPath  = dirSrcJava + "/com/concept/crew/info/jaxb";
	public static final String resourcePath 	= mavenProjectPath + "/" +mavenProjectName +"/" + dirSrcResource;
	public static final String pomPath 			= mavenProjectPath + "/" +mavenProjectName + "/pom.xml";
	public static final String targetPath 		= mavenProjectPath + "/" + mavenProjectName + "/target";
	public static final String jarFileName 		= mavenProjectName + "-1.0.jar";
	public static final String xsdLocalPath     = mavenProjectPath + "/" + "temp";
	public static final String settingsFilePath = "settings.xml";
	public static final String pathToRootClass 	= targetPath + "/" + "classes";
	
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
}