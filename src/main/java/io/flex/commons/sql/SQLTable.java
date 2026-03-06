package io.flex.commons.sql;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SQLTable {

	private String name;
	
	private SQLDatabase database;
	
	private Set<SQLColumn> primary = new HashSet<SQLColumn>();
	
	private Map<String, SQLColumn> columns = new HashMap<String, SQLColumn>();
	
    // Keep this package private
	SQLTable(SQLDatabase database, String name, SQLColumn... columns) throws SQLException {
		
		if (columns == null || columns.length == 0)
			throw new UnsupportedOperationException("Cannot add table without any columns, columns must be defined.");
		
		this.name = name;
		
		for (SQLColumn column : columns) {
			
			if (this.columns.containsKey(column.getName()))
			    throw new SQLException("Duplicate column '" + column.getName() + "' not permitted.");
			
			this.columns.put(column.getName(), column);
			
			if (column.isPrimary())
				this.primary.add(column);
			
		}
		
		this.database = database;
		
	}
	
	public String getName() {
		return this.name;
	}
	
	public Set<SQLColumn> getPrimaryColumns() {
		return this.primary;
	}
	
	public SQLColumn getColumn(String name) {
		return this.columns.get(name);
	}
	
	public Collection<SQLColumn> getColumns() {
		return this.columns.values();
	}
	
	@Override
	public String toString() {
		return this.name.toLowerCase();
	}
	
	public void createIfNotExists() throws SQLException {

	    List<String> primaryKeys = new ArrayList<String>();
	    StringBuilder columns = new StringBuilder();
	    
	    for (SQLColumn column : this.columns.values()) {
	    	
	        if (columns.length() > 0)
	        	columns.append(", ");
	        
	        StringBuilder def = new StringBuilder();
	        
	        def.append(SQLDataType.IDENTIFIER_QUOTE).append(column.getName()).append(SQLDataType.IDENTIFIER_QUOTE).append(" ").append(column.getType().name());
            
            SQLDataType type = column.getType();
	        
	        if (column.getLength() > 0 && (type == SQLDataType.CHAR || type == SQLDataType.VARCHAR))
	            def.append("(").append(column.getLength()).append(")");
	        
	        if (column.isNotNull())
	            def.append(" NOT NULL");
	        
	        if (column.isUnique())
	            def.append(" UNIQUE");
	        
	        if (column.isAutoIncrement() && (type == SQLDataType.INT || type == SQLDataType.BIGINT))
	            def.append(" AUTO_INCREMENT");
	        
	        if (column.getDefault() != null) {
	        	
	            def.append(" DEFAULT ");
	            
	            Object value = column.getDefault();
	            
	            if (type == SQLDataType.BOOLEAN)
	                def.append((Boolean) value ? "TRUE" : "FALSE");
	            
	            else if (type == SQLDataType.VARCHAR || type == SQLDataType.CHAR || type == SQLDataType.TINYTEXT || type == SQLDataType.TEXT)
	                def.append(SQLDataType.STRING_QUOTE).append(value).append(SQLDataType.STRING_QUOTE);
	            
	            else def.append(value);
	            
	        }
	        
	        if (column.isPrimary())
	            primaryKeys.add(column.getName());
	        
	        columns.append(def);
	        
	    }
	    
	    if (!primaryKeys.isEmpty())
	    	columns.append(", PRIMARY KEY (").append(String.join(", ", primaryKeys)).append(")");
	    
	    String sql = "CREATE TABLE IF NOT EXISTS " + this.name + " (" + columns + ")";
	    
	    this.database.execute(sql);
	    
	}
	
}
