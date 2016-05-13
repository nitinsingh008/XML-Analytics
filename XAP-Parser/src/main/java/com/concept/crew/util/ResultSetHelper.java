package com.concept.crew.util;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.Date;


public final class ResultSetHelper {

	public static Boolean toBoolean(ResultSet rs, int columnIndex) throws SQLException {
		String flag = rs.getString(columnIndex);
		if (rs.wasNull()) {
			return null;
		}

		return StringUtil.toBoolean(flag);
	}

	public static Boolean toBoolean(ResultSet rs, String column) throws SQLException {
		String flag = rs.getString(column);
		if (rs.wasNull()) {
			return null;
		}

		return StringUtil.toBoolean(flag);
	}

	public static Date toDate(ResultSet rs, int columnIndex) throws SQLException {
		Date value = rs.getDate(columnIndex);
		if (rs.wasNull()) {
			return null;
		}

		return value;
	}

	public static Date toDate(ResultSet rs, String column) throws SQLException {
		Date value = rs.getDate(column);
		if (rs.wasNull()) {
			return null;
		}

		return value;
	}

	public static Double toDouble(ResultSet rs, int columnIndex) throws SQLException {
		Double value = rs.getDouble(columnIndex);
		if (rs.wasNull()) {
			return null;
		}
		DecimalFormat dFormat = new DecimalFormat("#.0#####");
		value = Double.parseDouble(dFormat.format(value));
		return value;
	}

	public static Double toDouble(ResultSet rs, String column) throws SQLException {
		Double value = rs.getDouble(column);
		if (rs.wasNull()) {
			return null;
		}
		DecimalFormat dFormat = new DecimalFormat("#.0#####");
		value = Double.parseDouble(dFormat.format(value));
		return value;
	}

	public static Integer toInteger(ResultSet rs, int columnIndex) throws SQLException {
		Integer value = rs.getInt(columnIndex);
		if (rs.wasNull()) {
			return null;
		}

		return value;
	}

	public static Integer toInteger(ResultSet rs, String column) throws SQLException {
		Integer value = rs.getInt(column);
		if (rs.wasNull()) {
			return null;
		}

		return value;
	}

	public static Long toLong(ResultSet rs, int columnIndex) throws SQLException {
		Long value = rs.getLong(columnIndex);
		if (rs.wasNull()) {
			return null;
		}

		return value;
	}

	public static Long toLong(ResultSet rs, String column) throws SQLException {
		Long value = rs.getLong(column);
		if (rs.wasNull()) {
			return null;
		}

		return value;
	}

	public static String toString(ResultSet rs, int columnIndex) throws SQLException {
		String value = rs.getString(columnIndex);
		if (rs.wasNull()) {
			return null;
		}

		return value;
	}

	public static String toString(ResultSet rs, String column) throws SQLException {
		String value = rs.getString(column);
		if (rs.wasNull()) {
			return null;
		}

		return value;
	}

	public static Timestamp toTimestamp(ResultSet rs, int columnIndex) throws SQLException {
		Timestamp value = rs.getTimestamp(columnIndex);
		if (rs.wasNull()) {
			return null;
		}

		return value;
	}

	public static Timestamp toTimestamp(ResultSet rs, String column) throws SQLException {
		Timestamp value = rs.getTimestamp(column);
		if (rs.wasNull()) {
			return null;
		}

		return value;
	}

	public static boolean close(ResultSet resultSet) {
		if (GeneralUtil.isNotNull(resultSet)) {
			try {
				resultSet.close();
			} catch (SQLException e) {
				e.printStackTrace();
				return Boolean.FALSE;
			}
		}

		return Boolean.TRUE;
	}
}
