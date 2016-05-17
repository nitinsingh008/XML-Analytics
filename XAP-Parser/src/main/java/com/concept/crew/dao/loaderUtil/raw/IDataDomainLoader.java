package com.concept.crew.dao.loaderUtil.raw;

import java.sql.PreparedStatement;

import com.concept.crew.dao.loaderUtil.IDataReader;
import com.concept.crew.dao.loaderUtil.IDataWriter;
import com.concept.crew.info.raw.InstrumentRaw;
import com.concept.crew.util.ConcurrentCacheable;
import com.concept.crew.util.Prepare;


public interface IDataDomainLoader 
								extends IDataReader<InstrumentRaw>, 
												IDataWriter<InstrumentRaw>, 
													Prepare<InstrumentRaw, PreparedStatement>,
														ConcurrentCacheable<Long, InstrumentRaw> 
{

	void setOrderBy(String orderBy);

	String getWriteQuery();
}
