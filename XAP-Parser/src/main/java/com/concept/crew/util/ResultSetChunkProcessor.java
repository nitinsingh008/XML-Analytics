package com.concept.crew.util;

import java.sql.SQLException;

public interface ResultSetChunkProcessor<T>
{
	public void processChunkResultSet(T obj) throws SQLException;
}
