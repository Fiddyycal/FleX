package io.flex.commons.file;

import static java.util.Objects.requireNonNull;

public class Variable<T> {

	private String variable;
	private T value;
	
	public Variable(String variable, T value) {
		
		requireNonNull(variable, "variable must not be null");
		
		this.variable = variable != null ? variable : "";
		this.value = value;
		
	}
	
	public String variable() {
		return this.variable;
	}
	
	public T value() {
		return this.value;
	}
	
}
