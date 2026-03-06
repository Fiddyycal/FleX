package io.flex.commons.sql;

public class SQLColumn {

	private boolean unique = false;
	private boolean primary = false;
	private boolean notNull = false;
	private boolean increment = false;
	
	private SQLDataType type;
	
	private String name;
	
	private Object def;
	
	private int length = -1;
	
	private SQLColumn(String name, SQLDataType type) {
		this.name = name;
		this.type = type;
	}
	
	public String getName() {
		return this.name;
	}
	
	public SQLDataType getType() {
		return this.type;
	}
	
	public Object getDefault() {
		return this.def;
	}
	
	public int getLength() {
		return this.length;
	}
	
	public SQLColumn def(Object def) {
		this.def = def;
		return this;
	}
	
	public SQLColumn unique() {
		this.unique = true;
		return this;
	}
	
	public SQLColumn primary() {
		this.primary = true;
		this.notNull = true;
		return this;
	}
	
	public SQLColumn notNull() {
		this.notNull = true;
		return this;
	}
	
	public SQLColumn autoIncrement() {
		
		if (this.type != SQLDataType.INT && this.type != SQLDataType.BIGINT)
	        throw new IllegalStateException("AUTO_INCREMENT requires an integer type.");
		
		this.increment = true;
	    this.notNull = true;
		return this;
		
	}
	
	public boolean isUnique() {
		return this.unique;
	}
	
	public boolean isPrimary() {
		return this.primary;
	}
	
	public boolean isNotNull() {
		return this.notNull;
	}
	
	public boolean isAutoIncrement() {
		return this.increment;
	}
	
	public static SQLColumn of(String name, SQLDataType type) {
		return new SQLColumn(name, type);
	}
	
	public static SQLColumn of(String name, SQLDataType type, int length) {
		
		if (type != SQLDataType.CHAR && type != SQLDataType.VARCHAR)
	        throw new IllegalStateException("length requires an char type.");
		
		SQLColumn column = new SQLColumn(name, type);
		
		column.length = length;
		
		return column;
		
	}
	
	@Override
	public String toString() {
		return this.name.toLowerCase();
	}
	
}
