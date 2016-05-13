package com.concept.crew.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;




public final class Iterate {

	public static <T> void forEach(final Collection<T> collection, final Command<T> execute) {
		if (CollectionsUtil.isEmpty(collection))
			return;

		for (T t : collection) {
			execute.execute(t);
		}
	}

	public static <T> Collection<T> select(final Collection<T> collection, final Select<T> select) {
		if (CollectionsUtil.isEmpty(collection))
			return Collections.<T> emptyList();

		Collection<T> selected = new ArrayList<T>(collection.size());
		for (T t : collection) {
			if (select.select(t))
				selected.add(t);
		}

		return selected;
	}

	public static <T> T selectFirst(final Collection<T> collection, final Select<T> select) {
		if (CollectionsUtil.isEmpty(collection))
			return null;

		for (T t : collection) {
			if (select.select(t))
				return t;
		}

		return null;
	}

	public static <T> T selectFirst(final Collection<T> collection) {
		return selectFirst(collection, new Select.ALL<T>());
	}

	public static <T> T selectLast(final Collection<T> collection, final Select<T> select) {
		if (CollectionsUtil.isEmpty(collection))
			return null;

		T last = null;
		for (T t : collection) {
			if (select.select(t))
				last = t;
		}

		return last;
	}

	public static <T> T selectLast(final Collection<T> collection) {
		return selectLast(collection, new Select.ALL<T>());
	}

	public static <T> Integer count(final Collection<T> collection, final Select<T> select) {
		if (CollectionsUtil.isEmpty(collection))
			return 0;

		int counter = 0;
		for (T t : collection) {
			if (select.select(t))
				counter++;
		}

		return counter;
	}

	public static <T> T selectOne(final Collection<T> collection, final Select<T> select) {
		if (CollectionsUtil.isEmpty(collection))
			return null;

		return selectFirst(collection, select);
	}

	public static <T> T selectOne(final Collection<T> collection, final SelectOne<T> selectOne) {
		if (CollectionsUtil.isEmpty(collection))
			return null;

		return selectOne.select(collection);
	}

	public static <T, K> Collection<T> unique(final Collection<T> collection, final GroupBy<T, K> groupBy, final Filter<K> filter, final SelectOne<T> selectOne) {
		return Group.unique(collection, groupBy, filter, selectOne).values();
	}

	public static <T, K> Collection<T> unique(final Collection<T> collection, final GroupBy<T, K> groupBy, final Filter<K> filter) {
		return Group.unique(collection, groupBy, filter).values();
	}

	public static <T, K> Collection<T> unique(final Collection<T> collection, final GroupBy<T, K> groupBy, final SelectOne<T> selectOne) {
		return Group.unique(collection, groupBy, selectOne).values();
	}

	public static <T, K> Collection<T> unique(final Collection<T> collection, final GroupBy<T, K> groupBy) {
		return Group.unique(collection, groupBy).values();
	}

	public static <T> Collection<Collection<T>> partition(final Collection<T> collection, final int size) {

		if (CollectionsUtil.isEmpty(collection))
			return Collections.<Collection<T>> emptyList();

		Collection<Collection<T>> partitioned = new ArrayList<Collection<T>>();
		Collection<T> inner = new ArrayList<T>(size);

		int index = 0;
		for (T t : collection) {
			inner.add(t);
			++index;
			if (index == collection.size() || size == inner.size()) {
				partitioned.add(inner);
				inner = new ArrayList<T>();
			}
		}

		return partitioned;
	}

	public static <S, T> Collection<T> transform(final Collection<S> collection, final Transformer<S, T> transformer) {
		if (CollectionsUtil.isEmpty(collection))
			return Collections.emptyList();

		Collection<T> transformed = new ArrayList<T>(collection.size());
		for (S s : collection) {
			transformed.add(transformer.tansform(s));
		}

		return transformed;
	}

	public static <E> Iterator<E> roundRobinIterator(final Collection<E> collection) {
		return new Iterator<E>() {

			Collection<E> localCollection = CollectionsUtil.newCollection(collection);
			Iterator<E> localIterator = localCollection.iterator();

			@Override
			public boolean hasNext() {
				return true;
			}

			@Override
			public E next() {
				if (!localIterator.hasNext()) {
					localIterator = localCollection.iterator();
				}

				return localIterator.next();
			}

			@Override
			public void remove() {
				throw new IllegalArgumentException("remove not allowd");
			}
		};
	}
}
