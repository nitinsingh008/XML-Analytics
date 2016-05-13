package com.concept.crew.util;

import java.util.List;

public interface ListFetcher<T, T1>
{
    public List<T1> fetch(T obj);
}
