package com.concept.crew.processor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.log4j.Logger;

import com.concept.crew.dao.XapDBRoutine;
import com.concept.crew.util.FrameworkSettings;

public class DBScriptRunner 
{
	private static Logger 		logger 			= Logger.getLogger(DBScriptRunner.class);

	private FrameworkSettings projectSetting;

	public DBScriptRunner(FrameworkSettings projectSetting) {
		super();
		this.projectSetting = projectSetting;
	}

	public void executeScripts()
	{
		Connection conn = XapDBRoutine.getConnection();
		Statement stat  = null;
		File sqlFile = getSqlFile(new File(projectSetting.getResourcePath()));
		
		if(sqlFile == null)
		{
			logger.warn("SQL file not generated, hence Skipping");
			return;
		}
		
		try 
		{
			BufferedReader br   = new BufferedReader(new FileReader(projectSetting.getResourcePath()+"/"+sqlFile));
			StringBuffer sb 	= new StringBuffer();
			String s            = new String();
			
			while ((s = br.readLine()) != null) 
			{
				sb.append(s);
			}
			br.close();
			
			String[] queries = sb.toString().split(";");
			stat = conn.createStatement();
			
			executeDropStatments(stat, queries);
			createparentTables(stat, queries);
			creatChildTables(stat, queries);
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SQLException e) 
		{
			logger.warn("Database Exception while executing script`");
			e.printStackTrace();
		} 
		finally
		{
			if(stat != null)
			{
				try {
					stat.close();
					
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(conn != null){
				XapDBRoutine.cleanUp();
			}
		}
		
	}
	
	private static File getSqlFile(File resourcePath){
		if(resourcePath.isDirectory()){
			
			FilenameFilter filter = new FilenameFilter(){
				public boolean accept(File dir, String name) {
					return name.startsWith("createTable");  //name.endsWith(".sql");
				}				
			};
						
			String[] list  = resourcePath.list(filter);
			
			if(list == null || list.length == 0){
				logger.warn("no .sql file found");
				return null;
			}
			
			return new File(list[0]);
		}
		return null;
	}
	
	private void executeDropStatments(Statement stat, String[] queries){
		for (String query : queries) {
			if (!query.trim().equals("") && query.contains("DROP")) {
				logger.warn("DDL to be execute --> " + query);
				try {
					stat.executeUpdate(query);
				} catch (SQLException e) {
					logger.warn("DDL not found in DB...");
				}
			}
		}
	}
	
	private void createparentTables(Statement stat, String[] queries) throws SQLException{
		for (String query : queries) {
			if (!query.trim().equals("") && !query.contains("FOREIGN") && !query.contains("DROP")) {
				logger.warn("DDL to be execute --> " + query);
				stat.executeUpdate(query);
			}
		}
	}
	
	private void creatChildTables(Statement stat, String[] queries) throws SQLException{
		for (String query : queries) {
			if (!query.trim().equals("") && query.contains("FOREIGN")) {
				logger.warn("DDL to be execute --> " + query);
				stat.executeUpdate(query);
			}
		}
	}
}
