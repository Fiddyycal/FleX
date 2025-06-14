package io.flex;

public enum FrameworkType {
	
	MINECRAFT_BUKKIT("FleX/Fukkit Injection", "BukkitAPI v1.1.1b", "v2.56.3b-SNAPSHOT"),
	
	MINECRAFT_BUNGEE("FleX/FungeeCord Plugin", "BungeeCordAPI v1.1.1b", "v1.0.1-SNAPSHOT"),
	
	GTA_RP("FleX/GTAV Script Modification", "Unknown", null),
	
	COUNTERSTRIKE_GO("FleX/Source Modification", "SourcePawn v1.10", null),
	
	PU_BATTLEGROUNDS("FleX/Pubg Portal", "PUBG API Developer Portal Beta", null);
	
	private String id;
	private String api;
	private String version;
	
	private FrameworkType(String id, String api, String version) {
		this.id = id != null ? id : "FleX/Unspecified";
		this.api = api != null ? api : "No Application Programming Interface (API)";
		this.version = version != null ? version : "Work in progress... (Coming Soon)";
	}
	
	public String api() {
		return this.api;
	}
	
	public String version() {
		return this.version;
	}
	
	public String toString() {
		return this.id;
	}

}
