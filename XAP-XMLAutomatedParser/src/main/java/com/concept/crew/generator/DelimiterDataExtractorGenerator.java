package com.concept.crew.generator;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import com.concept.crew.info.GenerateRequest;
import com.concept.crew.util.Constants;

public class DelimiterDataExtractorGenerator implements IGenerate{

	private  VelocityContext context;
	
	public DelimiterDataExtractorGenerator(){
		context = new  VelocityContext();
	}
	
	
	@Override
	public void generate(GenerateRequest request) {
		if(request.getInputType().equals(Constants.inputType.DELIMITED.toString())){
			VelocityEngine velocityEngine = request.getVelocityEngine();
			File dir = new File(request.getProjectSetting().getPathToLoaderType());
			if (!dir.exists()) {
				dir.mkdirs();
			}
			
			Iterator<String> tableMapIt = request.getTableMap().keySet().iterator();
			// in this case it wil be only one
			String tableName = tableMapIt.next();
			context.put("classImport", Constants.pojoPackageName + ".*");
			context.put("classType", tableName);
				
			try {
				Template template = velocityEngine.getTemplate("./src/main/resources/templates/DelimitedDataExtractor.java.vtl");
				BufferedWriter writer = new BufferedWriter(new FileWriter(
						new File(request.getProjectSetting().getPathToLoaderType() + File.separator +"DelimiterDataExtractor.java")));
				template.merge(context, writer);
				writer.flush();
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}

}
