package com.concept.crew.processor;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.maven.shared.invoker.MavenInvocationException;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import com.concept.crew.info.DBColumns;
import com.concept.crew.util.AutomationHelper;
import com.concept.crew.util.Constants;
import com.concept.crew.util.FrameworkSettings;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

public class FrameworkGenerator 
{
	private static Logger 		logger 			= Logger.getLogger(FrameworkGenerator.class);

	private static VelocityEngine velocityEngine;
	private static VelocityContext context;
	private static FrameworkSettings projectSetting;
	private static Multimap<String, DBColumns> tableMap;
	private static String root;
	private static String postFix;
	private static Boolean isDelimited;
	private static AutomationHelper helper;
	
	public static void initialize(FrameworkSettings setting, Multimap<String, DBColumns> map, String rootNode, String tablePostFix, Boolean isDelimitedReq, AutomationHelper autoHelper)	
	{
		velocityEngine = new VelocityEngine();
		velocityEngine.init();       
		projectSetting = setting;
		tableMap = map;
		root = rootNode;
		postFix = tablePostFix;
		isDelimited = isDelimitedReq;
		helper = autoHelper;
	}
	
	public static void generateAll() throws MavenInvocationException
	{
		logger.warn("----------------------------------------");
		logger.warn("Generating Loader Framework Code");
		logger.warn("----------------------------------------");
		generateParentWrapper();
		generateEssentials();
		helper.buildMavenProject();
		generateSchedule();	
		
		generateLoaderType();
		generateLoadSaveProcessor();
		logger.warn("Loader Framework Code ready");
	}
	
	public static void generateEssentials(){
		Template template = null;
		BufferedWriter writer = null;
		context = null;
		context = new VelocityContext();
		File scheduleDir = new File(projectSetting.getPathToLoaderType());
		if (!scheduleDir.exists()) {
			scheduleDir.mkdirs();
		}
		context.put("Class", "ParentInfoWrapper");
		
		try {
			template = velocityEngine.getTemplate("./src/main/resources/templates/AbstractDataLoader.java.vtl");
			writer = new BufferedWriter(new FileWriter(new File(projectSetting.getPathToGenerateSchedules()+ File.separator +  "AbstractDataLoader.java")));
			template.merge(context, writer);
			
			template = velocityEngine.getTemplate("./src/main/resources/templates/IDataDomainLoader.java.vtl");
			writer = new BufferedWriter(new FileWriter(new File(projectSetting.getPathToGenerateSchedules() + File.separator + "IDataDomainLoader.java")));
			template.merge(context, writer);			
		} catch (IOException e) {
		}
				
	}
	
	public static void generateSchedule() {
		Template template = null;
		BufferedWriter writer = null;
		context = null;
		context = new VelocityContext();
		
		File scheduleDir = new File(projectSetting.getPathToGenerateSchedules());
		if (!scheduleDir.exists()) {
			scheduleDir.mkdirs();
		}
		
		Iterator<String> tableMapIt = tableMap.keySet().iterator();
		while (tableMapIt.hasNext()) {

			String tableName = tableMapIt.next();
			if (tableName == null || tableName.contains("$") || tableName.toUpperCase().contains("OBJECTFACTORY")) {
				continue;
			}

			Collection<DBColumns> columnList = tableMap.get(tableName);

			for (DBColumns columns : columnList) {
				String columnName = columns.getName();
				if (columnName.length() > 30) {
					columns.setName(columnName.substring(0, 30));
				}
				columns.setName(columnName.substring(0, 1).toUpperCase() + columnName.substring(1));
				// sb.append(columns.getName().toUpperCase()).append("\t\t").
				// append(columns.getDataType().toUpperCase()).append(",\n");
			}
			
			if(isDelimited){
				context.put("Import", Constants.pojoPackageName + ".*" );
			}else{
				context.put("Import", Constants.packageName + ".*");
			}
			String infoName = tableName;
			String className = infoName +  "Loader";
			context.put("Root", root);
			context.put("Info", infoName);
			context.put("ClassName", className);
			context.put("columnList", columnList);
			
			try
			{
				if(tableName.equals(root))
				{
					template = velocityEngine.getTemplate("./src/main/resources/templates/RootLoader.java.vtl");
					writer = new BufferedWriter(new FileWriter(new File(projectSetting.getPathToGenerateSchedules() + File.separator + className + ".java")));
					template.merge(context, writer);
				}
				else
				{
					template = velocityEngine.getTemplate("./src/main/resources/templates/ScheduleLoader.java.vtl");
					writer = new BufferedWriter(new FileWriter(new File(projectSetting.getPathToGenerateSchedules() + File.separator + className + ".java")));
					template.merge(context, writer);
				}
				writer.flush();
				writer.close();
				logger.warn("Generated Child Loder " + className);
			}
			catch(Exception e){
				e.printStackTrace();
			}
		}
	}
	
	public static void generateParentWrapper() {
		context = null;
		context = new VelocityContext();

		File scheduleDir = new File(projectSetting.getPathToParentInfoWrapper());
		if (!scheduleDir.exists()) {
			scheduleDir.mkdirs();
		}

		if (isDelimited) {
			context.put("Import", Constants.pojoPackageName + "." + root);
		} else {
			context.put("Import", Constants.packageName + "." + root);
		}
		context.put("Root", root);
		String className = "ParentInfoWrapper.java";
		try 
		{
			Template template = velocityEngine.getTemplate("./src/main/resources/templates/ParentInfoWrapper.java.vtl");
			BufferedWriter writer = new BufferedWriter(new FileWriter(new File(projectSetting.getPathToParentInfoWrapper() + File.separator + className)));
			template.merge(context, writer);
			writer.flush();
			writer.close();
			logger.warn("Generated Parent Loder " + className);
		} 
		catch (IOException e) {
		}
	}
	
	public static void generateLoadSaveProcessor() {
		context = null;
		context = new VelocityContext();

		File scheduleDir = new File(projectSetting.getPathToLoadSaveProcessor());
		if (!scheduleDir.exists()) {
			scheduleDir.mkdirs();
		}

		if (isDelimited) {
			context.put("Import", Constants.pojoPackageName + "." + root);
		} else {
			context.put("Import", Constants.packageName + "." + root);
		}
		context.put("Root", root);
		try {
			Template template = velocityEngine.getTemplate("./src/main/resources/templates/LoadSaveProcessor.java.vtl");
			BufferedWriter writer = new BufferedWriter(new FileWriter(new File(projectSetting.getPathToLoadSaveProcessor() + File.separator +"LoadSaveProcessor.java")));
			template.merge(context, writer);
			writer.flush();
			writer.close();
		} catch (IOException e) {

		}
	}
	
	public static void generateLoaderType() {
		context = null;
		context = new VelocityContext();
		
		File scheduleDir = new File(projectSetting.getPathToLoaderType());
		if (!scheduleDir.exists()) {
			scheduleDir.mkdirs();
		}

		Map<String, StringBuffer> nameMap = new HashMap<String, StringBuffer>();
		Iterator<String> tableMapIt = tableMap.keySet().iterator();
		String tableNameWithPostFix = null;
		while (tableMapIt.hasNext()) {
			String tableName = tableMapIt.next();
			StringBuffer sb = new StringBuffer(tableName);
			if (tableName == null || tableName.contains("$")
					|| tableName.toUpperCase().contains("OBJECTFACTORY")) {
				continue;
			}

			if (postFix != null && !"".equals(postFix)) {
				tableNameWithPostFix = (tableName + "_" + postFix).toUpperCase();
			} else {
				tableNameWithPostFix = tableName.toUpperCase();
			}

			nameMap.put(tableNameWithPostFix,  sb.append("Loader").append(".class"));
		}
		context.put("mapp", nameMap);		
		try {
			Template template = velocityEngine.getTemplate("./src/main/resources/templates/LoadersType.java.vtl");
			BufferedWriter writer = new BufferedWriter(new FileWriter(new File(projectSetting.getPathToLoaderType() + File.separator + "LoadersType.java")));
			template.merge(context, writer);
			writer.flush();
			writer.close();
		} catch (IOException e) {

		}
	}
	
	public static void main(String[] args) throws Exception
	{
		
		//generateParentWrapper("Instrument");
		FrameworkSettings fg = new FrameworkSettings("LoadersFramework");
		Multimap<String, DBColumns> tableMap =  ArrayListMultimap.create();
		DBColumns column = new DBColumns();
		tableMap.put("Instrument", column);
		tableMap.put("CallSchedule", column);
		tableMap.put("PutSchedule", column);
		tableMap.put("Ratings", column);
		tableMap.put("Redemption", column);
		tableMap.put("ObjectFactory", column);
		//initialize(fg, tableMap, "Instrument", "raw", false);
		
		//generateParentWrapper();
		
		//generateLoaderType();
		generateLoadSaveProcessor();
		
      /*  Template template = velocityEngine.getTemplate("./src/main/resources/templates/RootLoader.java.vtl" );

        StringWriter writer = new StringWriter();
        //BufferedWriter writer = new BufferedWriter(new FileWriter("./src/main/resources/JavaClasses/InstrumentLoader.java"));
        
        template.merge( context, writer );
        writer.flush();
        writer.close();

        System.out.println( writer.toString() );    */    
	}

}
