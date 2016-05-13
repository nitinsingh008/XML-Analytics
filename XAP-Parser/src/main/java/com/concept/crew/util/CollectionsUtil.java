package com.concept.crew.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class CollectionsUtil {

	public static <T> Collection<T> aggregate(Collection<T>... collections) {
		Collection<T> newAggregation = newCollection((Collection<T>) null);
		for (Collection<T> collection : collections) {
			newAggregation.addAll(collection);
		}

		return newAggregation;
	}

	public static <T> Collection<T> aggregate(Collection<T> collection, T... elements) {
		Collection<T> newCollection = newCollection(collection);
		for (T t : elements) {
			newCollection.add(t);
		}

		return newCollection;
	}

	public static <T> Collection<T> newCollection(Collection<T> collection) {
		if (isNotEmpty(collection)) {
			ArrayList<T> arrayList = new ArrayList<T>(collection.size());
			arrayList.addAll(collection);
			return arrayList;
		}

		return new ArrayList<T>();
	}

	public static <T> Collection<T> asCollection(T[] elements) {
		return newCollection(elements);
	}

	public static <T> Collection<T> newCollection(T... elements) {
		return newList(elements);
	}

	public static <T> Collection<T> toCollection(T element) {
		List<T> list = new ArrayList<T>(1);
		list.add(element);
		return list;
	}

	public static <T> Set<T> asSet(Collection<T> collection) {
		if (isNotEmpty(collection))
			return new HashSet<T>(collection);

		return new HashSet<T>();
	}

	public static <T> Set<T> asSet(T[] elements) {
		return newSet(elements);
	}

	public static <T> Set<T> newSet(T... elements) {
		return asSet(Arrays.asList(elements));
	}

	public static <T> Set<T> toSet(T element) {
		Set<T> set = new HashSet<T>(1);
		set.add(element);
		return set;
	}

	public static <T> List<T> asList(Collection<T> collection) {
		if (isNotEmpty(collection))
			return new ArrayList<T>(collection);

		return new ArrayList<T>();
	}

	public static <T> List<T> asList(T[] elements) {
		return newList(elements);
	}

	public static <T> List<T> newList(T... elements) {
		if (GeneralUtil.isNotNull(elements)) {
			ArrayList<T> arrayList = new ArrayList<T>(elements.length);
			arrayList.addAll(Arrays.asList(elements));
			return arrayList;
		}

		return new ArrayList<T>();
	}

	public static <T> List<T> toList(T element) {
		List<T> list = new ArrayList<T>(1);
		list.add(element);
		return list;
	}

	public static <T> Collection<Collection<T>> partition(final Collection<T> collection, final int size) {
		return Iterate.partition(collection, size);
	}

	public static <T> Boolean areEqual(final Collection<T> collection1, final Collection<T> collection2, Comparator<T> comparator) {
		if (!areSameSize(collection1, collection2))
			return Boolean.FALSE;

		ArrayList<T> sortedList1 = new ArrayList<T>(collection1.size());
		sortedList1.addAll(collection1);
		Collections.sort(sortedList1, comparator);

		ArrayList<T> sortedList2 = new ArrayList<T>(collection2.size());
		sortedList2.addAll(collection2);
		Collections.sort(sortedList2, comparator);

		return collection1.equals(collection2);
	}

	public static <T> Boolean areSameSize(final Collection<T> collection1, final Collection<T> collection2) {
		if (isEmpty(collection1) && isEmpty(collection2))
			return Boolean.TRUE;

		if (collection1 == collection2)
			return Boolean.TRUE;

		if (isNotEmpty(collection1) && isNotEmpty(collection2) && (collection1.size() - collection2.size()) == 0)
			return Boolean.TRUE;

		return Boolean.FALSE;
	}

	public static <T> Boolean isEmpty(Collection<T> collection) {
		if (GeneralUtil.isNull(collection))
			return Boolean.TRUE;

		return collection.isEmpty();
	}

	public static <T> Boolean isNotEmpty(Collection<T> collection) {
		return !isEmpty(collection);
	}

	public static <T> Boolean hasMinimunCount(Collection<T> collection, int count) {
		return !isEmpty(collection) && collection.size() >= count;
	}

	public static <T> Boolean hasMaximumCount(Collection<T> collection, int count) {
		return !isEmpty(collection) && collection.size() == count;
	}

	public static <T> String join(Iterable<T> iterable, String delimiter) {
		return StringUtil.join(iterable, delimiter);
	}

	public static <T> boolean containsAll(Set<T> set, T... elements) {
		return set.containsAll(newCollection(elements));
	}

	public static <T> boolean containsAny(Set<T> set, T... elements) {
		return !Collections.disjoint(set, newCollection(elements));
	}

/*	public static <T> Collection<T> intersection(Collection<T> collection1, Collection<T> collection2) {
		return common(collection1, collection2, new AsItIsTransformer<T>());
	}*/

	public static <T, R> Collection<T> common(Collection<T> collection1, Collection<R> collection2, Transformer<T, R> transformer) {
		if (isEmpty(collection1) || isEmpty(collection2))
			return Collections.emptyList();

		Collection<T> common = new ArrayList<T>();
		for (T t : collection1) {
			R tansformed = transformer.tansform(t);
			if (collection2.contains(tansformed))
				common.add(t);
		}

		return common;
	}

/*	public static <T> Collection<T> disjoint(Collection<T> collection1, Collection<T> collection2) {
		if (isEmpty(collection1) && isEmpty(collection2))
			return Collections.emptyList();

		if (isEmpty(collection1) && isNotEmpty(collection2))
			return collection2;

		if (isEmpty(collection2) && isNotEmpty(collection1))
			return collection1;

		Collection<T> common = intersection(collection1, collection2);

		Collection<T> disjoint = new ArrayList<T>();
		for (T t : collection1) {
			if (!common.contains(t))
				disjoint.add(t);
		}

		for (T t : collection2) {
			if (!common.contains(t))
				disjoint.add(t);
		}

		return disjoint;
	}*/
}
