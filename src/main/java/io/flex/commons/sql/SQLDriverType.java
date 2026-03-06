package io.flex.commons.sql;

public enum SQLDriverType {
	
	MYSQL("mysql");
	
	private String jdbc;
	
	private SQLDriverType(String jdbc) {
		this.jdbc = jdbc;
	}
	
	@Override
	public String toString() {
		return this.jdbc;
	}
	
}
