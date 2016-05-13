package com.concept.crew.util;

import java.sql.SQLException;

public class DBRoutineDebug
{
	public final String logPrefix;
	public final String sqlString;
	public long milliSeconds;
	public long connectionMilliSeconds;
	public long stmtMilliSeconds;
	public final long startTime;
	public long endTime;
	public SQLException exception;
	public boolean autocommit;
	public boolean profiling = true;

	public DBRoutineDebug(String logPrefix, String sqlString)
	{
		this.logPrefix = logPrefix;
		this.sqlString = sqlString;
		this.startTime = System.currentTimeMillis();
	}

	public DBRoutineDebug(String logPrefix, String sqlString, boolean profiling)
	{
		this.logPrefix = logPrefix;
		this.sqlString = sqlString;
		this.startTime = System.currentTimeMillis();
		this.profiling = profiling;
	}

	public void connectionAcquired()
	{
		if(profiling)
			connectionMilliSeconds = System.currentTimeMillis() - startTime;
	}

	public void statementAcquired()
	{
		if(profiling)
			stmtMilliSeconds = System.currentTimeMillis() - startTime - connectionMilliSeconds;
	}

	public void end()
	{
		if (profiling) {
			endTime = System.currentTimeMillis();
			milliSeconds = endTime - startTime;
		}
	}

	@Override
	public String toString()
	{
		if(!profiling) 
			return "";
		
		return logPrefix + sqlString + " took " + milliSeconds + " ms, getConnection took " + connectionMilliSeconds + " ms, prepareStatement took " + stmtMilliSeconds + " ms autocommit=" + autocommit;
	}
}
