package io.flex.commons.file;

public class VariableString extends Variable<String> {
	
	private VariableString(String variable, String value) {
		super(variable, value);
	}

	public static VariableString of(String variable, String value) {
		return new VariableString(variable, value);
	}
	
}
