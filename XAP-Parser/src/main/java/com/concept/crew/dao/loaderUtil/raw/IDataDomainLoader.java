package com.concept.crew.dao.loaderUtil.raw;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.concept.crew.dao.loaderUtil.IDataReader;
import com.concept.crew.dao.loaderUtil.IDataWriter;
import com.concept.crew.dao.loaderUtil.IDomainLoader;
import com.concept.crew.dao.loaderUtil.raw.LoadersType.BondFilter;
import com.concept.crew.dao.loaderUtil.raw.LoadersType.BondSchema;
import com.concept.crew.info.raw.InstrumentRaw;
import com.concept.crew.util.ConcurrentCacheable;
import com.concept.crew.util.Constants.BondCopy;
import com.concept.crew.util.Constants.QueryIDType;
import com.concept.crew.util.Pair;
import com.concept.crew.util.Populate;
import com.concept.crew.util.Prepare;


public interface IDataDomainLoader 
								extends IDataReader<InstrumentRaw>, 
											Populate<InstrumentRaw, ResultSet>, 
												IDataWriter<InstrumentRaw>, 
													Prepare<InstrumentRaw, PreparedStatement>,
														ConcurrentCacheable<Long, InstrumentRaw> 
{
	String INST_ID = "INST_ID";
	String UNION = " UNION ";
	String AND = "AND";

	void setBondSchema(BondSchema bondSchema);

	BondSchema getBondSchema();

	void setBondDomain(IDomainLoader bondDomain);

	void setBondCopy(BondCopy bondCopy);

	BondCopy getBondCopy();

	void setQueryIDType(Set<QueryIDType> queryIDTypes);

	void setFilterCriteria(List<Pair<BondFilter, Object>> filters);

	void setOrderBy(String orderBy);

	Map<Pair<BondCopy, QueryIDType>, String> getReadQueryMap();

	Map<BondCopy, String> getWriteQueryMap();
}
