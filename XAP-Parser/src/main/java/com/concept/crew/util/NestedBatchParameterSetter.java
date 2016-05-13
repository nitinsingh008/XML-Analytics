package com.concept.crew.util;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public interface NestedBatchParameterSetter<T, T1>
{
    void setParam(PreparedStatement ps, int rowNum, T parent, T1 theObj) throws SQLException;
}
