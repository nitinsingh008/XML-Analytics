package com.concept.crew.processor;

import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.concept.crew.info.DBColumns;
import com.concept.crew.util.FrameworkSettings;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

public class XSDTableGenerator extends TableGenerator
{	
	public XSDTableGenerator(String xsdName,FrameworkSettings projectSetting) 
	{
		super(xsdName,projectSetting);
	}

	public Multimap<String, DBColumns> parse(Boolean typed) throws Exception
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
	
}
