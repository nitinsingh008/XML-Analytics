package com.concept.crew.processor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import com.concept.crew.info.DBColumns;
import com.concept.crew.util.Constants;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

public class CsvTableGenerator extends TableGenerator{

	private String delimiter;
	
	public CsvTableGenerator(String xsdName) {
		super(xsdName);
	}
	
	public CsvTableGenerator(String xsdName,String delimiter) {
		super(xsdName);
		this.delimiter = delimiter;
	}

	@Override
	public Multimap<String, DBColumns> parse(boolean haveHeaderInFile) throws Exception {
		Multimap<String, DBColumns> csvTableInfo =  ArrayListMultimap.create();
		File csvFile = new File(xsdName);
		
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
						DBColumns column = new DBColumns();
						if(haveHeaderInFile){
							column.setName(head.toUpperCase());
						}else{
							column.setName(Constants.delimiterTableHeaderPrefi+index);
						}
						column.setDataType(sqlDataType(head,false));
						csvTableInfo.put(tableName, column);
					}
				}
				
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
	 
	public static void main(String[] args) {
		CsvTableGenerator generator = new CsvTableGenerator("Delimiter_Sample.TXT", "\t");
		try {
			generator.parse(true);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

