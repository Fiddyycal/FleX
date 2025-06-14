package org.fukkit.consequence;

public enum ConvictionType {
	
	BAN("Ban"), MUTE("Mute"), KICK("Kick"), REPORT("Report");

	private String name;
	
	private ConvictionType(String name) {
		this.name = name;
	}
	
	@Override
	public String toString() {
		return this.name;
	}
	
}
