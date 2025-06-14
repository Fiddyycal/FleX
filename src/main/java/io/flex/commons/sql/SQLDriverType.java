package io.flex.commons.sql;

public enum SQLDriverType {
	
	SQLITE("sqlite", "SQL", "TBL_NAME",   "NAME",       "TYPE",       "table",      "SQLITE_MASTER",             "SQLITE_MASTER"),
	MYSQL("mysql",   "*",   "TABLE_NAME", "TABLE_NAME", "TABLE_TYPE", "BASE TABLE", "INFORMATION_SCHEMA.TABLES", "INFORMATION_SCHEMA.COLUMNS");

	public static final int SELECT_ALL = 0, SELECT_COLUMN_TABLE_NAME = 1, TABLE_NAME = 2, TABLE_TYPE = 3, BASE_TABLE = 4, SCHEMA_TABLES = 5, SCHEMA_COLUMNS = 6;
	
	private String jdbc;
	
	private String[] keys;
	
	private SQLDriverType(String jdbc, String selectAll, String selectColumnTableName, String tableName, String tableType, String baseTable, String schemaTables, String schemaColumns) {
		this.jdbc = jdbc;
		this.keys = new String[] { selectAll, selectColumnTableName, tableName, tableType, baseTable, schemaTables, schemaColumns };
	}
	
	public String getKey(int key) {
		return this.keys[key];
	}
	
	@Override
	public String toString() {
		return this.jdbc;
	}
	
}
