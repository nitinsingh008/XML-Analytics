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

public class LoaderTypeGenerator implements IGenerate {

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

		Map<String, StringBuffer> nameMap = new HashMap<String, StringBuffer>();
		Iterator<String> tableMapIt = request.getTableMap().keySet().iterator();
		String tableNameWithPostFix = null;
		while (tableMapIt.hasNext()) {
			String tableName = tableMapIt.next();
			StringBuffer sb = new StringBuffer(tableName);
			if (tableName == null || tableName.contains("$")
					|| tableName.toUpperCase().contains("OBJECTFACTORY")) {
				continue;
			}

			if (request.getPostFix() != null && !"".equals(request.getPostFix())) {
				tableNameWithPostFix = (tableName + "_" + request.getPostFix()).toUpperCase();
			} else {
				tableNameWithPostFix = tableName.toUpperCase();
			}

			nameMap.put(tableNameWithPostFix,  sb.append("Loader").append(".class"));
		}
		context.put("mapp", nameMap);		
		try {
			Template template = velocityEngine.getTemplate("./src/main/resources/templates/LoadersType.java.vtl");
			BufferedWriter writer = new BufferedWriter(new FileWriter(
					new File(request.getProjectSetting().getPathToLoaderType() + File.separator + "LoadersType.java")));
			template.merge(context, writer);
			writer.flush();
			writer.close();
		} catch (IOException e) {

		}
	}

}
