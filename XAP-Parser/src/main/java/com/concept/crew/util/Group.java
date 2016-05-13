package com.concept.crew.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.concept.crew.util.Transformers.AsItIsTransformer;


public final class Group {

	public static <T, K, R> void group(final Collection<T> collection, final GroupBy<T, K> groupBy, final Filter<K> filter, final Transformer<T, R> transformer, Map<K, Collection<R>> result) {
		if (CollectionsUtil.isEmpty(collection))
			return;

		for (T t : collection) {
			K key = groupBy.groupBy(t);
			if (!filter.filter(key)) {
				Collection<R> data = GeneralUtil.nvl(result.get(key), new ArrayList<R>());
				data.add(transformer.tansform(t));
				result.put(key, data);
			}
		}
	}

	public static <T, K, R> Map<K, Collection<R>> group(final Collection<T> collection, final GroupBy<T, K> groupBy, final Filter<K> filter, final Transformer<T, R> transformer) {
		if (CollectionsUtil.isEmpty(collection))
			return Collections.<K, Collection<R>> emptyMap();

		Map<K, Collection<R>> result = new HashMap<K, Collection<R>>(collection.size());

		group(collection, groupBy, filter, transformer, result);

		return result;
	}

	public static <T, K, R> void group(final Collection<T> collection, final GroupBy<T, K> groupBy, final Transformer<T, R> transformer, Map<K, Collection<R>> result) {
		group(collection, groupBy, new Filter.NONE<K>(), transformer, result);
	}

	public static <T, K, R> Map<K, Collection<R>> group(final Collection<T> collection, final GroupBy<T, K> groupBy, final Transformer<T, R> transformer) {
		return group(collection, groupBy, new Filter.NONE<K>(), transformer);
	}

	public static <T, K> void group(final Collection<T> collection, final GroupBy<T, K> groupBy, final Filter<K> filter, Map<K, Collection<T>> result) {
		group(collection, groupBy, filter, new AsItIsTransformer<T>(), result);
	}

	public static <T, K> Map<K, Collection<T>> group(final Collection<T> collection, final GroupBy<T, K> groupBy, final Filter<K> filter) {
		return group(collection, groupBy, filter, new AsItIsTransformer<T>());
	}

	public static <T, K> void group(final Collection<T> collection, final GroupBy<T, K> groupBy, Map<K, Collection<T>> result) {
		group(collection, groupBy, new Filter.NONE<K>(), new AsItIsTransformer<T>(), result);
	}

	public static <T, K> Map<K, Collection<T>> group(final Collection<T> collection, final GroupBy<T, K> groupBy) {
		return group(collection, groupBy, new Filter.NONE<K>(), new AsItIsTransformer<T>());
	}

	public static <T, K, R> Map<K, R> unique(final Collection<T> collection, final GroupBy<T, K> groupBy, final Filter<K> filter, final Transformer<T, R> transformer, final SelectOne<R> selectOne) {
		if (CollectionsUtil.isEmpty(collection))
			return Collections.<K, R> emptyMap();

		Map<K, Collection<R>> groupedByKey = group(collection, groupBy, filter, transformer);

		Map<K, R> unique = new HashMap<K, R>();
		for (K groupKey : groupedByKey.keySet()) {
			Collection<R> groupValues = groupedByKey.get(groupKey);
			R selected = Iterate.selectOne(groupValues, selectOne);

			unique.put(groupKey, selected);
		}

		return unique;
	}

	public static <T, K, R> Map<K, R> unique(final Collection<T> collection, final GroupBy<T, K> groupBy, final Filter<K> filter, final Transformer<T, R> transformer) {
		return unique(collection, groupBy, filter, transformer, new SelectOne.UNIQUE<R>());
	}

	public static <T, K, R> Map<K, R> unique(final Collection<T> collection, final GroupBy<T, K> groupBy, final Transformer<T, R> transformer, final SelectOne<R> selectOne) {
		return unique(collection, groupBy, new Filter.NONE<K>(), transformer, selectOne);
	}

	public static <T, K, R> Map<K, R> unique(final Collection<T> collection, final GroupBy<T, K> groupBy, final Transformer<T, R> transformer) {
		return unique(collection, groupBy, new Filter.NONE<K>(), transformer);
	}

	public static <T, K> Map<K, T> unique(final Collection<T> collection, final GroupBy<T, K> groupBy, final Filter<K> filter, final SelectOne<T> selectOne) {
		return unique(collection, groupBy, filter, new AsItIsTransformer<T>(), selectOne);
	}

	public static <T, K> Map<K, T> unique(final Collection<T> collection, final GroupBy<T, K> groupBy, final Filter<K> filter) {
		return unique(collection, groupBy, filter, new AsItIsTransformer<T>());
	}

	public static <T, K> Map<K, T> unique(final Collection<T> collection, final GroupBy<T, K> groupBy, final SelectOne<T> selectOne) {
		return unique(collection, groupBy, new Filter.NONE<K>(), new AsItIsTransformer<T>(), selectOne);
	}

	public static <T, K> Map<K, T> unique(final Collection<T> collection, final GroupBy<T, K> groupBy) {
		return unique(collection, groupBy, new Filter.NONE<K>(), new AsItIsTransformer<T>());
	}

	public static <K, V> Boolean isEmpty(final Map<K, ? extends Collection<V>> map) {
		if (MapUtil.isEmpty(map))
			return Boolean.TRUE;

		Collection<V> all = new ArrayList<V>();
		Collection<? extends Collection<V>> values = map.values();
		for (Collection<V> value : values) {
			if (CollectionsUtil.isNotEmpty(value))
				all.addAll(value);
		}

		return all.isEmpty();
	}

	public static <K, V> Boolean isNotEmpty(final Map<K, ? extends Collection<V>> map) {
		return !isEmpty(map);
	}
}
