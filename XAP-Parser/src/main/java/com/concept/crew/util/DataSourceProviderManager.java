package com.concept.crew.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.apache.log4j.Logger;

/**
 * Manage {@link DataSourceProvider} references including registration, eviction and other aspects.
 * 
 * @author rajesh.kumar
 * 
 */
public class DataSourceProviderManager {
	
	private static Logger log = Logger.getLogger(DataSourceProviderManager.class);
	private static Map<String, DataSourceProvider> instanceMap = new HashMap<String, DataSourceProvider>();

	/**
	 * Register the supplied {@link DataSourceProvider}</br></br>
	 * 
	 * May throw {@link RuntimeException} if data source provider itself or its identifier is null
	 * 
	 * @param ds
	 * @return <code>false</code> if a data source provider is already registered, <code>true</code> otherwise
	 */
	public static synchronized boolean register(DataSourceProvider ds) {
		if (ds == null || ds.getId() == null || "".equals(ds.getId().trim())) {
			throw new RuntimeException("Invalid data source identifier, can't be null or empty");
		}
		if (instanceMap.containsKey(ds.getId())) {
			log.debug("Datasource already registered with identifier:" + ds.getId());
		} else {
			instanceMap.put(ds.getId(), ds);
			log.info("Registring datasource with identifier:" + ds.getId());
			return true;
		}
		return false;
	}

	/**
	 * Remove the reference of {@link DataSourceProvider} identified by specified identifier.
	 * 
	 * @param id
	 *            data source identifier
	 */
	public static synchronized void releaseDataSourceProvider(String id) {
		if (id != null) {
			instanceMap.remove(id);
		}
	}

	/**
	 * Returns the data source provider, if registered the supplied identifier
	 * 
	 * @param id
	 * @return
	 */
	public static synchronized DataSourceProvider getDataSourceProvider(String id) {
		return instanceMap.get(id);
	}

	/**
	 * Shutdown specified connection pool
	 * 
	 * @param id
	 */
	public static synchronized void shutdown(String id) {
		DataSourceProvider pool = instanceMap.remove(id);
		if (pool != null) {
			log.debug("Removing datasource registered with identifier:" + id);
			try {
				pool.shutdown();
			} catch (Throwable t) {
				log.info("Got error while shutting down from data source provider:" + t);
			}
		}
	}

	/**
	 * Shutdown all data source and releases its references
	 */
	public static synchronized void shutdownAll() {
		// Create a copy to avoid concurrent modification exception
		HashSet<String> hs = new HashSet<String>(instanceMap.keySet());
		for (String id : hs) {
			shutdown(id);
		}
	}

	/**
	 * Return the number of data source currently maintained
	 */
	public static int currentSize() {
		return instanceMap.size();
	}
}
