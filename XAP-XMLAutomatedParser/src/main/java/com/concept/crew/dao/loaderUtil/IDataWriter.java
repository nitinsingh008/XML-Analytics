package com.concept.crew.dao.loaderUtil;

import com.concept.crew.util.BatchParameterSetter;
import com.concept.crew.util.Writer;

public interface IDataWriter<T> extends Writer<T>, BatchParameterSetter<T> 
{
	int getWriterRecordCount();
}
