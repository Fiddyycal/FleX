package org.fukkit.command.defaults;

import java.util.HashMap;
import java.util.Map;

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
    public boolean perform(String[] args, String[] flags) {
		
		boolean reply = this.command.equalsIgnoreCase("reply") || this.command.equalsIgnoreCase("r") || this.command.equalsIgnoreCase("chatback");
		
		if (args.length < 1 || (!reply && args.length < 2)) {
			this.usage();
        	return false;
		}
		
		FleXPlayer fp = reply ? REPLY_STORE.get(this.getPlayer()) : Fukkit.getPlayer(args[0]);
		
		if (reply && fp == null) {
			// TODO: No one to reply to.
			this.getPlayer().sendMessage("[ThemeMessage=COMMAND_REPLY_FAILURE]");
			return false;
		}
		
		if (fp == null) {
			this.playerNotFound(args[0]);
			return false;
		}
		
		if (!fp.isOnline()) {
			this.playerNotOnline(fp);
			return false;
		}
		
		if (fp == this.getPlayer()) {
			// TODO:
			this.getPlayer().sendMessage("[ThemeMessage=COMMAND_MESSAGE_FAILURE_SELF]");
    		return false;
    	}
		
		if (fp.getServer() != this.getPlayer().getServer()) {
			this.playerNotAccessible(fp);
    		return false;
		}
		
		StringBuilder arguments = new StringBuilder();
		
		for (int i = reply ? 0 : 1; i < args.length; i++)
			arguments.append((arguments.length() > 0 ? " " : "") + args[i]);
		
		REPLY_STORE.put(fp, this.getPlayer());
		
		// TODO:
		this.getPlayer().sendMessage(this.getPlayer().getTheme().format("<pp>[<spc>To\\s<sc>" + fp.getDisplayName(this.getPlayer().getTheme()) + "<pp>]\\s<lore>" + arguments.toString()));
		
		fp.sendMessage(fp.getTheme().format("<pp>[<spc>From\\s<sc>" + this.getPlayer().getDisplayName(fp.getTheme()) + "<pp>]\\s<lore>" + arguments.toString()));
		
    	return true;
        
    }

}
