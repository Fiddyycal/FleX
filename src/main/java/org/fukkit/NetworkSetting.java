package org.fukkit;

import java.util.ArrayList;

import org.fukkit.event.entity.EntityCleanEvent.CleanType;

import net.md_5.fungee.server.ServerRegion;

public enum NetworkSetting {

	SERVER_REGION("region", ServerRegion.NA.toString()),
	
	CLEAN_TYPE("settings.clean", CleanType.SQUEAKY.name()),
	
	JOIN_TELEPORT("settings.join-teleport", true),
	
	RESET_WORLDS("settings.reset-worlds", true),
	
	BACKUP_WORLDS("settings.backup-worlds", false),
		
	MESSAGES_JOIN_LEAVE("settings.join-leave-messages", false),
	
	VERSION_BLACKLIST("settings.version-blacklist", new ArrayList<String>()),
	
	UNREGISTER_COMMANDS("unregister-commands", new ArrayList<String>());
	
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
