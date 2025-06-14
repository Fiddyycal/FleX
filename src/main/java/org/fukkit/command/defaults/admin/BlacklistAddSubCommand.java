package org.fukkit.command.defaults.admin;

public class BlacklistAddSubCommand extends AbstractAdminSubCommand {
	
	public BlacklistAddSubCommand(AdminCommand command) {
		super(command, "blacklist", "bl");
	}

	@Override
	public boolean perform(String[] args, String[] flags) {
		
		if (args.length != 2 && args.length != 3) {
			this.command.usage("/<command> blacklist/bl <player> [reason]");
			return false;
		}
		
		// TODO
		return true;
		
	}

}
