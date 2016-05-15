package com.concept.crew.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.Collections;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.apache.maven.shared.invoker.DefaultInvocationRequest;
import org.apache.maven.shared.invoker.DefaultInvoker;
import org.apache.maven.shared.invoker.InvocationRequest;
import org.apache.maven.shared.invoker.InvocationResult;
import org.apache.maven.shared.invoker.Invoker;
import org.apache.maven.shared.invoker.MavenInvocationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class AutomationHelper 
{
	private static Logger 		logger 			= Logger.getLogger(AutomationHelper.class);
	
	  private static void runProcess(String command) throws Exception {
	    Process pro = Runtime.getRuntime().exec(command, null, new File(Constants.compilationPath));	    
	    pro.waitFor();
	    System.out.println("compilation completed " + pro.exitValue());
	  }

	 public static void  compileJaxbInfos(){
	    try {
	      runProcess("javac *.java");
	    } catch (Exception e) {
	      e.printStackTrace();
	    }
	  }
	 
	 
	 public static void copyFiles(){
		 File srcDir = new File("D:\\XAP Workspace\\XAP-XMLAutomatedParser\\src\\main\\java\\com\\concept\\crew\\info\\jaxb");
		 
		 File targetDir = new File("D:\\XAP Workspace\\XAP-XMLAutomatedParser\\target\\classes\\com\\concept\\crew\\info\\jaxb");
		 
		 if(!targetDir.exists()){
			 System.out.println("creating directory folder to copy .class files");
			 targetDir.mkdirs();
		 }
		 
		 try {
			Thread.sleep(2000);
		 } catch (InterruptedException e) {
			e.printStackTrace();
		 }
		 
		 if(srcDir.isDirectory()){
			 System.out.println("Start copying class files");
			 File[] content = srcDir.listFiles();
			 for(int i = 0; i < content.length; i++) {
				 if(content[i].getName().contains(".class")){
					 System.out.println("copying file : " + content[i].getName());
					 content[i].renameTo(new File(targetDir, content[i].getName())) ;
				 }				  
			 }			 
		 }			 
	 }
	 
	public static void createMavenProject(File xsdFile) 
										throws IOException,MavenInvocationException 
	{
		logger.warn("Initializing at " + Constants.mavenProjectPath);
		File srcDir = new File(Constants.mavenProjectPath);
		if (!srcDir.exists()) 
		{
			boolean dirCreated = srcDir.mkdirs();
			if(!dirCreated)
				throw new IOException(srcDir.getAbsolutePath() + " can't be created");
		}

		deleteExistingProject(new File(Constants.mavenProjectPath+ "/" +Constants.mavenProjectName));
		
		InvocationRequest request = new DefaultInvocationRequest();
		request.setGoals(Collections.singletonList("archetype:generate"));
		request.setInteractive(false);
		//request.setGlobalSettingsFile(new File(Constants.settingsFilePath));
		Properties properties = new Properties();
		properties.setProperty("groupId", "com.concept.crew.app");
		properties.setProperty("artifactId", "LoadersFramework");
		properties.setProperty("archetypeArtifactId", "maven-archetype-quickstart");
		properties.setProperty("version", "1.0");
		request.setProperties(properties);
		Invoker invoker = new DefaultInvoker();
		invoker.setWorkingDirectory(srcDir);
		// Setting repo directory location 
		invoker.setLocalRepositoryDirectory(new File(Constants.m2_repository));
		logger.warn("----------------------------------");
		logger.warn("Generating project in Batch mode");
		logger.warn("----------------------------------");
		InvocationResult result = invoker.execute(request);
		
		if (result.getExitCode() != 0) {
			throw new IllegalStateException("archetype:generate failed.");
		}
		logger.warn("Maven project created successfully : " + Constants.mavenProjectName);
		
		File resourcesDir = new File(Constants.resourcePath);
		if(!resourcesDir.exists()){
			resourcesDir.mkdirs();
		}
		//Files.copy(srcDir.toPath(), resourcesDir.toPath(), StandardCopyOption.ATOMIC_MOVE);
	}
	
	public static void buildMavenProject() throws MavenInvocationException
	{
		InvocationRequest request = new DefaultInvocationRequest();
		request.setPomFile(new File(Constants.pomPath));
		//request.setGoals( Collections.singletonList( "install" ) );
		request.setGoals( Arrays.asList( "install", "-DskipTests=true" ) );
		Invoker invoker = new DefaultInvoker();
		InvocationResult result = invoker.execute( request );
		logger.warn("-----------------------------");
		logger.warn("Building project");
		logger.warn("-----------------------------");
		if (result.getExitCode() != 0) {
			throw new IllegalStateException("maven build failed.");
		}
	}
	
	private static void deleteExistingProject(File files) throws IOException {
		if (files.isDirectory()) {
			for (File file : files.listFiles())
				deleteExistingProject(file);
		}
		if (!files.delete())
			System.out.println("Maven project not present...creating new");
	}
	
	
	public static String fetchRootNode(File xsdFile) throws ParserConfigurationException, SAXException, IOException
	{
		String roodNode = null;
		final Thread currentThread = Thread.currentThread();
		final ClassLoader contextClassLoader = currentThread.getContextClassLoader();

		final InputStream inputStream = contextClassLoader.getResourceAsStream(xsdFile.getName());
		
		//parse the document
		DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder;
		docBuilder 		= docBuilderFactory.newDocumentBuilder();
		Document doc 	= docBuilder.parse(inputStream);
		NodeList list 	= doc.getElementsByTagName("xs:element"); 
		
		if(list != null && list.item(0) != null)
		{
			Element element = (Element)list.item(0) ;
			roodNode =  element.getAttribute("name");
		}
		if(roodNode == null)
		{
			// Try searching by xsd
			list 	= doc.getElementsByTagName("xsd:element"); 
			if(list != null && list.item(0) != null)
			{
				Element element = (Element)list.item(0) ;
				roodNode =  element.getAttribute("name");
			}
		}
		
		
		return roodNode;
	}
	
	public static <T> Class<T> getRootClass(File xsdFile) throws ParserConfigurationException, SAXException, IOException, ClassNotFoundException{
		String rootElement = fetchRootNode(xsdFile);
		String qualifiedName = Constants.packageName  +"."+rootElement;		
		File file = new File(Constants.pathToRootClass);
		URL urlList[] = {file.toURI().toURL()};
		URLClassLoader loader = new URLClassLoader(urlList);
		Class<T> rootClass = (Class<T>) loader.loadClass(qualifiedName);
		
		return rootClass;		
	}
}
