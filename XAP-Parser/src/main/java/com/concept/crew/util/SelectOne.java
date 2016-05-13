package com.concept.crew.util;

import java.util.Collection;


public interface SelectOne<T> {

	class UNIQUE<T> implements SelectOne<T> {

		@Override
		public T select(Collection<T> collection) {
			if (CollectionsUtil.hasMaximumCount(collection, 1))
				return Iterate.selectFirst(collection);

			throw new RuntimeException("Too many values for unique key, please verify data.");
		}
	}

	class FIRST<T> implements SelectOne<T> {
		@Override
		public T select(Collection<T> collection) {
			return Iterate.selectFirst(collection);
		}

	}

	class LAST<T> implements SelectOne<T> {
		@Override
		public T select(Collection<T> collection) {
			return Iterate.selectLast(collection);
		}

	}

	T select(Collection<T> collection);
}
