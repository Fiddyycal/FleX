package org.fukkit.command.defaults.admin;

import org.bukkit.command.CommandSender;

public class BlacklistAddSubCommand extends AbstractAdminSubCommand {
	
	public BlacklistAddSubCommand(AdminCommand command) {
		super(command, "blacklist", "bl");
	}

	@Override
	public boolean perform(CommandSender sender, String[] args, String[] flags) {
		
		if (args.length != 2 && args.length != 3) {
			this.command.usage(sender, "/<command> blacklist/bl <player> [reason]");
			return false;
		}
		
		// TODO
		return true;
		
	}

}
