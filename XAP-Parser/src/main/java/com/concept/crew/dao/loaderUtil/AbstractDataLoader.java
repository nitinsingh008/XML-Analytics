package com.concept.crew.dao.loaderUtil;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentMap;

import javax.xml.datatype.XMLGregorianCalendar;

import com.concept.crew.dao.loaderUtil.raw.IDataDomainLoader;
import com.concept.crew.info.raw.ParentInfoWrapper;
import com.concept.crew.util.GeneralUtil;
import com.concept.crew.util.StringUtil;
import com.concept.crew.util.ThreadManager;


public abstract class AbstractDataLoader implements IDataDomainLoader 
{

	private static 	SimpleDateFormat yyyyMMdd = new SimpleDateFormat("yyyy-MM-dd"); // e.g. <issueDate>2012-09-30</issueDate>

	private String 	orderBy;
	private int 	readerRecordCount = 0;

	private String writeQuery;
	private List<List<String>> writerQueryParameters;
	private int writerRecordCount = 0;

	private ConcurrentMap<Long, ParentInfoWrapper> cache;


	
	@Override
	public void setOrderBy(String orderBy) {
		this.orderBy = orderBy;
	}

	@Override
	public void setCache(ConcurrentMap<Long, ParentInfoWrapper> cache) {
		this.cache = cache;
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
	public void setParam(PreparedStatement statement, int rowNum, ParentInfoWrapper bond) throws SQLException {
		ThreadManager.checkInterruption();
		int prepared = prepare(bond, statement);
		writerRecordCount += prepared;
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
		String writeQuery = getWriteQuery();
		return GeneralUtil.nvl(writeQuery, StringUtil.emptyString());
	}


	/*@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result ;
		
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
*/
	
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
