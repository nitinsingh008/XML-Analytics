package com.concept.crew.dao.loaderUtil.raw;

import java.sql.PreparedStatement;
import java.util.List;

import com.concept.crew.dao.loaderUtil.IDataReader;
import com.concept.crew.dao.loaderUtil.IDataWriter;
import com.concept.crew.dao.loaderUtil.raw.LoadersType.BondFilter;
import com.concept.crew.info.raw.InstrumentRaw;
import com.concept.crew.util.ConcurrentCacheable;
import com.concept.crew.util.Pair;
import com.concept.crew.util.Prepare;


public interface IDataDomainLoader 
								extends IDataReader<InstrumentRaw>, 
												IDataWriter<InstrumentRaw>, 
													Prepare<InstrumentRaw, PreparedStatement>,
														ConcurrentCacheable<Long, InstrumentRaw> 
{
	String INST_ID = "INST_ID";
	String UNION = " UNION ";
	String AND = "AND";

	void setFilterCriteria(List<Pair<BondFilter, Object>> filters);

	void setOrderBy(String orderBy);

	String getWriteQuery();
}
