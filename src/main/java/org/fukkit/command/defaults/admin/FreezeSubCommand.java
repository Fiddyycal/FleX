package org.fukkit.command.defaults.admin;

import org.bukkit.command.CommandSender;

public class FreezeSubCommand extends AbstractAdminSubCommand {
	
	public FreezeSubCommand(AdminCommand command) {
		super(command, "freeze", "frozen", "pause");
	}

	@Override
	public boolean perform(CommandSender sender, String[] args, String[] flags) {

		if (args.length != 1 && args.length != 2) {
			this.command.usage(sender, "/<command> freeze/frozen/pause [reason]");
			return false;
		}
		
		// TODO: "An Administrator has frozen the server on the basis of a problematic update:\nno further information"

		/*
		 * /frozen, /affected
		 * 
		 * &8&l&m                                &r
		 * &4&lFrozen Activity&8&l:
		 *  &8&l- &c&lDay/Night Cycle
		 *  &8&l- &c&lWeather Cycle
		 *  &8&l- &c&lBlock Physics
		 *  &8&l- &c&lMob Spawning
		 * &a&lAllowed Activity&8&l:
		 *  &8&l- &c&lPlayer Movement
		 *  &8&l- &c&lPlayer Chat
		 * &8&l&m                                &r
		 * 
		 */
		
		return true;
		
	}

}
