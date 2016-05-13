package com.concept.crew.util;

import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Non-Transactional dbroutine implementation
 *
 */
public class DBRoutinePool extends DBRoutine
{
	private static final Logger log = Logger.getLogger(DBRoutinePool.class);

	DataSourceProvider provider = null;

	/**
	 * @param id
	 */
	public DBRoutinePool(String id)
	{
		provider = DataSourceProviderManager.getDataSourceProvider(id);
	}

	/**
	 * @param dsp
	 */
	public DBRoutinePool(DataSourceProvider dsp)
	{
		provider = dsp;
	}
	
	/* (non-Javadoc)
	 * @see com.markit.credit.dbroutine.DBRoutine#getConnection()
	 */
	public Connection getConnection() throws SQLException
	{
		return provider.getDataSource().getConnection(provider.getDatabaseUser(), provider.getDatabasePassword());
	}

	/* (non-Javadoc)
	 * @see com.markit.credit.dbroutine.DBRoutine#cleanup(java.sql.Connection)
	 */
	public void cleanup(Connection conn)
	{
		try { if (conn.getAutoCommit() == false) conn.rollback(); }
		catch (Throwable e) {
			log.info("Exception while rolling back during cleanup" + e);
		}
		try { conn.close(); }
		catch (Throwable e) {
			log.info("Exception while closing connection" + e);
		}
	}
	
	/* (non-Javadoc)
	 * @see com.markit.credit.dbroutine.DBRoutine#debugStr()
	 */
	public String debugStr()
	{
		return provider.getInfo();
	}

    @Override
    public int getNumActiveConn()
    {
        return provider.getNumActiveConn();
    }

    @Override
    public int getMaxActiveConn()
    {
        return provider.getMaxActiveConn();
    }

    @Override
    public int getNumIdleConn()
    {
        return provider.getNumIdleConn();
    }

    @Override
    public int getMaxIdleConn()
    {
        return provider.getMaxIdleConn();
    }

    @Override
    public int getMaxWait()
    {
        return provider.getMaxWait();
    }

}
