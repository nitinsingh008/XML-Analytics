package com.concept.crew.info;

public class DBDetails {
	 private String jdbcUrl;
	 private String user;
	 private String password;
	 private String dbType;
	 private String drive;
	
	 public DBDetails(String jdbcUrl, String user, String password,
			String dbType, String drive) {
		super();
		this.jdbcUrl = jdbcUrl;
		this.user = user;
		this.password = password;
		this.dbType = dbType;
		this.drive = drive;
	}

	public String getJdbcUrl() {
		return jdbcUrl;
	}

	public String getUser() {
		return user;
	}

	public String getPassword() {
		return password;
	}

	public String getDbType() {
		return dbType;
	}

	public String getDrive() {
		return drive;
	}
	 
}
