package com.concept.crew.dao.loaderUtil.raw;

import java.sql.PreparedStatement;

import com.concept.crew.dao.loaderUtil.IDataReader;
import com.concept.crew.dao.loaderUtil.IDataWriter;
import com.concept.crew.info.raw.ParentInfoWrapper;
import com.concept.crew.util.ConcurrentCacheable;
import com.concept.crew.util.Prepare;


public interface IDataDomainLoader 
								extends IDataReader<ParentInfoWrapper>, 
												IDataWriter<ParentInfoWrapper>, 
													Prepare<ParentInfoWrapper, PreparedStatement>,
														ConcurrentCacheable<Long, ParentInfoWrapper> 
{

	void setOrderBy(String orderBy);

	String getWriteQuery();
}
