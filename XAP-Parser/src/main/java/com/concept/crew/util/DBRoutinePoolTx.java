package com.concept.crew.util;

import java.sql.Connection;
import java.sql.SQLException;

import org.apache.log4j.Logger;

/**
 * DBRoutine implementation with transaction support.
 *
 */
public class DBRoutinePoolTx extends DBRoutinePool
{
	private static final Logger log = Logger.getLogger(DBRoutinePoolTx.class);

	private Connection conn = null;
	public DBRoutinePoolTx(String tag)
	{
		super(tag);
	}

	public Connection getConnection() throws SQLException
	{
		return conn;
	}

	public void beginTransaction() throws SQLException
	{
		if (conn != null)
		{
			super.cleanup(conn);
			conn = null;
		}
		conn = provider.getDataSource().getConnection(provider.getDatabaseUser(), provider.getDatabasePassword());
		conn.setAutoCommit(false);
	}

	public void rollbackTransaction() throws SQLException
	{
		try {
			if (conn != null)
				conn.rollback();
		} finally {
			try { if (conn != null) conn.close(); }
			catch (Throwable e) {
				log.info("Exception while closing connection" + e);
			}
			conn = null;
		}
	}

	public void commitTransaction() throws SQLException
	{
		try {
			conn.commit();
		} finally {
			try { conn.rollback(); }
			catch (Throwable e) {
				log.info("Exception while rolling back transaction" + e);
			}
			try { conn.close(); }
			catch (Throwable e) {
				log.info("Exception while closing connection" + e);
			}
			conn = null;
		}
	}

	@Override
	public void cleanup(Connection conn)
	{
	}

	@Override
	public void commit(Connection conn) throws SQLException
	{
		//Do nothing.
	}
}
