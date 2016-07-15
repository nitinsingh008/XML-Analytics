package com.concept.crew.processor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.List;

import org.apache.log4j.Logger;

import com.concept.crew.info.DBColumns;
import com.concept.crew.util.AutomationHelper;
import com.concept.crew.util.Constants;
import com.concept.crew.util.FrameworkSettings;
import com.concept.crew.util.PomDependencyUpdater;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.univocity.parsers.common.processor.BeanListProcessor;
import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;

public class CsvTableGenerator<E> extends TableGenerator{

	private String delimiter;
	private static final Logger LOGGER = Logger.getLogger(CsvTableGenerator.class);
	
	public CsvTableGenerator(String xsdName,String delimiter,FrameworkSettings projectSetting,AutomationHelper helper) {
		super(xsdName,projectSetting,helper);
		this.delimiter = delimiter;
	}

	@Override
	public Multimap<String, DBColumns> parse(Boolean haveHeaderInFile,
											 String dbType) throws Exception 
	{
		LOGGER.warn("-----------------------------");
		LOGGER.warn("Convert Meta-data to Relational model ");
		LOGGER.warn("-----------------------------");
		
		Multimap<String, DBColumns> csvTableInfo =  ArrayListMultimap.create();
		File csvFile = new File(Constants.xsdLocalPath+File.separator+xsdName);
		
		if(csvFile.exists()){
			String tableName = csvFile.getName().substring(0,csvFile.getName().lastIndexOf(".")).toUpperCase();
			BufferedReader reader = null;
			try{
				reader = new BufferedReader(new FileReader(csvFile));
				String line = reader.readLine();
				String[] headers = line.split(delimiter);
				if(headers !=null){
					int index = 1;
					for (String head : headers) {
						index++;
						DBColumns column = new DBColumns();
						if(haveHeaderInFile){
							column.setName(head.toUpperCase());
						}else{
							column.setName(Constants.delimiterTableHeaderPrefi+index);
						}
						
						column.setOriginalDataType("String");
						column.setDataType(sqlDataType(head,false, dbType));
						column.setPosition(index-1);
						csvTableInfo.put(tableName, column);
					}
				}
				LOGGER.warn("Creating POJO for Mapping");
				new PojoGenerator(projectSetting).generatePOJO(csvTableInfo);
				// move ojdbc jars
				autoHelper.copyUtilityJars();
				// compile generated project
				//new AutomationHelper(projectSetting).buildMavenProject();
				autoHelper.buildMavenProject();
				// load class file
				autoHelper.copyJaxbClasses(true);
				
				//loadClassesFromJar();
				loadClassesFromFolder(true);
				
			}catch(IOException e){
				
			}finally {
				try{
					if(reader!=null){
						reader.close();
					}
				}catch(IOException e){
					
				}
			}
		}
		return csvTableInfo;
	}
	
	/*
	 * This method will take the Info 
	 * 
	 * Usage
	 * 	CsvTableGenerator<DynamicInfo> parse = new CsvTableGenerator<DynamicInfo>();  
	 * 	parse.parseFile(localPathWithFileName, DynamicInfo.class);
	 * 
	 */
	public List<E> parseFileData(Boolean haveHeaderInFile,String delimiter, Class<E> className) throws Exception 
	{
		if(xsdName == null )
		{
			throw new Exception("File name is mandatory for reading file");
		}
		
		List<E> beans = null;
		// BeanListProcessor converts each parsed row to an instance of a given class, then stores each instance into a list.
		BeanListProcessor<E> processor = new BeanListProcessor<E>(className);
		
		CsvParserSettings settings = newCsvInputSettings(null,delimiter);
		
		settings.setRowProcessor(processor);
		settings.setHeaderExtractionEnabled(haveHeaderInFile);
		settings.setIgnoreLeadingWhitespaces(true);
		settings.setIgnoreTrailingWhitespaces(true);

		CsvParser parser = new CsvParser(settings);
		 	
		try 
		{
			parser.parse(newReader(xsdName));
		} 
		catch (Exception e) 
		{
			System.err.println("Parse Error: " + e.getMessage()); 
			throw e;
		}	
      
		// The BeanListProcessor provides a list of objects extracted from the input.
		beans = processor.getBeans();

		return beans;
	}	
	
	protected CsvParserSettings newCsvInputSettings(char[] lineSeparator,String delimiter) 
	{
		CsvParserSettings out = new CsvParserSettings();
		if (lineSeparator == null) 
		{
			out.setLineSeparatorDetectionEnabled(true);
		} 
		else 
		{
			out.getFormat().setLineSeparator(lineSeparator);
		}
		
		out.getFormat().setDelimiter(delimiter.charAt(0));
		
		return out;
	}
	
	public Reader newReader(String filename) throws Exception 
	{
		BufferedReader reader = new BufferedReader( new FileReader(filename) ); 
		return reader;
	}	
	 
	public static void main(String[] args) {
		/*FrameworkSettings projectSetting = new FrameworkSettings("LoaderFramework");
		CsvTableGenerator generator = new CsvTableGenerator("Delimiter_Sample.TXT", "\t",projectSetting);
		try {
			Multimap<String, DBColumns> tableInfo = generator.parse(true, "Oracle");
			new PojoGenerator(projectSetting).generatePOJO(tableInfo);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
	}
}

