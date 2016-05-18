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
import com.concept.crew.util.Constants.BondCopy;
import com.concept.crew.util.Iterate;
import com.concept.crew.util.Pair;
import com.concept.crew.util.StringUtil;
import com.concept.crew.util.ThreadManager;

/**
 * 
 * BoandLoader is a class level package implementation to readBonds and
 * writeBonds from and to the Database.
 * 
 */
public class DataLoader {


	/**
	 * 
	 * BondWriter Interface
	 * 
	 */
	public static interface BondWriter 
	{

		/**
		 * 
		 * @param bonds
		 */
		public abstract void writeData(Collection<ParentInfoWrapper> bonds);
	}

	/**
	 * Factory for creating {@code BondWriter}
	 * 
	 * @param bondCopies
	 * @return
	 */
	public static final BondWriter createWriter() 
	{
		return new BondWriterImpl();
	}


	/**
	 * 
	 * BondWriter Implementation
	 * 
	 */
	private static final class BondWriterImpl implements BondWriter {
		private static final Logger logger = Logger.getLogger(BondWriterImpl.class);


		private BondWriterImpl() 
		{
		}

		@Override
		public void writeData(Collection<ParentInfoWrapper> bonds) 
		{

			Collection<Pair<ParentInfoWrapper, List<IDataDomainLoader>>> bondsAndDomainWriters = new ArrayList<Pair<ParentInfoWrapper, List<IDataDomainLoader>>>();

			Iterate.forEach(bonds, new PairInstrumentWithItsTargetedWriters(bondsAndDomainWriters));
			try 
			{
				String logKey = "";

				logger.info(new StringBuilder("Start "));

				ThreadManager bondWriterThreadManager = ThreadManager.UNO;
				final CompletionService<Void> completionService = ThreadManager.newCompletionService(bondWriterThreadManager);
				final List<Future<Void>> writeBondTasks = new ArrayList<Future<Void>>();

				Collection<Collection<Pair<ParentInfoWrapper, List<IDataDomainLoader>>>> partitions = CollectionsUtil.partition(bondsAndDomainWriters, 1);

				for (Collection<Pair<ParentInfoWrapper, List<IDataDomainLoader>>> partition : partitions){
					Callable<Void> call = new DataWriterImpl<ParentInfoWrapper, IDataDomainLoader>(logKey, partition);
					Future<Void> task = completionService.submit(call);
					writeBondTasks.add(task);
				}

				ThreadManager.handleTaskCompletion(writeBondTasks, completionService);
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
	private static final class PairInstrumentWithItsTargetedWriters implements Command<ParentInfoWrapper> {

		private static final Logger logger = Logger.getLogger(PairInstrumentWithItsTargetedWriters.class);

		private final Collection<Pair<ParentInfoWrapper, List<IDataDomainLoader>>> bondsAndDomainWriters;

		private PairInstrumentWithItsTargetedWriters(Collection<Pair<ParentInfoWrapper, List<IDataDomainLoader>>> bondsAndDomainWriters) {
			this.bondsAndDomainWriters = bondsAndDomainWriters;
		}
		
		@Override
		public void execute(ParentInfoWrapper bond) {
			Collection<Domain> domainList = Domain.getWriters();
			List<IDataDomainLoader> instrumentDomainWriters = new ArrayList<IDataDomainLoader>();
			for (Domain domain : domainList) {
				List<IDataDomainLoader> newInstrumentDomainWriters;
				try {
					newInstrumentDomainWriters = createInstrumentDomainWriters(domain, null);
					instrumentDomainWriters.addAll(newInstrumentDomainWriters);
				} catch (InstantiationException e) {
					throw new RuntimeException(e);
				} catch (IllegalAccessException e) {
					throw new RuntimeException(e);
				}				
			}
			bondsAndDomainWriters.add(new Pair<ParentInfoWrapper, List<IDataDomainLoader>>(bond, instrumentDomainWriters));
		}


		private List<IDataDomainLoader> createInstrumentDomainWriters(Domain domain, BondCopy bondCopy) 
						throws InstantiationException, IllegalAccessException 
		{
			List<IDataDomainLoader> newInstrumentDomainWriters = createDomainWriterInstances(domain);

			List<IDataDomainLoader> instrumentDomainWriters = new ArrayList<IDataDomainLoader>();
			
			for (IDataDomainLoader newInstrumentDomainWriter : newInstrumentDomainWriters){
				String writeQuery = newInstrumentDomainWriter.getWriteQuery();
				if (StringUtil.isNotEmpty(writeQuery)){
					instrumentDomainWriters.add(newInstrumentDomainWriter);
				}
			}

			return instrumentDomainWriters;
		}

		private final List<IDataDomainLoader> createDomainWriterInstances(Domain domain) 
					throws InstantiationException, IllegalAccessException 
		{
			List<Class<? extends IDataDomainLoader>> domainWriters = domain.getDomainWriters();

			List<IDataDomainLoader> newInstrumentDomainWriters = new ArrayList<IDataDomainLoader>();
			for (Class<? extends IDataDomainLoader> domainWriter : domainWriters) {
				IDataDomainLoader newInstance = domainWriter.newInstance();
				newInstrumentDomainWriters.add(newInstance);
			}

			return newInstrumentDomainWriters;
		}
		
	}
}
