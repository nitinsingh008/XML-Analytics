package com.concept.crew.util;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.log4j.Logger;
import org.apache.maven.shared.invoker.DefaultInvocationRequest;
import org.apache.maven.shared.invoker.DefaultInvoker;
import org.apache.maven.shared.invoker.InvocationRequest;
import org.apache.maven.shared.invoker.InvocationResult;
import org.apache.maven.shared.invoker.Invoker;
import org.apache.maven.shared.invoker.MavenInvocationException;
import org.codehaus.plexus.util.FileUtils;

public class MavenHelper {
	
	private static Logger 		logger 			= Logger.getLogger(MavenHelper.class);
	private FrameworkSettings projectSetting = null;
	
	public MavenHelper(FrameworkSettings projectSetting) {
		super();
		this.projectSetting = projectSetting;
	} 
	
	/*
	 * Initialization of Project
	 * 1. Create "C:/XAP" if doesn't exists [Base Working Directory]
	 * 2. Install Apache Maven 
	 * 3. Create Local m2 repository
	 * 4. Install xapDB [JAVADB - DERBY Database ]
	 */
	public void initialization() throws IOException
	{
		logger.warn("------------- XAP Started ---------------------");
		logger.warn("Initializing at Folder => " + Constants.mavenProjectPath);
		File workingBaseDirectory = new File(Constants.mavenProjectPath);
		if (!workingBaseDirectory.exists()) 
		{
			boolean dirCreated = workingBaseDirectory.mkdirs();
			if(!dirCreated)
				throw new IOException(workingBaseDirectory.getAbsolutePath() + " can't be created");
		}
		
		logger.warn("Installing apache-maven-3.2.2 .......");
		//installMaven();
		// Unzipping Maven 
		installZippedDirectory(Constants.mavenZip, Constants.mavenProjectPath);
		logger.warn("Maven Installed successfully");
		logger.warn("---------------------------------------------------");
		logger.warn("Creating local m2 repository => " + Constants.m2_repository);
		File m2Repo =  new File(Constants.m2_repository);
		if(!m2Repo.exists())
		{
			m2Repo.mkdirs();
		}
		
		// Unzipping Derby utilities 
		installZippedDirectory(Constants.debryDb, Constants.databaseLoc);
		
		ClassLoader classLoader = getClass().getClassLoader();
		File sourceFile = new File(classLoader.getResource(Constants.dbBatchFile).getFile());	
		try 
		{
			File destDir = new File(Constants.databaseLoc);
			FileUtils.copyFileToDirectory(sourceFile, destDir);
		} catch (IOException e) {
			e.printStackTrace();
		}
	} 
	/*
	 * Initialization method should be called before calling this method 
	 */
	public void createMavenProject() 
										throws IOException,MavenInvocationException 
	{
		logger.warn("---------------------------------------------------");
		logger.warn("Generating Maven project in Batch mode");
		logger.warn("---------------------------------------------------");
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
		logger.warn("Project Generated Successfully");
		logger.warn(projectSetting.getProjectName());
		File resourcesDir = new File(projectSetting.getResourcePath());
		if(!resourcesDir.exists()){
			resourcesDir.mkdirs();
		}
		// updating dependency in pom file
		addMavenDependancies(); // for XML
	}
	
	public void buildMavenProject() throws MavenInvocationException
	{
		InvocationRequest request = new DefaultInvocationRequest();
		request.setPomFile(new File(projectSetting.getPomPath()));
		request.setGoals( Arrays.asList( "install", "-DskipTests=true" ) );
		Invoker invoker = new DefaultInvoker();
		setMavenHome(invoker); // Setting Maven Home(if wrongly set)
		logger.warn("---------------------------------------------------");
		logger.warn("Building Maven project");
		logger.warn("---------------------------------------------------");		
		InvocationResult result = invoker.execute( request );

		if (result.getExitCode() != 0) 
		{
			logger.warn("Maven Build failure" + result.getExitCode());
			//throw new IllegalStateException("maven build failed.");
		}
		else
		{
			logger.warn("BUILD SUCCESS");
			logger.warn("---------------------------------------------------");
		}
	}
	
	private void addMavenDependancies()
	{
		logger.warn("Adding dependancies in pom.xml");
		PomDependencyUpdater.addDependancyForUtilities(projectSetting.getPomPath(),"ConceptCrewUtil", "ConceptCrewUtil", "1.0","ConceptCrewUtil-1.0.jar");
		PomDependencyUpdater.addDependancyForUtilities(projectSetting.getPomPath(),"ojdbc6", "ojdbc6", "1.0","ojdbc6.jar");
		PomDependencyUpdater.addDependancyForUtilities(projectSetting.getPomPath(), "log4j", "log4j", "1.2.15","log4j-1.2.15.jar");
		PomDependencyUpdater.addDependancyForUtilities(projectSetting.getPomPath(), "commons-io", "commons-io", "2.4", "commons-io-2.4.jar");
		PomDependencyUpdater.addDependancyForUtilities(projectSetting.getPomPath(), "commons-dbcp", "commons-dbcp", "1.3","commons-dbcp-1.3.jar");
		// adding new dependency for apache unilocity
		PomDependencyUpdater.addDependancyForUtilities(projectSetting.getPomPath(),"univocity-parsers", "com.univocity", "2.0.2", "univocity-parsers-2.0.2.jar");
	}
	
	/*
	 * 
	 */
	private void installZippedDirectory(String zipFilePath, 
									    String destDirectory) throws IOException
	{
		ClassLoader classLoader = getClass().getClassLoader();
		File file = new File(classLoader.getResource(zipFilePath).getFile());		
		//File file = new File("D:\\XAP Workspace\\concept-XML-analysis\\target\\classes\\" + File.separator + zipFilePath);
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
	
	public static void setMavenHome(Invoker invoker)
	{
        File file = new File(Constants.mavenHome);
        invoker.setMavenHome(file);
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
	
}
