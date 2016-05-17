package com.concept.crew.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.concept.crew.dao.LoaderDBRoutine;

public class QueryReaderUtil {
	public static final String QUERY_BASE_LOCN = "C:\\XAP\\LoadersFramework\\src\\main\\resources";

	public static String getTextFileContents(final String fileName)  {
		
		File resourcePath = new File(QUERY_BASE_LOCN);
		
		FilenameFilter filter = new FilenameFilter(){
			public boolean accept(File dir, String name) {
				return name.startsWith(fileName);  
			}				
		};

		String[] list  = resourcePath.list(filter);
		
		if(list == null || list.length == 0){
			System.out.println("no .sql file found");
			return null;
		}
		StringBuilder sb = new StringBuilder();
		String fResName = QUERY_BASE_LOCN + list[0];

		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(new File(fResName)));
			String line = br.readLine();
			while (line != null) {
				sb.append(line);
				sb.append("\n");
				line = br.readLine();
			}	
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}catch (IOException ioe) {
			ioe.printStackTrace();
		} finally {
			try {
				br.close();
			}catch(Exception e) {

			}
		}
		return sb.toString();
	}
	
	private static final String SQL_SELECT_NEW_PK = "select level CORE_REF_DATA.INSTRUMENT_SEQ.nextval id from dual connect by level <= ";
	
	public static List<Integer> loadNewGoldenInstId(int cacheSize)
			throws Exception {

		DBRoutinePoolTx brdDBRoutineTx = new DBRoutinePoolTx(LoaderDBRoutine.getPoolname());

		brdDBRoutineTx.beginTransaction();
		Connection conn = brdDBRoutineTx.getConnection();

		List<Integer> retvalList = new ArrayList<Integer>();
		Statement stmt = null;
		ResultSet rs = null;
		try {
			stmt = conn.createStatement();
			rs = stmt.executeQuery(SQL_SELECT_NEW_PK + cacheSize);
			while (rs.next()) {
				retvalList.add(rs.getInt("id"));
			}

		} catch (SQLException e) {
			brdDBRoutineTx.rollbackTransaction();
		} finally {
			stmt.close();
			rs.close();
			brdDBRoutineTx.cleanup(conn);
		}
		return retvalList;
	}

}
