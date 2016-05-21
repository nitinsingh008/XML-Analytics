package com.concept.crew.apis;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.Logger;

import com.concept.crew.dao.XapDBRoutine;
import com.concept.crew.info.DBColumns;
import com.concept.crew.processor.CsvTableGenerator;
import com.concept.crew.processor.DBScriptRunner;
import com.concept.crew.processor.FrameworkGenerator;
import com.concept.crew.processor.JaxbTableGenerator;
import com.concept.crew.processor.TableGenerator;
import com.concept.crew.util.AutomationHelper;
import com.concept.crew.util.Constants;
import com.concept.crew.util.FrameworkSettings;
import com.concept.crew.util.JaxbInfoGenerator;
import com.concept.crew.util.XSDParseRequest;
import com.google.common.collect.Multimap;

public class StartAutomation 
{
	private static Logger 		logger 			= Logger.getLogger(StartAutomation.class);
	private FrameworkSettings projectSetting;
	private XSDParseRequest request;
	private String rootNode = null;
	private static SimpleDateFormat ddmmyyyhhmm = new SimpleDateFormat("ddmmyyyyHHmm");
	
	public StartAutomation(XSDParseRequest request) {
		super();
		this.request = request;
		String inputFileName = request.getParsedXSDPath().substring(request.getParsedXSDPath().lastIndexOf(File.separator)+1, request.getParsedXSDPath().lastIndexOf("."));
		projectSetting = new FrameworkSettings("LF"+"_"+inputFileName+"_"+ddmmyyyhhmm.format(new Date(System.currentTimeMillis())));
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
		Multimap<String, DBColumns> tableInfo = null;
		if(createScripts)
		{
					
			AutomationHelper ah = new AutomationHelper(projectSetting);
			
			ah.installMaven();
			
			//1. Create maven project
			logger.warn("Start creating new Maven Project");
			ah.createMavenProject();
			
			if(request.getInputType().equals(Constants.inputType.XML.toString()))
			{
				//2. Generate Jaxb object in new maven project from the input XSD
				logger.warn("Generating JAXB Objects in new maven project");		
				JaxbInfoGenerator gen = new JaxbInfoGenerator(projectSetting);
				gen.generateInfos(inputMetaDataFile.getAbsolutePath());
				
				//3. Build Maven project
				logger.warn("Build Maven project");
				ah.buildMavenProject();
			}
	
			// 4. Generate tables from XSD
			logger.warn("Generating Table Scripts");
			tableInfo = tableGenerator(inputMetaDataFile);
			
			
			// 6. Last Step = > Build Maven project again
			logger.warn("Build Maven project");
			ah.buildMavenProject();
		}

		// Generate loaders automatically - Main/Schedules
		if(createFramework)
		{
			Boolean isDelimited = false;
			if(request.getInputType().equals(Constants.inputType.DELIMITED.toString())){
				isDelimited = Boolean.TRUE;
			}
			FrameworkGenerator.initialize(projectSetting, tableInfo, rootNode, request.getDatabaseTablePostFix(), isDelimited);
			FrameworkGenerator.generateAll();
		}
		
		if(createTable)
		{
			logger.warn("Creating Tables in schema");
			XapDBRoutine.initializeDBRoutine(request.getDatabaseType(), request.getTnsEntry(), request.getUserName(), request.getPassword());
			
			if(XapDBRoutine.testAndValidateDBConnection())
			{
				new DBScriptRunner(projectSetting).executeScripts();
			}
			else
			{
				System.out.println("Db Connectivity Test failed");
			}
			
			logger.warn("Table created....");
		}
	}

	/*
	 * Generate tables from XSD or Info       - Tables
	 * Script will be created at /resource Folder
	 */
	public Multimap<String, DBColumns> tableGenerator(File xsdFile) throws Exception
	{
		TableGenerator generator =  null;
		if(request.getInputType().equals(Constants.inputType.XML.toString()))
		{
			generator =  new JaxbTableGenerator(xsdFile.getName(),projectSetting);
			rootNode = AutomationHelper.fetchRootNode(xsdFile);
		} 
		else if(request.getInputType().equals(Constants.inputType.DELIMITED.toString()))
		{
			generator =  new CsvTableGenerator(xsdFile.getName(),request.getDelimiter(),projectSetting);
			rootNode = xsdFile.getName().substring(0,xsdFile.getName().lastIndexOf(".")).toUpperCase();
		}
		
		// RAW Table
		Multimap<String, DBColumns> tableMap = generator.parse(request.getHaveHeaderData(), request.getDatabaseType());	
		generator.tableScripts(tableMap, request.getDatabaseTablePostFix(), rootNode, request.getDatabaseType());
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
	
	public static void main(String[] args) throws Exception
	{
		XSDParseRequest request = new XSDParseRequest();
		request.setParsedXSDPath(args[0]);
		request.setDatabaseType("JavaDB_DERBY");
		request.setTnsEntry("jdbc:derby:c:\\XAP\\database;create=true");
		request.setUserName("");
		request.setPassword("");
		request.setCreateScript(true);
		request.setHaveHeaderData(Boolean.TRUE);
		request.setInputType(Constants.inputType.XML.toString());
		request.setCreateTable(false);
		//new StartAutomation(request).createFrameWork();
		new StartAutomation(request).doAll();
	}
	
/*	public static void main(String[] args) throws Exception
	{
		XSDParseRequest request = new XSDParseRequest();
		request.setParsedXSDPath(args[0]);
		request.setDatabaseType("ORACLE");
		request.setTnsEntry("jdbc:oracle:thin:@ (DESCRIPTION = (ADDRESS = (PROTOCOL = TCP)(HOST = lon2odcdvscan01.markit.partners)(PORT = 1521)) (CONNECT_DATA = (SERVER = DEDICATED) (SERVICE_NAME = BRD02DV)))");
		request.setUserName("CORE_REF_DATA");
		request.setPassword("CORE_REF_DATA");
		request.setCreateScript(true);
		request.setHaveHeaderData(Boolean.TRUE);
		request.setInputType(Constants.inputType.XML.toString());
		request.setCreateTable(false);
		//new StartAutomation(request).createFrameWork();
		new StartAutomation(request).doAll();
	}*/
}
