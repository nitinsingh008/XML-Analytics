package com.concept.crew.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StoredProcResult<T>
{
    public List<T> list = new ArrayList<T>();;
    public Map<Integer, Object> outParamMap = new HashMap<Integer, Object>();
    public int executeUpdateReturn = 0;
}
