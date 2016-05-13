package com.concept.crew.util;

import org.apache.commons.dbcp.cpdsadapter.DriverAdapterCPDS;
import org.apache.commons.dbcp.datasources.SharedPoolDataSource;
import org.apache.log4j.Logger;

import javax.sql.DataSource;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Apache DBCP based implementation for {@link DataSourceProvider}
 */
public class ConnectionPool implements DataSourceProvider
{
	private static Logger log = Logger.getLogger(ConnectionPool.class);
	private String VALIDATION_QUERY;
	private String jdbcURL;
	private String dbUser;
	private String dbPass;
	private String jdbcdriver;
	private String dbType;
	private final SharedPoolDataSource ds;
	private final String tag;
	private boolean autocommit = false;
	
	private ConnectionPool(String tag, Properties properties) throws ClassNotFoundException
	{
		this.tag = tag;
		log.info(properties);
		// properties.list(System.out);
		jdbcURL = properties.getProperty("JDBCURL");
		dbUser = properties.getProperty("DBUSER");
		dbPass = properties.getProperty("DBPASS");
		jdbcdriver = properties.getProperty("JDBC_DRIVER");
		VALIDATION_QUERY = properties.getProperty("VALIDATION_QUERY");
		
		dbType = properties.getProperty("DB_TYPE");
		if(dbType==null){
			dbType = "ORACLE";
		}
		if(dbType.equalsIgnoreCase("ORACLE")&&jdbcdriver==null){
			jdbcdriver = "oracle.jdbc.driver.OracleDriver";
		}
		if(dbType.equalsIgnoreCase("MSSQL")&&jdbcdriver==null){
			jdbcdriver = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
		}

		if(dbType.equalsIgnoreCase("ORACLE")&&VALIDATION_QUERY==null){
			VALIDATION_QUERY = "select sysdate from dual";
		} 
		if(dbType.equalsIgnoreCase("MSSQL")&&VALIDATION_QUERY==null){
			VALIDATION_QUERY = "select GETDATE()";
		} 
		
		log.info("jdbcURL = "+jdbcURL+" : dbUser = "+dbUser+" : dbPass = "+dbPass+" : jdbcdriver = "+jdbcdriver+" : VALIDATION_QUERY = "+VALIDATION_QUERY+" : dbType = "+dbType);
		String programName = properties.getProperty("PROGRAM_NAME");

		DriverAdapterCPDS cpds = new DriverAdapterCPDS();
		cpds.setDriver(jdbcdriver);
        cpds.setUrl(jdbcURL);
		if (programName == null || "".equals(programName))
			programName= "DBRoutine connection";
		programName += " <";
		try {
			if (InetAddress.getLocalHost() != null && InetAddress.getLocalHost().getHostName() != null){
				programName += InetAddress.getLocalHost().getHostName();
			}
		} catch (UnknownHostException ignored) {
			programName += "UnknownHost";
		}
		programName += ">";
        Properties connProp = new Properties();
        //Length of v$session.program is 48
        if (programName.length() > 48)
        	programName = programName.substring(0, 47);
        log.info("programName: " + programName);
        connProp.put("v$session.program", programName);
        cpds.setConnectionProperties(connProp);

        SharedPoolDataSource tds = new SharedPoolDataSource();
        tds.setConnectionPoolDataSource(cpds);
        int maxConnections = 30;
        try {
        	maxConnections = Integer.parseInt(properties.getProperty("MAX_CONNECTIONS"));
        }catch(NumberFormatException e) {
        }
        int maxIdleConnections = 8;
        try {
        	maxIdleConnections = Integer.parseInt(properties.getProperty("MAX_IDLE_CONNECTIONS"));
        }catch(NumberFormatException e) {
        }
        int maxWait = 10 * 60 * 1000;
        try {
        	maxWait = Integer.parseInt(properties.getProperty("MAX_CONNECTION_WAIT_TIME"));
        }catch(NumberFormatException e) {
        }
    	autocommit = Boolean.parseBoolean(properties.getProperty("AUTOCOMMIT"));

        tds.setMaxActive(maxConnections);
        tds.setMaxWait(maxWait);
        tds.setValidationQuery(VALIDATION_QUERY);
        tds.setMaxIdle(maxIdleConnections);
        try {
        	int minEvictableIdleTimeMillis = Integer.parseInt(properties.getProperty("MIN_EVICTABLE_IDLE_TIME_MILLIS"));
            tds.setTimeBetweenEvictionRunsMillis(24 * 60 * 60 * 1000); //run evictor every 24 hours
            tds.setMinEvictableIdleTimeMillis(minEvictableIdleTimeMillis);
            tds.setNumTestsPerEvictionRun((maxConnections/2)+1);
        } catch (NumberFormatException e) {
        }
        tds.setTestOnBorrow(true);
        tds.setDefaultAutoCommit(autocommit);

        ds = tds;

        log.info(debugStr());
        //System.out.println(debugStr());
	}

	/**
	 * Create and Register DBCP connection pool with the supplied parameters
	 * 
	 * @deprecated Use {@link DataSourceProviderManager} instead
	 * @param tag
	 * @param properties
	 */
	public static synchronized void initialize(String tag, Properties properties)
	{
		if (DataSourceProviderManager.getDataSourceProvider(tag) != null)
			return;
		try
		{
			ConnectionPool pool = new ConnectionPool(tag, properties);
			DataSourceProviderManager.register(pool);
		}
		catch(Exception e)
		{
			log.error("Could not initialize connection pool for " + tag + " : ", e);
			e.printStackTrace();
			//Throw an unchecked exception - there's not much we can do if for some
			//reason the OracleDriver is not found
			throw new IllegalStateException("Could not initialize connection pool for " + tag, e);
		}
	}

	/**
	 * Create and Register DBCP connection pool with the supplied parameters
	 * 
	 * @deprecated Use {@link DataSourceProviderManager} instead
	 * @param tag
	 * @param systemPropertyFileName
	 */
	public static synchronized void initialize(String tag, String systemPropertyFileName)
	{
		if (DataSourceProviderManager.getDataSourceProvider(tag) != null)
			return;
		String propFileName = System.getProperty(systemPropertyFileName);
		log.info(systemPropertyFileName + " => " + propFileName);
		try
		{
			Properties properties = new Properties();
			InputStream is = new FileInputStream(propFileName);
			properties.load(is);
			initialize(tag, properties);
		}
		catch(Exception e)
		{
			log.error("Could not initialize connection pool for " + tag + " : ", e);
			e.printStackTrace();
			//Throw an unchecked exception - there's not much we can do if for some
			//reason the OracleDriver is not found
			throw new IllegalStateException("Could not initialize connection pool for " + tag, e);
		}
	}

	/**
	 * Returns data source provider
	 * 
	 * @deprecated use {@link DataSourceProviderManager} instead
	 * @see com.markit.credit.dbroutine.DataSourceProviderManager#getDataSourceProvider(String)
	 * @param tag
	 * @return
	 */
	public static synchronized ConnectionPool getInstance(String tag)
	{
		DataSourceProvider obj = DataSourceProviderManager.getDataSourceProvider(tag);
		if (obj instanceof ConnectionPool) {
			return (ConnectionPool) obj;
		}
		return null;
	}

	/**
	 * @return
	 * @throws SQLException
	 */
	public Connection getConnection() throws SQLException
	{
		Connection conn = getConnection(dbUser, dbPass);
		if (conn == null)
		{
			log.error("Could not get connection from DB Pool");
			return null;
		}
		return conn;
	}
	
	/**
	 * @param dbUser
	 * @param dbPass
	 * @return
	 * @throws SQLException
	 */
	private Connection getConnection(String dbUser, String dbPass) throws SQLException
	{
		Connection conn  = null;
		if(dbUser==null){
			conn =  ds.getConnection();
		} else {
			conn = ds.getConnection(dbUser, dbPass);
		}
		return conn;
	}
	
	public String debugStr()
	{
		return "tag => " + tag + "\n" + 
		       "jdbc-url => " + jdbcURL + "\n" +
		       "dbUser => " + dbUser + "\n" +
		       "connection-pool { " + "\n" +
		       "                  pool => " + (ds == null ? "TOMCAT-JDBC" : "DBCP") + "\n" +
		       (poolDebug(ds)) +
		       "                }" + "\n"
		       ;
	}
	

	private Object poolDebug(SharedPoolDataSource ds1)
	{
		return (ds1 == null ? "" :
	       (
	       "                  active connections => " + ds1.getNumActive() + "\n" +
	       "                  max active connections => " + ds1.getMaxActive() + "\n" +
	       "                  idle connections => " + ds1.getNumIdle() + "\n" +
	       "                  max idle connections => " + ds1.getMaxIdle() + "\n" +
	       "                  max wait time => " + ds1.getMaxWait() + "\n" +
	       "                  validationQuery => " + ds1.getValidationQuery() +  "\n"
	       ));
	}

	public static synchronized void shutdownAll()
	{
		DataSourceProviderManager.shutdownAll();
	}
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.markit.credit.dbroutine.DataSourceProvider#shutdown()
	 */
	public synchronized void shutdown() throws Exception {
		// ((SharedPoolDataSource)getDataSource()).close();
		// There may be a case where DataSourceProviderManager wont hold the reference and shutdown called directly on this class.
		ds.close();
		DataSourceProviderManager.releaseDataSourceProvider(getId());
	}

    public String getDatabaseUser() {
        return dbUser;
    }

    public String getDatabasePassword() {
        return dbPass;
    }

    /* (non-Javadoc)
     * @see com.markit.credit.dbroutine.DataSourceProvider#getDataSource()
     */
	public DataSource getDataSource() {
		return ds;
	}

	/* (non-Javadoc)
	 * @see com.markit.credit.dbroutine.DataSourceProvider#getId()
	 */
	public String getId() {
		return tag;
	}

	/* (non-Javadoc)
	 * @see com.markit.credit.dbroutine.DataSourceProvider#getInfo()
	 */
	public String getInfo() {
		return debugStr();
	}

    public int getNumActiveConn()
    {
        return ds.getNumActive();
    }

    public int getMaxActiveConn()
    {
        return ds.getMaxActive();
    }

    public int getNumIdleConn()
    {
        return ds.getNumIdle();
    }

    public int getMaxIdleConn()
    {
        return ds.getMaxIdle();
    }

    public int getMaxWait()
    {
        return ds.getMaxWait();
    }

}
