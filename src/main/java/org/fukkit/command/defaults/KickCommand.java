package org.fukkit.command.defaults;

import org.fukkit.clickable.Menu;
import org.fukkit.command.Command;
import org.fukkit.command.FlaggedCommand;
import org.fukkit.command.RestrictCommand;
import org.fukkit.consequence.PunishmentType;
import org.fukkit.consequence.gui.SanctionGui;
import org.fukkit.entity.FleXPlayer;

@FlaggedCommand(flags = { "-s" })
@RestrictCommand(permission = "flex.command.punish", disallow = {})
@Command(name = "kick", usage = "/<command> <player> [-s]", aliases = { "flexkick" })
public class KickCommand extends AbstractSanctionCommand {
	
	@Override
	protected Menu getMenu(FleXPlayer other, boolean ip, boolean silent) {
		return new SanctionGui(this.getPlayer(), other, PunishmentType.KICK, false, silent);
	}
	
}
