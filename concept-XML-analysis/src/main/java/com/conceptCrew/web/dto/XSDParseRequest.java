package com.conceptCrew.web.dto;

import org.springframework.stereotype.Component;

@Component("XSDParseRequest")
public class XSDParseRequest {

	String parsedXSD;
	Boolean doAll;
	Boolean createScript;
	Boolean createTable;
	Boolean createFramework;
	String databaseType;
	String tnsEntry;
	String userName;
	String password;
	public String getParsedXSD() {
		return parsedXSD;
	}
	public void setParsedXSD(String parseXSD) {
		this.parsedXSD = parseXSD;
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
	
	
}
