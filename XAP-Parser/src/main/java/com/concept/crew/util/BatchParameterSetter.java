package com.concept.crew.util;


import java.sql.PreparedStatement;
import java.sql.SQLException;

public interface BatchParameterSetter<T>
{
    void setParam(PreparedStatement ps, int rowNum, T obj) throws SQLException;
}
