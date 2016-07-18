package com.concept.crew.generator;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import com.concept.crew.info.GenerateRequest;
import com.concept.crew.util.Constants;

public class XMLParserGenerator extends IGenerate {

	private  VelocityContext context;
	
	public XMLParserGenerator() {
		context = new  VelocityContext();
	}
	
	@Override
	public void generate(GenerateRequest request) {
		
		if(Constants.inputType.DELIMITED.toString().equals(request.getInputType())){
			return;
		}
		
		VelocityEngine velocityEngine = request.getVelocityEngine();
		File dir = new File(request.getProjectSetting().getPathToParser());
		if (!dir.exists()) {
			dir.mkdirs();
		}
		
		context.put("Import", Constants.packageName + ".*");
		context.put("ClassName", "XapParsingUtil");
		context.put("xsd", request.getXsdFile().getName());
		context.put("StartingTag", request.getStartingElement());
		context.put("StartingTagClass", request.getStartingElement()+".class");
		
		String fileLocation = IGenerate.VTL_LOC_1+"templates/XapParsingUtil.java.vtl";

		try {
			File templateLocation = new File(fileLocation);
			if(!templateLocation.exists()){
				fileLocation = IGenerate.getResourcePathOnServer()+"templates/XapParsingUtil.java.vtl";
			}
			Template template = velocityEngine.getTemplate(fileLocation);
			BufferedWriter writer = new BufferedWriter(new FileWriter(
					new File(request.getProjectSetting().getPathToParser() + File.separator +"XapParsingUtil.java")));
			template.merge(context, writer);
			writer.flush();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

}
