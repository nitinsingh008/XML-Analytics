package com.concept.crew.util;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;


public class MapUtil {

	private static final SortedMap<Object, Object> EMPTY_SORTED_MAP = Collections.unmodifiableSortedMap(new TreeMap<Object, Object>());

	@SuppressWarnings("unchecked")
	public static <K, V> SortedMap<K, V> emptySortedMap() {
		return (SortedMap<K, V>) EMPTY_SORTED_MAP;
	}

	public static <K, V> Boolean isEmpty(final Map<K, V> map) {
		if (GeneralUtil.isNull(map))
			return Boolean.TRUE;

		return map.isEmpty();
	}

	public static <K, V> Boolean isNotEmpty(final Map<K, V> map) {
		return !isEmpty(map);
	}

	public static <K, V> Collection<V> getValues(final Map<K, V> map, final Set<K> keys) {
		if (CollectionsUtil.isEmpty(keys))
			return Collections.emptyList();

		Collection<Entry<K, V>> selectedEntries = Iterate.select(map.entrySet(), new Select<Entry<K, V>>() {

			@Override
			public boolean select(Entry<K, V> entry) {
				return keys.contains(entry.getKey());
			}
		});

		return Iterate.<Entry<K, V>, V> transform(selectedEntries, new Transformer<Entry<K, V>, V>() {

			@Override
			public V tansform(Entry<K, V> entry) {
				return entry.getValue();
			}

		});
	}

	public static <K, V> Map<K, V> newMap(Object[][] objects) {
		Map<K, V> treeMap = new HashMap<K, V>();
		hydrateNewMap(treeMap, objects);
		return treeMap;
	}

	public static <K, V> SortedMap<K, V> newSorterMap(Object[][] objects) {
		SortedMap<K, V> treeMap = new TreeMap<K, V>();
		hydrateNewMap(treeMap, objects);
		return treeMap;
	}

	@SuppressWarnings("unchecked")
	private static <V, K> void hydrateNewMap(Map<K, V> map, Object[][] objects) {
		for (int i = 0; i < objects.length; i++) {
			Object[] object = objects[i];
			map.put((K) object[0], (V) object[1]);
		}
	}

	public static <K, V> Map<K, V> aggregate(Map<K, V>... maps) {
		Map<K, V> aggregated = new HashMap<K, V>();
		for (Map<K, V> map : maps) {
			aggregated.putAll(map);
		}

		return aggregated;
	}

	public static <K, V> Map<K, V> newMapWithDefaultValue(List<K> keyList, V defaultValue) {
		Map<K, V> map = new HashMap<K, V>();
	    if(keyList != null) {
	    	for(K key : keyList) {
	    		map.put(key, defaultValue);
	        }	
	    }
	    return map;
	}
	
    
	public static void main(String[] args) {
		Map<String, String> map = new HashMap<String, String>();
		map.put("A", "1");
		map.put("B", "2");
		map.put("C", "3");
		map.put("D", "4");

		System.out.println(getValues(map, CollectionsUtil.newSet("D", "B", "E")));

		Map<String, String> emptyMap = Collections.emptyMap();
		System.out.println(isEmpty(emptyMap));

		emptyMap = new HashMap<String, String>();
		System.out.println(isEmpty(emptyMap));

		emptyMap.put("A", null);
		System.out.println(isEmpty(emptyMap));
	}
}
