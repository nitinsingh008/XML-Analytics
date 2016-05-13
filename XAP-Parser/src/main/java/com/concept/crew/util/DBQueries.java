package com.concept.crew.util;

public class DBQueries {
        
		public static final String DELETE_MRD_REQUEST_QUERY = " DELETE FROM CORE_FEED.MRD_BBG_INTERNAL_REQUEST "
				+ " WHERE MODIFIED_ON < ? AND STATUS IN ('" + Constants.STATUS_COMPLETED + "', '" + Constants.STATUS_FAILED + "') ";
        
		
		
		public static final String DELETE_BBG_FEEDFILE_REQUEST_TABLE_QUERY = " DELETE FROM CORE_FEED.BBG_FEEDFILE "
				+ " WHERE MODIFIED_ON < ? AND STATUS IN ('" + Constants.STATUS_COMPLETED + "', '" + Constants.STATUS_FAILED + "') ";
		
}
