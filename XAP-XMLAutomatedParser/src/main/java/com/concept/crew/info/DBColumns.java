package com.concept.crew.info;

public class DBColumns 
{
	private String name;
	private String dataType;
	private int    size;
	private int position;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDataType() {
		return dataType;
	}
	public void setDataType(String dataType) {
		this.dataType = dataType;
	}
	public int getSize() {
		return size;
	}
	public void setSize(int size) {
		this.size = size;
	}

	public int getPosition() {
		return position;
	}
	public void setPosition(int position) {
		this.position = position;
	}
	@Override
	public String toString() {
		return "DBColumns [name=" + name + ", dataType=" + dataType + ", size="
				+ size + "]";
	}
	
}
