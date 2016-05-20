package com.concept.crew.processor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.List;

import com.concept.crew.info.DBColumns;
import com.concept.crew.util.AutomationHelper;
import com.concept.crew.util.Constants;
import com.concept.crew.util.FrameworkSettings;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.univocity.parsers.common.processor.BeanListProcessor;
import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;

public class CsvTableGenerator<E> extends TableGenerator{

	private String delimiter;
	
	
	public CsvTableGenerator(String xsdName,String delimiter,FrameworkSettings projectSetting) {
		super(xsdName,projectSetting);
		this.delimiter = delimiter;
	}

	@Override
	public Multimap<String, DBColumns> parse(Boolean haveHeaderInFile) throws Exception {
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
						column.setDataType(sqlDataType(head,false));
						column.setPosition(index-1);
						csvTableInfo.put(tableName, column);
					}
				}
				new PojoGenerator(projectSetting).generatePOJO(csvTableInfo);
				// compile generated project
				new AutomationHelper(projectSetting).buildMavenProject();
				// load class file
				loadClassesFromJar();
				
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
	public List<E> parse(boolean haveHeaderInFile,String delimiter, Class<E> className) throws Exception 
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
		settings.setHeaderExtractionEnabled(true);
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
		FrameworkSettings projectSetting = new FrameworkSettings("LoaderFramework");
		CsvTableGenerator generator = new CsvTableGenerator("Delimiter_Sample.TXT", "\t",projectSetting);
		try {
			Multimap<String, DBColumns> tableInfo = generator.parse(true);
			new PojoGenerator(projectSetting).generatePOJO(tableInfo);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

