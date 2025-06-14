package org.fukkit.command.defaults;

import org.fukkit.combat.gui.CombatGui;
import org.fukkit.command.Command;
import org.fukkit.command.FlaggedCommand;
import org.fukkit.command.FleXCommandAdapter;
import org.fukkit.command.GlobalCommand;
import org.fukkit.command.RestrictCommand;

@GlobalCommand
@FlaggedCommand(flags = { "-r" })
@RestrictCommand(permission = "flex.command.combat", disallow = {})
@Command(name = "combat", aliases = { "knockback", "kb", "hitreg", "hr" }, usage = "/<command> <player> [-f]")
public class CombatCommand extends FleXCommandAdapter {
	
	@Override
	public boolean perform(String[] args, String[] flags) {
		
		if (args.length != 0) {
			this.usage();
			return false;
		}
		
		this.getPlayer().openMenu(new CombatGui(this.getPlayer().getTheme()), true);
		return true;
		
	}

}
