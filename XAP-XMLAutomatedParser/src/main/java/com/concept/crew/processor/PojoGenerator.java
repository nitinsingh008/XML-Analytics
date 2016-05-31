package com.concept.crew.processor;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Set;

import com.concept.crew.info.DBColumns;
import com.concept.crew.util.Constants;
import com.concept.crew.util.FrameworkSettings;
import com.google.common.collect.Multimap;

public class PojoGenerator {

	private FrameworkSettings projectSetting;
	
		
	public PojoGenerator(FrameworkSettings projectSetting) {
		super();
		this.projectSetting = projectSetting;
	}


	public Boolean generatePOJO(Multimap<String, DBColumns> tableInfo){
	
		Set<String> tableNames = tableInfo.keySet();
		for (String table : tableNames) {
			StringBuilder pojoContaint = new StringBuilder();
			String tableName = table.toUpperCase();
			pojoContaint.append("package "+Constants.pojoPackageName+";").append("\n\n");
			pojoContaint.append("import com.univocity.parsers.annotations.Parsed;").append("\n\n\n");
			pojoContaint.append("public class "+tableName+" {").append("\n");
			// generate getter and setter
			StringBuilder variableDeclaration = new StringBuilder();
			StringBuilder getterSetterDeclaration = new StringBuilder();
			List<DBColumns> columns = (List<DBColumns>) tableInfo.get(table);
			for (DBColumns dbColumns : columns) {
				variableDeclaration.append("\t").append("@Parsed(index="+dbColumns.getPosition()+")").append("\n");
				variableDeclaration.append("\t").append("private String "+dbColumns.getName().toLowerCase()+";").append("\n\n");
				String methodName = dbColumns.getName().substring(0,1).toUpperCase()+dbColumns.getName().substring(1).toLowerCase();
				dbColumns.setGetterName("get"+methodName+"()");
				dbColumns.setSetterName("set"+methodName+"()");
				// getter method
				getterSetterDeclaration.append("\t").append("public String get"+methodName+"(){").append("\n");
				getterSetterDeclaration.append("\t").append("\t").append("return "+dbColumns.getName().toLowerCase()+";").append("\n");
				getterSetterDeclaration.append("\t").append("}\n\n");
				// setter method
				getterSetterDeclaration.append("\t").append("public void set"+methodName+"(String "+dbColumns.getName().toLowerCase()+"){").append("\n");
				getterSetterDeclaration.append("\t").append("\t this."+dbColumns.getName().toLowerCase()+"="+dbColumns.getName().toLowerCase()+";").append("\n");
				getterSetterDeclaration.append("\t").append("}\n\n");
			}
			pojoContaint.append(variableDeclaration);
			pojoContaint.append("\n\n");
			pojoContaint.append(getterSetterDeclaration);
			pojoContaint.append("\n\n");
			pojoContaint.append(" }");
			// generating java file for this
			File packageDirectory = new File(projectSetting.getPojoPackagePath());
			if(!packageDirectory.exists()){
				packageDirectory.mkdirs();
			}
			
			BufferedWriter writer = null;
			try {
				writer = new BufferedWriter(new FileWriter(new File(projectSetting.getPojoPackagePath(), tableName+".java")));
				writer.write(pojoContaint.toString());
				writer.flush();
			} catch (IOException e) {

			}finally {
				try{
					if(writer!=null){
						writer.close();
					}
				}catch(IOException e){
					
				}
			}
		}
		return Boolean.TRUE;
	}
}
