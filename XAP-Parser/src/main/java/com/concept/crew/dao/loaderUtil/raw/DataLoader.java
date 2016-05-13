package com.concept.crew.dao.loaderUtil.raw;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.apache.log4j.Logger;

import com.concept.crew.dao.loaderUtil.DataReaderImpl;
import com.concept.crew.dao.loaderUtil.DataWriterImpl;
import com.concept.crew.dao.loaderUtil.IDataReader;
import com.concept.crew.dao.loaderUtil.raw.LoadersType.BondDomain;
import com.concept.crew.dao.loaderUtil.raw.LoadersType.BondFilter;
import com.concept.crew.dao.loaderUtil.raw.LoadersType.BondSchema;
import com.concept.crew.info.raw.InstrumentRaw;
import com.concept.crew.util.CollectionsUtil;
import com.concept.crew.util.Command;
import com.concept.crew.util.Constants.BondCopy;
import com.concept.crew.util.Constants.QueryIDType;
import com.concept.crew.util.Group;
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
	 * BondReader Interface
	 * 
	 */
	public static interface BondReader {


		/**
		 * 
		 * @return A {@code Map<BondCopy, Map<Long, Instrument>>>}
		 *         <ol>
		 *         <li>{@code Map<GOLDEN,  Map<Long(instrumentId), Instrument>>}</li>
		 *         <li>{@code Map<STAGING, Map<Long(instrumentId), Instrument>>}</li>
		 *         </ol>
		 */
		Map<BondCopy, Map<Long, InstrumentRaw>> readBonds();
	}

	/**
	 * Factory for creating {@code BondReader}
	 * 
	 * @param bondIds
	 * @param source
	 * @param bondCopies
	 * @return
	 */
	public static final BondReader createReader(Map<QueryIDType, Set<String>> bondIds, String source, BondCopy... bondCopies) 
	{
		return createReader(BondSchema.CORE_FEED, bondIds, source, bondCopies);
	}


	/**
	 * Factory for creating {@code BondReader}
	 * 
	 * @param jobDetail
	 * @param bondIds
	 * @param source
	 * @param bondCopies
	 * @return
	 */
	public static final BondReader createReader(BondSchema bondSchema, Map<QueryIDType, Set<String>> bondIds, String source, BondCopy... bondCopies) 
	{
		return new BondReaderImpl(bondSchema, bondIds, source, bondCopies);
	}

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
		 *            - A {@code Collection<Instrument>}
		 * @param instrumentDomainLoaders
		 *            - An {@code instrumentDomainLoader[]}
		 */
		public abstract void writeBonds(Collection<InstrumentRaw> bonds);
	}

	/**
	 * Factory for creating {@code BondWriter}
	 * 
	 * @param bondCopies
	 * @return
	 */
	public static final BondWriter createWriter(BondCopy... bondCopies) 
	{
		return createWriter(BondSchema.CORE_FEED, bondCopies);
	}


	/**
	 * Factory for creating {@code BondWriter}
	 * 
	 * @param bondSchema
	 * @param jobDetail
	 * @param bondCopies
	 * @return
	 */
	public static final BondWriter createWriter(BondSchema bondSchema,  BondCopy... bondCopies) 
	{
		return new BondWriterImpl(bondSchema,  bondCopies);
	}


	/**
	 * 
	 * BondReader Implementation
	 * 
	 */
	private static final class BondReaderImpl implements BondReader 
	{
		private static final Logger logger = Logger.getLogger(BondReaderImpl.class);

		private  BondSchema bondSchema;

		private final List<BondCopy> bondCopies;

		private final Set<QueryIDType> queryIDTypes;

		private final Map<BondCopy, List<Pair<BondFilter, Object>>> filterMap;

		private BondReaderImpl(Map<QueryIDType, Set<String>> bondIds, String source, BondCopy... bondCopies) 
		{
			this(BondSchema.CORE_FEED, bondIds, source, bondCopies);
		}

		private BondReaderImpl(BondSchema bondSchema, 
								Map<QueryIDType, Set<String>> bondIds, String source, BondCopy... bondCopies) 
		{
			this.bondSchema = bondSchema;
			this.bondCopies = Arrays.<BondCopy> asList(bondCopies);


			// get distinct query id types, if the queryIDTypes set is empty
			// throws runtime exception
			this.queryIDTypes = getQueryIDTypes(bondIds);

			// load the filter map
			this.filterMap = loadFilterMap();
		}

		@Override
		public Map<BondCopy, Map<Long, InstrumentRaw>> readBonds() 
		{
			return readBonds(BondDomain.getReaders());
		}

		// TODO Check
		private Map<BondCopy, Map<Long, InstrumentRaw>> readBonds(List<BondDomain> bondDomains)
		{
			int numberOfDomains = bondDomains.size();
			int numberOfBondCopies = bondCopies.size();
			int initialCapacity = numberOfDomains * numberOfBondCopies;

			final Map<BondCopy, Map<Long, InstrumentRaw>> bonds = new HashMap<BondCopy, Map<Long, InstrumentRaw>>(numberOfBondCopies);

			try 
			{
				
				StringBuilder action = new StringBuilder("reading MarkitBondRaws from ").append(initialCapacity).append(" [").append(bondSchema).append("].").append(bondCopies).append(" tables.");
				logger.info(new StringBuilder("Start ").append(action));

				ThreadManager bondReaderThreadManager = ThreadManager.UNO;
				final CompletionService<List<InstrumentRaw>> completionService = ThreadManager.newCompletionService(bondReaderThreadManager);
				final List<Future<List<InstrumentRaw>>> readBondTasks = new ArrayList<Future<List<InstrumentRaw>>>(initialCapacity);

				for (BondCopy bondCopy : bondCopies) 
				{
					final ConcurrentMap<Long, InstrumentRaw> cache = setupCache(bonds, bondCopy);

					for (BondDomain bondDomain : bondDomains) 
					{
						List<Pair<BondFilter, Object>> filters = filterMap.get(bondCopy);
						List<IDataDomainLoader> instrumentDomainReaders = createInstrumentDomainReaders(bondDomain, bondCopy, filters, cache);

						for (IDataDomainLoader instrumentDomainReader : instrumentDomainReaders) 
						{
							Callable<List<InstrumentRaw>> call = new DataReaderImpl<InstrumentRaw, IDataReader<InstrumentRaw>>(instrumentDomainReader);
							Future<List<InstrumentRaw>> task = completionService.submit(call);
							readBondTasks.add(task);
						}
					}
				}

				ThreadManager.handleTaskCompletion(readBondTasks, completionService);
				logger.info(new StringBuilder("Finished ").append(action));

				
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			} catch (ExecutionException e) {
				throw new RuntimeException(e);
			} catch (InstantiationException e) {
				throw new RuntimeException(e);
			} catch (IllegalAccessException e) {
				throw new RuntimeException(e);
			}

			return bonds;
		}


		private List<IDataDomainLoader> createInstrumentDomainReaders(BondDomain bondDomain, 
																		   BondCopy bondCopy, 
																		   List<Pair<BondFilter, Object>> filters, 
																		   ConcurrentMap<Long, InstrumentRaw> cache) 
													throws InstantiationException, IllegalAccessException 
		{
			List<IDataDomainLoader> newInstrumentDomainReaders = createDomainReaderInstances(bondDomain);
			setupInstrumentDomainReaders(bondDomain, bondCopy, filters, cache, newInstrumentDomainReaders);

			List<IDataDomainLoader> instrumentDomainReaders = new ArrayList<IDataDomainLoader>();
			
			for (IDataDomainLoader newInstrumentDomainReader : newInstrumentDomainReaders)
			{
				if (StringUtil.isNotEmpty(newInstrumentDomainReader.getReadQuery()))
					instrumentDomainReaders.add(newInstrumentDomainReader);
			}

			return instrumentDomainReaders;
		}

		private final List<IDataDomainLoader> createDomainReaderInstances(BondDomain bondDomain) throws InstantiationException, IllegalAccessException {
			List<Class<? extends IDataDomainLoader>> domainReaders = bondDomain.getDomainReaders();

			List<IDataDomainLoader> newInstrumentDomainReaders = new ArrayList<IDataDomainLoader>();
			for (Class<? extends IDataDomainLoader> domainReader : domainReaders) 
			{
				IDataDomainLoader newInstance = domainReader.newInstance();
				newInstrumentDomainReaders.add(newInstance);
			}

			return newInstrumentDomainReaders;
		}

		private void setupInstrumentDomainReaders(BondDomain bondDomain, BondCopy bondCopy, List<Pair<BondFilter, Object>> filters, ConcurrentMap<Long, InstrumentRaw> cache, List<IDataDomainLoader> instrumentDomainReaders) {
			for (IDataDomainLoader instrumentDomainReader : instrumentDomainReaders) 
			{
				instrumentDomainReader.setBondSchema(bondSchema);
				instrumentDomainReader.setBondDomain(bondDomain);
				instrumentDomainReader.setBondCopy(bondCopy);

				instrumentDomainReader.setQueryIDType(queryIDTypes);
				//instrumentDomainReader.setFilterCriteria(filters);
				instrumentDomainReader.setOrderBy(bondDomain.getOrderBy());
				instrumentDomainReader.setCache(cache);
			}
		}

		private Set<QueryIDType> getQueryIDTypes(Map<QueryIDType, Set<String>> bondIds) 
		{
			if (Group.isEmpty(bondIds))
				throw new RuntimeException("Empty Bond-Ids.");

			Set<QueryIDType> set = new HashSet<QueryIDType>();

			for (QueryIDType queryIDType : bondIds.keySet())
			{
				Set<String> values = bondIds.get(queryIDType);
				if (CollectionsUtil.isNotEmpty(values))
					set.add(queryIDType);
			}

			return set;
		}

		private ConcurrentMap<Long, InstrumentRaw> setupCache(final Map<BondCopy, Map<Long, InstrumentRaw>> bonds, 
															  BondCopy bondCopy) 
		{
			final ConcurrentMap<Long, InstrumentRaw> data = new ConcurrentHashMap<Long, InstrumentRaw>();
			bonds.put(bondCopy, data);
			return data;
		}

		private Map<BondCopy, List<Pair<BondFilter, Object>>> loadFilterMap() 
		{
			Map<BondCopy, List<Pair<BondFilter, Object>>> filterMap = new HashMap<BondCopy, List<Pair<BondFilter, Object>>>();
/*			Pair<BondFilter, Object> processingIdFilter = new Pair<BondFilter, Object>(BondFilter.PROCESSING_ID, processingId);

			filterMap.put(BondCopy.GOLDEN, loadGoldenFilters(processingIdFilter));
			filterMap.put(BondCopy.STAGING, loadStagingFilters(processingIdFilter));
*/
			return filterMap;
		}

	}

	/**
	 * 
	 * BondWriter Implementation
	 * 
	 */
	private static final class BondWriterImpl implements BondWriter {
		private static final Logger logger = Logger.getLogger(BondWriterImpl.class);

		private final BondSchema bondSchema;
		private final List<BondCopy> bondCopies;

		private BondWriterImpl(BondSchema bondSchema,  BondCopy... bondCopies) 
		{
			this.bondSchema = bondSchema;
			this.bondCopies = Arrays.<BondCopy> asList(bondCopies);
		}


		private BondWriterImpl(BondCopy... bondCopies)
		{
			this(BondSchema.CORE_FEED, bondCopies);
		}

		@Override
		public void writeBonds(Collection<InstrumentRaw> bonds) 
		{

			// local cache of the BRDWriters - MAINTAINED IN INSERTION ORDER
			Map<Pair<BondDomain, BondCopy>, List<IDataDomainLoader>> domainWritersCache = new LinkedHashMap<Pair<BondDomain, BondCopy>, List<IDataDomainLoader>>();

			// Collection of Instrument and its targeted BRDWriters
			Collection<Pair<InstrumentRaw, List<IDataDomainLoader>>> bondsAndDomainWriters = new ArrayList<Pair<InstrumentRaw, List<IDataDomainLoader>>>();

			Iterate.forEach(bonds, new PairInstrumentWithItsTargetedWriters(bondSchema, bondCopies, domainWritersCache, bondsAndDomainWriters));

			int numberOfDomains = domainWritersCache.size();
			int numberOfBondCopies = bondCopies.size();
			int numberOfInstruments = bonds.size() * numberOfBondCopies;

			try 
			{
				String logKey = "";

				StringBuilder action = new StringBuilder("writing ").append(numberOfInstruments).append(" Instrument(s) to ").append(numberOfDomains).append(" [").append(bondSchema).append("].").append(bondCopies).append(" tables.");
				logger.info(new StringBuilder("Start ").append(action));

				//ThreadManager bondWriterThreadManager = ThreadManager.FIVE_GUYS;
				ThreadManager bondWriterThreadManager = ThreadManager.UNO;
				final CompletionService<Void> completionService = ThreadManager.newCompletionService(bondWriterThreadManager);
				final List<Future<Void>> writeBondTasks = new ArrayList<Future<Void>>();

				Collection<Collection<Pair<InstrumentRaw, List<IDataDomainLoader>>>> partitions = CollectionsUtil.partition(bondsAndDomainWriters, 1);

				for (Collection<Pair<InstrumentRaw, List<IDataDomainLoader>>> partition : partitions) 
				{
					Callable<Void> call = new DataWriterImpl<InstrumentRaw, IDataDomainLoader>(logKey, partition);
					Future<Void> task = completionService.submit(call);
					writeBondTasks.add(task);
				}

				ThreadManager.handleTaskCompletion(writeBondTasks, completionService);
				logger.info(new StringBuilder(logKey).append("Finished ").append(action));

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

		private BondSchema bondSchema;
		private List<BondCopy> bondCopies;
		private final Map<Pair<BondDomain, BondCopy>, List<IDataDomainLoader>> domainWritersCache;
		private final Collection<Pair<InstrumentRaw, List<IDataDomainLoader>>> bondsAndDomainWriters;

		private PairInstrumentWithItsTargetedWriters(BondSchema bondSchema, List<BondCopy> bondCopies, Map<Pair<BondDomain, BondCopy>, List<IDataDomainLoader>> domainWritersCache, Collection<Pair<InstrumentRaw, List<IDataDomainLoader>>> bondsAndDomainWriters) {
			this.bondSchema = bondSchema;
			this.bondCopies = bondCopies;
			this.domainWritersCache = domainWritersCache;
			this.bondsAndDomainWriters = bondsAndDomainWriters;
		}

		@Override
		public void execute(InstrumentRaw bond) 
		{
			Collection<BondDomain> bondDomains = BondDomain.getWriters();

			try 
			{
				List<IDataDomainLoader> instrumentDomainWriters = new ArrayList<IDataDomainLoader>();

				for (BondCopy bondCopy : bondCopies) 
				{
					for (BondDomain bondDomain : bondDomains) {
						//logger.debug(bondDomain + "-" + bondCopy + "-" + new InstrumentColumns.InstrumentIdentifiers().getValue(bond));
						Pair<BondDomain, BondCopy> keyBondDomainBondCopy = new Pair<BondDomain, BondCopy>(bondDomain, bondCopy);
						List<IDataDomainLoader> cachedInstrumentDomainWriters = domainWritersCache.get(keyBondDomainBondCopy);

						if (CollectionsUtil.isNotEmpty(cachedInstrumentDomainWriters)) {
							instrumentDomainWriters.addAll(cachedInstrumentDomainWriters);
						} else {
							List<IDataDomainLoader> newInstrumentDomainWriters = createInstrumentDomainWriters(bondDomain, bondCopy);
							instrumentDomainWriters.addAll(newInstrumentDomainWriters);
							domainWritersCache.put(keyBondDomainBondCopy, newInstrumentDomainWriters);
						}
					}

				}

				bondsAndDomainWriters.add(new Pair<InstrumentRaw, List<IDataDomainLoader>>(bond, instrumentDomainWriters));
			} catch (InstantiationException e) 
			{
				throw new RuntimeException(e);
			} 
			catch (IllegalAccessException e) 
			{
				throw new RuntimeException(e);
			}
		}

		private List<IDataDomainLoader> createInstrumentDomainWriters(BondDomain bondDomain, BondCopy bondCopy) 
						throws InstantiationException, IllegalAccessException 
		{
			List<IDataDomainLoader> newInstrumentDomainWriters = createDomainWriterInstances(bondDomain);
			setupInstrumentDomainWriters(bondDomain, bondCopy, newInstrumentDomainWriters);

			List<IDataDomainLoader> instrumentDomainWriters = new ArrayList<IDataDomainLoader>();
			
			for (IDataDomainLoader newInstrumentDomainWriter : newInstrumentDomainWriters) 
			{
				String writeQuery = newInstrumentDomainWriter.getWriteQuery();
				if (StringUtil.isNotEmpty(writeQuery)) 
				{
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

		private void setupInstrumentDomainWriters(BondDomain bondDomain, 
												  BondCopy bondCopy, 
												  List<IDataDomainLoader> instrumentDomainWriters) 
		{
			
			for (IDataDomainLoader instrumentDomainWriter : instrumentDomainWriters) 
			{
				instrumentDomainWriter.setBondSchema(bondSchema);
				instrumentDomainWriter.setBondDomain(bondDomain);
				instrumentDomainWriter.setBondCopy(bondCopy);
			}
		}


	}
}
