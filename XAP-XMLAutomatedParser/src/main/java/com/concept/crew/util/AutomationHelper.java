package com.concept.crew.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.google.common.io.Files;

public class AutomationHelper extends MavenHelper
{
	private static Logger 		logger 			= Logger.getLogger(AutomationHelper.class);

	private FrameworkSettings projectSetting = null; 
	

	  public AutomationHelper(FrameworkSettings projectSetting) 
	  {
		super(projectSetting);
		this.projectSetting = projectSetting;
	}
	
	private static void deleteExistingProject(File files) throws IOException {
		if (files.isDirectory()) {
			for (File file : files.listFiles())
				deleteExistingProject(file);
		}
		if (!files.delete())
			System.out.println("Maven project not present...creating new");
	}
	
	
	public static Pair<String, String> fetchRootNode(File xsdFile) throws ParserConfigurationException, SAXException, IOException
	{
		String mainElement = "";
		String startingElement = "";
		final Thread currentThread = Thread.currentThread();
		final ClassLoader contextClassLoader = currentThread.getContextClassLoader();
		final InputStream inputStream = contextClassLoader.getResourceAsStream(xsdFile.getName());
		
		//parse the document
		DocumentBuilderFactory  docBuilderFactory 	= DocumentBuilderFactory.newInstance();
		DocumentBuilder 		docBuilder 			= docBuilderFactory.newDocumentBuilder();
		Document 				doc 				= docBuilder.parse(inputStream);
		doc.getDocumentElement().normalize();
		Element rootElement = doc.getDocumentElement();  // Will contain either xsd:schema or xsd:element
		
		NodeList children = rootElement.getChildNodes(); // List all Child
		
		// Iterate to find 1st Complex Element
	    for (int i = 0; i < children.getLength(); i++) 
	    {
	    	Node current = children.item(i);
	    	if (current.getNodeType() == Node.ELEMENT_NODE) 
	    	{
	    		Element element = (Element) current;
	    		if (element.getTagName().contains("complexType")) 
	    		{
	    			System.out.println("Tag Name is : " + element.getTagName());
	    			startingElement = element.getAttribute("name");
	    			// Find 1st Child of COMPLEX element
	    			NodeList complexChilds = element.getChildNodes(); // List all Child
	    			for (int j = 0; j < complexChilds.getLength(); j++) 
	    			{
	    				 Node childCurrent = complexChilds.item(j);
	    				 if (childCurrent.getNodeType() == Node.ELEMENT_NODE) 
	    				 {
	    					 Element ele = (Element) childCurrent;
	    					 
	    					 if (ele.getTagName().contains("sequence"))
	    					 {
	    						 // Find 1st Child
	    						 NodeList seqChilds = ele.getChildNodes();
	    						 for (int k = 0; k < seqChilds.getLength(); k++) 
	    						 {
	    							 Node childCurr = seqChilds.item(k);
	    							 if (childCurr.getNodeType() == Node.ELEMENT_NODE) 
	    							 {
	    								 Element targetElement = (Element) childCurr;
	    								 
	    								 System.out.println("Tag Name is : " + targetElement.getAttribute("name"));
	    								 mainElement = targetElement.getAttribute("name");
	    								 return new Pair<String,String>(startingElement,mainElement);
	    							 }
	    						 }
	    					 }
	    				 }
	    			}
	    		}
	    	}
	    }
		
		return new Pair<String,String>(startingElement,mainElement);
	}
	
	 public void copyUtilityJars()
	 {
		 logger.warn("Copy Utility jars to new Maven Project");
	    	File targetDir = new File(projectSetting.getPathToUtilityJar());
	    	if(!targetDir.exists()){
	    		targetDir.mkdirs();
	    	}
	    	
	    	File sourceDir = new File("./src/lib");
	    	if(!sourceDir.isDirectory()){
	    		URL resource = AutomationHelper.class.getClassLoader().getResource("/");
	    		String rootPath = resource.getPath().substring(0, resource.getPath().indexOf("XAP")+3);
	    		sourceDir = new File(rootPath+"/DEPENDENCY-JARS/lib");
	    	}
	    	logger.warn("utitlity jar path"+sourceDir.getAbsolutePath());
	    	 if(sourceDir.isDirectory()){
				 File[] content = sourceDir.listFiles();
				 for(int i = 0; i < content.length; i++) {
					 if(content[i].getName().contains(".jar")){
						targetDir = new File(projectSetting.getPathToUtilityJar()+ File.separator+ content[i].getName());
						try {
							Files.copy(content[i].getAbsoluteFile(), targetDir.getAbsoluteFile());
						} catch (IOException e) {
							e.printStackTrace();
						}
					 }				  
				 }			 
			 }	
	    }
	    
	 public void copyJaxbClasses(Boolean isDelimited){
		 String workFolderLoc = null;
		 String src = null;
		 
		 if(isDelimited){
			  workFolderLoc = Constants.pojoWorkFolder; 
			  src = projectSetting.getPathToRootClass()+ File.separator +"/com/concept/crew/info/pojo";
		 }else{
			 workFolderLoc = Constants.jaxbWorkFolder; 
			 src = projectSetting.getPathToRootClass()+ File.separator +"/com/concept/crew/info/jaxb";
		 }
		 
		 File targetDir = new File(workFolderLoc);
		 if(!targetDir.exists()){
	    		targetDir.mkdirs();
	     }
		 
		 File sourceDir = new File(src);
		 if(sourceDir.isDirectory()){
			 File[] content = sourceDir.listFiles();
			 for(int i = 0; i < content.length; i++) {
				 if(content[i].getName().contains(".class")){
					targetDir = new File(workFolderLoc+ File.separator+ content[i].getName());
					try {
						Files.copy(content[i].getAbsoluteFile(), targetDir.getAbsoluteFile());
					} catch (IOException e) {
						e.printStackTrace();
					}
				 }				  
			 }			 
		 }	
	 }
	 
	 public void doChoreOperations(){
	    	File sourceDir = new File("./src/main/java/com/concept/crew/dao/loaderUtil");
	    	File targetDir = new File(projectSetting.getPathToLoaderType());
	    	
	    	if(!targetDir.exists()){
	    		targetDir.mkdirs();
	    	}
	    	
	    	 if(sourceDir.isDirectory()){
				 File[] content = sourceDir.listFiles();
				 for(int i = 0; i < content.length; i++) {
					 if(content[i].getName().contains(".java")){
						targetDir = new File(projectSetting.getPathToLoaderType()+ File.separator+ content[i].getName());
						try {
							Files.copy(content[i].getAbsoluteFile(), targetDir.getAbsoluteFile());
						} catch (IOException e) {
							e.printStackTrace();
						}
					 }				  
				 }			 
			 }	
	    	 
	    	//copy log4j xml	    	 
			targetDir = new File(projectSetting.getResourcePath());
			if (!targetDir.exists()) {
				targetDir.mkdirs();
			}
	
			if (sourceDir.isDirectory()) {
				File[] content = sourceDir.listFiles();
				for (int i = 0; i < content.length; i++) {
					if (content[i].getName().contains(".xml")) {
						targetDir = new File(projectSetting.getResourcePath() + File.separator + content[i].getName());
						try {
							Files.copy(content[i].getAbsoluteFile(), targetDir.getAbsoluteFile());
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			}
	    	 //copy xsd if XSD request
			if (Constants.inputType.XML.toString().equals(
					projectSetting.getRequestType())) {
				sourceDir = new File(Constants.xsdLocalPath);
	
				if (sourceDir.isDirectory()) {
					File[] content = sourceDir.listFiles();
					for (int i = 0; i < content.length; i++) {
						if (content[i].getName().contains(projectSetting.getInputFileName())) {
							targetDir = new File(projectSetting.getResourcePath() + File.separator + content[i].getName());
							try {
								Files.copy(content[i].getAbsoluteFile(), targetDir.getAbsoluteFile());
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					}
				}
			}
	}
	
	public static void main(String args[]) throws Exception
	{
		String startingTag = null;
		String mainElement = "";
		final Thread currentThread = Thread.currentThread();
		final ClassLoader contextClassLoader = currentThread.getContextClassLoader();
		final InputStream inputStream = contextClassLoader.getResourceAsStream("SampleBondsXSD.xsd");
		
		//parse the document
		DocumentBuilderFactory  docBuilderFactory 	= DocumentBuilderFactory.newInstance();
		DocumentBuilder 		docBuilder 			= docBuilderFactory.newDocumentBuilder();
		Document 				doc 				= docBuilder.parse(inputStream);
		doc.getDocumentElement().normalize();
		Element rootElement = doc.getDocumentElement();  // Will contain either xsd:schema or xsd:element
		
		NodeList children = rootElement.getChildNodes(); // List all Child
		
		// Iterate to find 1st Complex Element
	    for (int i = 0; i < children.getLength(); i++) 
	    {
	    	Node current = children.item(i);
	    	if (current.getNodeType() == Node.ELEMENT_NODE) 
	    	{
	    		Element element = (Element) current;
	    		if (element.getTagName().contains("complexType")) 
	    		{
	    			System.out.println("Tag Name is : " + element.getTagName());
	    			
	    			startingTag = element.getAttribute("name");
	    			System.out.println("Starting Element :  " + startingTag);
	    			// Find 1st Child of COMPLEX element	    			
	    			NodeList complexChilds = element.getChildNodes(); // List all Child
	    			for (int j = 0; j < complexChilds.getLength(); j++) 
	    			{
	    				 Node childCurrent = complexChilds.item(j);
	    				 if (childCurrent.getNodeType() == Node.ELEMENT_NODE) 
	    				 {
	    					 Element ele = (Element) childCurrent;
	    					 
	    					 if (ele.getTagName().contains("sequence"))
	    					 {
	    						 // Find 1st Child
	    						 NodeList seqChilds = ele.getChildNodes();
	    						 for (int k = 0; k < seqChilds.getLength(); k++) 
	    						 {
	    							 Node childCurr = seqChilds.item(k);
	    							 if (childCurr.getNodeType() == Node.ELEMENT_NODE) 
	    							 {
	    								 Element targetElement = (Element) childCurr;
	    								 
	    								 System.out.println("Tag Name is : " + targetElement.getAttribute("name"));
	    								 mainElement = targetElement.getAttribute("name");
	    								return;
	    							 }
	    						 }
	    					 }
	    				 }
	    			}
	    		}
	    	}
	    }
	    
	    Pair<String,String> pair = new Pair<String, String>(startingTag, mainElement);
	    System.out.println("Starting Element : " + pair.getLeft());
	    System.out.println("Root Element : " + pair.getRight());
	}
	
   
}
