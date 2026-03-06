package io.flex.commons.sql;

import java.sql.Blob;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import io.flex.FleX.Task;
import io.flex.commons.Nullable;
import io.flex.commons.Severity;
import io.flex.commons.console.Console;

public class SQLRow {
	
	private SQLTable table;
	
	private SQLDatabase database;
	
    private Map<SQLColumn, Object> entries = new HashMap<SQLColumn, Object>();
	
    public Map<SQLColumn, Object> getEntries() {
		return this.entries;
	}
    
    private boolean stale = false;
	
	SQLRow() throws SQLException {
		this(null, null, null);
	}
	
    // Keep this package private
	SQLRow(SQLDatabase database, SQLTable table, SQLMap entries) throws SQLException {
		
		if (database == null && table == null && entries == null)
			return;
		
		for (Entry<String, Object> verify : entries.entrySet()) {
			
			String column = verify.getKey();
			Object value = verify.getValue();
			
			SQLColumn col = table.getColumn(column);
			
			if (col != null)
				this.entries.put(table.getColumn(column), value);
			
			else throw new IllegalArgumentException("The table '" + table + "' doesn't have the column '" + column + "' in it's schema. Reminder, case sensitivity is being enforced.");
			
		}
		
		this.table = table;
		this.database = database;
		
	}
	
	public SQLTable getTable() {
		return this.table;
	}
	
	public <V> V get(String key) {
		return this.get(key, null);
	}
	
	@SuppressWarnings("unchecked")
	public <V> V get(String key, @Nullable V def) {
		
	    if (key == null)
	    	return def;
	    
	    SQLColumn column = this.entries.keySet().stream().filter(c -> c.getName().equalsIgnoreCase(key)).findFirst().orElse(null);
	    
	    if (column == null)
	    	return def;
	    
	    Object value = this.entries.get(column);
	    
	    if (value == null)
	    	return def;
	    
	    if (def instanceof String || value instanceof String)
	        return (V) value.toString();
	    
	    return (V) value;
	    
	}
	
	// Primitives
	public int getInt(String key) {
		return this.getInt(key, -1);
	}
	
	public int getInt(String key, int def) {
		
	    Object obj = this.get(key, def);
	    
	    if (obj instanceof Number)
	    	return ((Number)obj).intValue();
	    
	    try {
	        return Integer.parseInt(obj.toString());
	    } catch (NumberFormatException e) {
	        return def;
	    }
	    
	}
	
	public boolean getBoolean(String key) {
		return this.getBoolean(key, false);
	}
	
	public boolean getBoolean(String key, boolean def) {
		
	    Object obj = this.get(key, def);
	    
	    if (obj == null)
	    	return def;
	    
	    if (obj instanceof Boolean)
	        return (Boolean)obj;
	    
	    if (obj instanceof Number)
	        return ((Number) obj).intValue() == 1;
	    
	    String str = obj.toString().toLowerCase();
	    
	    if (str.equals("true") || str.equals("1"))
	    	return true;
	    
	    if (str.equals("false") || str.equals("0"))
	    	return false;
	    
	    return def;
	    
	}
	
	public byte getByte(String key) {
		return this.getByte(key, (byte)-1);
	}
	
	public byte getByte(String key, @Nullable byte def) {
		
	    Object obj = this.get(key, def);
	    
		try {
			return (byte)obj;
		} catch (ClassCastException e) {
			try {
				return (byte) (obj != null ? Byte.parseByte(obj.toString()) : -1);
			} catch (NumberFormatException | NullPointerException fail) {
				return def;
			}
		}
		
	}
	
	public short getShort(String key) {
		return this.getShort(key, (short)-1);
	}
	
	public short getShort(String key, @Nullable short def) {
		
	    Object obj = this.get(key, def);
	    
		try {
			return (short)obj;
		} catch (ClassCastException e) {
			try {
				return (short) (obj != null ? Short.parseShort(obj.toString()) : def);
			} catch (NumberFormatException | NullPointerException fail) {
				return def;
			}
		}
		
	}
	
	public double getDouble(String key) {
		return this.getDouble(key, -1D);
	}
	
	public double getDouble(String key, double def) {
		
	    Object obj = this.get(key, def);
	    
	    if (obj instanceof Number)
	    	return ((Number)obj).doubleValue();
	    
	    try {
	        return Double.parseDouble(obj.toString());
	    } catch (NumberFormatException e) {
	        return def;
	    }
	    
	}
	
	public long getLong(String key) {
		return this.getLong(key, -1L);
	}
	
	public long getLong(String key, long def) {
		
	    Object obj = this.get(key, def);
	    
	    if (obj instanceof Number)
	    	return ((Number)obj).longValue();
	    
	    try {
	        return Long.parseLong(obj.toString());
	    } catch (NumberFormatException e) {
	        return def;
	    }
	    
	}
	
	public float getFloat(String key) {
		return this.getFloat(key, -1f);
	}
	
	public float getFloat(String key, float def) {
		
	    Object obj = this.get(key, def);
	    
	    if (obj instanceof Number)
	    	return ((Number)obj).floatValue();
	    
	    try {
	        return Float.parseFloat(obj.toString());
	    } catch (NumberFormatException e) {
	        return def;
	    }
	    
	}
	
	// Java
	public String getString(String key) {
		return this.getString(key, null);
	}
	
	public String getString(String key, @Nullable String def) {
		
	    Object obj = this.get(key, def);
	    
		try {
			return (String)obj;
		} catch (ClassCastException e) {
			return obj != null ? obj.toString() : def;
		}
		
	}
	
	// File
	public Blob getBlob(String key) {
		return this.getBlob(key, null);
	}
	
	public Blob getBlob(String key, @Nullable Blob def) {
		try {
			return (Blob) this.get(key, def);
		} catch (ClassCastException e) {
			return def;
		}
	}
	
	// File
	public byte[] getByteArray(String key) {
		return this.getByteArray(key, null);
	}
	
	public byte[] getByteArray(String key, @Nullable byte[] def) {
		try {
			return (byte[]) this.get(key, def);
		} catch (ClassCastException e) {
			return def;
		}
	}
	
	public <V> SQLRow set(String column, @Nullable V value) {
		
		SQLColumn col = this.table.getColumn(column);
		
		if (col == null)
			throw new IllegalArgumentException("The table '" + this.table + "' doesn't have the column '" + column + "' in it's schema. Reminder, case sensitivity is being enforced.");
			
		this.entries.put(col, value);
		this.stale = true;
		
		return this;
		
	}
	
	public int update() throws SQLException {
		return this.update(false);
	}
	
	public int update(boolean force) throws SQLException {
		
	    if (this.database == null)
	        return 0;
	    
	    if (!force && !this.stale)
	        return 0;
	    
	    Set<SQLColumn> primaryColumns = this.table.getPrimaryColumns();
	    
	    if (primaryColumns == null || primaryColumns.isEmpty())
	        throw new SQLException("Cannot update row without a primary column.");
	    
	    for (SQLColumn pk : primaryColumns)
	        if (!this.entries.keySet().stream().anyMatch(e -> e.getName().equalsIgnoreCase(pk.getName())))
	            throw new SQLException("Primary key value must be defined for the '" + pk.getName() + "' primary column.");
	    
	    List<Entry<SQLColumn, Object>> nonPrimary = new ArrayList<>();
	    
	    for (Entry<SQLColumn, Object> entry : this.entries.entrySet()) {
	    	
	        boolean primary = primaryColumns.stream().anyMatch(pk -> pk.getName().equalsIgnoreCase(entry.getKey().getName()));
	        
	        if (!primary)
	        	nonPrimary.add(entry);
	        
	    }
	    
	    if (nonPrimary.isEmpty())
	        return 0;
	    
	    StringBuilder updateBuilder = new StringBuilder("UPDATE ").append(this.table).append(" SET ");
	    
	    for (int i = 0; i < nonPrimary.size(); i++) {
	    	
	        if (i > 0)
	            updateBuilder.append(", ");

	        updateBuilder.append(SQLDataType.IDENTIFIER_QUOTE).append(nonPrimary.get(i).getKey().getName()).append(SQLDataType.IDENTIFIER_QUOTE).append(" = ?");
	        
	    }
	    
	    StringBuilder whereBuilder = new StringBuilder(" WHERE ");
	    
	    int whereCount = 0;
	    
	    for (SQLColumn pk : primaryColumns) {
	    	
	        if (whereCount++ > 0)
	            whereBuilder.append(" AND ");
	        
	        whereBuilder.append(SQLDataType.IDENTIFIER_QUOTE).append(pk.getName()).append(SQLDataType.IDENTIFIER_QUOTE).append(" = ?");
	        
	    }
	    
	    String query = updateBuilder.toString() + whereBuilder.toString();
	    
	    if (query.contains("flex_user")) {
	    	
	        long placeholderCount = query.chars().filter(ch -> ch == '?').count();
	        
	        System.out.println("[FLEX USER DEBUG] Query: " + query);
	        System.out.println("[FLEX USER DEBUG] Placeholder count: " + placeholderCount);
	        System.out.println("[FLEX USER DEBUG] Non-primary count: " + nonPrimary.size());
	        System.out.println("[FLEX USER DEBUG] Primary count: " + primaryColumns.size());
	        
	    }
	    
	    final int[] affectedArr = { 0 };
	    
	    this.database.open(c -> {
	    	
	        try (PreparedStatement statement = c.getDriverConnection().prepareStatement(query)) {
	        	
	            int index = 1;
	            
	            for (Entry<SQLColumn, Object> entry : nonPrimary) {
	            	
	                Object value = entry.getValue();
	                
	                if (query.contains("flex_user"))
	                    System.out.println("[FLEX USER DEBUG] SET #" + index +" -> " + entry.getKey().getName() + " = " + value);
	                
	                if (value == null)
	                    statement.setNull(index++, entry.getKey().getType().getVendorTypeNumber());
	                
	                else statement.setObject(index++, value);
	                
	            }
	            
	            for (SQLColumn pk : primaryColumns) {
	            	
	                Object value = this.entries.entrySet().stream().filter(e -> e.getKey().getName().equalsIgnoreCase(pk.getName())).map(Entry::getValue).findFirst().orElse(null);
	                
	                if (query.contains("flex_user"))
	                    System.out.println("[FLEX USER DEBUG] WHERE #" + index + " -> " + pk.getName() + " = " + value);
	                
	                statement.setObject(index++, value);
	                
	            }
	            
	            if (query.contains("flex_user")) {
	            	
	                long placeholderCount = query.chars().filter(ch -> ch == '?').count();
	                
	                System.out.println("[FLEX USER DEBUG] Final bind count: " + (index - 1));
	                System.out.println("[FLEX USER DEBUG] Placeholder count: " + placeholderCount);
	                
	            }
	            
	            affectedArr[0] += statement.executeUpdate();
	            
	            this.stale = false;
	            
	        } catch (SQLException e) {
	        	
	            Task.error("SQL", "An error occurred executing update: " + query + ".");
	            Console.log("SQL", Severity.ERROR, new SQLException("Failed query " + query + ": " + e.getMessage()));
	            
	        }
	        
	    });

	    return affectedArr[0];
	}
	
	public static SQLRow emptyRow() {
		try {
			return new SQLRow();
		} catch (SQLException ignore) {
			return null;
		}
	}
	
}
