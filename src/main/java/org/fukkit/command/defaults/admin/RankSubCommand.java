package org.fukkit.command.defaults.admin;

import org.bukkit.command.CommandSender;
import org.fukkit.Fukkit;
import org.fukkit.Memory;
import org.fukkit.entity.FleXPlayer;
import org.fukkit.reward.Rank;
import org.fukkit.theme.Theme;
import org.fukkit.theme.ThemeMessage;

import io.flex.commons.file.Language;
import io.flex.commons.file.Variable;
import io.flex.commons.utils.ArrayUtils;

public class RankSubCommand extends AbstractAdminSubCommand {
	
	public RankSubCommand(AdminCommand command) {
		super(command, "setrank", "giverank", "rank", "r");
	}

	@Override
	public boolean perform(CommandSender sender, String[] args, String[] flags) {
		
		if (args.length < 2) {
			this.command.usage(sender, "/<command> setrank/giverank/rank/r <player> <rank> [reason]");
			return false;
		}
		
		FleXPlayer player = sender instanceof FleXPlayer ? (FleXPlayer) sender : null;
		FleXPlayer fp = player;
		
		Theme theme = player != null ? player.getTheme() : Memory.THEME_CACHE.getDefaultTheme();
		
		String name = args[0];
		Rank rank = null;
		
		StringBuilder reason = new StringBuilder();
		
	    for (String arg : ArrayUtils.remove(args, args[0], args[1])) {
	    	
	        if (reason.length() != 0)
	        	reason.append(" ");
	        
	        reason.append(arg);
	        
	    }
		
		fp = Fukkit.getPlayer(name);
		rank = Memory.RANK_CACHE.get(args[1]);
		
		if (fp == null) {
			this.command.playerNotFound(sender, name);
			return false;
		}

		if (rank == null) {
			
			if (player != null)
				player.sendMessage(ThemeMessage.RANK_FAILURE_NOT_FOUND.format(theme, player.getLanguage(), new Variable<String>("%rank%", args[1])));
			
			else sender.sendMessage(ThemeMessage.RANK_FAILURE_NOT_FOUND.format(theme, Language.ENGLISH, new Variable<String>("%rank%", args[1])));
			
			return false;
			
		}
		
		if (fp.getRank() == rank) {
			
			Variable<?>[] variables = {
				
				new Variable<String>("%display%", fp.getDisplayName(fp.getTheme(), true)),
				new Variable<String>("%player%", fp.getName()),
				new Variable<String>("%rank%", rank.getName())
				
			};

			if (player != null) {
				
				player.sendMessage(player != fp ?
						
						ThemeMessage.RANK_FAILURE_OTHER.format(theme, fp.getLanguage(), variables) :
						ThemeMessage.RANK_FAILURE.format(theme, fp.getLanguage(), variables)
						
				);
				
			} else sender.sendMessage(ThemeMessage.RANK_FAILURE_OTHER.format(theme, Language.ENGLISH, variables));
			
			return false;
			
		}
		
		fp.setRank(rank);
		
		String reas = args.length > 2 ? reason.toString() : "No reason found";
		
		Variable<?>[] variables = {
			
			new Variable<String>("%display%", fp.getDisplayName(theme, true)),
			new Variable<String>("%player%", fp.getName()),
			new Variable<String>("%rank%", rank.getName()),
			new Variable<String>("%reason%", reas)
			
		};
		
		if (player == null)
			sender.sendMessage(ThemeMessage.RANK_SUCCESS_OTHER.format(theme, Language.ENGLISH, variables));
			
		else {
			
			if (player != fp)
				player.sendMessage(ThemeMessage.RANK_SUCCESS_OTHER.format(theme, player.getLanguage(), variables));
			
		}
		
		if (player == fp || (player != fp && fp.isOnline()))
			fp.sendMessage(ThemeMessage.RANK_SUCCESS.format(fp.getTheme(), fp.getLanguage(), variables));
		
		return true;
		
	}

}
