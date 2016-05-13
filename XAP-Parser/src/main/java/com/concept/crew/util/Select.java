package com.concept.crew.util;

public interface Select<T> {
	class ALL<T> implements Select<T> {

		@Override
		public boolean select(T t) {
			return true;
		}
	}

	class NONE<T> implements Select<T> {

		@Override
		public boolean select(T t) {
			return false;
		}
	}

	boolean select(T t);
}
