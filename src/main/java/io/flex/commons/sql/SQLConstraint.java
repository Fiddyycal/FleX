package io.flex.commons.sql;

public enum SQLConstraint {
	
	NOT_NULL, UNIQUE, PRIMARY_KEY, FOREIGN_KEY, CHECK, DEFAULT, INDEX;
	
	@Override
	public String toString() {
		return super.name().replace("_", " ");
	}

}
