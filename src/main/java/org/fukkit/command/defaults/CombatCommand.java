package org.fukkit.command.defaults;

import org.bukkit.command.CommandSender;
import org.fukkit.combat.gui.CombatGui;
import org.fukkit.command.Command;
import org.fukkit.command.FlaggedCommand;
import org.fukkit.command.FleXCommandAdapter;
import org.fukkit.command.GlobalCommand;
import org.fukkit.command.RestrictCommand;
import org.fukkit.entity.FleXPlayer;

@GlobalCommand
@FlaggedCommand(flags = { "-r" })
@RestrictCommand(permission = "flex.command.combat", disallow = {})
@Command(name = "combat", aliases = { "knockback", "kb", "hitreg", "hr" }, usage = "/<command> <player> [-f]")
public class CombatCommand extends FleXCommandAdapter {
	
	@Override
	public boolean perform(CommandSender sender, String[] args, String[] flags) {
		
		if (args.length != 0) {
			this.usage(sender);
			return false;
		}
		
		((FleXPlayer)sender).openMenu(new CombatGui(((FleXPlayer)sender).getTheme()), true);
		return true;
		
	}

}
