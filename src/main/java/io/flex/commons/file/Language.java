package io.flex.commons.file;

public enum Language {
	
	ENGLISH("en_US"),
	ENGLISH_AU("en_AU");
	
	private String name;
	
	private Language(String name) {
		this.name = name;
	}
	
	public String toString() {
		return this.name;
	}

}
