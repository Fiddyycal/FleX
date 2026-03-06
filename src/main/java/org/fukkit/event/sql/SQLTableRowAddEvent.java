package org.fukkit.event.sql;

import io.flex.commons.sql.SQLDatabase;
import io.flex.commons.sql.SQLRow;

public class SQLTableRowAddEvent extends SQLRowEvent {

	public SQLTableRowAddEvent(SQLDatabase connection, String table, SQLRow row) {
		super(connection, table, row);
	}

}
