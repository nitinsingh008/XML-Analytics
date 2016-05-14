package com.concept.crew.util;

import java.io.Serializable;

public class XSDParseRequest implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	String inputType;
	String databaseTablePostFix;
	String delimiter;
	Boolean haveHeaderData;
	String parsedXSDPath;
	Boolean doAll;
	Boolean createScript;
	Boolean createTable;
	Boolean createFramework;
	String databaseType;
	String tnsEntry;
	String userName;
	String password;
	
	public String getParsedXSDPath() {
		return parsedXSDPath;
	}
	public void setParsedXSDPath(String parsedXSDPath) {
		this.parsedXSDPath = parsedXSDPath;
	}
	public Boolean getDoAll() {
		return doAll;
	}
	public void setDoAll(Boolean doAll) {
		this.doAll = doAll;
	}
	public Boolean getCreateScript() {
		return createScript;
	}
	public void setCreateScript(Boolean createScript) {
		this.createScript = createScript;
	}
	public Boolean getCreateTable() {
		return createTable;
	}
	public void setCreateTable(Boolean createTable) {
		this.createTable = createTable;
	}
	public Boolean getCreateFramework() {
		return createFramework;
	}
	public void setCreateFramework(Boolean createFramework) {
		this.createFramework = createFramework;
	}
	public String getDatabaseType() {
		return databaseType;
	}
	public void setDatabaseType(String databaseType) {
		this.databaseType = databaseType;
	}
	public String getTnsEntry() {
		return tnsEntry;
	}
	public void setTnsEntry(String tnsEntry) {
		this.tnsEntry = tnsEntry;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getInputType() {
		return inputType;
	}
	public void setInputType(String inputType) {
		this.inputType = inputType;
	}
	public String getDatabaseTablePostFix() {
		return databaseTablePostFix;
	}
	public void setDatabaseTablePostFix(String databaseTablePostFix) {
		this.databaseTablePostFix = databaseTablePostFix;
	}
	public String getDelimiter() {
		return delimiter;
	}
	public void setDelimiter(String delimiter) {
		this.delimiter = delimiter;
	}
	public Boolean getHaveHeaderData() {
		return haveHeaderData;
	}
	public void setHaveHeaderData(Boolean haveHeaderData) {
		this.haveHeaderData = haveHeaderData;
	}
	
	
}
