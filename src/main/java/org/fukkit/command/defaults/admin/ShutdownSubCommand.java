package org.fukkit.command.defaults.admin;

import org.bukkit.command.CommandSender;

public class ShutdownSubCommand extends AbstractAdminSubCommand {
	
	public ShutdownSubCommand(AdminCommand command) {
		super(command, "shutdown", "stop");
	}

	@Override
	public boolean perform(CommandSender sender, String[] args, String[] flags) {

		if (args.length != 1 && args.length != 2) {
			this.command.usage(sender, "/<command> shutdown/stop [reason]");
			return false;
		}
		
		// TODO: "An Administrator has shut the server down on the basis of a problematic update:\nno further information"
		return true;
		
	}

}
