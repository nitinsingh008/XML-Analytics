package com.concept.crew.util;

import org.apache.log4j.FileAppender;
import org.apache.log4j.RollingFileAppender;

public class CustomRollingFileAppender extends RollingFileAppender
{
	
	/**
	* The default constructor simply calls its {@link FileAppender#FileAppender
	* parents constructor}.
	*/
	public CustomRollingFileAppender() 
	{
		super();
		this.fileName = Constants.logFilePath;
	}

}
