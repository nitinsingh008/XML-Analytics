package com.concept.crew.apis;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.apache.log4j.Logger;

import com.concept.crew.info.DBDetails;
import com.concept.crew.util.Constants;
import com.concept.crew.util.FrameworkSettings;
import com.concept.crew.util.XSDParseRequest;

public class ParseIncomingRequest {
	private static final Logger log = Logger.getLogger("ParseIncomingRequest.class");
	private File fileToBeParsed;
	private FrameworkSettings projectSetting;
	private XSDParseRequest request;
	
	public ParseIncomingRequest(File fileToBeParsed , FrameworkSettings projectSetting,XSDParseRequest request) {
		this.fileToBeParsed = fileToBeParsed;
		this.projectSetting = projectSetting;
		this.request = request;
	}
	
	public void process(){
		log.warn("Starting");
		Map<String,Class> cls = loadMainClass();
		Class mainClass = cls.get(Constants.mainProcessor);
		if( mainClass == null){
			System.out.println("Processor class not found");
			return;
		}
		try {
			Object instance = mainClass.newInstance();
			DBDetails dbDetails =  projectSetting.getDbDetails();
			if(projectSetting.getRequestType().equals(Constants.inputType.XML)){
				Method myMethod = mainClass.getMethod("execute",  new Class[] { File.class, String.class ,String.class, String.class, String.class });
			    myMethod.invoke(instance, new Object[] { fileToBeParsed, dbDetails.getDbType(), dbDetails.getJdbcUrl(), 
			    					dbDetails.getUser(), dbDetails.getPassword() });
			}else if(projectSetting.getRequestType().equals(Constants.inputType.DELIMITED)){
				Class pojoCls = cls.get(Constants.delimiterPojoClass);
				Method myMethod = mainClass.getMethod("execute",  new Class[] { File.class, String.class ,String.class, String.class, String.class,
						Boolean.class,String.class,Class.class });
			    myMethod.invoke(instance, new Object[] { fileToBeParsed, dbDetails.getDbType(), dbDetails.getJdbcUrl(), 
			    					dbDetails.getUser(), dbDetails.getPassword(),request.getHaveHeaderData(),request.getDelimiter(),pojoCls});
			}
			
			
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
	
	private Map<String,Class> loadMainClass(){
		JarFile jarFile;
		String processor;
		Map<String,Class> result = new HashMap<String, Class>();
		Class cls = null;
		Class pojoOfdelimiter = null;
		try {
			jarFile = new JarFile(projectSetting.getTargetPath()+ "\\" + projectSetting.getJarFileName());
			Enumeration e = jarFile.entries();
			URL[] urls = { new URL("jar:file:" + projectSetting.getTargetPath()+ "\\" + projectSetting.getJarFileName()+"!/"),
						   new URL("jar:file:" + projectSetting.getPathToUtilityJar() + "/log4j-1.2.15.jar"+"!/"),
						   new URL("jar:file:" + projectSetting.getPathToUtilityJar() + "/ConceptCrewUtil-1.0.jar"+"!/"),
						   new URL("jar:file:" + projectSetting.getPathToUtilityJar() + "/ojdbc6.jar"+"!/"),
						   new URL("jar:file:" + projectSetting.getPathToUtilityJar() + "/commons-dbcp-1.3.jar"+"!/"),
						   new URL("jar:file:" + projectSetting.getPathToUtilityJar() + "/commons-io-2.4.jar"+"!/"),
						   new URL("jar:file:" + projectSetting.getPathToUtilityJar() + "/commons-pool-1.5.4.jar"+"!/"),
						   new URL("jar:file:" + projectSetting.getPathToUtilityJar() + "/univocity-parsers-2.0.2.jar"+"!/")};
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
			    
			    if(className.contains(Constants.pojoPackageName)){
			    	pojoOfdelimiter = loader.loadClass(className);
			    	result.put(Constants.delimiterPojoClass, pojoOfdelimiter);
			    }
			    if(className.contains(Constants.packageToProcess))
			    {
			    	if(Constants.inputType.DELIMITED.toString().equals(projectSetting.getRequestType())){
			    		processor = Constants.packageToProcess + "." + "DelimitedFileProcessor";
			    	}else{
			    		processor = Constants.packageToProcess + "." + "XMLFileProcessor";
			    	}    	
			    	 cls = loader.loadClass(processor);
			    	 result.put(Constants.mainProcessor, cls);
			    }else{
			    	loader.loadClass(className);
			    }
		    }
			jarFile.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		} catch (ClassNotFoundException e1) {
			e1.printStackTrace();
		}
		return result;
	}

}
