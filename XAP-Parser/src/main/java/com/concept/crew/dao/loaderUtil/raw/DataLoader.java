package com.concept.crew.dao.loaderUtil.raw;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.apache.log4j.Logger;

import com.concept.crew.dao.loaderUtil.DataWriterImpl;
import com.concept.crew.dao.loaderUtil.raw.LoadersType.Domain;
import com.concept.crew.info.raw.ParentInfoWrapper;
import com.concept.crew.util.CollectionsUtil;
import com.concept.crew.util.Command;
import com.concept.crew.util.Iterate;
import com.concept.crew.util.Pair;
import com.concept.crew.util.StringUtil;
import com.concept.crew.util.ThreadManager;

/**
 * 
 * DataLoader is a class level package implementation to 
 * writeData from and to the Database.
 * 
 */
public class DataLoader {


	/**
	 * 
	 * DBWriter Interface
	 * 
	 */
	public static interface DbWriter 
	{

		/**
		 * 
		 * @param data
		 */
		public abstract void writeData(Collection<ParentInfoWrapper> data);
	}

	/**
	 * Factory for creating {@code DbWriter}
	 *  
	 */
	public static final DbWriter createWriter() 
	{
		return new DbWriterImpl();
	}


	/**
	 * 
	 * DbWriter Implementation
	 * 
	 */
	private static final class DbWriterImpl implements DbWriter {
		private static final Logger logger = Logger.getLogger(DbWriterImpl.class);


		private DbWriterImpl() 
		{
		}

		@Override
		public void writeData(Collection<ParentInfoWrapper> data) 
		{

			Collection<Pair<ParentInfoWrapper, List<IDataDomainLoader>>> dataAndDomainWriters = new ArrayList<Pair<ParentInfoWrapper, List<IDataDomainLoader>>>();

			Iterate.forEach(data, new PairDataWithItsTargetedWriters(dataAndDomainWriters));
			try 
			{
				String logKey = "";

				logger.info(new StringBuilder("Start "));

				ThreadManager dataWriterThreadManager = ThreadManager.UNO;
				final CompletionService<Void> completionService = ThreadManager.newCompletionService(dataWriterThreadManager);
				final List<Future<Void>> writeTasks = new ArrayList<Future<Void>>();

				Collection<Collection<Pair<ParentInfoWrapper, List<IDataDomainLoader>>>> partitions = CollectionsUtil.partition(dataAndDomainWriters, 1);

				for (Collection<Pair<ParentInfoWrapper, List<IDataDomainLoader>>> partition : partitions){
					Callable<Void> call = new DataWriterImpl<ParentInfoWrapper, IDataDomainLoader>(logKey, partition);
					Future<Void> task = completionService.submit(call);
					writeTasks.add(task);
				}

				ThreadManager.handleTaskCompletion(writeTasks, completionService);
				logger.info(new StringBuilder(logKey).append("Finished "));

			} catch (InterruptedException e)
			{
				throw new RuntimeException(e);
			} 
			catch (ExecutionException e) 
			{
				throw new RuntimeException(e);
			}
			finally
			{
				ThreadManager.shutdown();
			}
		}
	}

	/**
	 * 
	 * Helper Command
	 * 
	 */
	private static final class PairDataWithItsTargetedWriters implements Command<ParentInfoWrapper> {

		private static final Logger logger = Logger.getLogger(PairDataWithItsTargetedWriters.class);

		private final Collection<Pair<ParentInfoWrapper, List<IDataDomainLoader>>> dataAndDomainWriters;

		private PairDataWithItsTargetedWriters(Collection<Pair<ParentInfoWrapper, List<IDataDomainLoader>>> dataAndDomainWriters) {
			this.dataAndDomainWriters = dataAndDomainWriters;
		}
		
		@Override
		public void execute(ParentInfoWrapper data) {
			Collection<Domain> domainList = Domain.getWriters();
			List<IDataDomainLoader> dataDomainWriters = new ArrayList<IDataDomainLoader>();
			for (Domain domain : domainList) {
				List<IDataDomainLoader> newDomainWriters;
				try {
					newDomainWriters = createDataDomainWriters(domain);
					dataDomainWriters.addAll(newDomainWriters);
				} catch (InstantiationException e) {
					throw new RuntimeException(e);
				} catch (IllegalAccessException e) {
					throw new RuntimeException(e);
				}				
			}
			dataAndDomainWriters.add(new Pair<ParentInfoWrapper, List<IDataDomainLoader>>(data, dataDomainWriters));
		}


		private List<IDataDomainLoader> createDataDomainWriters(Domain domain) 
						throws InstantiationException, IllegalAccessException 
		{
			List<IDataDomainLoader> newDomainWriters = createDomainWriterInstances(domain);

			List<IDataDomainLoader> domainWriters = new ArrayList<IDataDomainLoader>();
			
			for (IDataDomainLoader newDataDomainWriter : newDomainWriters){
				String writeQuery = newDataDomainWriter.getWriteQuery();
				if (StringUtil.isNotEmpty(writeQuery)){
					domainWriters.add(newDataDomainWriter);
				}
			}

			return domainWriters;
		}

		private final List<IDataDomainLoader> createDomainWriterInstances(Domain domain) 
					throws InstantiationException, IllegalAccessException 
		{
			List<Class<? extends IDataDomainLoader>> domainWriters = domain.getDomainWriters();

			List<IDataDomainLoader> newDomainWriters = new ArrayList<IDataDomainLoader>();
			for (Class<? extends IDataDomainLoader> domainWriter : domainWriters) {
				IDataDomainLoader newInstance = domainWriter.newInstance();
				newDomainWriters.add(newInstance);
			}

			return newDomainWriters;
		}
		
	}
}
