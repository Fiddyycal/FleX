package org.fukkit;

import java.util.ArrayList;

import org.fukkit.event.entity.EntityCleanEvent.CleanType;

import net.md_5.fungee.server.ServerRegion;

public enum NetworkSetting {

	SERVER_REGION("Region", ServerRegion.NA.toString()),
	
	CLEAN_TYPE("Settings.Clean", CleanType.SQUEAKY.name()),
	
	JOIN_TELEPORT("Settings.Join-Teleport", true),
	
	RESET_WORLDS("Settings.Reset-Worlds", true),
	
	BACKUP_WORLDS("Settings.Backup-Worlds", false),
		
	MESSAGES_JOIN_LEAVE("Settings.Join-Leave-Messages", false),
	
	VERSION_BLACKLIST("Settings.Version-Blacklist", new ArrayList<String>()),
	
	UNREGISTER_COMMANDS("Unregister-Commands", new ArrayList<String>());
	
	private String key;
	private Object def;
	
	private NetworkSetting(String key, Object def) {
		this.key = key;
		this.def = def;
	}
	
	public String getKey() {
		return this.key;
	}
	
	public Object getDefault() {
		return this.def;
	}
	
}
