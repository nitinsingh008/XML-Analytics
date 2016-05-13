package com.concept.crew.util;

public interface Command<T> {
	void execute(T t);
}
