package com.concept.crew.dao.loaderUtil.raw;

import java.sql.Types;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;

import com.concept.crew.dao.loader.raw.CallScheduleLoader;
import com.concept.crew.dao.loader.raw.InstrumentLoader;
import com.concept.crew.dao.loaderUtil.IDomainLoader;
import com.concept.crew.util.CollectionsUtil;
import com.concept.crew.util.StringUtil;

public class LoadersType
{

	public static enum BondSchema 
	{
		CORE_FEED,
		CORE_REF_DATA, 
	}

	public static enum BondFilter 
	{
		SOURCE("M.SOURCE", Types.VARCHAR), //
		VERSION("M.VERSION", Types.VARCHAR), //
		NONE("", Types.NULL);

		private final String name;
		private final Integer sqlType;

		private BondFilter(String name, Integer sqlType) 
		{
			this.name = name;
			this.sqlType = sqlType;
		}

		public String getName() 
		{
			return name;
		}

		public Integer getSqlType() 
		{
			return sqlType;
		}
	}

	@SuppressWarnings("unchecked")
	public static enum BondDomain implements IDomainLoader
	{
		INSTRUMENT_RAW
		 (CollectionsUtil.<Class<? extends IDataDomainLoader>> toList(InstrumentLoader.class), // SELECT - Reader
		  CollectionsUtil.<Class<? extends IDataDomainLoader>> toList(InstrumentLoader.class)  // INSERT - Writer
		  ),
			
		CALLSCHEDULE_RAW
		 (CollectionsUtil.<Class<? extends IDataDomainLoader>> toList(CallScheduleLoader.class), // SELECT - Reader
		  CollectionsUtil.<Class<? extends IDataDomainLoader>> toList(CallScheduleLoader.class)  // INSERT - Writer
		  )
		 
	                           
		// ******* ADD NEW LOADER ABOVE THIS COMMENT *******
		;

		private static Set<String> 		domainNamesCache;
		private static List<BondDomain> readersCache;
		private static List<BondDomain> writersCache;

		private static final ReentrantLock domainNamesCacheLock = new ReentrantLock();
		private static final ReentrantLock readersCacheLock 	= new ReentrantLock();
		private static final ReentrantLock writersCacheLock 	= new ReentrantLock();

		private final List<Class<? extends IDataDomainLoader>> domainReaders;
		private final List<Class<? extends IDataDomainLoader>> domainWriters;

		private final String orderBy;

		private final boolean isReader;
		private final boolean isWriter;

		/*
		 * Constructor for setting enum parameters
		 */
		private BondDomain(List<Class<? extends IDataDomainLoader>> domainReaders, 
						   List<Class<? extends IDataDomainLoader>> domainWriters) 
		{
			this(domainReaders, domainWriters, StringUtil.emptyString());
		}

		/*
		 * Overloaded constructor
		 */
		private BondDomain(List<Class<? extends IDataDomainLoader>> domainReaders, 
						   List<Class<? extends IDataDomainLoader>> domainWriters, 
						   String orderBy) 
		{
			this.domainReaders = domainReaders;
			this.domainWriters = domainWriters;

			this.isReader = CollectionsUtil.isNotEmpty(domainReaders);
			this.isWriter = CollectionsUtil.isNotEmpty(domainWriters);

			this.orderBy = orderBy;
		}

		public boolean isReader() 
		{
			return isReader;
		}

		public boolean isWriter() 
		{
			return isWriter;
		}

		@Override
		public List<Class<? extends IDataDomainLoader>> getDomainReaders() 
		{
			return domainReaders;
		}

		@Override
		public List<Class<? extends IDataDomainLoader>> getDomainWriters() 
		{
			return domainWriters;
		}

		@Override
		public String getOrderBy() 
		{
			return this.orderBy;
		}

		public static List<BondDomain> getReaders() 
		{
			readersCacheLock.lock();
			try {
				if (CollectionsUtil.isNotEmpty(readersCache))
					return readersCache;

				readersCache = new ArrayList<BondDomain>();
				for (BondDomain bondDomain : BondDomain.values()) {
					if (bondDomain.isReader())
						readersCache.add(bondDomain);
				}

				return readersCache;
			} finally {
				readersCacheLock.unlock();
			}
		}

		public static List<BondDomain> getWriters() 
		{
			writersCacheLock.lock();
			try {
				if (CollectionsUtil.isNotEmpty(writersCache))
					return writersCache;

				writersCache = new ArrayList<BondDomain>();
				for (BondDomain bondDomain : BondDomain.values()) {
					if (bondDomain.isWriter())
						writersCache.add(bondDomain);
				}

				return writersCache;
			} finally {
				writersCacheLock.unlock();
			}
		}

		public static Set<String> getNames() {
			domainNamesCacheLock.lock();
			try {
				if (CollectionsUtil.isNotEmpty(domainNamesCache))
					return domainNamesCache;

				domainNamesCache = new HashSet<String>();
				for (BondDomain bondDomain : BondDomain.values()) {
					domainNamesCache.add(bondDomain.name());
				}

				return domainNamesCache;
			} finally {
				domainNamesCacheLock.unlock();
			}
		}

		@Override
		public String toString() {
			return this.name();
		}
	}
}
