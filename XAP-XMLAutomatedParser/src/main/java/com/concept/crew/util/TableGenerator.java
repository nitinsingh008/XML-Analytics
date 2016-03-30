package com.concept.crew.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.concept.crew.info.DBColumns;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

public class TableGenerator 
{	
	private static Set<String> xsdDataTypes = new HashSet<String>();
	private String xsdName ;
	
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
	
	public void tableScripts(Multimap<String, DBColumns> tableMap, 
							 String tableSuffix)
							 					throws Exception
	{
		System.out.println("Start Generating scripts for " +tableSuffix);
		String fileName = Constants.dirSrcResource + "createTable";
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
				sb.append("_").append(tableSuffix);
			}
			sb.append(" ( ") ;
			Collection<DBColumns> columnList = tableMap.get(tableName);
			
			for(DBColumns columns :columnList)
			{
				sb.append(columns.getName()).append(" ").append(columns.getDataType()).append(", ");
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
	
	public Multimap<String, DBColumns> parseGenericSchema(boolean typed) throws Exception
	{
		Multimap<String, DBColumns> tableMap = ArrayListMultimap.create();
		final Thread currentThread = Thread.currentThread();
		final ClassLoader contextClassLoader = currentThread.getContextClassLoader();

		final InputStream inputStream = contextClassLoader.getResourceAsStream(xsdName);
		
		//parse the document
		DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder;
		docBuilder 		= docBuilderFactory.newDocumentBuilder();
		Document doc 	= docBuilder.parse(inputStream);
		NodeList list 	= doc.getElementsByTagName("xs:element"); 
		
		StringBuilder mainTable = new StringBuilder();
			
		String currTableName = "";

		for(int i = 0 ; i < list.getLength(); i++)
		{
			Element element = (Element)list.item(i) ; 
			
			if("MarkitMultiBond".equalsIgnoreCase(element.getAttribute("name")) ||
					"SubArtifacts".equalsIgnoreCase(element.getAttribute("name")))
			{
				//System.out.println("Ignore => " + element.getAttribute("name")); 
			}
			else if(element.hasAttributes() 
						&& element.getAttribute("type") == "" 
							&& element.hasChildNodes())
			{
				//System.out.println(element.getAttribute("name") + " ******** Schedule ********"); 	
				currTableName = element.getAttribute("name");	
			}			
			else if(element.hasAttributes() 
					&& element.getAttribute("type") != "" 
						&& 	xsdDataTypes.contains(element.getAttribute("type"))  ) // Main Table
			{
				//System.out.println(element.getAttribute("name")); 
				DBColumns column = new DBColumns();
				column.setName(element.getAttribute("name"));
				column.setDataType(sqlDataType(element.getAttribute("type"), typed));
				tableMap.put(currTableName, column);
				
				Node sibling = element.getNextSibling();
				
				while (!(sibling instanceof Element) && sibling != null) 
				{
					sibling = sibling.getNextSibling();
				}
				//System.out.println(currTableName + " => " + element.getAttribute("name")); 
				if(sibling == null)
				{
					currTableName = "MarkitBond";
				}
			}
			else
			{
				//System.out.print(" ******************* "+element.getAttribute("name")); 
			}
		}	
	    System.out.println(mainTable);		
	    return tableMap;
	}
	
	
	public Multimap<String, DBColumns> parseSchema(boolean typed) throws Exception
	{
		System.out.println("Start Parsing XSD");
		Multimap<String, DBColumns> tableMap = ArrayListMultimap.create();
		final Thread currentThread = Thread.currentThread();
		final ClassLoader contextClassLoader = currentThread.getContextClassLoader();

		final InputStream inputStream = contextClassLoader.getResourceAsStream(xsdName);
		
		//parse the document
		DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder;
		docBuilder 		= docBuilderFactory.newDocumentBuilder();
		Document doc 	= docBuilder.parse(inputStream);
		NodeList list 	= doc.getElementsByTagName("xs:element"); 
		
		StringBuilder mainTable = new StringBuilder();
			
		String currTableName = "";

		for(int i = 0 ; i < list.getLength(); i++)
		{
			Element element = (Element)list.item(i) ; 
			
			if("MarkitMultiBond".equalsIgnoreCase(element.getAttribute("name")) ||
					"SubArtifacts".equalsIgnoreCase(element.getAttribute("name")))
			{
				//System.out.println("Ignore => " + element.getAttribute("name")); 
			}
			else if(element.hasAttributes() 
						&& element.getAttribute("type") == "" 
							&& element.hasChildNodes())
			{
				//System.out.println(element.getAttribute("name") + " ******** Schedule ********"); 	
				currTableName = element.getAttribute("name");	
			}			
			else if(element.hasAttributes() && element.getAttribute("type") != "" ) // Main Table
			{
				//System.out.println(element.getAttribute("name")); 
				DBColumns column = new DBColumns();
				column.setName(element.getAttribute("name"));
				column.setDataType(sqlDataType(element.getAttribute("type"), typed));
				tableMap.put(currTableName, column);
				
				Node sibling = element.getNextSibling();
				
				while (!(sibling instanceof Element) && sibling != null) 
				{
					sibling = sibling.getNextSibling();
				}
				//System.out.println(currTableName + " => " + element.getAttribute("name")); 
				if(sibling == null)
				{
					currTableName = "MarkitBond";
				}
			}
			else
			{
				//System.out.print(" ******************* "+element.getAttribute("name")); 
			}
		}	
	    System.out.println(mainTable);		
	    return tableMap;
	}

	private static String sqlDataType(String xsdType, boolean typed)
	{
		String sqlType = "VARCHAR2(100)";
		if(typed)
		{
			sqlType = sqlDataType(xsdType);
		}
		return sqlType;
	}
	
	private static String sqlDataType(String xsdType)
	{
		String sqlType ="";
		
		if("xs:string".equalsIgnoreCase(xsdType))
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
