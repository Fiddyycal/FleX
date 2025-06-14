package org.fukkit.command.defaults.admin;

import java.util.function.BiPredicate;

import io.flex.commons.cache.Cacheable;
import io.flex.commons.utils.ArrayUtils;

public abstract class AbstractAdminSubCommand implements Cacheable, BiPredicate<String[], String[]> {
	
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
	public boolean test(String[] args, String[] flags) {
		return this.perform(ArrayUtils.remove(args, args[0]), flags);
	}
	
	public abstract boolean perform(String[] args, String[] flags);
	
}
