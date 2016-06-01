package com.concept.crew.apis;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import org.apache.log4j.Logger;
import com.concept.crew.info.DBDetails;
import com.concept.crew.util.Constants;
import com.concept.crew.util.FrameworkSettings;

public class ParseIncomingRequest {
	private static final Logger log = Logger.getLogger("ParseIncomingRequest.class");
	private File fileToBeParsed;
	private FrameworkSettings projectSetting;
	
	public ParseIncomingRequest(File fileToBeParsed , FrameworkSettings projectSetting) {
		this.fileToBeParsed = fileToBeParsed;
		this.projectSetting = projectSetting;
	}
	
	public void process(){
		log.warn("Starting");
		Class cls = loadMainClass();
		if(cls == null){
			System.out.println("Processor class not found");
			return;
		}
		try {
			Object instance = cls.newInstance();
			
			Method myMethod = cls.getMethod("execute",  new Class[] { File.class, String.class ,String.class, String.class, String.class });
			DBDetails dbDetails =  projectSetting.getDbDetails();
		    myMethod.invoke(instance, new Object[] { fileToBeParsed, dbDetails.getDbType(), dbDetails.getJdbcUrl(), 
		    					dbDetails.getUser(), dbDetails.getPassword() });
			
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		
	}
	
	private Class loadMainClass(){
		JarFile jarFile;
		String processor;
		Class cls = null;
		try {
			jarFile = new JarFile(projectSetting.getTargetPath()+ "\\" + projectSetting.getJarFileName());
			Enumeration e = jarFile.entries();
			URL[] urls = { new URL("jar:file:" + projectSetting.getTargetPath()+ "\\" + projectSetting.getJarFileName()+"!/") };
			URLClassLoader loader = URLClassLoader.newInstance(urls);
			
			while (e.hasMoreElements()) 
		    {
		        JarEntry je = (JarEntry) e.nextElement();
		        if(je.isDirectory() || !je.getName().endsWith(".class"))
		        {
		            continue;
		        }
			    // -6 because of .class
			    String className = je.getName().substring(0,je.getName().length()-6);
			    className = className.replace('/', '.');
			    
			    if(className.contains(Constants.packageToProcess))
			    {
			    	if(Constants.inputType.DELIMITED.toString().equals(projectSetting.getRequestType())){
			    		processor = Constants.packageToProcess + "." + "DelimitedFileProcessor";
			    	}else{
			    		processor = Constants.packageToProcess + "." + "XMLFileProcessor";
			    	}
			    	 cls = loader.loadClass(processor);					    
			    }
		    }
			jarFile.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		} catch (ClassNotFoundException e1) {
			e1.printStackTrace();
		}
		return cls;
	}

}
