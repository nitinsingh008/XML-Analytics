package com.concept.crew.dao.loaderUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.Callable;

import org.apache.log4j.Logger;

import com.concept.crew.dao.LoaderDBRoutine;
import com.concept.crew.util.CollectionsUtil;
import com.concept.crew.util.Command;
import com.concept.crew.util.DBRoutinePoolTx;
import com.concept.crew.util.GeneralUtil;
import com.concept.crew.util.Iterate;
import com.concept.crew.util.Pair;
import com.concept.crew.util.StringUtil;

public class DataWriterImpl<T, W extends IDataWriter<T>> 
											implements Callable<Void> 
{

	private static final Logger logger = Logger.getLogger(DataWriterImpl.class);

	private final Collection<Pair<T, List<W>>> dataAndWriters;

	public DataWriterImpl(Collection<T> data, W writer) 
	{
		this.dataAndWriters = covertToPair(data, CollectionsUtil.toList(writer));
	}

	public DataWriterImpl( Collection<T> data, List<W> writers) 
	{
		this.dataAndWriters = covertToPair(data, writers);
	}

	public DataWriterImpl(String logKey, Collection<Pair<T, List<W>>> dataAndWriters) 
	{
		this.dataAndWriters = dataAndWriters;
	}


	/*
	 * @Override
	 * @see java.util.concurrent.Callable#call()
	 */
	public Void call() throws Exception 
	{
		DBRoutinePoolTx dbRoutine = new DBRoutinePoolTx(LoaderDBRoutine.getPoolname());
		dbRoutine.beginTransaction();
		
		Connection conn = dbRoutine.getConnection();

		Map<W, PreparedStatement> writerStatementsCache = new LinkedHashMap<W, PreparedStatement>();
		try 
		{
			Iterate.forEach(dataAndWriters, new PrepareWriterStatements<T, W>(conn, writerStatementsCache));

			Set<Entry<W, PreparedStatement>> writerStatements = writerStatementsCache.entrySet();
			Iterate.forEach(writerStatements, new ExecuteWriterStatements<T, W>());

			dbRoutine.commitTransaction();
		} 
		catch (Throwable throwable) 
		{
			dbRoutine.rollbackTransaction();
			throw new RuntimeException("Failed writing data" + " - " + throwable.getMessage(), throwable);
		} 
		finally 
		{
			Collection<PreparedStatement> preparedStatements = writerStatementsCache.values();
			cleanup(preparedStatements);
			dbRoutine.cleanup(conn);
		}

		return null;
	}

	private Collection<Pair<T, List<W>>> covertToPair(Collection<T> data, List<W> writers) 
	{
		Collection<Pair<T, List<W>>> newDataWriters = new ArrayList<Pair<T, List<W>>>();

		for (T t : data) 
		{
			newDataWriters.add(new Pair<T, List<W>>(t, writers));
		}

		return newDataWriters;
	}

	private static final class PrepareWriterStatements<T, W extends IDataWriter<T>> implements Command<Pair<T, List<W>>> 
	{
		//private final String logKey;
		private final Connection conn;
		private final Map<W, PreparedStatement> writerStatementsCache;

		private PrepareWriterStatements(Connection conn, Map<W, PreparedStatement> writerStatementsCache) {
			//this.logKey = logKey;
			this.conn = conn;
			this.writerStatementsCache = writerStatementsCache;
		}

		@Override
		public void execute(Pair<T, List<W>> dataAndWritersPair) 
		{
			T data = dataAndWritersPair.getLeft();
			Collection<W> writers = dataAndWritersPair.getRight();

			for (W writer : writers) 
			{
				String loaderName = writer.toString();
				String writeQuery = writer.getWriteQuery();
				if (StringUtil.isNotEmpty(writeQuery)) 
				{
					PreparedStatement stmt;
					try 
					{
						stmt = GeneralUtil.nvl(writerStatementsCache.get(writer), conn.prepareStatement(writeQuery));
						writer.setParam(stmt, 1, data);
						writerStatementsCache.put(writer, stmt);
					} 
					catch (SQLException e) 
					{
						throw new RuntimeException("Failed prepare Writer for " + loaderName + " - " + e.getMessage(), e);
					}
				}
			}
		}
	}

	private static final class ExecuteWriterStatements<T, W extends IDataWriter<T>> implements Command<Entry<W, PreparedStatement>> {


		public ExecuteWriterStatements() 
		{}

		@Override
		public void execute(Entry<W, PreparedStatement> writerStatement) {
			W writer = writerStatement.getKey();
			String loaderName = writer.toString();
			PreparedStatement preparedStatement = writerStatement.getValue();

			StringBuilder action = new StringBuilder("execute Writer for ").append(loaderName);
			logger.debug(new StringBuilder("Start ").append(action));
			try 
			{
				preparedStatement.executeBatch();
			} 
			catch (SQLException e) 
			{
				throw new RuntimeException(new StringBuilder("Failed ").append(action).toString() + " - " + e.getMessage(), e);
			}
			logger.debug(new StringBuilder("Finished ").append(action).append(" ").append(writer.getWriterRecordCount()).append(" Statements(s) executed."));
		}
	}

	private void cleanup(Collection<PreparedStatement> preparedStatements) 
	{
		if (CollectionsUtil.isNotEmpty(preparedStatements)) 
		{
			for (PreparedStatement preparedStatement : preparedStatements) 
			{
				try 
				{
					if (GeneralUtil.isNotNull(preparedStatement) && !preparedStatement.isClosed()) 
					{
						preparedStatement.close();
					}
				} 
				catch (Exception e) 
				{
					logger.error(e);
				}
			}
		}
	}
}
