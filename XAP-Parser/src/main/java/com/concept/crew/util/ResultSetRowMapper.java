package com.concept.crew.util;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface ResultSetRowMapper<T>
{
	T mapRow(ResultSet rs, int rowNum, String[] headers) throws SQLException;
}
