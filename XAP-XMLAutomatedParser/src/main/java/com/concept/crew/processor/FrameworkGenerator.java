package com.concept.crew.processor;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import com.concept.crew.info.DBColumns;
import com.concept.crew.util.Constants;
import com.google.common.collect.Multimap;

public class FrameworkGenerator 
{
	private static VelocityEngine velocityEngine;
	private static VelocityContext context;

	public static void initialize()
	{
		velocityEngine = new VelocityEngine();
		velocityEngine.init();
        context = new VelocityContext();
	}
	
	public static void generate(Multimap<String, DBColumns> tableMap)
	{
		Iterator<String> tableMapIt =  tableMap.keySet().iterator();
		while(tableMapIt.hasNext())
		{

			String tableName = tableMapIt.next();
			if(tableName == null ||  tableName.contains("$") || tableName.toUpperCase().contains("OBJECTFACTORY"))
			{
				continue;
			}
			
			Collection<DBColumns> columnList = tableMap.get(tableName);
			
			for(DBColumns columns :columnList)
			{
				String columnName = columns.getName();
				if(columnName.length() > 30)
				{
					columns.setName(columnName.substring(0, 30));
				}
				columns.setName(columnName.substring(0, 1).toUpperCase() + columnName.substring(1));
				//sb.append(columns.getName().toUpperCase()).append("\t\t").
				  // append(columns.getDataType().toUpperCase()).append(",\n");
			}
	        String infoName = tableName;
	        context.put("Info", infoName);
	        context.put("ClassName", infoName + "Loader");
	        context.put("columnList", columnList);
			
	        Template template = velocityEngine.getTemplate("./src/main/resources/ScheduleLoader.java.vtl" );
	        StringWriter writer = new StringWriter();
	        template.merge( context, writer );
	        System.out.println( writer.toString() );  
		}
		
		

		
	}
	
	public static void generateParentWrapper(String root){
		context = null;
		context = new VelocityContext(); 
		// check to be added for XSD and Delimited file for setting Import
	     context.put("Import", Constants.packageName + "."+root);
		 context.put("Root", root);
		 Template template = velocityEngine.getTemplate("./src/main/resources/ParentInfoWrapper.java.vtl" );
		 StringWriter writer = new StringWriter();
		//BufferedWriter writer = new BufferedWriter(new FileWriter("./src/main/resources/JavaClasses/ParentInfoWrapper.java"));
	     template.merge( context, writer );
	     System.out.println( writer.toString() );  
	     try {
	    	 writer.flush();
	         writer.close();
		} catch (IOException e) {
			
		}
	}
	
	public static void main(String[] args) throws Exception
	{
		initialize();
		generateParentWrapper("Instrument");
       /* String infoName = "Instrument";
        context.put("Info", infoName);
        context.put("ClassName", infoName + "Loader");


        Template template = velocityEngine.getTemplate("./src/main/resources/ParentInfoWrapper.java.vtl" );

        StringWriter writer = new StringWriter();
        //BufferedWriter writer = new BufferedWriter(new FileWriter("./src/main/resources/JavaClasses/InstrumentLoader.java"));
        
        template.merge( context, writer );
        writer.flush();
        writer.close();

        System.out.println( writer.toString() );    */    
	}

}
