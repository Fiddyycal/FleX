package org.fukkit.event.sql;

import io.flex.commons.sql.SQLDatabase;
import io.flex.commons.sql.SQLRow;

public class SQLRowEvent extends SQLTableEvent {

	private SQLRow row;
	
	public SQLRowEvent(SQLDatabase connection, String table, SQLRow row) {
		
		super(connection, table);
		
		this.row = row;
		
	}
	
	public SQLRow getRow() {
		return this.row;
	}

}
