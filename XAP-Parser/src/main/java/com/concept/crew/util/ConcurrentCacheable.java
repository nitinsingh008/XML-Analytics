package com.concept.crew.util;

import java.util.concurrent.ConcurrentMap;

public interface ConcurrentCacheable<K, T> {
	void setCache(ConcurrentMap<K, T> cache);
}
