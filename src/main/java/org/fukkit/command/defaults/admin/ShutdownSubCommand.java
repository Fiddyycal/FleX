package org.fukkit.command.defaults.admin;

import org.bukkit.command.CommandSender;

public class ShutdownSubCommand extends AbstractAdminSubCommand {
	
	public ShutdownSubCommand(AdminCommand command) {
		super(command);
	}

	@Override
	public boolean perform(CommandSender sender, String[] args, String[] flags) {

		if (args.length != 1 && args.length != 2) {
			this.command.usage(sender, "/<command> shutdown [reason]");
			return false;
		}
		
		// TODO: "An Administrator has shut the server down on the basis of a problematic update:\n[reason] OR no further information"
		return true;
		
	}

}
