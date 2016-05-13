package com.concept.crew.util;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

/**
 * Base(abstract) class which provides very simple spring like interface to jdbc, retaining full control.
 * @author Niraj.Sheth
 *
 */
public abstract class DBRoutine
{
	public static final String DEFAULT_NULL_TYPE_SYSPROP_NAME =  "DBR-defaultNullType";
	private static final Logger log = Logger.getLogger(DBRoutine.class);
	private Logger myLogger = null;
	private String logPrefix = "";
	private int fetchSize = 2000;
	private int batchThreshold = 2000;
	private boolean profiling = false;
	
	public abstract Connection getConnection() throws SQLException;
	public abstract void cleanup(Connection conn);
	public abstract String debugStr();
	public abstract int getNumActiveConn();
    public abstract int getMaxActiveConn();
    public abstract int getNumIdleConn();
    public abstract int getMaxIdleConn();
    public abstract int getMaxWait();
	
	/**
	 * Return single value, first column of the first row if available.<br>
	 * --Supported <b>returnType</b> objects are Date, Timestamp, String, Boolean, Integer, Long, Float, Double, BigDecimal<br>
	 * and explicit assignable (java.lang.Class.isAssignableFrom) classes
	 * 
	 * @param returnType
	 * @param sql
	 * @return
	 * @throws SQLException
	 */
	public <T> T executeSingleValueQuery(Class<T> returnType, String sql) throws SQLException {
		return executeSingleValueQuery(returnType, sql, null);
	}

	/**
	 * Return single value, first column of the first row if available.<br>
	 * --Supported <b>returnType</b> objects are Date, Timestamp, String, Boolean, Integer, Long, Float, Double, BigDecimal<br>
	 * and explicit assignable (java.lang.Class.isAssignableFrom) classes
	 * 
	 * @param returnType
	 * @param sql
	 * @param paramList
	 * @return
	 * @throws SQLException
	 */
	public <T> T executeSingleValueQuery(Class<T> returnType, String sql, List<EnumMap<DBParam, Object>> paramList) throws SQLException {
		T retVal = null;
		Connection conn = null;
		String debugStr = debugStr(sql);
		DBRoutineDebug debugObj = new DBRoutineDebug(logPrefix, debugStr, profiling);
		try {
			conn = getConnection();
			debugObj.connectionAcquired();
			PreparedStatement stmt = conn.prepareStatement(sql);
			debugObj.statementAcquired();
			debugObj.autocommit = conn.getAutoCommit();
			int paramIndex = 1;
			if (paramList != null) {
				for (EnumMap<DBParam, Object> param : paramList) {
					stmt.setObject(paramIndex++, (Object) param.get(DBParam.OBJECT), (Integer) param.get(DBParam.TYPE));
				}
			}
			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				retVal = convert(returnType, rs, 1);
			}
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
			debugObj.end();
			debugObj.exception = e;
			log(debugObj);
			throw e;
		} finally {
			cleanup(conn);
		}
		if (profiling) {
			debugObj.end();
			log(debugObj);
		}

		return retVal;
	}

	/**
	 * Fetch and convert/cast the ResultSet value to T type
	 * 
	 * @param obj
	 * @return
	 * @throws SQLException
	 */
	@SuppressWarnings("unchecked")
	private static <T> T convert(Class<T> t, ResultSet rs, int columnIndex) throws SQLException {

		if (t == null) {
			throw new IllegalArgumentException("Return Type is Null");
		}

		if (t.isAssignableFrom(String.class)) {
			return (T) rs.getString(columnIndex);
		}
		if (t.isAssignableFrom(Boolean.class)) {
			return (T) (rs.getBoolean(columnIndex) ? Boolean.TRUE : Boolean.FALSE);
		}
		if (t.isAssignableFrom(Timestamp.class)) {
			return (T) (rs.getTimestamp(columnIndex));
		}
		if (t.isAssignableFrom(Date.class)) {
			return (T) (rs.getDate(columnIndex));
		}

		if (t.isAssignableFrom(Boolean.class)) {
			return (T) (rs.getBoolean(columnIndex) ? Boolean.TRUE : Boolean.FALSE);
		}

		if (t.isAssignableFrom(Integer.class)) {
			Integer v = (Integer) rs.getInt(columnIndex);
			return (T) (rs.wasNull() ? null : v);
		}
		if (t.isAssignableFrom(Long.class)) {
			Long v = (Long) rs.getLong(columnIndex);
			return (T) (rs.wasNull() ? null : v);
		}
		if (t.isAssignableFrom(Float.class)) {
			return (T) (Float) rs.getFloat(columnIndex);
		}
		if (t.isAssignableFrom(Double.class)) {
			Double v = (Double) rs.getDouble(columnIndex);
			return (T) (rs.wasNull() ? null : v);
		}

		if (t.isAssignableFrom(BigDecimal.class)) {
			return (T) rs.getBigDecimal(columnIndex);
		}

		Object obj = rs.getObject(columnIndex);
		if (t.getClass().isAssignableFrom(obj.getClass())) {
			return (T) obj;
		}

		throw new IllegalArgumentException("Unsupported type (" + t.getClass() + "),"
				+ " only Date, Timestamp, String, Boolean, Integer, Long, Float, Double, BigDecimal"
				+ " and explicit assignable (java.lang.Class.isAssignableFrom) classes are supported!");
	}
	
	

	public <T> List<T> executeQuery(String sql, ResultSetRowMapper<T> mapper) throws SQLException
	{
		List<T> list = new ArrayList<T>();
		Connection conn = null;
		String debugStr = debugStr(sql);
		DBRoutineDebug debugObj = new DBRoutineDebug(logPrefix, debugStr, profiling);
		try
		{
			conn = getConnection();
			debugObj.connectionAcquired();
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setFetchSize(getFetchSize());
			debugObj.statementAcquired();
			debugObj.autocommit = conn.getAutoCommit();
			ResultSet rs = stmt.executeQuery();
			String[] headers = new String[rs.getMetaData().getColumnCount()];
			for(int i=1; i<=rs.getMetaData().getColumnCount(); i++)
			{
				headers[i-1] = rs.getMetaData().getColumnLabel(i).toUpperCase();
			}
			int rowNum = 0;
			while (rs.next())
			{
				list.add(mapper.mapRow(rs, rowNum++, headers));
			}
			rs.close();
			stmt.close();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			debugObj.end();
			debugObj.exception = e;
			log(debugObj);
			throw e;
		}
		finally
		{
			cleanup(conn);
		}
		if (profiling)
		{
			debugObj.end();
			log(debugObj);
		}
		return list;
	}

	public <T> Map<String, List<T>> executeQueryForMapList(String sql, ResultSetRowMapper<T> mapper) throws SQLException
	{
		return executeQueryForMapList(sql, mapper, 1);
	}

	public <T> Map<String, List<T>> executeQueryForMapList(String sql, ResultSetRowMapper<T> mapper, int keyIndex) throws SQLException
	{
		Map<String, List<T>> mapList = new LinkedHashMap<String, List<T>>();
		Connection conn = null;
		String debugStr = debugStr(sql);
		DBRoutineDebug debugObj = new DBRoutineDebug(logPrefix, debugStr, profiling);
		try
		{
			conn = getConnection();
			debugObj.connectionAcquired();
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setFetchSize(getFetchSize());
			debugObj.statementAcquired();
			debugObj.autocommit = conn.getAutoCommit();
			ResultSet rs = stmt.executeQuery();
			String[] headers = new String[rs.getMetaData().getColumnCount()];
			for(int i=1; i<=rs.getMetaData().getColumnCount(); i++)
			{
				headers[i-1] = rs.getMetaData().getColumnLabel(i).toUpperCase();
			}
			int rowNum = 0;
			while (rs.next())
			{
				String key = rs.getString(keyIndex);
				if (mapList.containsKey(key) == false)
					mapList.put(key, new ArrayList<T>());
				mapList.get(key).add(mapper.mapRow(rs, rowNum++, headers));
			}
			rs.close();
			stmt.close();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			debugObj.end();
			debugObj.exception = e;
			log(debugObj);
			throw e;
		}
		finally
		{
			cleanup(conn);
		}
		if (profiling)
		{
			debugObj.end();
			log(debugObj);
		}
		return mapList;
	}

	public List<Map<String, Object>> executeQueryForListMap(String sql) throws SQLException
	{
		return executeQueryForListMap(sql, new ArrayList<EnumMap<DBParam, Object>>());
	}

	public List<Map<String, Object>> executeQueryForListMap(String sql, List<EnumMap<DBParam, Object>> paramList) throws SQLException
	{
		return executeQueryForListMap(sql, paramList, -1, null);
	}

	public List<Map<String, Object>> executeQueryForListMap(String sql, List<EnumMap<DBParam, Object>> paramList, final int count, ResultSetChunkProcessor<List<Map<String, Object>>> processor) throws SQLException
	{
		List<Map<String, Object>> listMap = new ArrayList<Map<String, Object>>();
		Connection conn = null;
		String debugStr = debugStr(sql);
		DBRoutineDebug debugObj = new DBRoutineDebug(logPrefix, debugStr, profiling);
		try
		{
			conn = getConnection();
			debugObj.connectionAcquired();
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setFetchSize(getFetchSize());
			debugObj.statementAcquired();
			debugObj.autocommit = conn.getAutoCommit();
			int paramIndex = 1;
			for (EnumMap<DBParam, Object> param : paramList)
			{
				stmt.setObject(paramIndex++, (Object)param.get(DBParam.OBJECT), (Integer)param.get(DBParam.TYPE));
			}
			ResultSet rs = stmt.executeQuery();
			String[] headers = new String[rs.getMetaData().getColumnCount()];
			for(int i=1; i<=rs.getMetaData().getColumnCount(); i++)
			{
				headers[i-1] = rs.getMetaData().getColumnLabel(i).toUpperCase();
			}
			while (rs.next())
			{
				Map<String, Object> rowMap = new LinkedHashMap<String, Object>();
				for (int i=0; i<headers.length; i++)
				{
					rowMap.put(headers[i], rs.getObject(i+1));
				}
				listMap.add(rowMap);
				if ((count > 0) && (processor != null))
				{
					if (listMap.size() == count)
					{
						processor.processChunkResultSet(listMap);
						listMap.clear();
					}
				}
			}
			rs.close();
			stmt.close();
			if ((count > 0) && (processor != null))
			{
				if (listMap.size() > 0)
				{
					processor.processChunkResultSet(listMap);
					listMap.clear();
				}
			}
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			debugObj.end();
			debugObj.exception = e;
			log(debugObj);
			throw e;
		}
		finally
		{
			cleanup(conn);
		}
		if (profiling)
		{
			debugObj.end();
			log(debugObj);
		}
		return listMap;
	}

	public <T> List<T> executeQuery(String sql, ResultSetRowMapper<T> mapper, List<Object> paramList) throws SQLException
	{
		List<T> list = new ArrayList<T>();
		Connection conn = null;
		String debugStr = debugStrObjList(sql, paramList);
		DBRoutineDebug debugObj = new DBRoutineDebug(logPrefix, debugStr, profiling);
		try
		{
			conn = getConnection();
			debugObj.connectionAcquired();
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setFetchSize(getFetchSize());
			debugObj.statementAcquired();
			debugObj.autocommit = conn.getAutoCommit();
			int paramIndex = 1;
			for (Object param : paramList)
			{
				stmt.setObject(paramIndex++, param);
			}
			ResultSet rs = stmt.executeQuery();
			String[] headers = new String[rs.getMetaData().getColumnCount()];
			for(int i=1; i<=rs.getMetaData().getColumnCount(); i++)
			{
				headers[i-1] = rs.getMetaData().getColumnLabel(i).toUpperCase();
			}
			int rowNum = 0;
			while (rs.next())
			{
				list.add(mapper.mapRow(rs, rowNum++, headers));
			}
			rs.close();
			stmt.close();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			debugObj.end();
			debugObj.exception = e;
			log(debugObj);
			throw e;
		}
		finally
		{
			cleanup(conn);
		}
		if (profiling)
		{
			debugObj.end();
			log(debugObj);
		}
		return list;
	}

	public static enum DBParam { TYPE /* java.sql.Types.* */, OBJECT };
	public static enum INOUT_DBParam { IN_OUT /* 1=IN, 2=OUT, 3=IN_OUT */, TYPE /* java.sql.Types.* */, OBJECT  };

	public <T> List<T> executeQueryTypedParam(String sql, ResultSetRowMapper<T> mapper, List<EnumMap<DBParam, Object>> paramList) throws SQLException
	{
		List<T> list = new ArrayList<T>();
		Connection conn = null;
		String debugStr = debugStr(sql, paramList);
		DBRoutineDebug debugObj = new DBRoutineDebug(logPrefix, debugStr, profiling);
		try
		{
			conn = getConnection();
			debugObj.connectionAcquired();
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setFetchSize(getFetchSize());
			debugObj.statementAcquired();
			debugObj.autocommit = conn.getAutoCommit();
			int paramIndex = 1;
			for (EnumMap<DBParam, Object> param : paramList)
			{
				stmt.setObject(paramIndex++, (Object)param.get(DBParam.OBJECT), (Integer)param.get(DBParam.TYPE));
			}
			ResultSet rs = stmt.executeQuery();
			String[] headers = new String[rs.getMetaData().getColumnCount()];
			for(int i=1; i<=rs.getMetaData().getColumnCount(); i++)
			{
				headers[i-1] = rs.getMetaData().getColumnLabel(i).toUpperCase();
			}
			int rowNum = 0;
			while (rs.next())
			{
				list.add(mapper.mapRow(rs, rowNum++, headers));
			}
			rs.close();
			stmt.close();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			debugObj.end();
			debugObj.exception = e;
			log(debugObj);
			throw e;
		}
		finally
		{
			cleanup(conn);
		}
		if (profiling)
		{
			debugObj.end();
			log(debugObj);
		}
		return list;
	}

	public <T> List<Long> executeBatchWithGeneratedKeys(String sql, List<T> list, BatchParameterSetter<T> paramSetter) throws SQLException
	{
		List<Long> keyList = new ArrayList<Long>();
		Connection conn = null;
		String debugStr = debugStr(sql);
		DBRoutineDebug debugObj = new DBRoutineDebug(logPrefix, debugStr, profiling);
		try
		{
			conn = getConnection();
			debugObj.connectionAcquired();
			PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			debugObj.statementAcquired();
			debugObj.autocommit = conn.getAutoCommit();
			int count = 0;
			for (T obj : list)
			{
				count++;
				paramSetter.setParam(stmt, count, obj);
				stmt.addBatch();
				if (count % getBatchThreshold() == 0)
				{
					stmt.executeBatch();
				}
			}
			if (count % getBatchThreshold() != 0)
			{
				stmt.executeBatch();
			}
			ResultSet rs = stmt.getGeneratedKeys();
			while (rs.next())
			{
				keyList.add(rs.getLong(1));
			}
			rs.close();
			stmt.close();
			commit(conn);
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			debugObj.end();
			debugObj.exception = e;
			log(debugObj);
			throw e;
		}
		finally
		{
			cleanup(conn);
		}
		if (profiling)
		{
			debugObj.end();
			log(debugObj);
		}
		return keyList;
	}

	public <T> void executeBatch(String sql, List<T> list, BatchParameterSetter<T> paramSetter) throws SQLException
	{
		executeBatch(sql, list, paramSetter, null, null);
	}

	public <T> void executeBatch(String sql, List<T> list, BatchParameterSetter<T> paramSetter, String seqSql, SequenceSetter<T> seqSetter) throws SQLException
	{
		Connection conn = null;
		String debugStr = debugStr(sql);
		DBRoutineDebug debugObj = new DBRoutineDebug(logPrefix, debugStr, profiling);
		try
		{
			conn = getConnection();
			debugObj.connectionAcquired();
			PreparedStatement stmt = conn.prepareStatement(sql);
			debugObj.statementAcquired();
			debugObj.autocommit = conn.getAutoCommit();
			int count = 0;
			for (T obj : list)
			{
				count++;
				if (seqSql != null)
				{
					Statement seqStmt = conn.createStatement();
					ResultSet rs = seqStmt.executeQuery(seqSql);
					rs.next();
					Long seqNumber = rs.getLong(1);
					rs.close();
					seqStmt.close();
					seqSetter.setSequence(obj, seqNumber);
				}
				paramSetter.setParam(stmt, count, obj);
				stmt.addBatch();
				if (count % getBatchThreshold() == 0)
				{
					stmt.executeBatch();
				}
			}
			if (count % getBatchThreshold() != 0)
			{
				stmt.executeBatch();
			}
			stmt.close();
			commit(conn);
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			debugObj.end();
			debugObj.exception = e;
			log(debugObj);
			throw e;
		}
		finally
		{
			cleanup(conn);
		}
		if (profiling)
		{
			debugObj.end();
			log(debugObj);
		}
	}

	public <T, T1> void executeNestedBatch(String batchUpdateSql1, List<T> list, BatchParameterSetter<T> paramSetter1, String batchUpdateSql2, ListFetcher<T, T1> fetcher, NestedBatchParameterSetter<T, T1> paramSetter2) throws SQLException
	{
		executeNestedBatch(batchUpdateSql1, list, paramSetter1, batchUpdateSql2, fetcher, paramSetter2, null, null);
	}

	public <T, T1> void executeNestedBatch(String batchUpdateSql1, List<T> list, BatchParameterSetter<T> paramSetter1, String batchUpdateSql2, ListFetcher<T, T1> fetcher, NestedBatchParameterSetter<T, T1> paramSetter2, String seqSql, SequenceSetter<T> seqSetter) throws SQLException
	{
		Connection conn = null;
		String debugStr = debugStr(batchUpdateSql1, batchUpdateSql2);
		DBRoutineDebug debugObj = new DBRoutineDebug(logPrefix, debugStr, profiling);
		try
		{
			conn = getConnection();
			debugObj.connectionAcquired();
			PreparedStatement stmt1 = conn.prepareStatement(batchUpdateSql1);
			PreparedStatement stmt2 = conn.prepareStatement(batchUpdateSql2);
			debugObj.statementAcquired();
			debugObj.autocommit = conn.getAutoCommit();
			int count1 = 0;
			int count2 = 1;
			for (T obj : list)
			{
				count1++;
				if (seqSql != null)
				{
					Statement seqStmt = conn.createStatement();
					ResultSet rs = seqStmt.executeQuery(seqSql);
					rs.next();
					Long seqNumber = rs.getLong(1);
					rs.close();
					seqStmt.close();
					seqSetter.setSequence(obj, seqNumber);
				}
				paramSetter1.setParam(stmt1, count1, obj);
				stmt1.addBatch();
				List<T1> subList = fetcher.fetch(obj);
				for (T1 subObj : subList)
				{
					paramSetter2.setParam(stmt2, count2++, obj, subObj);
					stmt2.addBatch();
				}
				if (count1 % getBatchThreshold() == 0)
				{
					stmt1.executeBatch();
					stmt2.executeBatch();
				}
			}
			if (count1 % getBatchThreshold() != 0)
			{
				stmt1.executeBatch();
				stmt2.executeBatch();
			}
			stmt1.close();
			stmt2.close();
			commit(conn);
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			debugObj.end();
			debugObj.exception = e;
			log(debugObj);
			throw e;
		}
		finally
		{
			cleanup(conn);
		}
		if (profiling)
		{
			debugObj.end();
			log(debugObj);
		}
	}

	public <T> int executeUpdate(String sql, T obj, BatchParameterSetter<T> paramSetter) throws SQLException
	{
		int update = -1;
		Connection conn = null;
		String debugStr = debugStr(sql);
		DBRoutineDebug debugObj = new DBRoutineDebug(logPrefix, debugStr, profiling);
		try
		{
			conn = getConnection();
			debugObj.connectionAcquired();
			PreparedStatement stmt = conn.prepareStatement(sql);
			debugObj.statementAcquired();
			debugObj.autocommit = conn.getAutoCommit();
			paramSetter.setParam(stmt, 1, obj);
			update = stmt.executeUpdate();
			stmt.close();
			commit(conn);
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			debugObj.end();
			debugObj.exception = e;
			log(debugObj);
			throw e;
		}
		finally
		{
			cleanup(conn);
		}
		if (profiling)
		{
			debugObj.end();
			log(debugObj);
		}
		return update;
	}

	public int executeUpdate(String sql, List<EnumMap<DBParam, Object>> paramList) throws SQLException
	{
		int update = -1;
		Connection conn = null;
		String debugStr = debugStr(sql, paramList);
		DBRoutineDebug debugObj = new DBRoutineDebug(logPrefix, debugStr, profiling);
		try
		{
			conn = getConnection();
			debugObj.connectionAcquired();
			PreparedStatement stmt = conn.prepareStatement(sql);
			debugObj.statementAcquired();
			debugObj.autocommit = conn.getAutoCommit();
			int paramIndex = 1;
			for (EnumMap<DBParam, Object> param : paramList)
			{
				stmt.setObject(paramIndex++, (Object)param.get(DBParam.OBJECT), (Integer)param.get(DBParam.TYPE));
			}
			update = stmt.executeUpdate();
			stmt.close();
			commit(conn);
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			debugObj.end();
			debugObj.exception = e;
			log(debugObj);
			throw e;
		}
		finally
		{
			cleanup(conn);
		}
		if (profiling)
		{
			debugObj.end();
			log(debugObj);
		}
		return update;
	}

	public int executeUpdateObjectParamList(String sql, List<Object> paramList) throws SQLException
	{
		int update = -1;
		Connection conn = null;
		String debugStr = debugObjListStr(sql, paramList);
		DBRoutineDebug debugObj = new DBRoutineDebug(logPrefix, debugStr, profiling);
		try
		{
			conn = getConnection();
			debugObj.connectionAcquired();
			PreparedStatement stmt = conn.prepareStatement(sql);
			debugObj.statementAcquired();
			debugObj.autocommit = conn.getAutoCommit();
			int paramIndex = 1;
			for (Object param : paramList)
			{
				stmt.setObject(paramIndex++, param);
			}
			update = stmt.executeUpdate();
			stmt.close();
			commit(conn);
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			debugObj.end();
			debugObj.exception = e;
			log(debugObj);
			throw e;
		}
		finally
		{
			cleanup(conn);
		}
		if (profiling)
		{
			debugObj.end();
			log(debugObj);
		}
		return update;
	}

	/*
	 * for sp which returns resultset
	 */
	public <T> StoredProcResult<T> executeQueryStoredProc(String sql, ResultSetRowMapper<T> mapper, List<EnumMap<INOUT_DBParam, Object>> paramList) throws SQLException
	{
		StoredProcResult<T> returnObj = new StoredProcResult<T>();
		Connection conn = null;
		String debugStr = debugStrForSP(sql, paramList);
		DBRoutineDebug debugObj = new DBRoutineDebug(logPrefix, debugStr, profiling);
		try
		{
			conn = getConnection();
			debugObj.connectionAcquired();
            CallableStatement cs = conn.prepareCall(sql);
			cs.setFetchSize(getFetchSize());
			debugObj.statementAcquired();
			debugObj.autocommit = conn.getAutoCommit();
			int paramIndex = 1;
			for (EnumMap<INOUT_DBParam, Object> param : paramList)
			{
				int paramType = (Integer)param.get(INOUT_DBParam.IN_OUT);
				if ((paramType == 2) || (paramType == 3))
					cs.registerOutParameter(paramIndex, (Integer)param.get(INOUT_DBParam.TYPE));
				if ((paramType == 1) || (paramType == 3))
					cs.setObject(paramIndex, (Object)param.get(INOUT_DBParam.OBJECT), (Integer)param.get(INOUT_DBParam.TYPE));
				paramIndex++;
			}
			ResultSet rs = cs.executeQuery();
			String[] headers = new String[rs.getMetaData().getColumnCount()];
			for(int i=1; i<=rs.getMetaData().getColumnCount(); i++)
			{
				headers[i-1] = rs.getMetaData().getColumnLabel(i).toUpperCase();
			}
			int rowNum = 0;
			while (rs.next())
			{
				returnObj.list.add(mapper.mapRow(rs, rowNum++, headers));
			}
			paramIndex = 1;
			for (EnumMap<INOUT_DBParam, Object> param : paramList)
			{
				int paramType = (Integer)param.get(INOUT_DBParam.IN_OUT);
				if ((paramType == 2) || (paramType == 3))
				{
					returnObj.outParamMap.put(paramIndex, cs.getObject(paramIndex));
				}
				paramIndex++;
			}
			rs.close();
			cs.close();
			commit(conn);
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			debugObj.end();
			debugObj.exception = e;
			log(debugObj);
			throw e;
		}
		finally
		{
			cleanup(conn);
		}
		if (profiling)
		{
			debugObj.end();
			log(debugObj);
		}
		return returnObj;
	}

	public <T> StoredProcResult<T> executeUpdateStoredProc(String sql, List<EnumMap<INOUT_DBParam, Object>> paramList) throws SQLException
	{
		StoredProcResult<T> returnObj = new StoredProcResult<T>();
		Connection conn = null;
		String debugStr = debugStrForSP(sql, paramList);
		DBRoutineDebug debugObj = new DBRoutineDebug(logPrefix, debugStr, profiling);
		try
		{
			conn = getConnection();
			debugObj.connectionAcquired();
            CallableStatement cs = conn.prepareCall(sql);
			debugObj.statementAcquired();
			debugObj.autocommit = conn.getAutoCommit();
			int paramIndex = 1;
			for (EnumMap<INOUT_DBParam, Object> param : paramList)
			{
				int paramType = (Integer)param.get(INOUT_DBParam.IN_OUT);
				if ((paramType == 2) || (paramType == 3))
					cs.registerOutParameter(paramIndex, (Integer)param.get(INOUT_DBParam.TYPE));
				if ((paramType == 1) || (paramType == 3))
					cs.setObject(paramIndex, (Object)param.get(INOUT_DBParam.OBJECT), (Integer)param.get(INOUT_DBParam.TYPE));
				paramIndex++;
			}
			returnObj.executeUpdateReturn = cs.executeUpdate();
			paramIndex = 1;
			for (EnumMap<INOUT_DBParam, Object> param : paramList)
			{
				int paramType = (Integer)param.get(INOUT_DBParam.IN_OUT);
				if ((paramType == 2) || (paramType == 3))
				{
					returnObj.outParamMap.put(paramIndex, cs.getObject(paramIndex));
				}
				paramIndex++;
			}
			cs.close();
			commit(conn);
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			debugObj.end();
			debugObj.exception = e;
			log(debugObj);
			throw e;
		}
		finally
		{
			cleanup(conn);
		}
		if (profiling)
		{
			debugObj.end();
			log(debugObj);
		}
		return returnObj;
	}

	public int getBatchThreshold()
	{
		return batchThreshold;
	}

	public void setBatchThreshold(int batchThreshold)
	{
		this.batchThreshold = batchThreshold;
	}

	public int getFetchSize()
	{
		return fetchSize;
	}

	public void setFetchSize(int fetchSize)
	{
		this.fetchSize = fetchSize;
	}

	public void setProfiling(boolean profiling)
	{
		this.profiling = profiling;
	}

	public void setLogPrefix(String prefix)
	{
		this.logPrefix = prefix;
	}

	public void setLogger(Logger logger)
	{
		this.myLogger = logger;
	}

	private void log(DBRoutineDebug debugObj)
	{
		if (debugObj.exception != null)
			log.error(debugObj.logPrefix + debugObj.sqlString, debugObj.exception);
		else
			log.info(debugObj.toString());
		if (myLogger != null)
		{
			try
			{
				if (debugObj.exception == null)
					myLogger.info(debugObj);
				else
					myLogger.error(debugObj);
			}
			catch (Throwable t) {};
		}
	}

	/**
	 * Handy method to handle null value.
	 *          (Thanks David Kant)
	 * @param rs
	 * @param column
	 * @return
	 * @throws SQLException
	 */
	public static Double toDouble(ResultSet rs, String column) throws SQLException
	{
		double d = rs.getDouble(column);
		if (rs.wasNull())
		{
			return null;
		}
		
		return new Double(d);
	}

	public static Long toLong(ResultSet rs, String column) throws SQLException
	{
		long d = rs.getLong(column);
		if (rs.wasNull())
		{
			return null;
		}
		
		return new Long(d);
	}

	public static Integer toInteger(ResultSet rs, String column) throws SQLException
	{
		int d = rs.getInt(column);
		if (rs.wasNull())
		{
			return null;
		}
		
		return new Integer(d);
	}
	
	public static void setDouble(PreparedStatement ps, int index, Double d) throws SQLException
	{
		if (d == null)
		{
			ps.setNull(index, java.sql.Types.DOUBLE);
		}
		else
		{
			ps.setDouble(index, d);
		}
	}

	public static void setString(PreparedStatement ps, int index, String s) throws SQLException
	{
		if (s == null)
		{
			ps.setNull(index, java.sql.Types.VARCHAR);
		}
		else
		{
			ps.setString(index, s);
		}
	}

	public static void setLong(PreparedStatement ps, int index, Long l) throws SQLException
	{
		if (l == null)
		{
			ps.setNull(index, java.sql.Types.BIGINT);
		}
		else
		{
			ps.setLong(index, l);
		}
	}

	public static void setInteger(PreparedStatement ps, int index, Integer i) throws SQLException
	{
		if (i == null)
		{
			ps.setNull(index, java.sql.Types.INTEGER);
		}
		else
		{
			ps.setInt(index, i);
		}
	}
	
	public static void setDate(PreparedStatement ps, int index, java.sql.Date d) throws SQLException
	{
		if (d == null)
		{
			ps.setNull(index, java.sql.Types.DATE);
		}
		else
		{
			ps.setDate(index, d);
		}
	}
	
	private String debugStr(String sql)
	{
		if (profiling == false) {
			return "";
		}
		
		return "{sql=" + sql + "}";
	}

	private String debugStr(String sql1, String sql2)
	{
		if (profiling == false) {
			return "";
		}
		
		return "{sql1=" + sql1 + ", sql2=" + sql2 + "}";
	}

	private String debugStr(String sql, List<EnumMap<DBParam, Object>> paramList)
	{
		if (profiling == false) {
			return "";
		}

		String debugStr = "{sql=" + sql + ", param={";
		for (int i=0; i<paramList.size()-1; i++)
		{
			EnumMap<DBParam, Object> param = paramList.get(i);
			if (param != null) {
				debugStr += "(" + (Integer) param.get(DBParam.TYPE) + " " + (Object) param.get(DBParam.OBJECT) + "), ";
			} else {
				debugStr += "(null), ";
			}
		}
		if (paramList.size() > 0)
			debugStr += "(" + (Integer)paramList.get(paramList.size()-1).get(DBParam.TYPE) + " " + (Object)paramList.get(paramList.size()-1).get(DBParam.OBJECT) + ")";
		debugStr += "}}";
		return debugStr;
	}

	private String debugObjListStr(String sql, List<Object> paramList)
	{
		if (profiling == false) {
			return "";
		}

		String debugStr = "{sql=" + sql + ", param={";
		for (int i=0; i<paramList.size()-1; i++)
		{
			Object param = paramList.get(i);
			debugStr += "(" + param + "), ";
		}
		if (paramList.size() > 0)
			debugStr += "(" + paramList.get(paramList.size()-1) + ")";
		debugStr += "}}";
		return debugStr;
	}

	private String debugStrForSP(String sql, List<EnumMap<INOUT_DBParam, Object>> paramList) {
		if (profiling == false) {
			return "";
		}

		String debugStr = "{sql=" + sql + ", param={";
		for (int i = 0; i < paramList.size() - 1; i++) {
			debugStr += "(";
			EnumMap<INOUT_DBParam, Object> param = paramList.get(i);
			if (param != null) {
				int paramType = (Integer) param.get(INOUT_DBParam.IN_OUT);
				if ((paramType == 2) || (paramType == 3))
					debugStr += "OUT";
				if ((paramType == 1) || (paramType == 3))
					debugStr += "IN";

				debugStr += " " + (Integer) param.get(INOUT_DBParam.TYPE) + " " + (Object) param.get(INOUT_DBParam.OBJECT) + "), ";
			} else {
				debugStr += " null), ";
			}
		}
		if (paramList.size() > 0) {
			debugStr += "(";
			EnumMap<INOUT_DBParam, Object> param = paramList.get(paramList.size() - 1);
			if (param != null) {
				int paramType = (Integer) param.get(INOUT_DBParam.IN_OUT);
				if ((paramType == 2) || (paramType == 3))
					debugStr += "OUT";
				if ((paramType == 1) || (paramType == 3))
					debugStr += "IN";
				debugStr += " " + (Integer) param.get(INOUT_DBParam.TYPE) + " " + (Object) param.get(INOUT_DBParam.OBJECT) + ")";
			} else {
				debugStr += " null), ";
			}
		}
		debugStr += "}}";
		return debugStr;
	}

	private String debugStrObjList(String sql, List<Object> paramList)
	{
		if (profiling == false) {
			return "";
		}

		String debugStr = "{sql=" + sql + ", param={";
		for (int i=0; i<paramList.size()-1; i++)
		{
			debugStr += "(" + paramList.get(i) + "), ";
		}
		if (paramList.size() > 0)
			debugStr += "(" + paramList.get(paramList.size()-1) + ")";
		debugStr += "}}";
		return debugStr;
	}
	
	
	public void commit(Connection conn) throws SQLException
	{
		if (conn.getAutoCommit() == false)
			conn.commit();
	}

	/**
	 * Handy method to create a DBParam for specified type and value
	 * 
	 * @param type
	 *            java.sql.Types (int)
	 * @param value
	 * @return
	 */
	public static EnumMap<DBParam, Object> createParam(int/* java.sql.Types */type, Object value) {
		EnumMap<DBParam, Object> param = new EnumMap<DBRoutine.DBParam, Object>(DBParam.class);
		param.put(DBParam.TYPE, type);
		if (java.sql.Types.DATE == type && (value instanceof java.util.Date)) {
			value = new java.sql.Date(((java.util.Date) value).getTime());
		}
		param.put(DBParam.OBJECT, value);
		return param;
	}

	/**
	 * Handy method to create DBParams by auto discovering java.sql.Types for primitive classes<br>
	 * All the Number classes, Character, String, Boolean, Date and Timestamp are supported
	 * 
	 * @param value
	 * @return
	 */
	public static EnumMap<DBParam, Object> createParam(Object value) {
		if (value == null) {
			int type = java.sql.Types.NULL;
			
			String dn = System.getProperty(DEFAULT_NULL_TYPE_SYSPROP_NAME);
			if(dn != null) {
				if(dn.equalsIgnoreCase("char") || dn.equalsIgnoreCase("varchar")) {
					type = java.sql.Types.CHAR;
				} else if(dn.equalsIgnoreCase("numeric")) {
					type = java.sql.Types.NUMERIC;
				} else if(dn.equalsIgnoreCase("boolean")) {
					type = java.sql.Types.BOOLEAN;
				} else if(dn.equalsIgnoreCase("date")) {
					type = java.sql.Types.DATE;
				} else if(dn.equalsIgnoreCase("null")) {
					type = java.sql.Types.NULL;
				} else {
					log.error("Unspported value [" + dn + "] provided for property [" + DEFAULT_NULL_TYPE_SYSPROP_NAME
							+ "] valid values are [Char, Varchar, Numeric, Boolean, Date and Null]");
				}
			}
			return createParam(type, null);
		}

		int type = java.sql.Types.VARCHAR;
		if (Number.class.isAssignableFrom(value.getClass())) {
			type = java.sql.Types.NUMERIC;
		} else if (String.class.isAssignableFrom(value.getClass())) {
			type = java.sql.Types.VARCHAR;
		} else if (Character.class.isAssignableFrom(value.getClass())) {
			type = java.sql.Types.CHAR;
		} else if (Boolean.class.isAssignableFrom(value.getClass())) {
			type = java.sql.Types.BOOLEAN;
		} else if (Timestamp.class.isAssignableFrom(value.getClass())) {
			type = java.sql.Types.TIMESTAMP;
		} else if (Date.class.isAssignableFrom(value.getClass())) {
			// This should be after Timestamp check as Date is also assignable from Timestamp
			type = java.sql.Types.DATE;
		} else {
			throw new RuntimeException("Unsupported object :" + Object.class);
		}

		return createParam(type, value);
	}
	
	
}
