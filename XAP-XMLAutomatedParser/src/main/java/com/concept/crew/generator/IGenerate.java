package com.concept.crew.generator;

import com.concept.crew.info.GenerateRequest;

public abstract class IGenerate {

	public abstract void generate(GenerateRequest request);
	
	public static final String VTL_LOC_1 ="./src/main/resources/";
	
	public static String getResourcePathOnServer(){
		String resource = IGenerate.class.getClassLoader().getResource("/").getPath();
		String rootPath = resource.substring(0, resource.indexOf("XAP")+3);
		return rootPath+"/WEB-INF/classes/";
	}

}
