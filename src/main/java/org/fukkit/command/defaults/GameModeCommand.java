package org.fukkit.command.defaults;

import org.bukkit.GameMode;
import org.fukkit.Fukkit;
import org.fukkit.command.Command;
import org.fukkit.command.FleXCommandAdapter;
import org.fukkit.command.GlobalCommand;
import org.fukkit.command.RestrictCommand;
import org.fukkit.entity.FleXPlayer;
import org.fukkit.theme.Theme;
import org.fukkit.theme.ThemeMessage;

import io.flex.commons.file.Variable;
import io.flex.commons.utils.StringUtils;

@GlobalCommand
@RestrictCommand(permission = "flex.command.gamemode", disallow = {})
@Command(name = "gamemode", usage = "/<command> <mode> [<player>]", aliases = { "mode", "gm" })
public class GameModeCommand extends FleXCommandAdapter {

	@Override
    public boolean perform(String[] args, String[] flags) {

		if (args.length != 0 && args.length != 1 && args.length != 2) {
			this.usage(this.getPlayer().hasPermission("flex.command.gamemode.others") ? this.getUsage() : "/<command> [survival/creative/adventure/spectator]");
        	return false;
		}
		
		FleXPlayer fp = args.length == 2 ? Fukkit.getPlayer(args[1]) : this.getPlayer();
		
		if (this.getPlayer() != fp && !this.getPlayer().hasPermission("flex.command.gamemode.others")) {
        	this.noPermission();
    		return false;
    	}
		
		if (fp == null) {
			this.playerNotFound(args[1]);
			return false;
		}
		
		if (!fp.isOnline()) {
			this.playerNotOnline(fp);
			return false;
		}
		
		Theme theme = fp != null ? fp.getTheme() : this.getPlayer().getTheme();
		GameMode mode = fp != null ? fp.getPlayer().getGameMode() : GameMode.SURVIVAL;
		
		switch (mode) {
		case SURVIVAL:
			
			mode = GameMode.CREATIVE;
			break;
			
        case CREATIVE:
			
        	mode = GameMode.ADVENTURE;
			break;
			
		case ADVENTURE:
			
			mode = GameMode.SPECTATOR;
			break;
			
		case SPECTATOR:
			
			mode = GameMode.SURVIVAL;
			break;
	
		default:
			
			mode = GameMode.SURVIVAL;
			break;
			
		}
		
		if (args.length > 0)
			
			if (StringUtils.equalsIgnoreCaseAny(args[0], "0", "survivial", "sur", "s"))
				mode = GameMode.SURVIVAL;

			else if (StringUtils.equalsIgnoreCaseAny(args[0], "1", "creative", "cre", "c"))
				mode = GameMode.CREATIVE;
		
			else if (StringUtils.equalsIgnoreCaseAny(args[0], "2", "adventure", "adv", "c"))
				mode = GameMode.ADVENTURE;

			else if (StringUtils.equalsIgnoreCaseAny(args[0], "3", "spectator", "spec", "sp"))
				mode = GameMode.SPECTATOR;
		
			else {
				this.usage(this.getPlayer().hasPermission("flex.command.gamemode.others") ? this.getUsage() : "/<command> [enable/disable]");
	        	return false;
			}
		
		if (fp.getPlayer().getGameMode() == mode) {
			
			if (this.getPlayer() != fp)
				this.getPlayer().sendMessage(ThemeMessage.GAMEMODE_FAILURE_OTHER.format(this.getPlayer().getTheme(), this.getPlayer().getLanguage(),
			        	
				        new Variable<String>("%player%", fp.getName()),
						new Variable<GameMode>("%gamemode%", mode)
				        
				));
				
			else fp.sendMessage(ThemeMessage.GAMEMODE_FAILURE.format(theme, fp.getLanguage(),
		        		
			        	new Variable<String>("%player%", this.getPlayer().getName()),
						new Variable<GameMode>("%gamemode%", mode)
			        	
			    ));
			
			return false;
			
		}
		
    	fp.getPlayer().setGameMode(mode);
        
        fp.sendMessage(ThemeMessage.GAMEMODE_SUCCESS.format(theme, fp.getLanguage(),
        		
        		new Variable<String>("%player%", this.getPlayer().getName()),
				new Variable<GameMode>("%gamemode%", mode)
        		
        ));
        
        if (this.getPlayer() != fp) {
        	this.getPlayer().sendMessage(ThemeMessage.GAMEMODE_SUCCESS_OTHER.format(this.getPlayer().getTheme(), this.getPlayer().getLanguage(),
        			
					new Variable<String>("%player%", fp.getName()),
					new Variable<GameMode>("%gamemode%", mode)
             		
            ));
        }
        
    	return true;
        
    }
    
}
