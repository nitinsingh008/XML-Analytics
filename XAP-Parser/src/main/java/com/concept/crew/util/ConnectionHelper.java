package com.concept.crew.util;

import java.sql.Connection;
import java.sql.SQLException;


public final class ConnectionHelper {

	public static boolean close(Connection connection) {
		if (GeneralUtil.isNotNull(connection)) {
			try {
				connection.close();
			} catch (SQLException e) {
				e.printStackTrace();
				return Boolean.FALSE;
			}
		}

		return Boolean.TRUE;
	}
}
