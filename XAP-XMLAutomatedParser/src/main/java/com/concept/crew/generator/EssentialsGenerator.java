package com.concept.crew.generator;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import com.concept.crew.info.GenerateRequest;

public class EssentialsGenerator implements IGenerate {

	private  VelocityContext context;
	
	public EssentialsGenerator() {
		context = new  VelocityContext();
	}
	
	@Override
	public void generate(GenerateRequest request) {
		Template template = null;
		BufferedWriter writer = null;
		
		VelocityEngine velocityEngine = request.getVelocityEngine();
		
		File scheduleDir = new File(request.getProjectSetting().getPathToLoaderType());
		if (!scheduleDir.exists()) {
			scheduleDir.mkdirs();
		}
		
		try {
			context.put("ClassName", "AbstractDataLoader");
			template = velocityEngine.getTemplate("./src/main/resources/templates/AbstractDataLoader.java.vtl");
			writer = new BufferedWriter(new FileWriter(
					new File(request.getProjectSetting().getPathToLoaderType()+ File.separator +  "AbstractDataLoader.java")));
			template.merge(context, writer);
			writer.flush();
			writer.close();
			
			context.put("InterfaceName", "IDataDomainLoader");
			template = velocityEngine.getTemplate("./src/main/resources/templates/IDataDomainLoader.java.vtl");
			writer = new BufferedWriter(new FileWriter(
					new File(request.getProjectSetting().getPathToLoaderType() + File.separator + "IDataDomainLoader.java")));
			template.merge(context, writer);
			writer.flush();
			writer.close();
			
			context.put("InterfaceName", "IDomainLoader");
			template = velocityEngine.getTemplate("./src/main/resources/templates/IDomainLoader.java.vtl");
			writer = new BufferedWriter(new FileWriter(
					new File(request.getProjectSetting().getPathToLoaderType() + File.separator + "IDomainLoader.java")));
			template.merge(context, writer);
			writer.flush();
			writer.close();
		} catch (IOException e) {
		}
			
		File commonDir = new File(request.getProjectSetting().getPathToCommon());
		if (!commonDir.exists()) {
			commonDir.mkdirs();
		}
		context = null;
		context = new VelocityContext();
		context.put("ClassName", "DataWriter");		
		try {
			template = velocityEngine.getTemplate("./src/main/resources/templates/DataWriter.java.vtl");
			writer = new BufferedWriter(new FileWriter(
					new File(request.getProjectSetting().getPathToCommon()+ File.separator +  "DataWriter.java")));
			template.merge(context, writer);
			writer.flush();
			writer.close();
			
			context.put("ClassName", "DataLoader");		
			template = velocityEngine.getTemplate("./src/main/resources/templates/DataLoader.java.vtl");
			writer = new BufferedWriter(new FileWriter(
					new File(request.getProjectSetting().getPathToCommon()+ File.separator +  "DataLoader.java")));
			template.merge(context, writer);
			writer.flush();
			writer.close();
			
			context.put("ClassName", "DataWriterImpl");		
			template = velocityEngine.getTemplate("./src/main/resources/templates/DataWriterImpl.java.vtl");
			writer = new BufferedWriter(new FileWriter(
					new File(request.getProjectSetting().getPathToCommon()+ File.separator +  "DataWriterImpl.java")));
			template.merge(context, writer);
			writer.flush();
			writer.close();
			
		} catch (IOException e) {
		}
	}

}
