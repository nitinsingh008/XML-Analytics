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

public class ScheduleGenerator implements IGenerate{
	
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
			
			try
			{
				if(tableName.equals(request.getRoot()))
				{
					template = velocityEngine.getTemplate("./src/main/resources/templates/RootLoader.java.vtl");
					writer = new BufferedWriter(new FileWriter(new File(request.getProjectSetting().getPathToGenerateSchedules() + File.separator + className + ".java")));
					template.merge(context, writer);
				}
				else
				{
					template = velocityEngine.getTemplate("./src/main/resources/templates/ScheduleLoader.java.vtl");
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
