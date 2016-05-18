package com.concept.crew.dao.loaderUtil.raw;

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

	@SuppressWarnings("unchecked")
	public static enum Domain implements IDomainLoader
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
		private static List<Domain> readersCache;
		private static List<Domain> writersCache;

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
		private Domain(List<Class<? extends IDataDomainLoader>> domainReaders, 
						   List<Class<? extends IDataDomainLoader>> domainWriters) 
		{
			this(domainReaders, domainWriters, StringUtil.emptyString());
		}

		/*
		 * Overloaded constructor
		 */
		private Domain(List<Class<? extends IDataDomainLoader>> domainReaders, 
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

		public static List<Domain> getReaders() 
		{
			readersCacheLock.lock();
			try {
				if (CollectionsUtil.isNotEmpty(readersCache))
					return readersCache;

				readersCache = new ArrayList<Domain>();
				for (Domain domain : Domain.values()) {
					if (domain.isReader())
						readersCache.add(domain);
				}

				return readersCache;
			} finally {
				readersCacheLock.unlock();
			}
		}

		public static List<Domain> getWriters() 
		{
			writersCacheLock.lock();
			try {
				if (CollectionsUtil.isNotEmpty(writersCache))
					return writersCache;

				writersCache = new ArrayList<Domain>();
				for (Domain domain : Domain.values()) {
					if (domain.isWriter())
						writersCache.add(domain);
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
				for (Domain domain : Domain.values()) {
					domainNamesCache.add(domain.name());
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
