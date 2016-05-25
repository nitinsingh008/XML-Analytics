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

public class LoadSaveProcessorGenerator implements IGenerate {

	private  VelocityContext context;
	
	public LoadSaveProcessorGenerator() {
		context = new  VelocityContext();
	}
	
	
	@Override
	public void generate(GenerateRequest request) {
		VelocityEngine velocityEngine = request.getVelocityEngine();
		File scheduleDir = new File(request.getProjectSetting().getPathToLoadSaveProcessor());
		if (!scheduleDir.exists()) {
			scheduleDir.mkdirs();
		}

		if (request.isDelimited()) {
			context.put("Import", Constants.pojoPackageName + "." + request.getRoot());
		} else {
			context.put("Import", Constants.packageName + "." + request.getRoot());
		}
		context.put("Root", request.getRoot());
		try {
			Template template = velocityEngine.getTemplate("./src/main/resources/templates/LoadSaveProcessor.java.vtl");
			BufferedWriter writer = new BufferedWriter(new FileWriter(
					new File(request.getProjectSetting().getPathToLoadSaveProcessor() + File.separator +"LoadSaveProcessor.java")));
			template.merge(context, writer);
			writer.flush();
			writer.close();
		} catch (IOException e) {

		}
	}

}
