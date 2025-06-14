package org.fukkit.command;

import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.plugin.Plugin;

import io.flex.commons.Nullable;

public interface CommandFactory {
	
	public void initializeCommands();
	
	public void register(BukkitCommand... commands);
	
	public void unregister(BukkitCommand... commands);

	public void unregister(String... labels);
	
	public void unregister(@Nullable Plugin plugin, BukkitCommand... commands);

	public void unregister(@Nullable Plugin plugin, String... labels);
	
	public boolean isRegistered(String command);

	public boolean isKnown(String command);
	
}
