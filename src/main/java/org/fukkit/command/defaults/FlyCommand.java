package org.fukkit.command.defaults;

import org.bukkit.entity.Player;
import org.fukkit.Fukkit;
import org.fukkit.PlayerState;
import org.fukkit.command.Command;
import org.fukkit.command.FleXCommandAdapter;
import org.fukkit.command.RestrictCommand;
import org.fukkit.entity.FleXPlayer;
import org.fukkit.theme.Theme;
import org.fukkit.theme.ThemeMessage;

import io.flex.commons.file.Variable;
import io.flex.commons.utils.StringUtils;

@RestrictCommand(permission = "flex.command.fly", disallow = { PlayerState.INGAME_PVE_ONLY, PlayerState.INGAME, PlayerState.SPECTATING })
@Command(name = "fly", usage = "/<command> [<player>] [enable/disable]", aliases = "flight")
public class FlyCommand extends FleXCommandAdapter {
	
	@Override
    public boolean perform(String[] args, String[] flags) {

		if (args.length != 0 && args.length != 1 && args.length != 2) {
			this.usage(this.getPlayer().hasPermission("flex.command.fly.others") ? this.getUsage() : "/<command> [enable/disable]");
        	return false;
		}
		
		FleXPlayer fp = args.length > 0 ? Fukkit.getPlayer(args[0]) : this.getPlayer();
		
		if (fp == null) {
			this.playerNotFound(args[0]);
			return false;
		}
		
		if (fp != this.getPlayer() && !this.getPlayer().hasPermission("flex.command.fly.others")) {
        	this.noPermission();
    		return false;
    	}
		
		if (!fp.isOnline()) {
			this.playerNotOnline(fp);
			return false;
		}
		
		boolean flight = fp != null && fp.isOnline() ? !fp.getPlayer().getAllowFlight() : false;
		Theme theme = fp != null ? fp.getTheme() : this.getPlayer().getTheme();
		
		if (args.length > 1)
		
			if (StringUtils.equalsIgnoreCaseAny(args[1], "disable", "false", "off", "0"))
				flight = false;
		
			else if (StringUtils.equalsIgnoreCaseAny(args[1], "enable", "true", "on", "1"))
			    flight = true;
		
			else {
				this.usage(this.getPlayer().hasPermission("flex.command.fly.others") ? this.getUsage() : "/<command> [enable/disable]");
	        	return false;
			}
		
		Player pl = fp.getPlayer();
		
		if (pl.getAllowFlight() == flight) {
			
			if (this.getPlayer() != fp)
				this.getPlayer().sendMessage(ThemeMessage.FLIGHT_FAILURE_OTHER.format(this.getPlayer().getTheme(), this.getPlayer().getLanguage(),
			        	
				        new Variable<String>("%player%", fp.getName()),
						new Variable<String>("%flight%", flight ? Theme.success + "enabled" : Theme.failure + "disabled")
				        
				));
				
			else fp.sendMessage(ThemeMessage.FLIGHT_FAILURE.format(theme, fp.getLanguage(),
		        		
			        	new Variable<String>("%player%", this.getPlayer().getName()),
						new Variable<String>("%flight%", flight ? Theme.success + "enabled" : Theme.failure + "disabled")
			        	
			    ));
			
			return false;
			
		}
		
		pl.setAllowFlight(flight);
		pl.setFlying(flight);
        
        fp.sendMessage(ThemeMessage.FLIGHT_SUCCESS.format(theme, fp.getLanguage(),
        		
        		new Variable<String>("%player%", this.getPlayer().getName()),
				new Variable<String>("%flight%", flight ? Theme.success + "enabled" : Theme.failure + "disabled")
        		
        ));
        
        if (this.getPlayer() != fp) {
        	this.getPlayer().sendMessage(ThemeMessage.FLIGHT_SUCCESS_OTHER.format(this.getPlayer().getTheme(), this.getPlayer().getLanguage(),
        			
					new Variable<String>("%player%", fp.getName()),
					new Variable<String>("%flight%", flight ? Theme.success + "enabled" : Theme.failure + "disabled")
             		
            ));
        }
        
    	return true;
        
    }

}
