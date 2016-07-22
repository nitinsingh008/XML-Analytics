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

public class EssentialsGenerator extends IGenerate {

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
			String fileLocation = IGenerate.VTL_LOC_1+"templates/AbstractDataLoader.java.vtl";
			File templateLocation = new File(fileLocation);
			if(!templateLocation.exists()){
				fileLocation = IGenerate.getResourcePathOnServer()+"templates/AbstractDataLoader.java.vtl";
			}
			template = velocityEngine.getTemplate(fileLocation);
			writer = new BufferedWriter(new FileWriter(
					new File(request.getProjectSetting().getPathToLoaderType()+ File.separator +  "AbstractDataLoader.java")));
			template.merge(context, writer);
			writer.flush();
			writer.close();
			
			String domainFileLocation = IGenerate.VTL_LOC_1+"templates/IDataDomainLoader.java.vtl";
			File domainFile = new File(domainFileLocation);
			if(!domainFile.exists()){
				domainFileLocation = IGenerate.getResourcePathOnServer()+"templates/IDataDomainLoader.java.vtl";
			}
			context.put("InterfaceName", "IDataDomainLoader");
			template = velocityEngine.getTemplate(domainFileLocation);
			writer = new BufferedWriter(new FileWriter(
					new File(request.getProjectSetting().getPathToLoaderType() + File.separator + "IDataDomainLoader.java")));
			template.merge(context, writer);
			writer.flush();
			writer.close();
			
			String dataDomainLocation = IGenerate.VTL_LOC_1+"templates/IDomainLoader.java.vtl";
			File dataDomainFile = new File(dataDomainLocation);
			if(!dataDomainFile.exists()){
				dataDomainLocation = IGenerate.getResourcePathOnServer()+"templates/IDomainLoader.java.vtl";
			}
			context.put("InterfaceName", "IDomainLoader");
			template = velocityEngine.getTemplate(dataDomainLocation);
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
			String dataFileLocation = IGenerate.VTL_LOC_1+"templates/DataWriter.java.vtl";
			File dataFile = new File(dataFileLocation);
			if(!dataFile.exists()){
				dataFileLocation = IGenerate.getResourcePathOnServer()+"templates/DataWriter.java.vtl";
			}
			template = velocityEngine.getTemplate(dataFileLocation);
			writer = new BufferedWriter(new FileWriter(
					new File(request.getProjectSetting().getPathToCommon()+ File.separator +  "DataWriter.java")));
			template.merge(context, writer);
			writer.flush();
			writer.close();
			
			String dataLoaderLocation = IGenerate.VTL_LOC_1+"templates/DataLoader.java.vtl";
			File datLoaderFile = new File(dataLoaderLocation);
			if(!datLoaderFile.exists()){
				dataLoaderLocation = IGenerate.getResourcePathOnServer()+"templates/DataLoader.java.vtl";
			}
			context.put("ClassName", "DataLoader");		
			template = velocityEngine.getTemplate(dataLoaderLocation);
			writer = new BufferedWriter(new FileWriter(
					new File(request.getProjectSetting().getPathToCommon()+ File.separator +  "DataLoader.java")));
			template.merge(context, writer);
			writer.flush();
			writer.close();
			
			String dataWriterLocation = IGenerate.VTL_LOC_1+"templates/DataWriterImpl.java.vtl";
			File datawriter = new File(dataWriterLocation);
			if(!datawriter.exists()){
				dataWriterLocation = IGenerate.getResourcePathOnServer()+"templates/DataWriterImpl.java.vtl";
			}
			context.put("ClassName", "DataWriterImpl");		
			template = velocityEngine.getTemplate(dataWriterLocation);
			writer = new BufferedWriter(new FileWriter(
					new File(request.getProjectSetting().getPathToCommon()+ File.separator +  "DataWriterImpl.java")));
			template.merge(context, writer);
			writer.flush();
			writer.close();
			
		} catch (IOException e) {
		}
		
		File queryUtil = new File(request.getProjectSetting().getPathToLoaderType());
		if (!queryUtil.exists()) {
			queryUtil.mkdirs();
		}
		
		context.put("ResourcePath", request.getProjectSetting().getResourcePath());	
		StringBuffer sb= new StringBuffer();
		
		if(request.getDbType() != null){
			if(Constants.DatabaseType.ORACLE.toString().equals(request.getDbType())){
				sb.append("SELECT LEVEL, CORE_REF_DATA.").append(request.getRoot().toUpperCase()).append("_SEQ").append(".NEXTVAL ID FROM DUAL CONNECT BY LEVEL <=");
			}else if(Constants.DatabaseType.JavaDB_DERBY.toString().equals(request.getDbType())){
				sb.append("SELECT currentvalue ID FROM sys.syssequences WHERE sequencename ='").append(request.getRoot().toUpperCase()).append("_SEQ'");
			}
		}else{
			sb.append("SELECT LEVEL, CORE_REF_DATA.").append(request.getRoot().toUpperCase()).append("_SEQ").append(".NEXTVAL ID FROM DUAL CONNECT BY LEVEL <=");
		}
		
		context.put("PKSql", sb.toString());
		try {
			String dataReaderLocation = IGenerate.VTL_LOC_1+"templates/QueryReaderUtil.java.vtl";
			File datareader = new File(dataReaderLocation);
			if(!datareader.exists()){
				dataReaderLocation = IGenerate.getResourcePathOnServer()+"templates/QueryReaderUtil.java.vtl";
			}
			
			template = velocityEngine.getTemplate(dataReaderLocation);
			writer = new BufferedWriter(new FileWriter(
					new File(request.getProjectSetting().getPathToLoaderType()+ File.separator +  "QueryReaderUtil.java")));
			template.merge(context, writer);
			writer.flush();
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
