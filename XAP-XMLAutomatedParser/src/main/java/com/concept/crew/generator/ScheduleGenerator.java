package com.concept.crew.generator;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Collection;
import java.util.Iterator;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import com.concept.crew.info.DBColumns;
import com.concept.crew.info.GenerateRequest;
import com.concept.crew.util.Constants;

public class ScheduleGenerator extends IGenerate{
	
	private  VelocityContext context;
	
	public ScheduleGenerator() {
		context = new VelocityContext();
	}

	@Override
	public void generate(GenerateRequest request) {
		Template template = null;
		BufferedWriter writer = null;
		
		VelocityEngine velocityEngine = request.getVelocityEngine();
		
		File scheduleDir = new File(request.getProjectSetting().getPathToGenerateSchedules());
		if (!scheduleDir.exists()) {
			scheduleDir.mkdirs();
		}
		
		Iterator<String> tableMapIt = request.getTableMap().keySet().iterator();
		while (tableMapIt.hasNext()) {

			String tableName = tableMapIt.next();
			if (tableName == null || tableName.contains("$") || tableName.toUpperCase().contains("OBJECTFACTORY")) {
				continue;
			}

			Collection<DBColumns> columnList = request.getTableMap().get(tableName);

			for (DBColumns columns : columnList) {
				String columnName = columns.getName();
				if (columnName.length() > 30) {
					columns.setName(columnName.substring(0, 30));
				}
				columns.setName(columnName.substring(0, 1).toUpperCase() + columnName.substring(1));
			}
			
			if(request.isDelimited()){
				context.put("Import", Constants.pojoPackageName + ".*" );
			}else{
				context.put("Import", Constants.packageName + ".*");
			}
			String infoName = tableName;
			String className = infoName +  "Loader";
			context.put("Root", request.getRoot());
			context.put("Info", infoName);
			context.put("ClassName", className);
			context.put("columnList", columnList);
			String rootFileLocation = IGenerate.VTL_LOC_1+"templates/RootLoader.java.vtl";
			String scheduleFileLocation = IGenerate.VTL_LOC_1+"templates/ScheduleLoader.java.vtl";

			try {
				File templateLocation = new File(rootFileLocation);
				if(!templateLocation.exists()){
					rootFileLocation = IGenerate.getResourcePathOnServer()+"templates/RootLoader.java.vtl";
				}
				File templateScheduleLocation = new File(scheduleFileLocation);
				if(!templateScheduleLocation.exists()){
					scheduleFileLocation = IGenerate.getResourcePathOnServer()+"templates/ScheduleLoader.java.vtl";
				}
				
				if(tableName.equals(request.getRoot()))
				{
					template = velocityEngine.getTemplate(rootFileLocation);
					writer = new BufferedWriter(new FileWriter(new File(request.getProjectSetting().getPathToGenerateSchedules() + File.separator + className + ".java")));
					template.merge(context, writer);
				}
				else
				{
					template = velocityEngine.getTemplate(scheduleFileLocation);
					writer = new BufferedWriter(new FileWriter(new File(request.getProjectSetting().getPathToGenerateSchedules() + File.separator + className + ".java")));
					template.merge(context, writer);
				}
				writer.flush();
				writer.close();
			}
			catch(Exception e){
				e.printStackTrace();
			}
		}
		
	}

}
