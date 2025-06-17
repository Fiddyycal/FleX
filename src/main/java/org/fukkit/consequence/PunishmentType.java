package org.fukkit.consequence;

public enum PunishmentType {
	
	BAN("Ban"), MUTE("Mute"), KICK("Kick"), REPORT("Report");

	private String name;
	
	private PunishmentType(String name) {
		this.name = name;
	}
	
	@Override
	public String toString() {
		return this.name;
	}
	
}
