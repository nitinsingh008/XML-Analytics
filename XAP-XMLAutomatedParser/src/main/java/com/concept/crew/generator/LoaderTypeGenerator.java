package com.concept.crew.generator;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import com.concept.crew.info.GenerateRequest;

public class LoaderTypeGenerator extends IGenerate {

	private  VelocityContext context;
	
	public LoaderTypeGenerator() {
		this.context = new  VelocityContext();
	}
	
	
	@Override
	public void generate(GenerateRequest request) {
		VelocityEngine velocityEngine = request.getVelocityEngine();
		
		
		File scheduleDir = new File(request.getProjectSetting().getPathToLoaderType());
		if (!scheduleDir.exists()) {
			scheduleDir.mkdirs();
		}

		Map<String, String> nameMap = new HashMap<String, String>();
		Iterator<String> tableMapIt = request.getTableMap().keySet().iterator();
		String tableNameWithPostFix = null;
		String rootWithPostFix = null;
		
		
		if (request.getPostFix() != null && !"".equals(request.getPostFix())) {
			rootWithPostFix = (request.getRoot() + "_" + request.getPostFix()).toUpperCase();
		} else {
			rootWithPostFix = request.getRoot().toUpperCase();
		}
		String sbRoot = request.getRoot() + "Loader" + ".class";
		context.put("Root", rootWithPostFix);		
		context.put("RootLoader", sbRoot );		
		
		
		while (tableMapIt.hasNext()) {
			String tableName = tableMapIt.next();
			if (request.getRoot().equals(tableName) || tableName == null || tableName.contains("$")
					|| tableName.toUpperCase().contains("OBJECTFACTORY")) {
				continue;
			}

			if (request.getPostFix() != null && !"".equals(request.getPostFix())) {
				tableNameWithPostFix = (tableName + "_" + request.getPostFix()).toUpperCase();
			} else {
				tableNameWithPostFix = tableName.toUpperCase();
			}
			String loader = tableName + "Loader" + ".class";
			nameMap.put(tableNameWithPostFix,  loader);
		}
		context.put("mapp", nameMap);	
		String fileLocation = IGenerate.VTL_LOC_1+"templates/LoadersType.java.vtl";

		try {
			File templateLocation = new File(fileLocation);
			if(!templateLocation.exists()){
				fileLocation = IGenerate.getResourcePathOnServer()+"templates/LoadersType.java.vtl";
			}
			Template template = velocityEngine.getTemplate(fileLocation);
			BufferedWriter writer = new BufferedWriter(new FileWriter(
					new File(request.getProjectSetting().getPathToLoaderType() + File.separator + "LoadersType.java")));
			template.merge(context, writer);
			writer.flush();
			writer.close();
		} catch (IOException e) {

		}
	}

}
