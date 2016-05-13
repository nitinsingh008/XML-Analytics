package com.concept.crew.util;

import java.sql.SQLException;

public interface Prepare<S, T> {
	int prepare(S source, T target) throws SQLException;
}
