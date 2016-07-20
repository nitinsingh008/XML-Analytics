package com.concept.crew.generator;

import java.net.URL;

import com.concept.crew.info.GenerateRequest;

public abstract class IGenerate {

	public abstract void generate(GenerateRequest request);
	
	//public static final String VTL_LOC_1 ="./src/main/resources/";
	public static final String VTL_LOC_1 ="";
	
	public static String getResourcePathOnServer()
	{
		URL resource = IGenerate.class.getClassLoader().getResource("/");
		String rootPath = resource.getPath().substring(0, resource.getPath().indexOf("XAP")+3);
		if(rootPath.startsWith("/"))
		{
			rootPath = rootPath.substring(1);
		}
		//return rootPath+"/WEB-INF/classes/";
		return "";
	}

	public static void main(String args[])
	{
		String temp = "/D:/eclipse-Luna/svn_checkouts/trunk/concept-XML-analysis/target/.extract/webapps/XAP/WEB-INF/classes/templates/ParentInfoWrapper.java.vtl";
		
		if(temp.startsWith("/"))
		{
			temp = temp.substring(1);
		}
		System.out.println(temp);
	}
	
}
