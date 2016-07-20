package com.concept.crew.info;

import java.io.File;

import org.apache.velocity.app.VelocityEngine;

import com.concept.crew.util.FrameworkSettings;
import com.concept.crew.util.Pair;
import com.google.common.collect.Multimap;

public class GenerateRequest {

	private  VelocityEngine velocityEngine;
	private  FrameworkSettings projectSetting;
	private  Multimap<String, DBColumns> tableMap;
	private  Pair<String, String> xsdNodes;
	private  String postFix;
	private  Boolean isDelimited;
	private  File xsdFile;
	private  String inputType;
	private  File inputFile; //XML or CSV
	
	public GenerateRequest(FrameworkSettings projectSetting,
			Multimap<String, DBColumns> tableMap, Pair<String, String> xsdNodes, String postFix,
			Boolean isDelimited, File xsdFile,String inputType) {
		super();
		this.projectSetting = projectSetting;
		this.tableMap = tableMap;
		this.xsdNodes = xsdNodes;
		this.postFix = postFix;
		this.isDelimited = isDelimited;
		this.xsdFile = xsdFile;
		this.inputType = inputType;	
		this.velocityEngine = new VelocityEngine();
		velocityEngine.setProperty("resource.loader", "class");
		velocityEngine.setProperty("class.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
		this.velocityEngine.init();  
	}

	public VelocityEngine getVelocityEngine() {
		return velocityEngine;
	}
	
	public FrameworkSettings getProjectSetting() {
		return projectSetting;
	}

	public Multimap<String, DBColumns> getTableMap() {
		return tableMap;
	}

	public String getRoot() {
		return xsdNodes.getRight();
	}

	public String getPostFix() {
		return postFix;
	}

	public Boolean isDelimited() {
		return isDelimited;
	}

	public File getXsdFile() {
		return xsdFile;
	}

	public String getInputType() {
		return inputType;
	}
	
	public String getStartingElement(){
		return xsdNodes.getLeft();
	}

	public File getInputFile() {
		return inputFile;
	}

	
}
