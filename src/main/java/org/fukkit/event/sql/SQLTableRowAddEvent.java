package org.fukkit.event.sql;

import io.flex.commons.sql.SQLDatabase;
import io.flex.commons.sql.SQLRowWrapper;

public class SQLTableRowAddEvent extends SQLRowEvent {

	public SQLTableRowAddEvent(SQLDatabase connection, String table, SQLRowWrapper row) {
		super(connection, table, row);
	}

}
