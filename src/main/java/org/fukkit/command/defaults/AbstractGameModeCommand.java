package org.fukkit.command.defaults;

import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.fukkit.Fukkit;
import org.fukkit.command.FleXCommandAdapter;
import org.fukkit.command.GlobalCommand;
import org.fukkit.command.RestrictCommand;
import org.fukkit.entity.FleXPlayer;
import org.fukkit.theme.Theme;
import org.fukkit.theme.ThemeMessage;
import org.fukkit.utils.ThemeUtils;

import io.flex.commons.file.Language;
import io.flex.commons.file.Variable;
import io.flex.commons.utils.ArrayUtils;
import io.flex.commons.utils.StringUtils;

@GlobalCommand
@RestrictCommand(permission = "flex.command.gamemode", disallow = {})
public abstract class AbstractGameModeCommand extends FleXCommandAdapter {

	public boolean perform(CommandSender sender, String[] args, String[] flags) {

        Theme theme = ((FleXPlayer)sender).getTheme();
        Language lang = ((FleXPlayer)sender).getLanguage();
        String mode = StringUtils.capitalize(this.getGameMode().toString().toLowerCase());
		
		Player pl = ((FleXPlayer)sender).getPlayer();

        if (args.length == 0) {
        	
            if (pl.getGameMode() == this.getGameMode()) {
                ((FleXPlayer)sender).sendMessage(ThemeMessage.GAMEMODE_FAILURE.format(theme, lang, new Variable<String>("%gamemode%", mode)));
                return false;
            }

            pl.setGameMode(this.getGameMode());
            ((FleXPlayer)sender).sendMessage(ThemeMessage.GAMEMODE_SUCCESS.format(theme, lang, new Variable<String>("%gamemode%", mode)));
            return true;
            
        } else if (args.length == 1) {
        	
			String name = args[0];
			FleXPlayer fp = Fukkit.getPlayer(name);
			
			if (fp == null) {
				this.playerNotFound(sender, name);
				return false;
			}
			
			if (!fp.isOnline() || fp.isDisguised()) {
				this.playerNotOnline(sender, fp);
				return false;
			}
            
			Variable<?>[] variables = ArrayUtils.add(ThemeUtils.getNameVariables(fp, theme), new Variable<String>("%gamemode%", mode));
			
			if (pl.getGameMode() == this.getGameMode()) {
            	
                ((FleXPlayer)sender).sendMessage(ThemeMessage.GAMEMODE_FAILURE_OTHER.format(theme, lang, variables));
                return false;
                
            }

			pl.setGameMode(this.getGameMode());
            fp.sendMessage(ThemeMessage.GAMEMODE_SUCCESS.format(fp.getTheme(), variables));
            
            if (((FleXPlayer)sender) != fp)
                ((FleXPlayer)sender).sendMessage(ThemeMessage.GAMEMODE_SUCCESS_OTHER.format(theme, lang, variables));
            
            return true;
            
        } else {

            this.usage(sender);
            return false;
            
        }
        
    }
    
    public abstract GameMode getGameMode();
	
}
