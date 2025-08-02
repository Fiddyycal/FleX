package net.md_5.fungee.server;

import net.md_5.fungee.ProtocolVersion;

public enum ServerVersion {
	
	/**
	 * THE ORDER OF THIS MATTERS!!
	 */
	UNSPECIFIED("Unsupported", null, ProtocolVersion.UNSPECIFIED),
	
	v1_7_R4("1.7.4", "R0.4-SNAPSHOT", true, ProtocolVersion.v1_7),
	
	v1_8_R3("1.8.8", "R0.3-SNAPSHOT", true, ProtocolVersion.v1_8),
	
	v1_21_R2("1.21.8", "R0.2", ProtocolVersion.LATEST);
	
	public static final ServerVersion LATEST = ServerVersion.latest();
	
	private static ServerVersion latest() {
		
		for (ServerVersion version : values())
			if (version.isLatest())
				return version;
		
		return null;
		
	}
	
	private String version;
	private String revision;
	
	private boolean snapshot;
	
	ProtocolVersion client;
	
	private ServerVersion(String version, String revision, ProtocolVersion client) {
		this.version = version;
		this.revision = revision;
		this.snapshot = false;
	}
	
	private ServerVersion(String version, String revision, boolean snapshot, ProtocolVersion client) {
		this.version = version;
		this.revision = revision;
		this.snapshot = snapshot;
	}
	
	public String getName() {
		return this.version != null && this.revision != null ? this.version + "-" + this.revision : "Unspecified/Unsupported";
	}
	
	public String getRevision() {
		return this.revision;
	}
	
	public ProtocolVersion getNativeClientVersion() {
		return this.client;
	}
	
	public boolean is1_8() {
		return this.name().startsWith("v1_8");
	}
	
	public boolean isLatest() {
		return this.client == ProtocolVersion.LATEST;
	}
	
	public boolean isSupported() {
		return (this != UNSPECIFIED);
	}
	
	public boolean isSnapshot() {
		return (this.snapshot);
	}
	
	@Override
	public String toString() {
		return this.version;
	}
	
	public static ServerVersion valueOf(String version, boolean strict) {
		
		if (!strict) {
			
			for (ServerVersion ver : ServerVersion.values())
				if (ver.getName().equalsIgnoreCase(version) || ver.toString().equalsIgnoreCase(version))
					return ver;
			
		}
		
		return valueOf(version);
		
	}
	
}
