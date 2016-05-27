package com.concept.crew.processor;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
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

import org.apache.log4j.Logger;

import com.concept.crew.info.DBColumns;
import com.concept.crew.util.Constants;
import com.concept.crew.util.FrameworkSettings;
import com.google.common.collect.Multimap;

public abstract class TableGenerator 
{	
	private static Logger 		logger 			= Logger.getLogger(TableGenerator.class);
	
	protected FrameworkSettings 	projectSetting;
	protected static Set<String> 	xsdDataTypes = new HashSet<String>();
	protected String 				xsdName;
	private   static int 			width 		=45;
    private   static char 			fill 		= ' ';
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
	
	public abstract Multimap<String, DBColumns> parse(Boolean typed,
													  String dbType) throws Exception;
	
	public void tableScripts(Multimap<String, DBColumns> 	tableMap, 
							 String 						tableSuffix, 
							 String 						rootNode , 
							 String 						dbType)
							 					throws Exception
	{
		logger.warn("Generate Database Table scripts");
		String fileName = projectSetting.getResourcePath() + "createTable";
		String parentTableName = null;
		String constraintName = null;
		if(tableSuffix != null && tableSuffix != "")
		{
			fileName = fileName + "_" + tableSuffix + ".sql";
			parentTableName =  rootNode.toUpperCase() + "_"+ tableSuffix;
		}
		else
		{
			fileName = fileName + ".sql";
			parentTableName =  rootNode.toUpperCase() ;
		}
		logger.warn("Start Generating DB scripts "+ fileName);
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
		
		addDropScripts(bw, sb, tableMap.keySet(), tableSuffix, rootNode,  dbType);
		
		if(rootNode != null){
			sb = new StringBuffer();
			sb.append("\n") ;
			sb.append("CREATE SEQUENCE ").append(rootNode.toUpperCase()).append("_SEQ ").append("MINVALUE 1 START WITH 1 INCREMENT BY 1 ;");
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
			sb.append("CREATE TABLE ").append(tableName.toUpperCase());
			constraintName = tableName.toUpperCase();
			if(tableSuffix != null && tableSuffix != "")
			{
				sb.append("_").append(tableSuffix.toUpperCase());
				constraintName = tableName.toUpperCase() + "_" + tableSuffix.toUpperCase();
			}
			sb.append(" ( \n") ;
			
			if(tableName.equals(rootNode))
			{
				if(Constants.DatabaseType.JavaDB_DERBY.toString().equalsIgnoreCase(dbType))
				{
					sb.append("PKEY\t\tDECIMAL PRIMARY KEY, \n");		
				}
				else
				{
					sb.append("PKEY\t\tNUMBER PRIMARY KEY, \n");	
				}
			}
			else
			{
				if(Constants.DatabaseType.JavaDB_DERBY.toString().equalsIgnoreCase(dbType))
				{					
					sb.append("PARENT_KEY");
					sb.append(format("PARENT_KEY","DECIMAL,")).append("\n");
				}
				else
				{					
					sb.append("PARENT_KEY");
					sb.append(format("PARENT_KEY", "NUMBER,")).append("\n");
					//sb.append("PARENT_KEY\t\tNUMBER, \n");	
				}
			}
			
			Collection<DBColumns> columnList = tableMap.get(tableName);
			
			for(DBColumns columns :columnList)
			{
				if(columns.getName().length() > 30){
					columns.setName(columns.getName().substring(0, 30));
				}
				sb.append(columns.getName().toUpperCase()).
				   append(format(columns.getName(), columns.getDataType()).toUpperCase()).append(",\n");
			}
			
			if(!tableName.equals(rootNode)){
				sb.append("CONSTRAINT ").append(constraintName).append("_FK ").
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
	


	
	protected String sqlDataType(String xsdType, Boolean typed, String dbType)
	{
		String sqlType = "VARCHAR(100)";
		if(typed != null && typed.TRUE)
		{
			sqlType = sqlDataType(xsdType, dbType);
		}
		return sqlType;
	}
	
	protected String sqlDataType(String xsdType,
								 String dbType)
	{
		String sqlType ="";
		
		if("xs:string".equalsIgnoreCase(xsdType)
				|| "String".equalsIgnoreCase(xsdType))
		{
			// Supported for Oracle, JavaDB, MS-SQL Server
			sqlType = "VARCHAR(100)";
		}
		else if("xs:decimal".equalsIgnoreCase(xsdType)
					|| "LONG".equalsIgnoreCase(xsdType)
						|| "DOUBLE".equalsIgnoreCase(xsdType)
							|| "BIGDECIMAL".equalsIgnoreCase(xsdType))
		{
			// Supported for Oracle
			sqlType = "NUMBER";
			if(Constants.DatabaseType.JavaDB_DERBY.toString().equalsIgnoreCase(dbType))
			{
				sqlType = "DECIMAL";
			}
		}	
		else if("xs:dateTime".equalsIgnoreCase(xsdType)
				 || "XMLGregorianCalendar".equalsIgnoreCase(xsdType))
		{
			// Supported for Oracle, JavaDB
			sqlType = "DATE";
		}	
		else if("BOOLEAN".equalsIgnoreCase(xsdType))
		{
			// Supported for Oracle, JavaDB, MS-SQL Server
			sqlType = "CHAR(1)";
		}			
		else
		{
			sqlType = xsdType;
		}	
		return sqlType;
	}
	
	private static void addDropScripts(BufferedWriter 	bw, 
									   StringBuffer 	sb, 
									   Set<String> 		tableName, 
									   String 			tableSuffix, 
									   String 			rootNode , 
									   String 			dbType ) 
											   			throws IOException
	{	
		Iterator<String> itr = tableName.iterator();
		while(itr.hasNext())
		{
			String name = itr.next();
			if(name.contains("$") || name.equals(rootNode)){
				continue;
			}
			sb.append("DROP TABLE ").append(name.toUpperCase());
			
			if(tableSuffix != null && tableSuffix != "")
			{
				sb.append("_").append(tableSuffix.toUpperCase());
			}
			sb.append(";") ;
			sb.append("\n") ;
		}
	
		sb.append("DROP TABLE ").append(rootNode.toUpperCase());
		
		if(tableSuffix != null && tableSuffix != "")
		{
			sb.append("_").append(tableSuffix.toUpperCase());
		}
		sb.append(";") ;

		sb.append("\n") ;
		sb.append("DROP SEQUENCE ").append(rootNode.toUpperCase()).append("_").append("SEQ").append(";");
		bw.write(sb.toString());
		bw.newLine();
	}

	public void insertScripts(Multimap<String, DBColumns> tableMap,
							  String tableSuffix, String rootNode, String dbType) throws Exception 
	{
		
		if(tableMap.isEmpty()){
			return;
		}
		logger.warn("-----------------------------");
		logger.warn("Generate Database Insert's scripts");
		logger.warn("-----------------------------");

		StringBuffer sb = new StringBuffer();
		
		Iterator<String> tableMapIt =  tableMap.keySet().iterator();
		while(tableMapIt.hasNext())
		{
			String tableName = tableMapIt.next();
			String tableNameWithSuffix = "";
			if(tableName == null ||  tableName.contains("$") || tableName.toUpperCase().contains("OBJECTFACTORY")){
				continue;
			}
			String fileName = null;
			int columnCount = 0;
			fileName =  tableName + ".sql";
			if(tableSuffix != null && tableSuffix != ""){
				tableNameWithSuffix = tableName + "_" + tableSuffix;
			}else{
				tableNameWithSuffix =  tableName + ".sql";
			}
			logger.warn(fileName);
			
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
			
			sb.append("INSERT INTO ").append(tableNameWithSuffix.toUpperCase()).append("(");
			
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
			//logger.warn(sb);
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
	private static String format(String columnName, String dataType)
	{
		String temp = new String(
						new char[width - (columnName.length() + dataType.length())])
								.replace('\0', fill)  + dataType;
								
		
		return temp;
	}
	public static void main(String[] args) throws Exception
	{
		//TableGenerator tb = new TableGenerator(null);
		//Multimap<String, DBColumns> tableMap = tb.parseJaxbInfo(true);
		
		String colName  = "PUTENDDATE";
		String dataType = "DATE";
		System.out.println(colName + format(colName, dataType));
		
		colName  = "PUTENDDATEsdsd";
		dataType = "NUMBER";
		System.out.println(colName + format(colName, dataType));
		
		//String formattedSQL = new BasicFormatterImpl().format(sql);
		
	}
}
