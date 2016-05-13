package com.concept.crew.util;

import java.util.List;



public interface Reader<T> {
	String getReadQuery();

	// In the order of the parameter
	// passed to the SQL Query
	// Pair<java.sql.Types, Value>
	List<Pair<Integer, Object>> getReaderQueryParameters();
}