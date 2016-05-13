package com.concept.crew.dao.loaderUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.concurrent.Callable;

import org.apache.log4j.Logger;

import com.concept.crew.dao.LoaderDBRoutine;
import com.concept.crew.util.DBRoutine;
import com.concept.crew.util.DBRoutine.DBParam;
import com.concept.crew.util.DBRoutinePool;
import com.concept.crew.util.Iterate;
import com.concept.crew.util.Pair;
import com.concept.crew.util.StringUtil;
import com.concept.crew.util.Transformer;

public class DataReaderImpl<T, R extends IDataReader<T>> implements Callable<List<T>> 
{
	private static final Logger logger = Logger.getLogger(DataReaderImpl.class);

	private final R reader;

	public DataReaderImpl(R reader) 
	{
		this.reader = reader;
	}

	@Override
	public List<T> call() throws Exception 
	{
		String loaderName = reader.toString();
		logger.debug(new StringBuilder("Start call for ").append(loaderName));

		List<T> result = Collections.emptyList();
		String readQuery = reader.getReadQuery();
		if (StringUtil.isEmpty(readQuery))
			return result;

		DBRoutinePool brdDBRoutine = new DBRoutinePool(LoaderDBRoutine.getPoolname());

		List<EnumMap<DBParam, Object>> parameterList = buildQueryParameters();
		result = brdDBRoutine.executeQueryTypedParam(readQuery, reader, parameterList);

		logger.debug(new StringBuilder("Finished call for ").append(loaderName).append(" ").append(reader.getReaderRecordCount()).append(" Record(s) selected."));
		return result;
	}

	private List<EnumMap<DBParam, Object>> buildQueryParameters() {
		List<Pair<Integer, Object>> queryParameters = reader.getReaderQueryParameters();
		Collection<EnumMap<DBParam, Object>> transformedQueryParameters = Iterate.transform(queryParameters, new QueryParameterTransformer());
		List<EnumMap<DBParam, Object>> parameterList = new ArrayList<EnumMap<DBParam, Object>>(transformedQueryParameters);
		return parameterList;
	}

	private class QueryParameterTransformer implements Transformer<Pair<Integer, Object>, EnumMap<DBParam, Object>> {

		@Override
		public EnumMap<DBParam, Object> tansform(Pair<Integer, Object> from) {
			return DBRoutine.createParam(from.getLeft(), from.getRight());
		}
	}
}
