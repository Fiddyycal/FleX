package org.fukkit.command.defaults;

import org.fukkit.clickable.Menu;
import org.fukkit.command.Command;
import org.fukkit.command.FlaggedCommand;
import org.fukkit.command.GlobalCommand;
import org.fukkit.command.RestrictCommand;
import org.fukkit.consequence.PunishmentType;
import org.fukkit.consequence.gui.SanctionGui;
import org.fukkit.entity.FleXPlayer;

@GlobalCommand
@FlaggedCommand(flags = { "-i", "-s" })
@RestrictCommand(permission = "flex.command.punish", disallow = {})
@Command(name = "mute", usage = "/<command> <player> [-i, -s]", aliases = { "flexmute", "tempmute", "tmute" })
public class MuteCommand extends AbstractSanctionCommand {
	
	@Override
	protected Menu getMenu(FleXPlayer player, FleXPlayer other, boolean ip, boolean silent) {
		return new SanctionGui(player.getTheme(), other, PunishmentType.MUTE, ip, silent);
	}
	
}
