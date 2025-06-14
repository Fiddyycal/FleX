package org.fukkit.command;

import org.bukkit.command.CommandSender;
import org.fukkit.PlayerState;
import org.fukkit.entity.FleXPlayer;

import io.flex.commons.cache.Cacheable;

public interface FleXCommand extends Cacheable {
	
	public String[] getFlags();
	
	public String[] getUses();
	
	public long getDelay();
	
	public FleXPlayer getPlayer();
	
	public CommandSender getSender();
	
	public void playerNotFound(String name);
	
	public void playerNotOnline(FleXPlayer player);
	
	public void playerNotAccessible(FleXPlayer player);
	
	public void noPermission();
	
	public void unknownCommand();
	
	public void cantUse(double timeLeft);
	
	public void cantUse(PlayerState state);
	
	public void usage(String... usage);
	
	public void usage();
	
	public void invalid(String... usage);
	
	public void invalid();

	public void incompatible(String flag);
	
	public void unregister();
	
	public boolean isConsoleCommand();

	public boolean perform(String[] args, String[] flags);

}
