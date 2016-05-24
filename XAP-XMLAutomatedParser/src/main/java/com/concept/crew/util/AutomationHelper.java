package com.concept.crew.util;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.Collections;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

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
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.google.common.io.Files;

public class AutomationHelper 
{
	private static Logger 		logger 			= Logger.getLogger(AutomationHelper.class);

	private FrameworkSettings projectSetting = null; 
	private static Boolean isDependancyAdded = Boolean.FALSE;

	  public AutomationHelper(FrameworkSettings projectSetting) 
	  {
		super();
		this.projectSetting = projectSetting;
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
	/*
	 * Initialization of Project
	 * 1. Create "C:/XAP" if doesn't exists [Base Working Directory]
	 * 2. Install Apache Maven 
	 * 3. Create Local m2 repository
	 */
	public void initialization() throws IOException
	{
		logger.warn("Initializing => " + Constants.mavenProjectPath);
		File workingBaseDirectory = new File(Constants.mavenProjectPath);
		if (!workingBaseDirectory.exists()) 
		{
			boolean dirCreated = workingBaseDirectory.mkdirs();
			if(!dirCreated)
				throw new IOException(workingBaseDirectory.getAbsolutePath() + " can't be created");
		}
		
		logger.warn("Installing apache-maven-3.2.2 .......");
		installMaven();
		logger.warn("Maven installed successfully");
		logger.warn("----------------------------------");
		logger.warn("Creating local m2 repository => " + Constants.m2_repository);
		File m2Repo =  new File(Constants.m2_repository);
		if(!m2Repo.exists())
		{
			m2Repo.mkdirs();
		}
	} 
	/*
	 * Initialization method should be called before calling this method 
	 */
	public void createMavenProject() 
										throws IOException,MavenInvocationException 
	{
		logger.warn("----------------------------------------");
		logger.warn("Generating Maven project in Batch mode");
		logger.warn("----------------------------------------");
		File workingBaseDirectory = new File(Constants.mavenProjectPath);

		// no need to delete any existing project now
		//deleteExistingProject(new File(Constants.mavenProjectPath+ "/" +Constants.mavenProjectName));
		
		InvocationRequest request = new DefaultInvocationRequest();
		request.setGoals(Collections.singletonList("archetype:generate"));
		request.setInteractive(false);
		Properties properties = new Properties();
		properties.setProperty("groupId", "com.concept.crew.app");
		properties.setProperty("artifactId", projectSetting.getProjectName());
		properties.setProperty("archetypeArtifactId", "maven-archetype-quickstart");
		properties.setProperty("version", "1.0");
		request.setProperties(properties);
	
		Invoker invoker = new DefaultInvoker();
		invoker.setWorkingDirectory(workingBaseDirectory);
	
		File m2Repo =  new File(Constants.m2_repository); 	// Setting repo directory location 
		invoker.setLocalRepositoryDirectory(m2Repo);
		
		setMavenHome(invoker); // Setting Maven Home(if wrongly set)
		
		InvocationResult result = invoker.execute(request);
		
		if (result.getExitCode() != 0) 
		{
			logger.warn("Maven Project Creation failure" + result.getExitCode());
			//throw new IllegalStateException("archetype:generate failed.");
			return;
		}
		logger.warn("Project generated successfully : " + projectSetting.getProjectName());
		
		File resourcesDir = new File(projectSetting.getResourcePath());
		if(!resourcesDir.exists()){
			resourcesDir.mkdirs();
		}
		//Files.copy(srcDir.toPath(), resourcesDir.toPath(), StandardCopyOption.ATOMIC_MOVE);
	}
	
	public void buildMavenProject() throws MavenInvocationException
	{
		InvocationRequest request = new DefaultInvocationRequest();
		
		if(!isDependancyAdded){
			PomDependencyUpdater.addDependancyForUtilities(projectSetting.getPomPath(),"ConceptCrewUtil", "ConceptCrewUtil", "1.0","ConceptCrewUtil-1.0.jar");
			PomDependencyUpdater.addDependancyForUtilities(projectSetting.getPomPath(),"ojdbc6", "ojdbc6", "1.0","ojdbc6.jar");
			isDependancyAdded = Boolean.TRUE;
		}
		request.setPomFile(new File(projectSetting.getPomPath()));
		request.setGoals( Arrays.asList( "install", "-DskipTests=true" ) );
		Invoker invoker = new DefaultInvoker();
		setMavenHome(invoker); // Setting Maven Home(if wrongly set)
		logger.warn("-----------------------------");
		logger.warn("Building Maven project");
		logger.warn("-----------------------------");		
		InvocationResult result = invoker.execute( request );

		if (result.getExitCode() != 0) 
		{
			logger.warn("Maven Build failure" + result.getExitCode());
			//throw new IllegalStateException("maven build failed.");
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
/*		String roodNode = null;
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
		}*/
		
		String mainElement = "";
		final Thread currentThread = Thread.currentThread();
		final ClassLoader contextClassLoader = currentThread.getContextClassLoader();
		final InputStream inputStream = contextClassLoader.getResourceAsStream(xsdFile.getName());
		
		//parse the document
		DocumentBuilderFactory  docBuilderFactory 	= DocumentBuilderFactory.newInstance();
		DocumentBuilder 		docBuilder 			= docBuilderFactory.newDocumentBuilder();
		Document 				doc 				= docBuilder.parse(inputStream);

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
	    								 return mainElement;
	    							 }
	    						 }
	    					 }
	    				 }
	    			}
	    		}
	    	}
	    }
		
		return mainElement;
	}
	
	public <T> Class<T> getRootClass(File xsdFile) throws ParserConfigurationException, SAXException, IOException, ClassNotFoundException{
		String rootElement = fetchRootNode(xsdFile);
		String qualifiedName = Constants.packageName  +"."+rootElement;		
		File file = new File(projectSetting.getPathToRootClass());
		URL urlList[] = {file.toURI().toURL()};
		URLClassLoader loader = new URLClassLoader(urlList);
		Class<T> rootClass = (Class<T>) loader.loadClass(qualifiedName);
		
		return rootClass;		
	}
	
	public static void setMavenHome(Invoker invoker)
	{
/*		String mavenHomePath = "";
        if (System.getProperty("maven.home") != null && System.getProperty("maven.home").contains("EMBEDDED"))
        {
        	// i.e. running from Eclipse, need to set correct Home
        	mavenHomePath = System.getenv().get("M2_HOME");
        	invoker.setMavenHome(new File(mavenHomePath));
        }*/

        File file = new File(Constants.mavenHome);
        invoker.setMavenHome(file);
	}
	
	public static void main(String args[]) throws Exception
	{
		String mainElement = "";
		final Thread currentThread = Thread.currentThread();
		final ClassLoader contextClassLoader = currentThread.getContextClassLoader();
		final InputStream inputStream = contextClassLoader.getResourceAsStream("SampleBondsXSD.xsd");
		
		//parse the document
		DocumentBuilderFactory  docBuilderFactory 	= DocumentBuilderFactory.newInstance();
		DocumentBuilder 		docBuilder 			= docBuilderFactory.newDocumentBuilder();
		Document 				doc 				= docBuilder.parse(inputStream);

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
	    								 break;
	    							 }
	    						 }
	    					 }
	    				 }
	    			}
	    		}
	    	}
	    }
	}
	

	
	private void installMaven() throws IOException
	{
		String zipFilePath = Constants.mavenZip;
		String destDirectory = Constants.mavenProjectPath;
		
		ClassLoader classLoader = getClass().getClassLoader();
		//File file = new File(classLoader.getResource(zipFilePath).getFile());		
		File file = new File("D:\\XAP Workspace\\concept-XML-analysis\\target\\classes\\apache-maven-3.2.2.zip");
        File destDir = new File(destDirectory);
        if (!destDir.exists()) {
            destDir.mkdir();
        }
        ZipInputStream zipIn = new ZipInputStream(new FileInputStream(file));
        ZipEntry entry = zipIn.getNextEntry();
        // iterates over entries in the zip file
        while (entry != null) 
        {
            String filePath = destDirectory + File.separator + entry.getName();
            if (!entry.isDirectory()) 
            {
                // if the entry is a file, extracts it
                extractFile(zipIn, filePath);
            } 
            else 
            {
                // if the entry is a directory, make the directory
                File dir = new File(filePath);
                dir.mkdir();
            }
            zipIn.closeEntry();
            entry = zipIn.getNextEntry();
        }
        zipIn.close();

	}
	
    /**
     * Extracts a zip entry (file entry)
     * @param zipIn
     * @param filePath
     * @throws IOException
     */
    private void extractFile(ZipInputStream zipIn, String filePath) throws IOException {
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(filePath));
        byte[] bytesIn = new byte[4096];
        int read = 0;
        while ((read = zipIn.read(bytesIn)) != -1) {
            bos.write(bytesIn, 0, read);
        }
        bos.close();
    }	
    
    public void copyUtilityJars(){
    	File targetDir = new File(projectSetting.getPathToUtilityJar());
    	if(!targetDir.exists()){
    		targetDir.mkdirs();
    	}
    	
    	File sourceDir = new File("./src/lib");
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
    }
}
