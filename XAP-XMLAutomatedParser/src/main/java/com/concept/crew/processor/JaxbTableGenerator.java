package com.concept.crew.processor;

import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.reflections.Reflections;
import org.reflections.scanners.ResourcesScanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;

import com.concept.crew.info.DBColumns;
import com.concept.crew.util.Constants;
import com.concept.crew.util.FrameworkSettings;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

public class JaxbTableGenerator extends TableGenerator
{		
	public JaxbTableGenerator(String xsdName,FrameworkSettings projectSetting)
	{
		super(xsdName,projectSetting);
	}
	/*
	 * 
	 */
	public Multimap<String, DBColumns> parse(boolean typed, String old) throws Exception
	{
		Multimap<String, DBColumns> tableMap = ArrayListMultimap.create();
/*		File file = new File(Constants.dirSrcJava);
		URL urlList[] = {file.toURI().toURL()};
		//URLClassLoader loader = new URLClassLoader(urlList, Thread.currentThread().getContextClassLoader());
		URLClassLoader loader = new URLClassLoader(urlList);

		Class c = (Class) Class.forName(Constants.packageName, true, loader);*/
		
		List<ClassLoader> classLoadersList = new LinkedList<ClassLoader>();
		classLoadersList.add(ClasspathHelper.contextClassLoader());
		classLoadersList.add(ClasspathHelper.staticClassLoader());


		Reflections reflections = new Reflections(new ConfigurationBuilder()
		    .setScanners(new SubTypesScanner(false /* don't exclude Object.class */), new ResourcesScanner())
		    .setUrls(ClasspathHelper.forClassLoader(classLoadersList.toArray(new ClassLoader[0])))
		    .filterInputsBy(new FilterBuilder().include(FilterBuilder.prefix(Constants.packageName))));
		

		String currTableName = "";
		Set<Class<?>> classes = reflections.getSubTypesOf(Object.class);
		
		Iterator<Class<?>> iter = classes.iterator();
		while (iter.hasNext()) 
		{
		    System.out.println(iter.next());
		    Class cls = iter.next();
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
				
				tableMap.put(currTableName, column);
			}
    
		}
		
		return tableMap;
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
