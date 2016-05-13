package com.concept.crew.util;

import java.util.List;

public interface Writer<T> {
	String getWriteQuery();

	List<List<String>> getWriterQueryParameters();
}
