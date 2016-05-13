package com.concept.crew.dao.loaderUtil;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;

import javax.xml.datatype.XMLGregorianCalendar;

import com.concept.crew.dao.loaderUtil.raw.LoadersType.BondFilter;
import com.concept.crew.dao.loaderUtil.raw.LoadersType.BondSchema;
import com.concept.crew.dao.loaderUtil.raw.IDataDomainLoader;
import com.concept.crew.info.raw.InstrumentRaw;
import com.concept.crew.util.CollectionsUtil;
import com.concept.crew.util.Constants.BondCopy;
import com.concept.crew.util.Constants.QueryIDType;
import com.concept.crew.util.GeneralUtil;
import com.concept.crew.util.MapUtil;
import com.concept.crew.util.Pair;
import com.concept.crew.util.ResultSetHelper;
import com.concept.crew.util.StringUtil;
import com.concept.crew.util.ThreadManager;


public abstract class AbstractDataLoader implements IDataDomainLoader 
{

	private static 	SimpleDateFormat yyyyMMdd = new SimpleDateFormat("yyyy-MM-dd"); // e.g. <issueDate>2012-09-30</issueDate>

	private BondSchema 		bondSchema;
	private IDomainLoader 	bondDomain;
	private BondCopy 		bondCopy;

	protected List<Pair<BondFilter, Object>> filters;
	private Set<QueryIDType> 				 queryIDTypes;
	private List<Pair<Integer, Object>> 	 readerQueryParameters;
	
	private String 	orderBy;
	private String 	readQuery;
	private int 	readerRecordCount = 0;

	private String writeQuery;
	private List<List<String>> writerQueryParameters;
	private int writerRecordCount = 0;

	private ConcurrentMap<Long, InstrumentRaw> cache;

	@Override
	public void setBondSchema(BondSchema bondSchema) {
		this.bondSchema = bondSchema;
	}

	@Override
	public void setBondDomain(IDomainLoader bondDomain) {
		this.bondDomain = bondDomain;
	}

	@Override
	public void setBondCopy(BondCopy bondCopy) {
		this.bondCopy = bondCopy;
	}

	@Override
	public void setFilterCriteria(List<Pair<BondFilter, Object>> filters) {
		this.filters = filters;
	}

	@Override
	public void setQueryIDType(Set<QueryIDType> queryIDTypes) {
		this.queryIDTypes = queryIDTypes;
	}

	@Override
	public void setOrderBy(String orderBy) {
		this.orderBy = orderBy;
	}

	@Override
	public void setCache(ConcurrentMap<Long, InstrumentRaw> cache) {
		this.cache = cache;
	}

	public BondSchema getBondSchema() {
		return bondSchema;
	}

	@Override
	public BondCopy getBondCopy() {
		return bondCopy;
	}

	@Override
	public String getReadQuery() {
		readQuery = GeneralUtil.nvl(readQuery, buildReadQuery());
		return readQuery;
	}

	@Override
	public List<Pair<Integer, Object>> getReaderQueryParameters() {
		readerQueryParameters = GeneralUtil.nvl(readerQueryParameters, buildReaderQueryParameters());
		return readerQueryParameters;
	}

	@Override
	public String getWriteQuery() {
		writeQuery = GeneralUtil.nvl(writeQuery, buildWriteQuery());
		return writeQuery;
	}

	@Override
	public List<List<String>> getWriterQueryParameters() {
		writerQueryParameters = GeneralUtil.nvl(writerQueryParameters, new ArrayList<List<String>>());
		return writerQueryParameters;
	}



	@Override
	public int getWriterRecordCount() {
		return writerRecordCount;
	}

	@Override
	public int getReaderRecordCount() {
		return readerRecordCount;
	}

	@Override
	public InstrumentRaw mapRow(ResultSet rs, int rowNum, String[] headers) throws SQLException {
		ThreadManager.checkInterruption();
		long instrumentId = ResultSetHelper.toLong(rs, INST_ID);
		InstrumentRaw bond = new InstrumentRaw();
		bond = GeneralUtil.nvl(cache.putIfAbsent(instrumentId, bond), bond);
		//bond.setInstrumentId(instrumentId);
		populate(bond, rs);
		readerRecordCount = rowNum + 1;
		return bond;
	}

	@Override
	public void setParam(PreparedStatement statement, int rowNum, InstrumentRaw bond) throws SQLException {
		ThreadManager.checkInterruption();
		int prepared = prepare(bond, statement);
		writerRecordCount += prepared;
	}

	private final String buildReadQuery() {
		Map<Pair<BondCopy, QueryIDType>, String> queryMap = getReadQueryMap();

		Collection<String> queries = new ArrayList<String>();
		for (QueryIDType idType : queryIDTypes) {
			applyReaderQuery(queryMap, queries, idType);
		}

		if (CollectionsUtil.isEmpty(queries))
			return StringUtil.emptyString();

		StringBuilder queryBuilder = new StringBuilder(StringUtil.join(queries, UNION));
		queryBuilder = applyOrderBy(queryBuilder, queries.size());

		return queryBuilder.toString();
	}

	private StringBuilder applyOrderBy(StringBuilder queryBuilder, int numberOfUnions)
	{
		if (StringUtil.isNotEmpty(orderBy)) 
		{
			StringBuilder orderByBuilder = new StringBuilder();
			if (numberOfUnions > 1) 
			{
				orderByBuilder.append("SELECT * FROM (");
				orderByBuilder.append(queryBuilder);
				orderByBuilder.append(") ");
				orderByBuilder.append(orderBy);
				return orderByBuilder;
			} 
			else {
				queryBuilder.append(" ");
				queryBuilder.append(orderBy);
			}
		}

		return queryBuilder;
	}

	private List<Pair<Integer, Object>> buildReaderQueryParameters() {
		Map<Pair<BondCopy, QueryIDType>, String> queryMap = getReadQueryMap();

		List<Pair<Integer, Object>> sqlQueryParameters = new ArrayList<Pair<Integer, Object>>();
		for (QueryIDType idType : queryIDTypes) {
			applyReaderQueryParameter(queryMap, sqlQueryParameters, idType);
		}

		return sqlQueryParameters;
	}

	private void applyReaderQueryParameter(Map<Pair<BondCopy, QueryIDType>, String> queryMap, List<Pair<Integer, Object>> sqlQueryParameters, QueryIDType idType) {
		String query = queryMap.get(new Pair<BondCopy, QueryIDType>(bondCopy, idType));
		if (StringUtil.isNotEmpty(query))
			addReaderQueryParameter(sqlQueryParameters);
	}

	private void addReaderQueryParameter(List<Pair<Integer, Object>> sqlQueryParameters) 
	{
/*		for (Pair<BondFilter, Object> filter : filters) 
		{
			BondFilter bondFilter = filter.getLeft();
			Integer sqlType = bondFilter.getSqlType();
			Object value = filter.getRight();
			sqlQueryParameters.add(new Pair<Integer, Object>(sqlType, value));
		}*/
	}

	private void applyReaderQuery(Map<Pair<BondCopy, QueryIDType>, String> queryMap, Collection<String> queries, QueryIDType idType) {
		String query = queryMap.get(new Pair<BondCopy, QueryIDType>(bondCopy, idType));
		if (StringUtil.isNotEmpty(query)) {
			queries.add(addFilter(query));
		}
	}

	protected String addFilter(String query) {
		StringBuilder filterBuilder = new StringBuilder(query);
/*		for (Pair<BondFilter, Object> filter : filters) {
			BondFilter bondFilter = filter.getLeft();
			if (!BondFilter.NONE.equals(bondFilter)) {
				String name = bondFilter.getName();
				filterBuilder.append(" ").append(AND).append(" ");
				filterBuilder.append(name).append("=?");
			}
		}*/

		return filterBuilder.toString();
	}

	private String buildWriteQuery() 
	{
		Map<BondCopy, String> writeQueryMap = getWriteQueryMap();
		if (MapUtil.isEmpty(writeQueryMap))
			return StringUtil.emptyString();

		String writeQuery = writeQueryMap.get(bondCopy);

		return GeneralUtil.nvl(writeQuery, StringUtil.emptyString());
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((bondCopy == null) ? 0 : bondCopy.hashCode());
		result = prime * result + ((bondDomain == null) ? 0 : bondDomain.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AbstractDataLoader other = (AbstractDataLoader) obj;
		if (bondCopy != other.bondCopy)
			return false;
		if (bondDomain == null) {
			if (other.bondDomain != null)
				return false;
		} else if (!bondDomain.equals(other.bondDomain))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return this.getClass().getSimpleName() + " [bondSchema=" + bondSchema + ", bondDomain=" + bondDomain + ", bondCopy=" + bondCopy + "]";
	}
	
	protected static Date getSqlDateFromXMLGregorianCalendar(XMLGregorianCalendar date) 
	{
		Date sqlDate=null;
		if(date!=null){
			sqlDate = getSqlDate(date.toString());
		}
		return sqlDate;
	}
	
	private static Date getSqlDate(Object object) 
	{
		if(object==null)return null;
		Date date = null;
		try {
			if(object instanceof Timestamp) 
			{
				date = new Date (((Timestamp)object).getTime());
			} 
			else if(object instanceof Date) 
			{
				date = (Date)object;
			} 
			else if(object instanceof String) 
			{
					try {
						date = new Date(yyyyMMdd.parse((String)object).getTime());
					} catch (Exception e) {

					}
			}
		} catch (Exception e) {
			//logger.error(e.getMessage(),e);
		}
		return date;
	}


}
