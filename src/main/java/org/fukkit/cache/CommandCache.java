package org.fukkit.cache;

import org.bukkit.command.defaults.BukkitCommand;
import org.fukkit.Fukkit;
import org.fukkit.command.FleXCommand;

import io.flex.commons.cache.LinkedCache;

public class CommandCache extends LinkedCache<FleXCommand, String> {
	
	private static final long serialVersionUID = -86340809195221360L;
	
	public CommandCache() {
		super((command, name) -> ((BukkitCommand)command).getName().equalsIgnoreCase(name));
	}
	
	public FleXCommand getByAlias(String alias) {
		return this.stream().filter(c -> ((BukkitCommand)c).getAliases().stream().anyMatch(a -> a.equalsIgnoreCase(alias))).findFirst().orElse(null);
	}

	@Override
	public boolean load() {
		Fukkit.getCommandFactory().initializeCommands();
		return true;
	}
	
}
