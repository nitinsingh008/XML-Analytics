package com.concept.crew.util;

import java.util.Set;


public interface Filter<T> {

	class NONE<T> implements Filter<T> {

		@Override
		public boolean filter(T t) {
			return false;
		}
	}

	class NULL<T> implements Filter<T> {

		@Override
		public boolean filter(T t) {
			return GeneralUtil.isNull(t);
		}
	}

	class EmptySet<T> implements Filter<Set<T>> {
		@Override
		public boolean filter(Set<T> keys) {
			return CollectionsUtil.isEmpty(keys);
		}
	}

	class EmptyString implements Filter<String> {
		@Override
		public boolean filter(String key) {
			return StringUtil.isEmpty(key);
		}
	}

	boolean filter(T t);
}
