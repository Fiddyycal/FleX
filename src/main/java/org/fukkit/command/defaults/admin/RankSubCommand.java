package org.fukkit.command.defaults.admin;

import org.fukkit.Fukkit;
import org.fukkit.Memory;
import org.fukkit.entity.FleXPlayer;
import org.fukkit.reward.Rank;
import org.fukkit.theme.ThemeMessage;

import io.flex.commons.file.Language;
import io.flex.commons.file.Variable;
import io.flex.commons.utils.ArrayUtils;

public class RankSubCommand extends AbstractAdminSubCommand {
	
	public RankSubCommand(AdminCommand command) {
		super(command, "setrank", "giverank", "rank", "r");
	}

	@Override
	public boolean perform(String[] args, String[] flags) {
		
		if (args.length < 2) {
			this.command.usage("/<command> setrank/giverank/rank/r <player> <rank> [reason]");
			return false;
		}
		
		FleXPlayer fp = this.command.getPlayer();
		
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
			this.command.playerNotFound(name);
			return false;
		}

		if (rank == null) {
			

			if (this.command.getPlayer() != null)
				this.command.getPlayer().sendMessage(ThemeMessage.RANK_FAILURE_NOT_FOUND.format(fp.getTheme(), fp.getLanguage(), new Variable<String>("%rank%", args[1])));
			
			else this.command.getSender().sendMessage(ThemeMessage.RANK_FAILURE_NOT_FOUND.format(Memory.THEME_CACHE.stream().findFirst().get(), Language.ENGLISH, new Variable<String>("%rank%", args[1])));
			
			return false;
		}
		
		if (fp.getRank() == rank) {
			
			Variable<?>[] variables = {
				
				new Variable<String>("%display%", fp.getDisplayName(fp.getTheme(), true)),
				new Variable<String>("%player%", fp.getName()),
				new Variable<String>("%rank%", rank.getName())
				
			};
			
			if (this.command.getPlayer() != null) {
				
				this.command.getPlayer().sendMessage(this.command.getPlayer() != fp ?
						
						ThemeMessage.RANK_FAILURE_OTHER.format(fp.getTheme(), fp.getLanguage(), variables) :
						ThemeMessage.RANK_FAILURE.format(fp.getTheme(), fp.getLanguage(), variables)
						
				);
				
			} else 
				
				this.command.getPlayer().sendMessage(ThemeMessage.RANK_FAILURE_OTHER.format(Memory.THEME_CACHE.stream().findFirst().get(), Language.ENGLISH, variables));
			
			return false;
			
		}
		
		fp.setRank(rank);
		
		String reas = args.length > 2 ? reason.toString() : "No reason found";
		
		Variable<?>[] variables = {
			
			new Variable<String>("%display%", fp.getDisplayName(fp.getTheme(), true)),
			new Variable<String>("%player%", fp.getName()),
			new Variable<String>("%rank%", rank.getName()),
			new Variable<String>("%reason%", reas)
			
		};
		
		if (this.command.getPlayer() == null)
			this.command.getSender().sendMessage(ThemeMessage.RANK_SUCCESS_OTHER.format(Memory.THEME_CACHE.stream().findFirst().get(), Language.ENGLISH, variables));
			
		else {
			
			if (this.command.getPlayer() != fp)
				this.command.getPlayer().sendMessage(ThemeMessage.RANK_SUCCESS_OTHER.format(this.command.getPlayer().getTheme(), this.command.getPlayer().getLanguage(), variables));
			
		}
		
		if (this.command.getPlayer() == fp || (this.command.getPlayer() != fp && fp.isOnline()))
			fp.sendMessage(ThemeMessage.RANK_SUCCESS.format(fp.getTheme(), fp.getLanguage(), variables));
		
		return true;
		
	}

}
