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
import com.concept.crew.dao.loaderUtil.raw.LoadersType.BondDomain;
import com.concept.crew.info.raw.InstrumentRaw;
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
		public abstract void writeData(Collection<InstrumentRaw> bonds);
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
		public void writeData(Collection<InstrumentRaw> bonds) 
		{

			Collection<Pair<InstrumentRaw, List<IDataDomainLoader>>> bondsAndDomainWriters = new ArrayList<Pair<InstrumentRaw, List<IDataDomainLoader>>>();

			Iterate.forEach(bonds, new PairInstrumentWithItsTargetedWriters(bondsAndDomainWriters));
			try 
			{
				String logKey = "";

				logger.info(new StringBuilder("Start "));

				ThreadManager bondWriterThreadManager = ThreadManager.UNO;
				final CompletionService<Void> completionService = ThreadManager.newCompletionService(bondWriterThreadManager);
				final List<Future<Void>> writeBondTasks = new ArrayList<Future<Void>>();

				Collection<Collection<Pair<InstrumentRaw, List<IDataDomainLoader>>>> partitions = CollectionsUtil.partition(bondsAndDomainWriters, 1);

				for (Collection<Pair<InstrumentRaw, List<IDataDomainLoader>>> partition : partitions){
					Callable<Void> call = new DataWriterImpl<InstrumentRaw, IDataDomainLoader>(logKey, partition);
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
	private static final class PairInstrumentWithItsTargetedWriters implements Command<InstrumentRaw> {

		private static final Logger logger = Logger.getLogger(PairInstrumentWithItsTargetedWriters.class);

		private final Collection<Pair<InstrumentRaw, List<IDataDomainLoader>>> bondsAndDomainWriters;

		private PairInstrumentWithItsTargetedWriters(Collection<Pair<InstrumentRaw, List<IDataDomainLoader>>> bondsAndDomainWriters) {
			this.bondsAndDomainWriters = bondsAndDomainWriters;
		}
		
		@Override
		public void execute(InstrumentRaw bond) {
			Collection<BondDomain> bondDomains = BondDomain.getWriters();
			List<IDataDomainLoader> instrumentDomainWriters = new ArrayList<IDataDomainLoader>();
			for (BondDomain bondDomain : bondDomains) {
				List<IDataDomainLoader> newInstrumentDomainWriters;
				try {
					newInstrumentDomainWriters = createInstrumentDomainWriters(bondDomain, null);
					instrumentDomainWriters.addAll(newInstrumentDomainWriters);
				} catch (InstantiationException e) {
					throw new RuntimeException(e);
				} catch (IllegalAccessException e) {
					throw new RuntimeException(e);
				}				
			}
			bondsAndDomainWriters.add(new Pair<InstrumentRaw, List<IDataDomainLoader>>(bond, instrumentDomainWriters));
		}


		private List<IDataDomainLoader> createInstrumentDomainWriters(BondDomain bondDomain, BondCopy bondCopy) 
						throws InstantiationException, IllegalAccessException 
		{
			List<IDataDomainLoader> newInstrumentDomainWriters = createDomainWriterInstances(bondDomain);

			List<IDataDomainLoader> instrumentDomainWriters = new ArrayList<IDataDomainLoader>();
			
			for (IDataDomainLoader newInstrumentDomainWriter : newInstrumentDomainWriters){
				String writeQuery = newInstrumentDomainWriter.getWriteQuery();
				if (StringUtil.isNotEmpty(writeQuery)){
					instrumentDomainWriters.add(newInstrumentDomainWriter);
				}
			}

			return instrumentDomainWriters;
		}

		private final List<IDataDomainLoader> createDomainWriterInstances(BondDomain bondDomain) 
					throws InstantiationException, IllegalAccessException 
		{
			List<Class<? extends IDataDomainLoader>> domainWriters = bondDomain.getDomainWriters();

			List<IDataDomainLoader> newInstrumentDomainWriters = new ArrayList<IDataDomainLoader>();
			for (Class<? extends IDataDomainLoader> domainWriter : domainWriters) {
				IDataDomainLoader newInstance = domainWriter.newInstance();
				newInstrumentDomainWriters.add(newInstance);
			}

			return newInstrumentDomainWriters;
		}
		
	}
}
