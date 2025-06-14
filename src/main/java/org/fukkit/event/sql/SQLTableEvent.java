package org.fukkit.event.sql;

import io.flex.commons.sql.SQLDatabase;

public class SQLTableEvent extends SQLEvent {
	
	private String table;
	
	public SQLTableEvent(SQLDatabase connection, String table) {
		
		super(connection);
		
		this.table = table;
		
	}
	
	public String getTable() {
		return this.table;
	}

}
