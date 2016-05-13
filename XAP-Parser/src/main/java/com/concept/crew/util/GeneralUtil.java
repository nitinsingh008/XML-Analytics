package com.concept.crew.util;

import java.util.Collection;
import java.util.Map;

public final class GeneralUtil {

	public static <T> T nvl(T in, T out) {
		if (in == null) {
			return out;
		} else {
			return in;
		}
	}

	public static Boolean anyOneIsNull(Object... objects) {
		for (Object object : objects) {
			if (isNull(object))
				return Boolean.TRUE;
		}

		return Boolean.FALSE;
	}

	public static Boolean anyOneIsNotNull(Object... objects) {
		for (Object object : objects) {
			if (isNotNull(object))
				return Boolean.TRUE;
		}

		return Boolean.FALSE;
	}

	public static Boolean allNotNull(Object... objects) {
		if (anyOneIsNull(objects))
			return Boolean.FALSE;

		return Boolean.TRUE;
	}

	public static Boolean allNull(Object... objects) {
		if (anyOneIsNotNull(objects))
			return Boolean.FALSE;

		return Boolean.TRUE;
	}

	public static Boolean isNull(Object object) {
		if (object == null)
			return Boolean.TRUE;

		return Boolean.FALSE;
	}

	public static boolean isNotNull(Object object) {
		return !isNull(object);
	}

	/**
	 * @deprecated use {@link MapUtil.isEmpty(map)} instead.
	 */
	public static <K, V> Boolean isEmpty(Map<K, V> map) {
		return MapUtil.isEmpty(map);
	}

	/**
	 * @deprecated use {@link MapUtil.isNotEmpty(map)} instead.
	 */
	public static <K, V> Boolean notEmpty(Map<K, V> map) {
		return MapUtil.isNotEmpty(map);
	}

	/**
	 * @deprecated use {@link CollectionsUtil.isEmpty(collection)} instead.
	 */
	public static <T> Boolean isEmpty(Collection<T> collection) {
		return CollectionsUtil.isEmpty(collection);
	}

	/**
	 * @deprecated use {@link CollectionsUtil.isNotEmpty(collection)} instead.
	 */
	public static <T> Boolean notEmpty(Collection<T> collection) {
		return CollectionsUtil.isNotEmpty(collection);
	}

	public static Boolean isEmpty(Object[] array) {
		if (isNull(array))
			return Boolean.TRUE;

		return array.length == 0;
	}

	public static Boolean allAreEqual(Object... objects) {
		if (isNull(objects))
			return Boolean.FALSE;

		if (objects.length == 0)
			return Boolean.FALSE;

		if (objects.length == 1)
			return Boolean.TRUE;

		if (allNull(objects))
			return Boolean.TRUE;

		for (int i = 1; i < objects.length; i++) {
			Object first = objects[0];
			Object second = objects[i];
			if (anyOneIsNull(first, second))
				return Boolean.FALSE;

			if (!first.equals(second)) {
				return Boolean.FALSE;
			}
		}

		return Boolean.TRUE;
	}

	public static Boolean referenceEquals(Object obj1, Object obj2) {
		return obj1 == obj2;
	}
}
