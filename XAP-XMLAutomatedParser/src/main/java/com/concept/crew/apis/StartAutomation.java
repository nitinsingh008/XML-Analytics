package com.concept.crew.apis;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.Logger;

import com.concept.crew.dao.XapDBRoutine;
import com.concept.crew.info.DBColumns;
import com.concept.crew.info.GenerateRequest;
import com.concept.crew.processor.CsvTableGenerator;
import com.concept.crew.processor.DBScriptRunner;
import com.concept.crew.processor.GeneratorEngine;
import com.concept.crew.processor.JaxbTableGenerator;
import com.concept.crew.processor.TableGenerator;
import com.concept.crew.util.AutomationHelper;
import com.concept.crew.util.Constants;
import com.concept.crew.util.FrameworkSettings;
import com.concept.crew.util.JaxbInfoGenerator;
import com.concept.crew.util.Pair;
import com.concept.crew.util.XSDParseRequest;
import com.google.common.collect.Multimap;

public class StartAutomation 
{
	private static Logger 		logger 			= Logger.getLogger(StartAutomation.class);
	private FrameworkSettings projectSetting;
	private XSDParseRequest request;
	private String rootNode = null;
	private Pair<String, String> nodes = null;
	private static SimpleDateFormat ddmmyyyhhmm = new SimpleDateFormat("ddmmyyyyHHmm");
	private AutomationHelper autoHelper;
	
	public StartAutomation(XSDParseRequest request) {
		super();
		this.request = request;
		String inputFileName = request.getParsedXSDPath().substring(request.getParsedXSDPath().lastIndexOf(File.separator)+1, request.getParsedXSDPath().lastIndexOf("."));
		projectSetting = new FrameworkSettings("LF"+"_"+inputFileName+"_"+ddmmyyyhhmm.format(new Date(System.currentTimeMillis())), request.getInputType(), inputFileName);
		this.autoHelper = new AutomationHelper(projectSetting);
	}

	public void doAll() throws Exception{
		start(Boolean.TRUE, Boolean.TRUE, Boolean.TRUE);
	}
	
	public void createScript() throws Exception{
		start(Boolean.TRUE, Boolean.FALSE, Boolean.FALSE);
	}
	
	public void createTable() throws Exception{
		start(Boolean.TRUE, Boolean.TRUE, Boolean.FALSE);
	}
	
	public void createFrameWork() throws Exception{
		start(Boolean.TRUE, request.getCreateTable(), Boolean.TRUE);
	}

	private void start( Boolean 		  createScripts , 
						Boolean 		  createTable, 
						Boolean 		  createFramework) throws Exception
	{
		File inputMetaDataFile = new File(request.getParsedXSDPath()); 
		
	//	AutomationHelper autoHelper = new AutomationHelper(projectSetting);
		autoHelper.initialization();
		
		//1. Create Maven project with default settings
		autoHelper.createMavenProject();
		
		Multimap<String, DBColumns> tableInfo = null;
		
		//2. Generate JAXB object in new maven project from the input XSD
		if(request.getInputType().equals(Constants.inputType.XML.toString()))
		{
			logger.warn("---------------------------------------------------");
			logger.warn("Generating JAXB Objects in new maven project");		
			JaxbInfoGenerator jaxbGenerator = new JaxbInfoGenerator(projectSetting);
			jaxbGenerator.generateInfos(inputMetaDataFile.getAbsolutePath());
			autoHelper.copyUtilityJars();
			//3. Build Maven project
			autoHelper.buildMavenProject();
			autoHelper.copyJaxbClasses(false);
		}
		
		// 4. Generate tables from input Meta-data File [XSD/CSV]
		tableInfo = tableGenerator(inputMetaDataFile, createScripts);

		// Generate loaders automatically - Main/Schedules
		if(createFramework)
		{
			Boolean isDelimited = false;
			if(request.getInputType().equals(Constants.inputType.DELIMITED.toString()))
			{
				isDelimited = Boolean.TRUE;
			}
			autoHelper.doChoreOperations();
			GeneratorEngine.generateAll(new GenerateRequest(projectSetting, tableInfo, nodes, 
					request.getDatabaseTablePostFix(), isDelimited, inputMetaDataFile, request.getInputType()));
		}
		
		if(createTable)
		{
			logger.warn("Creating Tables in database");
			XapDBRoutine.initializeDBRoutine(request.getDatabaseType(), request.getTnsEntry(), request.getUserName(), request.getPassword(),projectSetting);
			
			if(XapDBRoutine.testAndValidateDBConnection())
			{
				new DBScriptRunner(projectSetting).executeScripts();
				logger.warn("Table created....");
			}
			else
			{
				System.out.println("Db Connectivity Test failed");
			}
		}
		
		// Build Maven project again
		autoHelper.buildMavenProject();
		logger.warn("---------------------------------------------------");
		logger.warn("FRAMEWORK GENERATION COMPLETED");
		logger.warn("PLEASE UPLOAD INPUT FILE");
		logger.warn("---------------------------------------------------");
		logger.warn("---------------------------------------------------");
	}

	/*
	 * Generate tables from XSD or Info       - Tables
	 * Script will be created at /resource Folder
	 */
	public Multimap<String, DBColumns> tableGenerator(File 		xsdFile,
													  Boolean 	createScripts) throws Exception
	{
		TableGenerator generator =  null;
		if(request.getInputType().equals(Constants.inputType.XML.toString()))
		{
			generator =  new JaxbTableGenerator(xsdFile.getName(),projectSetting,autoHelper);
			nodes = AutomationHelper.fetchRootNode(xsdFile);
			rootNode = nodes.getRight();
		} 
		else if(request.getInputType().equals(Constants.inputType.DELIMITED.toString()))
		{
			generator =  new CsvTableGenerator(xsdFile.getName(),request.getDelimiter(),projectSetting,autoHelper);
			rootNode = xsdFile.getName().substring(0,xsdFile.getName().lastIndexOf(".")).toUpperCase();
			nodes = new Pair<String, String>("", rootNode);
		}
		
		// RAW Table
		Multimap<String, DBColumns> tableMap = generator.parse(request.getHaveHeaderData(), request.getDatabaseType());	
		if(createScripts)
		{
			generator.tableScripts(tableMap, request.getDatabaseTablePostFix(), rootNode, request.getDatabaseType());
		}
		generator.insertScripts(tableMap, request.getDatabaseTablePostFix(), rootNode, request.getDatabaseType());
		return tableMap;
	}	
	
	public static boolean validateInputs(String args){
		
		File f = new File(args);
		if(!f.isFile()){
			System.out.println("Provide correct XSD path");
			return false;
		}
		return true;
	}
	
	public FrameworkSettings getProjectSetting() {
		return projectSetting;
	}

	public static void main(String[] args) throws Exception
	{
		XSDParseRequest request = new XSDParseRequest();
		request.setParsedXSDPath(args[0]);
		request.setDatabaseType("JavaDB_DERBY");
		request.setTnsEntry("jdbc:derby:c:\\XAP\\database;create=true");
		request.setUserName("CORE_REF_DATA");
		request.setPassword("CORE_REF_DATA");
		request.setCreateScript(true);
		request.setHaveHeaderData(Boolean.TRUE);
		request.setInputType(Constants.inputType.XML.toString());
		request.setCreateTable(false);
		//new StartAutomation(request).createFrameWork();
		new StartAutomation(request).doAll();
	}

	public XSDParseRequest getRequest() {
		return request;
	}
	

}
