package org.fukkit.command.defaults;

import org.bukkit.command.CommandSender;
import org.fukkit.Fukkit;
import org.fukkit.PlayerState;
import org.fukkit.command.Command;
import org.fukkit.command.FleXCommandAdapter;
import org.fukkit.command.GlobalCommand;
import org.fukkit.command.RestrictCommand;
import org.fukkit.entity.FleXPlayer;
import org.fukkit.event.player.FleXPlayerDisguiseEvent;
import org.fukkit.event.player.FleXPlayerDisguisedEvent;
import org.fukkit.event.player.FleXPlayerDisguisedEvent.Result;
import org.fukkit.theme.Theme;
import org.fukkit.theme.ThemeMessage;
import org.fukkit.utils.ThemeUtils;

import io.flex.commons.file.Language;

@GlobalCommand
@RestrictCommand(permission = "flex.command.disguise", disallow = { PlayerState.INGAME_PVE_ONLY, PlayerState.INGAME, PlayerState.SPECTATING, PlayerState.UNKNOWN })
@Command(name = "undisguise", aliases = { "ud", "und", "undis" }, usage = "/<command> [player]")
public class UnDisguiseCommand extends FleXCommandAdapter {
	
	public boolean perform(CommandSender sender, String[] args, String[] flags) {
		
		if (args.length != 0 && args.length != 1) {
			this.usage(sender);
			return false;
		}
		
		if (args.length == 1 && !((FleXPlayer)sender).hasPermission("flex.command.disguise.other")) {
			this.noPermission(sender);
			return false;
		}
		
		Theme theme = ((FleXPlayer)sender).getTheme();
		Language lang = ((FleXPlayer)sender).getLanguage();
		FleXPlayer player = args.length == 0 ? ((FleXPlayer)sender) : Fukkit.getPlayer(args[0]);
		
		if (player == null) {
			this.playerNotFound(sender, args[0]);
			return false;
		}
		
		if (!player.isOnline()) {
			this.playerNotOnline(sender, player);
			return false;
		}
		
		if (!player.isDisguised()) {
			player.sendMessage((player != ((FleXPlayer)sender) ? ThemeMessage.UNDISGUISE_FAILURE_OTHER : ThemeMessage.UNDISGUISE_FAILURE).format(theme, lang, ThemeUtils.getNameVariables(player, theme)));
			return false;
		}
		
		try {
			
			FleXPlayerDisguiseEvent load = new FleXPlayerDisguiseEvent(((FleXPlayer)sender), null);
			
			Fukkit.getEventFactory().call(load);
			
			if (load.isCancelled())
				return false;
			
			player.unDisguise();
			
		} catch (Exception e) {
			
			((FleXPlayer)sender).sendMessage(ThemeMessage.UNDISGUISE_FAILURE_ERROR.format(theme, lang, ThemeUtils.getNameVariables(player, theme)));
			return false;
			
		}
		
		FleXPlayerDisguiseEvent load = new FleXPlayerDisguisedEvent(((FleXPlayer)sender), null, Result.UNDISGUISE);
		
		Fukkit.getEventFactory().call(load);
		
		player.sendMessage(ThemeMessage.UNDISGUISE_SUCCESS.format(theme, lang, ThemeUtils.getNameVariables(((FleXPlayer)sender), theme)));
		
		if (((FleXPlayer)sender) != player)
			((FleXPlayer)sender).sendMessage(ThemeMessage.UNDISGUISE_SUCCESS_OTHER.format(theme, lang, ThemeUtils.getNameVariables(player, theme)));
		
		return true;
		
	}
	
}
