package com.concept.crew.util;

import java.sql.SQLException;

public interface Populate<T, F> {
	void populate(T object, F from) throws SQLException;
}
