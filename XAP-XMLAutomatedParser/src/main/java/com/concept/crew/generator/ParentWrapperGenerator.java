package com.concept.crew.generator;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import com.concept.crew.info.GenerateRequest;
import com.concept.crew.processor.GeneratorEngine;
import com.concept.crew.util.Constants;

public class ParentWrapperGenerator extends IGenerate {

	private  VelocityContext context;
	private static Logger 		logger 			= Logger.getLogger(ParentWrapperGenerator.class);
	public ParentWrapperGenerator() {
		context = new VelocityContext();
	}
	
	@Override
	public void generate(GenerateRequest request) {
		VelocityEngine velocityEngine = request.getVelocityEngine();
		
		File scheduleDir = new File(request.getProjectSetting().getPathToParentInfoWrapper());
		if (!scheduleDir.exists()) {
			scheduleDir.mkdirs();
		}

		if (request.isDelimited()) {
			context.put("Import", Constants.pojoPackageName + "." + request.getRoot());
		} else {
			context.put("Import", Constants.packageName + "." + request.getRoot());
		}
		context.put("Root", request.getRoot());
		String className = "ParentInfoWrapper.java";
		String fileLocation = IGenerate.VTL_LOC_1+"templates/ParentInfoWrapper.java.vtl";

		try 
		{
			File templateLocation = new File(fileLocation);
			if(!templateLocation.exists())
			{
				fileLocation = IGenerate.getResourcePathOnServer()+"templates/ParentInfoWrapper.java.vtl";
			}
			logger.warn("ParentInfoWrapper => "+ fileLocation);
			
			Template template = velocityEngine.getTemplate(fileLocation);
			BufferedWriter writer = new BufferedWriter(new FileWriter(
					new File(request.getProjectSetting().getPathToParentInfoWrapper() + File.separator + className)));
			template.merge(context, writer);
			writer.flush();
			writer.close();
			logger.warn("ParentInfoWrapper Done ");
		} 
		catch (IOException e) {
		}

	}

}
