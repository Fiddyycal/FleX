package org.fukkit.command.defaults.admin;

import io.flex.commons.utils.ArrayUtils;

public class DebugSubCommand extends AbstractAdminSubCommand {
	
	public DebugSubCommand(AdminCommand command) {
		super(command, "debug", "bug");
	}

	@Override
	public boolean perform(String[] args, String[] flags) {
		
		if (args.length != 1) {
			this.command.usage("/<command> database/configuration <name> [-l, -r, -c]");
			return false;
		}
		
		//boolean database = args[0].equalsIgnoreCase("database") || args[0].equalsIgnoreCase("data") || args[0].equalsIgnoreCase("sql") || args[0].equalsIgnoreCase("cloud");
		//boolean config = args[0].equalsIgnoreCase("configuration") || args[0].equalsIgnoreCase("config");

		boolean rows = ArrayUtils.contains(flags, "-r");
		boolean columns = ArrayUtils.contains(flags, "-c");
		boolean local = ArrayUtils.contains(flags, "-l");
		
		if (local) {
			
			if (rows) {
				this.command.getPlayer().sendMessage("...");
			}
			
			if (columns) {
				this.command.getPlayer().sendMessage("...");
			}
			
		} else {
			
			if (rows) {
				this.command.getPlayer().sendMessage("...");
			}
			
			if (columns) {
				this.command.getPlayer().sendMessage("...");
			}
			
		}
		return true;
		
	}

}
