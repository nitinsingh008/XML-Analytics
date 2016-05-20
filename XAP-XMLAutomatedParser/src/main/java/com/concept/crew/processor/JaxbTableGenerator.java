package com.concept.crew.processor;

import java.lang.reflect.Field;
import java.util.List;

import com.concept.crew.info.DBColumns;
import com.concept.crew.util.FrameworkSettings;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

public class JaxbTableGenerator extends TableGenerator
{		
	public JaxbTableGenerator(String xsdName,FrameworkSettings projectSetting)
	{
		super(xsdName,projectSetting);
	}
	

	public Multimap<String, DBColumns> parse(Boolean typed) throws Exception
	{
		Multimap<String, DBColumns> tableMap = ArrayListMultimap.create();
		List<Class> classes = loadClassesFromJar();
		String currTableName = "";
		for (Class cls : classes) {
			currTableName = cls.getName().substring(cls.getName().lastIndexOf('.') + 1);
		    
		    Field[] fields = cls.getDeclaredFields();
 
			for(Field field : fields)
			{
			    System.out.println("method = " + field.getName());
				DBColumns column = new DBColumns();
				column.setName(field.getName());
				
				String fieldTypeFullName = field.getType().getName();
				String fieldType = fieldTypeFullName.substring(fieldTypeFullName.lastIndexOf('.') + 1);
				column.setDataType(sqlDataType(fieldType, typed));
				String methodName = column.getName().substring(0, 1).toUpperCase()+column.getName().substring(1);
				column.setGetterName("get"+methodName+"()");
				column.setSetterName("set"+methodName+"()");
				tableMap.put(currTableName, column);
			}
		}

		return tableMap;
	}
	


	public static void main(String[] args) throws Exception
	{		
		JaxbTableGenerator tb = new JaxbTableGenerator("Sample_bond.xsd",new FrameworkSettings("LoaderFramework"));
		Multimap<String, DBColumns> tableMap = tb.parse(true);
	}
}
