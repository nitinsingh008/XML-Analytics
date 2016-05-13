package com.concept.crew.util;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.Date;
import java.util.List;



public final class StatementHelper {

	public static void setActive(PreparedStatement stmt, int index, Boolean param) throws SQLException {
		setActive(stmt, index, param, null);
	}

	public static void setActive(PreparedStatement stmt, int index, Boolean param, List<String> captureParameters) throws SQLException {
		String flagged = null;

		if (GeneralUtil.isNull(param)) {
			stmt.setNull(index, Types.NULL);
		} else {
			flagged = param ? "A" : "I";
			stmt.setString(index, flagged);
		}

		buildQueryLog(flagged, captureParameters);
	}

	public static void setBoolean(PreparedStatement stmt, int index, Boolean param) throws SQLException {
		setBoolean(stmt, index, param, null);
	}

	public static void setBoolean(PreparedStatement stmt, int index, Boolean param, List<String> captureParameters) throws SQLException {
		String flagged = null;

		if (GeneralUtil.isNull(param)) {
			stmt.setNull(index, Types.NULL);
		} else {
			flagged = param ? "Y" : "N";
			stmt.setString(index, flagged);
		}

		buildQueryLog(flagged, captureParameters);
	}

	public static void setDate(PreparedStatement stmt, int index, Date param) throws SQLException {
		setDate(stmt, index, param, null);
	}

	public static void setDate(PreparedStatement stmt, int index, Date param, List<String> captureParameters) throws SQLException {
		if (GeneralUtil.isNull(param)) {
			stmt.setNull(index, Types.NULL);
		} else {
			stmt.setDate(index, new java.sql.Date(param.getTime()));
		}

		buildQueryLog(formatDate(param), captureParameters);
	}

	public static void setDouble(PreparedStatement stmt, int index, Double param) throws SQLException {
		setDouble(stmt, index, param, null);
	}

	public static void setDouble(PreparedStatement stmt, int index, Double param, List<String> captureParameters) throws SQLException {
		if (GeneralUtil.isNull(param)) {
			stmt.setNull(index, Types.NULL);
		} else {
			stmt.setDouble(index, param);
		}

		buildQueryLog(param, captureParameters);
	}

	public static void setFormattedDouble(PreparedStatement stmt, int index, String format, Double param) throws SQLException {
		setFormattedDouble(stmt, index, format, param, null);
	}

	public static void setFormattedDouble(PreparedStatement stmt, int index, String format, Double param, List<String> captureParameters) throws SQLException {
		String formatted = null;

		if (GeneralUtil.isNull(param)) {
			stmt.setNull(index, Types.NULL);
		} else {
			formatted = String.format(format, param);
			stmt.setDouble(index, NumberUtil.convertToDouble(formatted));
		}

		buildQueryLog(formatted, captureParameters);
	}

	public static void setFormattedString(PreparedStatement stmt, int index, String format, String param) throws SQLException {
		setFormattedString(stmt, index, format, param, null);
	}

	public static void setFormattedString(PreparedStatement stmt, int index, String format, String param, List<String> captureParameters) throws SQLException {
		String formatted = null;

		if (GeneralUtil.isNull(param)) {
			stmt.setNull(index, Types.NULL);
		} else {
			formatted = String.format(format, param);
			stmt.setString(index, formatted);
		}

		buildQueryLog(formatted, captureParameters);
	}

	public static void setInteger(PreparedStatement stmt, int index, Integer param) throws SQLException {
		setInteger(stmt, index, param, null);
	}

	public static void setInteger(PreparedStatement stmt, int index, Integer param, List<String> captureParameters) throws SQLException {
		if (GeneralUtil.isNull(param)) {
			stmt.setNull(index, Types.NULL);
		} else {
			stmt.setInt(index, param);
		}

		buildQueryLog(param, captureParameters);
	}

	public static void setLong(PreparedStatement stmt, int index, Long param) throws SQLException {
		setLong(stmt, index, param, null);
	}

	public static void setLong(PreparedStatement stmt, int index, Long param, List<String> captureParameters) throws SQLException {
		if (GeneralUtil.isNull(param)) {
			stmt.setNull(index, Types.NULL);
		} else {
			stmt.setLong(index, param);
		}

		buildQueryLog(param, captureParameters);
	}

	public static void setString(PreparedStatement stmt, int index, String param) throws SQLException {
		setString(stmt, index, param, null);
	}

	public static void setString(PreparedStatement stmt, int index, String param, List<String> captureParameters) throws SQLException {
		if (GeneralUtil.isNull(param)) {
			stmt.setNull(index, Types.NULL);
		} else {
			stmt.setString(index, param);
		}

		buildQueryLog(escapeString(param), captureParameters);
	}

	public static void setTimestamp(PreparedStatement stmt, int index, Date param) throws SQLException {
		setTimestamp(stmt, index, param, null);
	}

	public static void setTimestamp(PreparedStatement stmt, int index, Date param, List<String> captureParameters) throws SQLException {
		if (GeneralUtil.isNull(param)) {
			stmt.setNull(index, Types.NULL);
		} else {
			stmt.setTimestamp(index, new Timestamp(param.getTime()));
		}

		buildQueryLog(formatTimestamp(param), captureParameters);
	}

	public static void setTimestamp(PreparedStatement stmt, int index, Timestamp param) throws SQLException {
		setTimestamp(stmt, index, param, null);
	}

	public static void setTimestamp(PreparedStatement stmt, int index, Timestamp param, List<String> captureParameters) throws SQLException {
		if (GeneralUtil.isNull(param)) {
			stmt.setNull(index, Types.NULL);
		} else {
			stmt.setTimestamp(index, param);
		}

		buildQueryLog(formatTimestamp(param), captureParameters);
	}

	public static void setVersion(PreparedStatement stmt, int index, Long param) throws SQLException {
		setVersion(stmt, index, param, null);
	}

	public static void setVersion(PreparedStatement stmt, int index, Long param, List<String> captureParameters) throws SQLException {
		long version = 1L;

		if (GeneralUtil.isNull(param)) {
			stmt.setLong(index, version);
		} else {
			version = param + 1;
			stmt.setLong(index, version);
		}

		buildQueryLog(version, captureParameters);
	}

	private static void buildQueryLog(Object param, List<String> captureParameters) {
		if (GeneralUtil.isNull(captureParameters))
			return;

		if (GeneralUtil.isNull(param))
			captureParameters.add("NULL");
		else
			captureParameters.add("'" + param + "'");
	}

	private static Object escapeString(String param) {
		if (GeneralUtil.isNull(param))
			return null;

		return param.replaceAll("'", "''");
	}

	private static String formatDate(Date param) {
		if (GeneralUtil.isNull(param))
			return null;

		return DateUtil.DD_MMM_YYYY.format(param);
	}

	private static String formatTimestamp(Date param) {
		if (GeneralUtil.isNull(param))
			return null;

		return DateUtil.DD_MMM_YYYY_HH_MM_SS_AP.format(param);
	}

	public static boolean isSelectQuery(String sqlQuery) {
		return sqlQuery.trim().toUpperCase().startsWith("SELECT");
	}

	public static boolean isDeleteQuery(String sqlQuery) {
		return sqlQuery.trim().toUpperCase().startsWith("DELETE");
	}

	public static boolean isInsertQuery(String sqlQuery) {
		return sqlQuery.trim().toUpperCase().startsWith("INSERT");
	}

	public static boolean isMergeQuery(String sqlQuery) {
		return sqlQuery.trim().toUpperCase().startsWith("MERGE");
	}

	public static boolean close(Statement stmt) {
		if (GeneralUtil.isNotNull(stmt)) {
			try {
				stmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
				return Boolean.FALSE;
			}
		}

		return Boolean.TRUE;
	}
}
