package io.flex.commons.sql;

import io.flex.commons.Nullable;

public class SQLCondition<T> {
	
	private String key;
	
	private T value;
	
	private boolean caseSensitive, contains;
	
	private SQLCondition(String key) {
		this(key, null);
	}
	
	private SQLCondition(String key, @Nullable T value) {
		this.value = value;
		this.key = key;
	}
	
	public static <T> SQLCondition<T> where(String column) {
		return new SQLCondition<T>(column);
	}
	
	public SQLCondition<T> contains(T value) {
		this.contains = true;
		this.caseSensitive = true;
		this.value = value;
		return this;
	}
	
	public SQLCondition<T> containsIgnoreCase(T value) {
		this.contains = true;
		this.caseSensitive = false;
		this.value = value;
		return this;
	}
	
	public SQLCondition<T> is(T value) {
		this.contains = false;
		this.caseSensitive = true;
		this.value = value;
		return this;
	}
	
	public SQLCondition<T> isIgnoreCase(T value) {
		this.contains = false;
		this.caseSensitive = false;
		this.value = value;
		return this;
	}
	
	public String key() {
		return this.key;
	}
	
	public T value() {
		return this.value;
	}
	
	public boolean isContainsOperation() {
		return this.contains;
	}
	
	public boolean isCaseSensitiveOperation() {
		return this.caseSensitive;
	}
	
	/**
	 * @deprecated Please use {@link SQLCondition#is(Object)} instead.
	 * @throws UnsupportedOperationException
	 */
	@Override
	@Deprecated
	public boolean equals(Object obj) throws UnsupportedOperationException {
		throw new UnsupportedOperationException("Please use {@link SQLCondition#is(Object)} instead.");
	}
	
	/**
	 * @deprecated Please use {@link SQLCondition#is(Object)} instead.
	 * @throws UnsupportedOperationException
	 */
	@Deprecated
	public boolean equalsIgnoreCase(Object obj) throws UnsupportedOperationException {
		throw new UnsupportedOperationException("Please use {@link SQLCondition#isIgnoreCase(Object)} instead.");
	}

}
