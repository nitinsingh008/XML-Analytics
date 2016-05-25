package com.concept.crew.info;

import org.apache.velocity.app.VelocityEngine;

import com.concept.crew.util.FrameworkSettings;
import com.google.common.collect.Multimap;

public class GenerateRequest {

	private  VelocityEngine velocityEngine;
	private  FrameworkSettings projectSetting;
	private  Multimap<String, DBColumns> tableMap;
	private  String root;
	private  String postFix;
	private  Boolean isDelimited;
	
	public GenerateRequest(FrameworkSettings projectSetting,
			Multimap<String, DBColumns> tableMap, String root, String postFix,
			Boolean isDelimited) {
		super();
		this.projectSetting = projectSetting;
		this.tableMap = tableMap;
		this.root = root;
		this.postFix = postFix;
		this.isDelimited = isDelimited;
		this.velocityEngine = new VelocityEngine();
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
		return root;
	}

	public String getPostFix() {
		return postFix;
	}

	public Boolean isDelimited() {
		return isDelimited;
	}
	
}
