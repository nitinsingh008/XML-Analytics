package com.concept.crew.generator;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import com.concept.crew.generator.IGenerate;
import com.concept.crew.info.GenerateRequest;
import com.concept.crew.util.Constants;

public class FileProcessorGenerator extends IGenerate {
	
	private  VelocityContext context;
	
	public FileProcessorGenerator() {
		context = new VelocityContext();	
	}
	
	@Override
	public void generate(GenerateRequest request) {
		Template template = null;
		BufferedWriter writer = null;
		
		VelocityEngine velocityEngine = request.getVelocityEngine();
		File dir = new File(request.getProjectSetting().getPathToProcessor());
		if (!dir.exists()) {
			dir.mkdirs();
		}
		
		String javaFileName = "";
		if(Constants.inputType.DELIMITED.toString().equals(request.getInputType())){
			javaFileName = "DelimitedFileProcessor.java";
			context.put("Import", Constants.pojoPackageName + "." + request.getRoot());
			context.put("ClassName", "DelimitedFileProcessor");
			context.put("type", "DELIMITED");
			context.put("ClassNameClass", "DelimitedFileProcessor.class");
			context.put("StartingTag", request.getRoot());
			context.put("FileToBeParsed", request.getInputFile());
			
			Iterator<String> tableMapIt = request.getTableMap().keySet().iterator();
			// in this case it wil be only one
			String tableName = tableMapIt.next();
			context.put("DelimiterclassType", tableName);
		}else{
			javaFileName = "XMLFileProcessor.java";
			context.put("Import", Constants.packageName + "." + request.getStartingElement());
			context.put("ClassName", "XMLFileProcessor");
			context.put("type", "XML");
			context.put("ClassNameClass", "XMLFileProcessor.class");
			context.put("StartingTag", request.getStartingElement());
			context.put("FileToBeParsed", request.getInputFile());
		}
		
		if(request.getProjectSetting().getDbDetails() != null){
			context.put("DbType", request.getProjectSetting().getDbDetails().getDbType());
			context.put("JdbcUrl", request.getProjectSetting().getDbDetails().getJdbcUrl());
			context.put("Username", request.getProjectSetting().getDbDetails().getUser());
			context.put("Password", request.getProjectSetting().getDbDetails().getPassword());
		}
		context.put("List", request.getRoot());
		String fileLocation = IGenerate.VTL_LOC_1+"templates/IncomingFileProcessor.java.vtl";
		try {
			File templateLocation = new File(fileLocation);
			if(!templateLocation.exists()){
				fileLocation = IGenerate.getResourcePathOnServer()+"templates/IncomingFileProcessor.java.vtl";
			}
			template = velocityEngine.getTemplate(fileLocation);
			writer = new BufferedWriter(new FileWriter(
					new File(request.getProjectSetting().getPathToProcessor() + File.separator + javaFileName)));
			template.merge(context, writer);
			writer.flush();
			writer.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	
	}

}
