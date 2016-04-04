package com.concept.crew.mock;

import java.io.File;

public class Test {

	  private static void runProcess(String command) throws Exception {
	    Process pro = Runtime.getRuntime().exec(command);
	    Runtime.getRuntime().exec(command, null, new File("D:\\XAP Workspace\\XAP-XMLAutomatedParser\\src\\main\\java\\com\\concept\\crew\\info\\jaxb"));
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
}
