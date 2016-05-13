package com.concept.crew.dao.loaderUtil;

import com.concept.crew.util.Reader;
import com.concept.crew.util.ResultSetRowMapper;

public interface IDataReader<T> extends Reader<T>, ResultSetRowMapper<T> 
{
	int getReaderRecordCount();
}
