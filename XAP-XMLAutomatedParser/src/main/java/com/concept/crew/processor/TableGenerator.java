package com.concept.crew.processor;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.concept.crew.info.DBColumns;
import com.concept.crew.util.Constants;
import com.google.common.collect.Multimap;

public abstract class TableGenerator 
{	
	protected static Set<String> xsdDataTypes = new HashSet<String>();
	protected String xsdName ;
	
	static
	{
		xsdDataTypes.add("xs:string");
		xsdDataTypes.add("xs:decimal");
		xsdDataTypes.add("xs:dateTime");
	}
	
	public TableGenerator(String xsdName)
	{
		this.xsdName = xsdName;
	}
	
	public abstract Multimap<String, DBColumns> parse(boolean typed) throws Exception;
	
	public void tableScripts(Multimap<String, DBColumns> tableMap, 
							 String tableSuffix)
							 					throws Exception
	{
		System.out.println("Start Generating scripts for " +tableSuffix);
		String fileName = Constants.resourcePath + "createTable";
		if(tableSuffix != null && tableSuffix != "")
		{
			fileName = fileName + "_" + tableSuffix + ".sql";
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
		
		Iterator<String> tableMapIt =  tableMap.keySet().iterator();
		while(tableMapIt.hasNext())
		{
			String tableName = tableMapIt.next();
			sb.append("CREATE TABLE ").append(tableName.toUpperCase());
			if(tableSuffix != null && tableSuffix != "")
			{
				sb.append("_").append(tableSuffix.toUpperCase());
			}
			sb.append(" ( ") ;
			Collection<DBColumns> columnList = tableMap.get(tableName);
			
			for(DBColumns columns :columnList)
			{
				sb.append(columns.getName().toUpperCase()).append(" ").
				   append(columns.getDataType().toUpperCase()).append(", ");
			}
			sb.deleteCharAt(sb.length()-2);
			sb.append(");");
			System.out.println(sb);
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
}
