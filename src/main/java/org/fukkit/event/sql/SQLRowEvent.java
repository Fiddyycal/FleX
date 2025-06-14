package org.fukkit.event.sql;

import io.flex.commons.sql.SQLDatabase;
import io.flex.commons.sql.SQLRowWrapper;

public class SQLRowEvent extends SQLTableEvent {

	private SQLRowWrapper row;
	
	public SQLRowEvent(SQLDatabase connection, String table, SQLRowWrapper row) {
		
		super(connection, table);
		
		this.row = row;
		
	}
	
	public SQLRowWrapper getRow() {
		return this.row;
	}

}
