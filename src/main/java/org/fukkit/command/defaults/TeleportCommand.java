package org.fukkit.command.defaults;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.fukkit.PlayerState;
import org.fukkit.api.helper.PlayerHelper;
import org.fukkit.command.Command;
import org.fukkit.command.FleXCommandAdapter;
import org.fukkit.command.GlobalCommand;
import org.fukkit.command.RestrictCommand;
import org.fukkit.entity.FleXPlayer;
import org.fukkit.theme.ThemeMessage;

import io.flex.commons.file.Variable;
import io.flex.commons.utils.ArrayUtils;

@GlobalCommand
@SuppressWarnings("deprecation")
@RestrictCommand(permission = "flex.command.teleport", disallow = { PlayerState.INGAME_PVE_ONLY, PlayerState.INGAME })
@Command(name = "teleport", usage = "/<command> <player> [destination player]", aliases = "tp")
public class TeleportCommand extends FleXCommandAdapter {

	public boolean perform(String[] args, String[] flags) {

		if (args.length != 1 && args.length != 2) {
			this.usage(this.getPlayer().hasPermission("flex.command.teleport.others") ? this.getUsage() : "/<command> <player>");
			return false;
		}
		
		FleXPlayer teleport = args.length == 2 ? PlayerHelper.getPlayer(args[0]) : this.getPlayer();
		FleXPlayer fp = args.length == 2 ? PlayerHelper.getPlayer(args[1]) : PlayerHelper.getPlayer(args[0]);
		
		if (teleport == null) {
			this.playerNotFound(args[0]);
			return false;
		}
		
		if (!teleport.isOnline()) {
			this.playerNotOnline(teleport);
			return false;
		}
		
		if (fp == null) {
			this.playerNotFound(args.length == 2 ? args[1] : args[0]);
			return false;
		}
		
		if (!fp.isOnline()) {
			this.playerNotOnline(fp);
			return false;
		}
		
		if (teleport.getServer() != fp.getServer()) {
			// TODO: add /sendTo
			fp.sendMessage(ChatColor.RED + "An error occured attempting to teleport a player across a FleXServer instance.");
    		return false;
		}
		
		Location location  = fp.getLocation();

		boolean safe = location.getBlock().getRelative(BlockFace.DOWN).getType() != Material.AIR;
		boolean extraSafe = teleport.getPlayer().getGameMode() == GameMode.CREATIVE || teleport.getPlayer().isFlying() || teleport.getPlayer().getAllowFlight();
		
		teleport.getPlayer().teleport(safe || extraSafe ? location : location.getWorld().getHighestBlockAt(location).getLocation());
		
		Variable<?>[] variables = {

				new Variable<String>("%name%", fp.getDisplayName()),
				new Variable<String>("%player%", fp.getName()),
				new Variable<String>("%display%", fp.getDisplayName(teleport.getTheme()))
				
		};

		teleport.sendMessage(ThemeMessage.TELEPORT_SUCCESS.format(teleport.getTheme(), teleport.getLanguage(), variables));
		
		if (args.length == 2 && teleport != fp)
			fp.sendMessage(ThemeMessage.TELEPORT_SUCCESS_OTHER.format(fp.getTheme(), fp.getLanguage(), ArrayUtils.add(variables,
					
					new Variable<String>("%tp_name%", teleport.getDisplayName()),
					new Variable<String>("%tp_player%", teleport.getName()),
					new Variable<String>("%tp_display%", teleport.getDisplayName(fp.getTheme()))
					
			)));
		
		return true;
		
    }
    
}
