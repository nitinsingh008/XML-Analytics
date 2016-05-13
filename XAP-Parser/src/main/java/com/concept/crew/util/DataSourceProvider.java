package com.concept.crew.util;


import javax.sql.DataSource;

/**
 * Defines database connections provider (DataSource/ConnectionPool)
 * 
 * @author rajesh.kumar
 * 
 */
public interface DataSourceProvider {

	/**
	 * Returns the object for obtaining database connections.<br>
	 * <br>
	 * The DataSource interface is implemented by a database driver vendor.
	 * 
	 * @return
	 */
	public abstract DataSource getDataSource();

	/**
	 * Returns the identifier for data source provider
	 * 
	 * @return
	 */
	public abstract String getId();

	/**
	 * Returns the text to describe current state of {@link DataSource}, can be used for logging debug info.<br>
	 * <br>
	 * Details may include such as number of maximum, minimum and active connections etc.<br>
	 * ** Ideally it should be less than 200 character.
	 * 
	 * @return
	 */
	public abstract String getInfo();

	/**
	 * Will be called to give shut down signal to data source
	 */
	public abstract void shutdown() throws Exception;

    public String getDatabaseUser();

    public String getDatabasePassword();

    public int getNumActiveConn();
    public int getMaxActiveConn();
    public int getNumIdleConn();
    public int getMaxIdleConn();
    public int getMaxWait();
}