package com.concept.crew.util;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Properties;

import org.apache.maven.shared.invoker.DefaultInvocationRequest;
import org.apache.maven.shared.invoker.DefaultInvoker;
import org.apache.maven.shared.invoker.InvocationRequest;
import org.apache.maven.shared.invoker.InvocationResult;
import org.apache.maven.shared.invoker.Invoker;
import org.apache.maven.shared.invoker.MavenInvocationException;

public class AutomationHelper {

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
	 
	public static void createMavenProject(File xsdFile) throws IOException,
			MavenInvocationException {
		File srcDir = new File(Constants.mavenProjectPath);
		if (!srcDir.exists()) 
		{
			boolean dirCreated = srcDir.mkdirs();
			if(!dirCreated)
				throw new IOException(srcDir.getAbsolutePath() + " can't be created");
		}

		InvocationRequest request = new DefaultInvocationRequest();
		request.setGoals(Collections.singletonList("archetype:generate"));
		request.setInteractive(false);
		Properties properties = new Properties();
		properties.setProperty("groupId", "com.concept.crew.app");
		properties.setProperty("artifactId", "LoadersFramework");
		properties.setProperty("archetypeArtifactId", "maven-archetype-quickstart");
		properties.setProperty("version", "1.0");
		request.setProperties(properties);
		Invoker invoker = new DefaultInvoker();
		invoker.setWorkingDirectory(srcDir);
		InvocationResult result = invoker.execute(request);

		if (result.getExitCode() != 0) {
			throw new IllegalStateException("archetype:generate failed.");
		}
		
		File resourcesDir = new File(Constants.resourcePath);
		if(!resourcesDir.exists()){
			resourcesDir.mkdirs();
		}
		//Files.copy(srcDir.toPath(), resourcesDir.toPath(), StandardCopyOption.ATOMIC_MOVE);
	}
	
	public static void buildMavenProject() throws MavenInvocationException{
		InvocationRequest request = new DefaultInvocationRequest();
		request.setPomFile(new File(Constants.pomPath));
		//request.setGoals( Collections.singletonList( "install" ) );
		request.setGoals( Arrays.asList( "install", "-DskipTests=true" ) );
		Invoker invoker = new DefaultInvoker();
		InvocationResult result = invoker.execute( request );
		if (result.getExitCode() != 0) {
			throw new IllegalStateException("maven build failed.");
		}
	}
}
