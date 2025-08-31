package org.fukkit.command.defaults;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.command.CommandSender;
import org.fukkit.Fukkit;
import org.fukkit.command.Command;
import org.fukkit.command.FleXCommandAdapter;
import org.fukkit.command.GlobalCommand;
import org.fukkit.entity.FleXPlayer;

@GlobalCommand
@Command(name = "message", usage = "/<command> [<player>] <message>", aliases = { "msg", "chat", "reply", "r", "chatback" })
public class MessageCommand extends FleXCommandAdapter {

	public static final Map<FleXPlayer, FleXPlayer> REPLY_STORE = new HashMap<FleXPlayer, FleXPlayer>();
	
	@Override
    public boolean perform(CommandSender sender, String[] args, String[] flags) {
		
		boolean reply = this.command.equalsIgnoreCase("reply") || this.command.equalsIgnoreCase("r") || this.command.equalsIgnoreCase("chatback");
		
		if (args.length < 1 || (!reply && args.length < 2)) {
			this.usage(sender);
        	return false;
		}
		
		FleXPlayer fp = reply ? REPLY_STORE.get(((FleXPlayer)sender)) : Fukkit.getPlayer(args[0]);
		
		if (reply && fp == null) {
			// TODO: No one to reply to.
			((FleXPlayer)sender).sendMessage("[ThemeMessage=COMMAND_REPLY_FAILURE]");
			return false;
		}
		
		if (fp == null) {
			this.playerNotFound(sender, args[0]);
			return false;
		}
		
		if (!fp.isOnline()) {
			this.playerNotOnline(sender, fp);
			return false;
		}
		
		if (fp == ((FleXPlayer)sender)) {
			// TODO:
			((FleXPlayer)sender).sendMessage("[ThemeMessage=COMMAND_MESSAGE_FAILURE_SELF]");
    		return false;
    	}
		
		if (fp.getServer() != ((FleXPlayer)sender).getServer()) {
			this.playerNotAccessible(sender, fp);
    		return false;
		}
		
		StringBuilder arguments = new StringBuilder();
		
		for (int i = reply ? 0 : 1; i < args.length; i++)
			arguments.append((arguments.length() > 0 ? " " : "") + args[i]);
		
		REPLY_STORE.put(fp, ((FleXPlayer)sender));
		
		// TODO:
		((FleXPlayer)sender).sendMessage(((FleXPlayer)sender).getTheme().format("<pp>[<spc>To\\s<sc>" + fp.getDisplayName(((FleXPlayer)sender).getTheme()) + "<pp>]\\s<lore>" + arguments.toString()));
		
		fp.sendMessage(fp.getTheme().format("<pp>[<spc>From\\s<sc>" + ((FleXPlayer)sender).getDisplayName(fp.getTheme()) + "<pp>]\\s<lore>" + arguments.toString()));
		
    	return true;
        
    }

}
