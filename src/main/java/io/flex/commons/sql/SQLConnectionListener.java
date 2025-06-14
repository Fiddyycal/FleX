package io.flex.commons.sql;

import java.sql.ResultSet;

public class SQLConnectionListener {

	public void onConnect() {}

	@Deprecated
	public void onSync() {}

	public void onValidate() {}

	public void onCreate(String table) {}

	public void onExecute(String query, boolean returned) {}

	public void onResult(String query, ResultSet result) {}

	public void onUpdate(String query, int affected) {}

	public void onClose() {}
	
}
