package com.concept.crew.processor;

import java.lang.reflect.Field;
import java.util.List;

import org.apache.log4j.Logger;

import com.concept.crew.info.DBColumns;
import com.concept.crew.util.AutomationHelper;
import com.concept.crew.util.FrameworkSettings;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

public class JaxbTableGenerator extends TableGenerator
{		
	private static final Logger log = Logger.getLogger(JaxbTableGenerator.class);
	public JaxbTableGenerator(String xsdName,FrameworkSettings projectSetting,AutomationHelper helper)
	{
		super(xsdName,projectSetting,helper);
	}
	

	public Multimap<String, DBColumns> parse(Boolean typed,
										     String dbType) throws Exception
	{

		log.warn("Convert Meta-data to Relational model");
		log.warn("---------------------------------------------------");
		
		Multimap<String, DBColumns> tableMap = ArrayListMultimap.create();
		//List<Class> classes = loadClassesFromJar();
		List<Class> classes = loadClassesFromFolder(false);
		String currTableName = "";
		for (Class cls : classes) 
		{
			currTableName = cls.getName().substring(cls.getName().lastIndexOf('.') + 1);
			log.warn("Class Name => " + currTableName);
		    Field[] fields = cls.getDeclaredFields();
 
			for(Field field : fields)
			{
			    //System.out.println("method = " + field.getName());
				DBColumns column = new DBColumns();
				column.setName(field.getName());
				
				String fieldTypeFullName = field.getType().getName();
				String fieldType = fieldTypeFullName.substring(fieldTypeFullName.lastIndexOf('.') + 1);
				if(fieldType != null && fieldType.equalsIgnoreCase("LIST"))
					continue;
				
				column.setDataType(sqlDataType(fieldType, typed, dbType));
				column.setOriginalDataType(fieldType);
				String methodName = column.getName().substring(0, 1).toUpperCase()+column.getName().substring(1);
				if("Boolean".equals(fieldType)){
					column.setGetterName("is"+methodName+"()");
				}else{
					column.setGetterName("get"+methodName+"()");
				}				
				column.setSetterName("set"+methodName+"()");
				tableMap.put(currTableName, column);
			}
		}

		return tableMap;
	}
	


	public static void main(String[] args) throws Exception
	{		
		/*JaxbTableGenerator tb = new JaxbTableGenerator("Sample_bond.xsd",new FrameworkSettings("LF_SampleBondsXSD_215920160159"));
		Multimap<String, DBColumns> tableMap = tb.parse(true,"Oracle");*/
	}
}
