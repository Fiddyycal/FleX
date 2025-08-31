package org.fukkit.command.defaults.admin;

import org.bukkit.command.CommandSender;

import io.flex.commons.cache.Cacheable;
import io.flex.commons.function.TriPredicate;
import io.flex.commons.utils.ArrayUtils;

public abstract class AbstractAdminSubCommand implements Cacheable, TriPredicate<CommandSender, String[], String[]> {
	
	protected AdminCommand command;
	
	private String[] aliases;
	
	public AbstractAdminSubCommand(AdminCommand command, String... aliases) {
		this.command = command;
		this.aliases = aliases;
	}
	
	public String[] getAliases() {
		return this.aliases;
	}
	
	@Override
	public boolean test(CommandSender sender, String[] args, String[] flags) {
		return this.perform(sender, ArrayUtils.remove(args, args[0]), flags);
	}
	
	public abstract boolean perform(CommandSender sender, String[] args, String[] flags);
	
}
