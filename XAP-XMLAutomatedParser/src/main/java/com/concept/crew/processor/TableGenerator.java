package com.concept.crew.processor;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.concept.crew.apis.StartAutomation;
import com.concept.crew.info.DBColumns;
import com.concept.crew.util.Constants;
import com.concept.crew.util.FrameworkSettings;
import com.google.common.collect.Multimap;

public abstract class TableGenerator 
{	
	private static Logger 		logger 			= Logger.getLogger(TableGenerator.class);
	
	protected FrameworkSettings projectSetting;

	protected static Set<String> xsdDataTypes = new HashSet<String>();
	protected String xsdName ;
	
	static
	{
		xsdDataTypes.add("xs:string");
		xsdDataTypes.add("xs:decimal");
		xsdDataTypes.add("xs:dateTime");
	}
	
	public TableGenerator(String xsdName,FrameworkSettings projectSetting)
	{
		this.xsdName = xsdName;
		this.projectSetting = projectSetting;
	}
	
	public abstract Multimap<String, DBColumns> parse(Boolean typed) throws Exception;
	
	public void tableScripts(Multimap<String, DBColumns> tableMap, String tableSuffix, String rootNode , String schema)
							 					throws Exception
	{
		logger.warn("Start Generating scripts for " +tableSuffix);
		String fileName = projectSetting.getResourcePath() + "createTable";
		String parentTableName = null;

		if(tableSuffix != null && tableSuffix != "")
		{
			fileName = fileName + "_" + tableSuffix + ".sql";
			parentTableName = schema+"."+rootNode.toUpperCase() + "_"+ tableSuffix;
		}
		
		File file = new File(fileName);

		// if file doesn't exists, then create it
		if (!file.exists()) 
		{
			try 
			{
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		FileWriter fw = new FileWriter(file.getAbsoluteFile());
		BufferedWriter bw = new BufferedWriter(fw);
		
		StringBuffer sb = new StringBuffer();
		
		addDropScripts(bw, sb, tableMap.keySet(), tableSuffix, rootNode,  schema);
		
		if(rootNode != null){
			sb = new StringBuffer();
			sb.append("\n") ;
			sb.append("CREATE SEQUENCE ").append(schema).append(".").append(rootNode.toUpperCase()).append("_SEQ ").append("MINVALUE 1 START WITH 1 INCREMENT BY 1 NOCACHE ;");
			bw.write(sb.toString());
			bw.newLine();
			sb = new StringBuffer();
		}
		
		Iterator<String> tableMapIt =  tableMap.keySet().iterator();
		while(tableMapIt.hasNext())
		{
			sb.append("\n") ;
			String tableName = tableMapIt.next();
			if(tableName == null ||  tableName.contains("$") || tableName.toUpperCase().contains("OBJECTFACTORY")){
				//tableName = tableName.replace("$", "_");
				continue;
			}
			sb.append("CREATE TABLE ").append(schema).append(".").append(tableName.toUpperCase());
			if(tableSuffix != null && tableSuffix != "")
			{
				sb.append("_").append(tableSuffix.toUpperCase());
			}
			sb.append(" ( \n") ;
			
			if(tableName.equals(rootNode))
			{
				sb.append("PKEY\t\tNUMBER PRIMARY KEY, \n");	
			}
			else
			{
				sb.append("PARENT_KEY\t\tNUMBER, \n");
			}
			
			Collection<DBColumns> columnList = tableMap.get(tableName);
			
			for(DBColumns columns :columnList)
			{
				if(columns.getName().length() > 30){
					columns.setName(columns.getName().substring(0, 30));
				}
				sb.append(columns.getName().toUpperCase()).append("\t\t").
				   append(columns.getDataType().toUpperCase()).append(",\n");
			}
			
			if(!tableName.equals(rootNode)){
				sb.append("CONSTRAINT ").append(tableName.toUpperCase()).append("_FK ").
				   append("FOREIGN KEY (PARENT_KEY) REFERENCES ").append(parentTableName).append("( PKEY)");
			}else{
				sb.deleteCharAt(sb.length()-2);
			}
			
			sb.append("\n);");
			logger.warn(sb);
			bw.write(sb.toString());
			bw.newLine();
			sb = new StringBuffer();
		}	
		bw.close();
	}
	

	public static void main(String[] args) throws Exception
	{
		//TableGenerator tb = new TableGenerator(null);
		//Multimap<String, DBColumns> tableMap = tb.parseJaxbInfo(true);
	}
	
	protected String sqlDataType(String xsdType, boolean typed)
	{
		String sqlType = "VARCHAR2(100)";
		if(typed)
		{
			sqlType = sqlDataType(xsdType);
		}
		return sqlType;
	}
	
	protected String sqlDataType(String xsdType)
	{
		String sqlType ="";
		
		if("xs:string".equalsIgnoreCase(xsdType)
				|| "String".equalsIgnoreCase(xsdType))
		{
			sqlType = "VARCHAR2(100)";
		}
		else if("xs:decimal".equalsIgnoreCase(xsdType))
		{
			sqlType = "NUMBER";
		}	
		else if("xs:dateTime".equalsIgnoreCase(xsdType))
		{
			sqlType = "DATE";
		}			
		else
		{
			sqlType = xsdType;
		}	
		return sqlType;
	}
	
	private static void addDropScripts(BufferedWriter bw, StringBuffer sb, Set<String> tableName, String tableSuffix, String rootNode , String schema ) throws IOException{
		
		Iterator<String> itr = tableName.iterator();
		while(itr.hasNext()){
			String name = itr.next();
			if(name.contains("$") || name.equals(rootNode)){
				continue;
			}
			sb.append("DROP TABLE ").append(schema).append(".").append(name.toUpperCase());
			
			if(tableSuffix != null && tableSuffix != "")
			{
				sb.append("_").append(tableSuffix.toUpperCase());
			}
			sb.append(";") ;
			sb.append("\n") ;
		}
	
		sb.append("DROP TABLE ").append(schema).append(".").append(rootNode.toUpperCase());
		
		if(tableSuffix != null && tableSuffix != "")
		{
			sb.append("_").append(tableSuffix.toUpperCase());
		}
		sb.append(";") ;

		sb.append("\n") ;
		sb.append("DROP SEQUENCE ").append(schema).append(".").append(rootNode.toUpperCase()).append("_").append("SEQ").append(";");
		bw.write(sb.toString());
		bw.newLine();
	}

	public void insertScripts(Multimap<String, DBColumns> tableMap,
							  String tableSuffix, String rootNode, String schema) throws Exception {
		
		if(tableMap.isEmpty()){
			return;
		}
		
		logger.warn("Start Generating Insert's scripts for " +tableSuffix);
		StringBuffer sb = new StringBuffer();
		
		Iterator<String> tableMapIt =  tableMap.keySet().iterator();
		while(tableMapIt.hasNext()){
			String tableName = tableMapIt.next();
			if(tableName == null ||  tableName.contains("$") || tableName.toUpperCase().contains("OBJECTFACTORY")){
				continue;
			}
			String fileName = null;
			int columnCount = 0;
			
			fileName =  tableName.toUpperCase();
			
			if(tableSuffix != null && tableSuffix != ""){
				fileName =  tableName.toUpperCase() + "_" + tableSuffix.toUpperCase();
			}
			
			File file = new File(projectSetting.getResourcePath() + fileName);
			if (!file.exists()){
				try 
				{
					file.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			
			if(schema != null && schema != ""){
				fileName = schema.toUpperCase()+ "." + fileName;
			}
			
			sb.append("INSERT INTO ").append(fileName).append("(");
			
			if(tableName.equals(rootNode)){
				sb.append("PKEY, ");	
				columnCount++;
			}else{
				sb.append("PARENT_KEY, ");
				columnCount++;
			}
			
			Collection<DBColumns> columnList = tableMap.get(tableName);
			
			for(DBColumns columns :columnList){
				sb.append(columns.getName().toUpperCase()).append(", ");
				columnCount++;
			}
			sb.deleteCharAt(sb.length()-2);
			sb.append(") ").append("VALUES (");
			
			for(int i = 0 ; i < columnCount ; i++){
				sb.append("?, ");
			}
			sb.deleteCharAt(sb.length()-2);
			sb.append(")");
			logger.warn(sb);
			bw.write(sb.toString());
			bw.close();
			sb = new StringBuffer();
		}
	}
	
	
	
	protected List<Class> loadClassesFromJar()
			throws IOException, MalformedURLException, ClassNotFoundException {
		
		
		//String pathToJar = Constants.targetPath+ "\\" + Constants.jarFileName;
		JarFile jarFile = new JarFile(projectSetting.getTargetPath()+ "\\" + projectSetting.getJarFileName());
		Enumeration e = jarFile.entries();
		List<Class> classes = new ArrayList<Class>();
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
		    
		    if(!className.contains(Constants.packageToCompile))
		    {
		    	continue;
		    }
		    
		    Class cls = loader.loadClass(className);
		    
		    classes.add(cls);
		    
	    }
		jarFile.close();
		return classes;
	}
}
