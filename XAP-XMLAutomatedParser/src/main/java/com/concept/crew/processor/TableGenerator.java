package com.concept.crew.processor;

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
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

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
	
	public void tableScripts(Multimap<String, DBColumns> tableMap, String tableSuffix, String rootNode , String schema)
							 					throws Exception
	{
		System.out.println("Start Generating scripts for " +tableSuffix);
		String fileName = Constants.resourcePath + "createTable";
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
			if(tableName.contains("$")){
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
	
	public String fetchRootNode(File xsdFile) throws ParserConfigurationException, SAXException, IOException{
		final Thread currentThread = Thread.currentThread();
		final ClassLoader contextClassLoader = currentThread.getContextClassLoader();

		final InputStream inputStream = contextClassLoader.getResourceAsStream(xsdName);
		
		//parse the document
		DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder;
		docBuilder 		= docBuilderFactory.newDocumentBuilder();
		Document doc 	= docBuilder.parse(inputStream);
		NodeList list 	= doc.getElementsByTagName("xs:element"); 
		
		if(list != null){
			Element element = (Element)list.item(0) ;
			return element.getAttribute("name");
		}
			
		return null;
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
}
